package fr.chklang.glink.application;

import java.io.IOException;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class Html5 extends Region {

	final WebView browser = new WebView();
	final WebEngine webEngine = browser.getEngine();
	private int port;

	public Html5(int pPort) throws IOException, InterruptedException {
		this.port = pPort;
		// apply the styles
		getStyleClass().add("browser");
		// load webengine
		webEngine.load("http://127.0.0.1:" + this.port + "/#/");
		// add the web view to the scene
		getChildren().add(browser);
	}

	@Override
	protected void layoutChildren() {
		double w = getWidth();
		double h = getHeight();
		layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
	}

	@Override
	protected double computePrefWidth(double height) {
		return 750;
	}

	@Override
	protected double computePrefHeight(double width) {
		return 500;
	}
}