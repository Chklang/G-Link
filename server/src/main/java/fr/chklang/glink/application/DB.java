package fr.chklang.glink.application;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;

public class DB {
	private static DB INSTANCE = null;
	
	public static DB getInstance() {
		if (DB.INSTANCE == null) {
			synchronized (DB.class) {
				if (DB.INSTANCE == null) {
					DB.INSTANCE = new DB();
				}
			}
		}
		return DB.INSTANCE;
	}
	
	private final EbeanServer server;
	
	private DB() {
		this.server = Ebean.getDefaultServer();
	}

	public EbeanServer getServer() {
		return server;
	}
}

