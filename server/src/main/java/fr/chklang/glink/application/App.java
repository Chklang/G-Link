package fr.chklang.glink.application;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.commons.lang3.SystemUtils;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.process.JerseyProcessingUncaughtExceptionHandler;
import org.glassfish.jersey.server.ResourceConfig;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.Provider;

import fr.chklang.glink.application.dto.HotkeyDTO;
import fr.chklang.glink.application.model.Configuration;
import fr.chklang.glink.application.rest.AssetsResource;
import fr.chklang.glink.application.rest.GLinkWebSocketApplication;
import fr.chklang.glink.application.rest.RestResource;
import io.ebean.EbeanServer;
import javafx.application.Application;
import javafx.application.Platform;
import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder;
import jxgrabkey.HotkeyConflictException;
import jxgrabkey.HotkeyListener;
import jxgrabkey.JXGrabKey;

public class App {
	private static App INSTANCE = null;

	public static App getInstance() {
		if (App.INSTANCE == null) {
			synchronized (App.class) {
				if (App.INSTANCE == null) {
					App.INSTANCE = new App();
				}
			}
		}
		return App.INSTANCE;
	}
	private int port;
	private HttpServer server;

	private boolean isShown = false;
	private int MY_HOTKEY_INDEX = 1;
	private HotkeyListener hotkeyListener;
	private boolean JXGrabberInitialized = false;

	private Provider provider;
	private boolean providerInitialized = false;
	
	private GLinkWebView webview;
	
	public App() {
	}
	
	public EbeanServer db() {
		return DB.getInstance().getServer();
	}
	
	public int getPort() {
		return this.port;
	}
	
	public void start() {
		this.initConfiguration();
		this.initHotkeys();
		this.runServer();
		Application.launch(GLinkWebView.class);
	}
	
	public void setWebview(GLinkWebView pWebview) {
		this.webview = pWebview;
	}
	
	private void removeHotkeys() {
		if (providerInitialized) {
			this.provider.reset();
			this.provider.stop();
		}

		// Shutdown JXGrabKey
		if (JXGrabberInitialized) {
			JXGrabKey.getInstance().removeHotkeyListener(hotkeyListener); // Optional
			JXGrabKey.getInstance().unregisterHotKey(MY_HOTKEY_INDEX); // Optional
		}
	}
	
	public void shutdown() {
		server.shutdownNow();
		
		this.removeHotkeys();

		Platform.exit();
		System.exit(0);
	}

	private void onGlobalHotKey() {
		Platform.runLater(() -> {
			if (isShown) {
				this.webview.hide();
			} else {
				this.webview.show();
			}
			isShown = !isShown;
		});
	}
	
	private void initConfiguration() {
		DB.getInstance().getServer().execute(() -> {
			if (Configuration.finder.byId("nbSquareX") == null) {
				new Configuration("nbSquaresX", "5").save();
			}
			if (Configuration.finder.byId("nbSquareY") == null) {
				new Configuration("nbSquaresY", "5").save();
			}
			if (Configuration.finder.byId("hotkey_ctrl") == null) {
				new Configuration("hotkey_ctrl", "true").save();
			}
			if (Configuration.finder.byId("hotkey_alt") == null) {
				new Configuration("hotkey_alt", "false").save();
			}
			if (Configuration.finder.byId("hotkey_shift") == null) {
				new Configuration("hotkey_shift", "false").save();
			}
			if (Configuration.finder.byId("hotkey") == null) {
				new Configuration("hotkey", "RIGHT").save();
			}
			
			Configuration.finder.all().forEach((pConf) -> {
				System.out.println("Conf : " + pConf.getKey() + " = " + pConf.getValue());
			});
		});
	}
	
	private HotkeyDTO getHotkeyConf() {
		Configuration lConfigurationCtrl = Configuration.finder.byId("hotkey_ctrl");
		Configuration lConfigurationAlt = Configuration.finder.byId("hotkey_alt");
		Configuration lConfigurationShift = Configuration.finder.byId("hotkey_shift");
		Configuration lConfigurationHotkey = Configuration.finder.byId("hotkey");
		
		HotkeyDTO lHotkeyDTO = new HotkeyDTO();
		lHotkeyDTO.hotkey_ctrl = Boolean.parseBoolean(lConfigurationCtrl.getValue());
		lHotkeyDTO.hotkey_alt = Boolean.parseBoolean(lConfigurationAlt.getValue());
		lHotkeyDTO.hotkey_shift = Boolean.parseBoolean(lConfigurationShift.getValue());
		lHotkeyDTO.hotkey = lConfigurationHotkey.getValue();
		
		return lHotkeyDTO;
	}

