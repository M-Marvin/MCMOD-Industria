package de.m_marvin.industria.core.util.ufns;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Supplier;

public abstract class SynchronizedFunctionalNetworkSpace<R, C extends FunctionalNetworkSpace.Component<R>, N extends SynchronizedFunctionalNetworkSpace.SynchronizedFunctionalNetwork<N, R, C, A>, A> extends FunctionalNetworkSpace<R, C, N, A> {
	
	public static enum UpdateType {
		COMPONENT_PUT,
		COMPONENT_REMOVE,
		NETWORK_UPDATE
	}
	
	public static record UpdateTicket<R>(int delay, R reference, UpdateType type) {} 
	
	protected Queue<UpdateTicket<R>> updateTickets = new ArrayDeque<>();
	
	public SynchronizedFunctionalNetworkSpace(Supplier<N> networkFactory, int traceLimit) {
		super(networkFactory, traceLimit);
		
	}
	
	public static abstract class SynchronizedFunctionalNetwork<N extends SynchronizedFunctionalNetwork<N, R, C, A>, R, C extends FunctionalNetworkSpace.Component<R>, A> extends FunctionalNetwork<N, R, C, A> {
		
		public abstract void afterChange();
		public abstract void onUpdate();
		
	}
	
	public abstract C findComponentAt(R reference);
	public abstract Collection<ParametrizedReference<R, A>> findConnectionsForComponent(C component);
	
	// Update buffers
	protected final Map<C, Collection<ParametrizedReference<R, A>>> putComponents = new HashMap<>();
	protected final Collection<R> removeReferences = new HashSet<>();
	protected final Collection<R> updateReferences = new HashSet<>();
	
	public void scheduledUpdateTicket(R reference, UpdateType type, int delay) {
		this.updateTickets.add(new UpdateTicket<R>(delay, reference, type));
	}
	
	public void updateTicket(R reference, UpdateType type) {
		this.updateTickets.add(new UpdateTicket<R>(0, reference, type));
	}
	
	public void processUpdates() {
		
		// Search for updates and fill buffers
		while (this.updateTickets.size() > 0) {
			UpdateTicket<R> ticket = this.updateTickets.poll();
			
			if (ticket.delay() > 0) {
				this.updateTickets.add(new UpdateTicket<>(ticket.delay() - 1, ticket.reference(), ticket.type()));
				continue;
			}
			
			switch (ticket.type()) {
			case COMPONENT_PUT: {
				C component = findComponentAt(ticket.reference());
				if (component == null) continue;
				if (this.putComponents.containsKey(component)) continue;
				this.putComponents.put(component, findConnectionsForComponent(component));
				continue;
			}
			case COMPONENT_REMOVE: {
				this.removeReferences.add(ticket.reference());
				continue;
			}
			case NETWORK_UPDATE: {
				this.updateReferences.add(ticket.reference());
				continue;
			}
			}
		}
		
		// Perform removal operations
		if (!this.removeReferences.isEmpty()) {
			removeComponents(this.removeReferences).forEach(SynchronizedFunctionalNetwork::afterChange);
			this.removeReferences.clear();
		}
		
		// Perform put operations
		if (!this.putComponents.isEmpty()) {
			putComponents(this.putComponents).forEach(SynchronizedFunctionalNetwork::afterChange);
			this.putComponents.clear();
		}
		
		// Perform network updates
		if (!this.updateReferences.isEmpty()) {
			this.updateReferences.stream()
				.filter(this.referenceIds::containsKey)
				.mapToInt(this.referenceIds::getInt)
				.mapToObj(this.ref2network::get)
				.distinct()
				.filter(Objects::nonNull)
				.forEach(SynchronizedFunctionalNetwork::onUpdate);
			this.updateReferences.clear();
		}
		
	}
	
}
