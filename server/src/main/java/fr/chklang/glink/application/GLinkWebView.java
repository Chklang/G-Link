package fr.chklang.glink.application;

import java.lang.reflect.Field;

import org.w3c.dom.Document;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class GLinkWebView extends Application {

	private Scene scene;
	private Stage stage = null;
	
	public GLinkWebView() {
		super();
	}
	
	public void show() {
		this.stage.show();
	}
	
	public void hide() {
		this.stage.hide();
	}

	@Override
	public void start(final Stage stage) throws Exception {
		App.getInstance().setWebview(this);

		this.stage = stage;
		Platform.setImplicitExit(false);
		// create the scene
		stage.setTitle("Web View");
		final WebView browser = new WebView();
		final WebEngine webEngine = browser.getEngine();
		webEngine.load("http://127.0.0.1:" + App.getInstance().getPort() + "/#/");

		scene = new Scene(browser);
		scene.setFill(null);
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		stage.setFullScreen(true);
		stage.setScene(scene);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				App.getInstance().shutdown();
			}
		});

		webEngine.documentProperty().addListener(
				(ObservableValue<? extends Document> observable, Document oldValue, Document newValue) -> {
					try {
						// Use reflection to retrieve the WebEngine's
						// private
						// 'page' field.
						Field f = webEngine.getClass().getDeclaredField("page");
						f.setAccessible(true);
						com.sun.webkit.WebPage page = (com.sun.webkit.WebPage) f.get(webEngine);
						page.setBackgroundColor((new java.awt.Color(0, 0, 0, 0)).getRGB());
					} catch (Exception e) {
					}
				});

	}
}