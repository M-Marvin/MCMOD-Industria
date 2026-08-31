package de.m_marvin.genet.nodal;

import de.m_marvin.genet.network.NetworkElement;

public abstract class Device<N, E> implements NetworkElement<N, E> {
	
	protected final E reference;
	
	public Device(E reference) {
		this.reference = reference;
	}
	
	@Override
	public E reference() {
		return this.reference;
	}
	
	public abstract void installToCircuit(CircuitSpace<N, E> circuits);
	
}
