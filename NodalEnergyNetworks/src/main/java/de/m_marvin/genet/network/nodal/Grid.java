package de.m_marvin.genet.network.nodal;

import java.util.Collection;

import de.m_marvin.genet.network.NetworkSpace;
import it.unimi.dsi.fastutil.ints.IntCollection;

public class Grid<N, E> extends NetworkSpace<N, E, Device<N, E>, Grid<N, E>>.Network {
	
	protected final CircuitSpace<N, E> circuits = new CircuitSpace<N, E>();
	
	public Grid(NetworkSpace<N, E, Device<N, E>, Grid<N, E>> networkSpace, IntCollection elements) {
		networkSpace.super();
		insertElements(elements);
	}
	
	public CircuitSpace<N, E> getCircuits() {
		return circuits;
	}

	@Override
	protected void afterElementsAdded(Collection<Device<N, E>> elements) {}

	@Override
	protected void afterElementsRemoved(Collection<Device<N, E>> elements) {}
	
	@Override
	protected void clearNetwork() {
		super.clearNetwork();
		this.circuits.reset();
	}
	
}
