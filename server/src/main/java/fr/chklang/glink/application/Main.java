package fr.chklang.glink.application;

import java.io.File;

/**
 * Main application
 *
 */
public class Main {

	public static void main(String[] args) {
		File lFile = new File("/");
		
		for (File lF : File.listRoots()) {
			System.out.println(lF.getAbsolutePath());
		}
		
		App lApp = App.getInstance();
		lApp.start();
	}

}
