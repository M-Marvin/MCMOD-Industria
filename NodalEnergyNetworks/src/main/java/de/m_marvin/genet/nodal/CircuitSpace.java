package de.m_marvin.genet.nodal;

import de.m_marvin.genet.network.NetworkSpace;
import it.unimi.dsi.fastutil.ints.IntSet;

public class CircuitSpace<N, E> extends NetworkSpace<N, E, Element<N, E>, Circuit<N, E>> {

	@Override
	protected Circuit<N, E> createNetwork(IntSet elements) {
		return new Circuit<N, E>(this, elements);
	}
	
}