	private void hotkeysLinux(HotkeyDTO pConf) {
		File lFile = new File("libJXGrabKey.so");
		if (!lFile.exists()) {
			throw new RuntimeException("Can't to load libJXGrabKey.so");
		}
		try {
			System.load(lFile.getCanonicalPath());
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}

		// Enable Debug Output
		JXGrabKey.setDebugOutput(true);

		// Register some Hotkey
		try {
			JXGrabKey.getInstance().registerAwtHotkey(MY_HOTKEY_INDEX, HotKeysUtils.linuxMask(pConf), HotKeysUtils.linuxKey(pConf));
		} catch (HotkeyConflictException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);

			JXGrabKey.getInstance().cleanUp(); // Automatically unregisters
												// Hotkeys and Listeners
			// Alternatively, just unregister the key causing this or leave it
			// as it is
			// the key may not be grabbed at all or may not respond when
			// numlock, capslock or scrollock is on
			return;
		}

		// Implement HotkeyListener
		hotkeyListener = new jxgrabkey.HotkeyListener() {
			public void onHotkey(int hotkey_idx) {
				if (hotkey_idx != MY_HOTKEY_INDEX)
					return;
				App.this.onGlobalHotKey();
			}
		};

		// Add HotkeyListener
		JXGrabKey.getInstance().addHotkeyListener(hotkeyListener);
		JXGrabberInitialized = true;
	}

	private void hotkeysWindows(HotkeyDTO pConf) {
		// Global hot key
		this.provider = Provider.getCurrentProvider(false);

		this.provider.register(
				KeyStroke.getKeyStroke(HotKeysUtils.linuxKey(pConf), HotKeysUtils.linuxMask(pConf)),
				(HotKey hotKey) -> {
			this.onGlobalHotKey();
		});
	}

	public void initHotkeys() {
		this.removeHotkeys();
		HotkeyDTO lConfKeys = this.getHotkeyConf();
		if (SystemUtils.IS_OS_LINUX) {
			this.hotkeysLinux(lConfKeys);
		} else {
			this.hotkeysWindows(lConfKeys);
		}
	}

	private void runServer() {
		ResourceConfig resourceConfig = new ResourceConfig(AssetsResource.class, RestResource.class,
				MyObjectMapperProvider.class, JacksonFeature.class);

		server = HttpServer.createSimpleServer(null,
				9000/* new PortRange(10_000, 60_000) */);
		NetworkListener lListenerGrizzly = server.getListener("grizzly");
		lListenerGrizzly.registerAddOn(new WebSocketAddOn());
		lListenerGrizzly.getTransport().getWorkerThreadPoolConfig()
				.setThreadFactory(new ThreadFactoryBuilder().setNameFormat("grizzly-http-server-%d")
						.setUncaughtExceptionHandler(new JerseyProcessingUncaughtExceptionHandler()).build());
		final WebSocketApplication lApplication = new GLinkWebSocketApplication();
		WebSocketEngine.getEngine().register("/socket", "/test", lApplication);
		HttpHandler lHandler = null;
		try {
			@SuppressWarnings("unchecked")
			Class<? extends HttpHandler> lClassHandler = (Class<? extends HttpHandler>) Class
					.forName("org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer");
			Constructor<? extends HttpHandler> lConstructor = lClassHandler
					.getDeclaredConstructor(javax.ws.rs.core.Application.class);
			lConstructor.setAccessible(true);
			lHandler = lConstructor.newInstance(resourceConfig);
		} catch (Exception e) {
			throw new RuntimeException("Cannot instantiate HTTP server", e);
		}
		ServerConfiguration lConf = server.getServerConfiguration();
		lConf.addHttpHandler(lHandler, HttpHandlerRegistration.builder().contextPath("").build());
		lConf.setPassTraceRequest(true);

		try {
			server.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Collection<NetworkListener> lListeners = server.getListeners();
		for (NetworkListener lListener : lListeners) {
			port = lListener.getPort();
		}
		System.out.println("Port : " + port);
	}
}
