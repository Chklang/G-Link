package fr.chklang.glink.application;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.commons.lang3.SystemUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.w3c.dom.Document;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.Provider;

import fr.chklang.glink.application.rest.AssetsResource;
import fr.chklang.glink.application.rest.RestResource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import jxgrabkey.HotkeyConflictException;
import jxgrabkey.HotkeyListener;
import jxgrabkey.JXGrabKey;

/**
 * Main application
 *
 */
public class Main extends Application {
	private Scene scene;
	private int port;
	private HttpServer server;

	private boolean isShown = true;
	private int MY_HOTKEY_INDEX = 1;
	private Stage stage = null;
	private HotkeyListener hotkeyListener;
	private boolean JXGrabberInitialized = false;

	private Provider provider;
	private boolean providerInitialized = false;

	public static void main(String[] args) {

		launch(args);
	}

	private void onGlobalHotKey() {
		Platform.runLater(() -> {
			if (isShown) {
				stage.hide();
			} else {
				stage.show();
			}
			isShown = !isShown;
		});
	}

	private void hotkeysLinux() {
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
			// int key = KeyEvent.VK_K, mask = KeyEvent.CTRL_MASK |
			// KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK;
			int key = KeyEvent.VK_LEFT, mask = KeyEvent.CTRL_MASK; // Conflicts
																	// on GNOME

			JXGrabKey.getInstance().registerAwtHotkey(MY_HOTKEY_INDEX, mask, key);
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
				Main.this.onGlobalHotKey();
			}
		};

		// Add HotkeyListener
		JXGrabKey.getInstance().addHotkeyListener(hotkeyListener);
		JXGrabberInitialized = true;
	}

	private void hotkeysWindows() {
		// Global hot key
		this.provider = Provider.getCurrentProvider(false);

		this.provider.register(KeyStroke.getKeyStroke("control LEFT"), (HotKey hotKey) -> {
			Main.this.onGlobalHotKey();
		});
	}

	private void hotkeys() {
		if (SystemUtils.IS_OS_LINUX) {
			this.hotkeysLinux();
		} else {
			this.hotkeysWindows();
		}
	}

	@Override
	public void start(final Stage stage) throws Exception {
		this.stage = stage;
		Platform.setImplicitExit(false);

		this.hotkeys();

		runServer();
		// create the scene
		stage.setTitle("Web View");
		final WebView browser = new WebView();
		final WebEngine webEngine = browser.getEngine();
		webEngine.load("http://127.0.0.1:" + this.port + "/#/");

		scene = new Scene(browser);
		scene.setFill(null);
		stage.setScene(scene);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				server.shutdownNow();

				if (providerInitialized) {
					Main.this.provider.reset();
					Main.this.provider.stop();
				}

				// Shutdown JXGrabKey
				if (JXGrabberInitialized) {
					JXGrabKey.getInstance().unregisterHotKey(MY_HOTKEY_INDEX); // Optional
					JXGrabKey.getInstance().removeHotkeyListener(hotkeyListener); // Optional
					JXGrabKey.getInstance().cleanUp();
				}

				Platform.exit();
				System.exit(0);
			}
		});

		webEngine.documentProperty()
				.addListener((ObservableValue<? extends Document> observable, Document oldValue, Document newValue) -> {
					try {
						// Use reflection to retrieve the WebEngine's private
						// 'page' field.
						Field f = webEngine.getClass().getDeclaredField("page");
						f.setAccessible(true);
						com.sun.webkit.WebPage page = (com.sun.webkit.WebPage) f.get(webEngine);
						page.setBackgroundColor((new java.awt.Color(0, 0, 0, 0)).getRGB());
					} catch (Exception e) {
					}
				});

	}

	private void runServer() throws IOException {
		ResourceConfig resourceConfig = new ResourceConfig(AssetsResource.class, RestResource.class,
				MyObjectMapperProvider.class, JacksonFeature.class);
		server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://127.0.0.1:0"), resourceConfig, false);
		server.start();
		Collection<NetworkListener> lListeners = server.getListeners();
		for (NetworkListener lListener : lListeners) {
			port = lListener.getPort();
		}
		System.out.println("Port : " + port);
	}
}
