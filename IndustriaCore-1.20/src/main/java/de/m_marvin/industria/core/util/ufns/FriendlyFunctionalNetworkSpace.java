package de.m_marvin.industria.core.util.ufns;

import java.util.function.Supplier;

public abstract class FriendlyFunctionalNetworkSpace<R, C extends FunctionalNetworkSpace.Component<R>, N extends SynchronizedFunctionalNetworkSpace.SynchronizedFunctionalNetwork<N, R, C, A>, A> extends SynchronizedFunctionalNetworkSpace<R, C, N, A> {

	public FriendlyFunctionalNetworkSpace(Supplier<N> networkFactory, int traceLimit) {
		super(networkFactory, traceLimit);
	}
	
	public C findComponentAt(R reference) {
		if (!this.referenceIds.containsKey(reference)) return null;
		return this.components.get(this.referenceIds.getInt(reference));
	}
	
	public N findNetworkAt(R reference) {
		if (!this.referenceIds.containsKey(reference)) return null;
		return this.ref2network.get(this.referenceIds.getInt(reference));
	}
	
	public N findNetworkAt(C component) {
		return findNetworkAt(component.reference());
	}
	
}
