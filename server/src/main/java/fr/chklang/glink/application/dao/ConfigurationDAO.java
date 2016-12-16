package fr.chklang.glink.application.dao;

import com.avaje.ebean.Finder;

import fr.chklang.glink.application.model.Configuration;

public class ConfigurationDAO extends Finder<String, Configuration> {

	public ConfigurationDAO() {
		super(Configuration.class);
	}

	public void deleteAll() {
		this.query().delete();
	}
}
