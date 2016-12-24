package fr.chklang.glink.application;

import org.avaje.datasource.DataSourceConfig;

import fr.chklang.glink.application.model.Configuration;
import fr.chklang.glink.application.model.Link;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.config.ServerConfig;

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
		ServerConfig lConfig = new ServerConfig();
		lConfig.setName("main");
		lConfig.addClass(Configuration.class);
		lConfig.addClass(Link.class);
		lConfig.setDatabasePlatformName("h2");
		
		DataSourceConfig lDataSourceConfig = new DataSourceConfig();
		lDataSourceConfig.setDriver("org.h2.Driver");
		lDataSourceConfig.setUrl("jdbc:h2:file:./db;DB_CLOSE_DELAY=-1");
		lDataSourceConfig.setUsername("sa");
		lDataSourceConfig.setPassword("");
		lConfig.setDataSourceConfig(lDataSourceConfig);
		
		this.server = EbeanServerFactory.create(lConfig);
		//this.server = Ebean.getServer("main");
	}

	public EbeanServer getServer() {
		return server;
	}
}

