package de.m_marvin.industria.core.util.types;

public class Holder<T> {
	
	public T value = null;
	
	public Holder() {}
	
	public Holder(T value) {
		this.value = value;
	}
	
	public void set(T value) {
		this.value = value;
	}
	
	public T get() {
		return value;
	}
	
}
