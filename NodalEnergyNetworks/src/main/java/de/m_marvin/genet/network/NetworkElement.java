package de.m_marvin.genet.network;

public interface NetworkElement<N, E, Ni extends NetworkSpace<N, E, ?, Ni>.Network> {
	
	public E name();
	public N[] nodes();
	
	public void onRemovalFromNetwork();
	public void onInsertionToNetwork(Ni network);
	
}
