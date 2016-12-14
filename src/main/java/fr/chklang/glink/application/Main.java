package fr.chklang.glink.application;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import fr.chklang.glink.application.rest.AssetsResource;
import fr.chklang.glink.application.rest.RestResource;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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
		scene = new Scene(new Html5(port), 1024, 768, Color.web("#666970"));
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				server.shutdownNow();
				Platform.exit();
				System.exit(0);
			}
		});
		
	}

	private void runServer() throws IOException {
		ResourceConfig resourceConfig = new ResourceConfig(AssetsResource.class, RestResource.class, MyObjectMapperProvider.class, JacksonFeature.class);
		server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://127.0.0.1:0"), resourceConfig, false);
		server.start();
		Collection<NetworkListener> lListeners = server.getListeners();
		for (NetworkListener lListener : lListeners) {
			port = lListener.getPort();
		}
		System.out.println("Port : " + port);
	}
}
