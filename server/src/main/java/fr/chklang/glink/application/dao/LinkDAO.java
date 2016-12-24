package fr.chklang.glink.application.dao;

import fr.chklang.glink.application.model.Link;
import io.ebean.Finder;

public class LinkDAO extends Finder<Integer, Link> {

	public LinkDAO() {
		super(Link.class);
	}

	public void deleteAll() {
		this.query().delete();
	}
	
	public Link getByName(String pName) {
		return this.query().where().eq("name", pName).findUnique();
	}
	
	public boolean deleteByName(String pName) {
		return this.query().where().eq("name", pName).delete() > 0;
	}
}
