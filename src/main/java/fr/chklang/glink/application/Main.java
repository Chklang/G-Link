package fr.chklang.glink.application;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Collection;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.w3c.dom.Document;

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

/**
 * Main application
 *
 */
public class Main extends Application {
	private Scene scene;
	private int port;
	private HttpServer server;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
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
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				server.shutdownNow();
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
