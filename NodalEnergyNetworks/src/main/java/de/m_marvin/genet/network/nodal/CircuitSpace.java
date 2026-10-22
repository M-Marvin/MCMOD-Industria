package de.m_marvin.genet.network.nodal;

import de.m_marvin.genet.network.NetworkSpace;
import it.unimi.dsi.fastutil.ints.IntCollection;

public class CircuitSpace<N, E> extends NetworkSpace<N, E, Element<N, E>, Circuit<N, E>> {

	@Override
	protected Circuit<N, E> createNetwork(IntCollection elements) {
		return new Circuit<N, E>(this, elements);
	}
	
}
