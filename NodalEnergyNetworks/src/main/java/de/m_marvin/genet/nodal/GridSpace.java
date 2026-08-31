package de.m_marvin.genet.nodal;

import de.m_marvin.genet.network.NetworkSpace;
import it.unimi.dsi.fastutil.ints.IntSet;

public class GridSpace<N, E> extends NetworkSpace<N, E, Device<N, E>, Grid<N, E>> {

	@Override
	protected Grid<N, E> createNetwork(IntSet elements) {
		return new Grid<N, E>(this, elements);
	}
	
	
	
}
