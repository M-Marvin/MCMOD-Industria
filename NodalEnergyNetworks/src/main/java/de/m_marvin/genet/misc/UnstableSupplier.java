package de.m_marvin.genet.misc;

@FunctionalInterface
public interface UnstableSupplier<T> {
	
	public T get() throws Exception;
	
}
