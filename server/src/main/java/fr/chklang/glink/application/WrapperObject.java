package fr.chklang.glink.application;

public class WrapperObject<T> {

	public T object;

	public WrapperObject() {
		super();
		this.object = null;
	}

	public WrapperObject(T object) {
		super();
		this.object = object;
	}
	
	
}
