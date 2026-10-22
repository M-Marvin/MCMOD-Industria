package de.m_marvin.genet.network.nodal;

import de.m_marvin.genet.network.NetworkSpace;
import it.unimi.dsi.fastutil.ints.IntCollection;

public class GridSpace<N, E> extends NetworkSpace<N, E, Device<N, E>, Grid<N, E>> {

	@Override
	protected Grid<N, E> createNetwork(IntCollection elements) {
		return new Grid<N, E>(this, elements);
	}

	@Override
	public void finalizeQueuedChanges() {
		super.finalizeQueuedChanges();
		this.ide2network.values().stream().distinct().forEach(network -> {
			network.getCircuits().finalizeQueuedChanges();
		});
	}
	
}
