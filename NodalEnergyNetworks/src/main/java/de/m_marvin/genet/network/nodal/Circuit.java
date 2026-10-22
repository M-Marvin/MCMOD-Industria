package de.m_marvin.genet.network.nodal;

import java.util.Collection;

import de.m_marvin.genet.network.NetworkSpace;
import de.m_marvin.tvnlnna.nodal.NodalNetwork;
import it.unimi.dsi.fastutil.ints.IntCollection;

public class Circuit<N, E> extends NetworkSpace<N, E, Element<N, E>, Circuit<N, E>>.Network {	
	
	private final NodalNetwork<N, E> nodal = new NodalNetwork<>();
	
	public Circuit(CircuitSpace<N, E> networkSpace, IntCollection elements) {
		networkSpace.super(elements);
	}
	
	public NodalNetwork<N, E> getNodal() {
		return nodal;
	}
	
	@Override
	protected void afterElementsAdded(Collection<Element<N, E>> elements) {
		elements.stream()
			.map(Element::getState)
			.forEach(this.nodal::addElement);
	}

	@Override
	protected void afterElementsRemoved(Collection<Element<N, E>> elements) {
		elements.stream()
			.map(Element::getState)
			.forEach(this.nodal::removeElement);
	}
	
	@Override
	protected void clearNetwork() {
		super.clearNetwork();
		this.nodal.reset();
	}
	
}
