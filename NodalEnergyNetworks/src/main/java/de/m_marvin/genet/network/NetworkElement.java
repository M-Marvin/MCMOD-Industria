package de.m_marvin.genet.network;

public interface NetworkElement<N, E> {
	
	public E reference();
	public N[] nodes();
	
}
