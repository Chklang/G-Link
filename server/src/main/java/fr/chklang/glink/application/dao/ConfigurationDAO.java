package fr.chklang.glink.application.dao;

import fr.chklang.glink.application.model.Configuration;
import io.ebean.Finder;

public class ConfigurationDAO extends Finder<String, Configuration> {

	public ConfigurationDAO() {
		super(Configuration.class, "main");
	}

	public void deleteAll() {
		this.query().delete();
	}
}
