package de.m_marvin.genet.misc;

public interface UnstableFunction<A, B> {
	
	public B apply(A a) throws Exception;
	
}
