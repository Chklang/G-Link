package fr.chklang.glink.application.model;

import javax.persistence.MappedSuperclass;

import io.ebean.Model;

@MappedSuperclass
public abstract class AbstractModel extends Model {

	@Override
	public void save() {
		db("main").save(this);
	}
	
	@Override
	public void update() {
		this.update("main");
	}
}
