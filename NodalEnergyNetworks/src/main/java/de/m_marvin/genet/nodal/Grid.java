package de.m_marvin.genet.nodal;

import de.m_marvin.genet.network.NetworkSpace;
import it.unimi.dsi.fastutil.ints.IntCollection;

public class Grid<N, E> extends NetworkSpace<N, E, Device<N, E>, Grid<N, E>>.Network {
	
	protected final CircuitSpace<N, E> circuits;
	
	public Grid(NetworkSpace<N, E, Device<N, E>, Grid<N, E>> networkSpace, IntCollection elements) {
		networkSpace.super(elements);
		this.circuits = new CircuitSpace<N, E>();
	}
	
	public CircuitSpace<N, E> getCircuits() {
		return circuits;
	}

	@Override
	protected void afterElementsAdded(IntCollection elements) {
		elements.intStream()
			.mapToObj(this::getElementInstance)
			.forEach(device -> device.installToCircuit(this.circuits));;
	}

	@Override
	protected void afterElementsRemoved() {
		this.circuits.reset();
	}
	
}
