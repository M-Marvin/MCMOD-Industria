package de.m_marvin.genet.util.ufns;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public abstract class SynchronizedFunctionalNetworkSpace<R, C extends FunctionalNetworkSpace.Component<R>, N extends SynchronizedFunctionalNetworkSpace.SynchronizedFunctionalNetwork<N, R, C, A>, A> extends FunctionalNetworkSpace<R, C, N, A> {
	
	public static enum UpdateType {
		COMPONENT_PUT,
		COMPONENT_REMOVE,
		NETWORK_UPDATE
	}
	
	public static record UpdateTicket<R>(int delay, R reference, UpdateType type, CompletableFuture<Void> future) {} 
	
	protected Queue<UpdateTicket<R>> updateTickets = new ArrayDeque<>();
	
	public SynchronizedFunctionalNetworkSpace(int traceLimit) {
		super(traceLimit);
	}

	public static abstract class SynchronizedFunctionalNetwork<N extends SynchronizedFunctionalNetwork<N, R, C, A>, R, C extends FunctionalNetworkSpace.Component<R>, A> extends FunctionalNetwork<N, R, C, A> {
		
		public abstract void afterChange();
		public abstract void onUpdate();
		
	}
	
	protected abstract C findOrCreateComponentAt(R reference);
	protected abstract Collection<ParametrizedReference<R, A>> findConnectionsForComponent(C component);
	
	@Override
	public void serializeNbt(CompoundTag nbt) {
		super.serializeNbt(nbt);
		
		ListTag updateTicketsNbt = new ListTag();
		for (UpdateTicket<R> ticket : this.updateTickets) {
			CompoundTag ticketNbt = new CompoundTag();
			ticketNbt.putInt("Delay", ticket.delay());
			ticketNbt.put("Reference", serializeReference(ticket.reference()));
			ticketNbt.putString("Type", ticket.type().name().toLowerCase());
			updateTicketsNbt.add(ticketNbt);
		}
		nbt.put("UpdateTickets", updateTicketsNbt);
		
	}
	
	@Override
	public void deserializeNbt(CompoundTag nbt) {
		super.deserializeNbt(nbt);
		
		ListTag updateTicketsNbt = new ListTag();
		this.updateTickets.clear();
		for (int i = 0; i < updateTicketsNbt.size(); i++) {
			CompoundTag ticketNbt = updateTicketsNbt.getCompound(i);
			int delay = ticketNbt.getInt("Delay");
			R reference = deserializeReference(ticketNbt.getCompound("Reference"));
			UpdateType type = UpdateType.valueOf(ticketNbt.getString("Type").toUpperCase());
			this.updateTickets.add(new UpdateTicket<R>(delay, reference, type, null));
		}
		
	}

	protected abstract CompoundTag serializeReference(R reference);
	protected abstract R deserializeReference(CompoundTag tag);
	
	public void scheduledUpdateTicket(R reference, UpdateType type, int delay) {
		this.updateTickets.add(new UpdateTicket<R>(delay, reference, type, null));
	}

	public CompletableFuture<Void> updateTicketCompletable(R reference, UpdateType type) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		this.updateTickets.add(new UpdateTicket<R>(0, reference, type, future));
		return future;
	}

	public void updateTicket(R reference, UpdateType type) {
		this.updateTickets.add(new UpdateTicket<R>(0, reference, type, null));
	}

	// Update buffers
	protected final Map<C, Collection<ParametrizedReference<R, A>>> putComponents = new HashMap<>();
	protected final Collection<R> removeReferences = new HashSet<>();
	protected final Collection<R> updateReferences = new HashSet<>();
	
	public void processUpdates() {
		
		// Search for updates and fill buffers
		Set<UpdateTicket<R>> processingTickets = new HashSet<SynchronizedFunctionalNetworkSpace.UpdateTicket<R>>();
		while (this.updateTickets.size() > 0 && (this.putComponents.size() + this.removeReferences.size() + this.updateReferences.size()) < 100) {
			UpdateTicket<R> ticket = this.updateTickets.poll();
			
			if (ticket.delay() > 0) {
				this.updateTickets.add(new UpdateTicket<R>(ticket.delay() - 1, ticket.reference(), ticket.type(), ticket.future()));
				continue;
			} else {
				processingTickets.add(ticket);
			}
			
			switch (ticket.type()) {
			case COMPONENT_PUT: {
				C component = findOrCreateComponentAt(ticket.reference());
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
		
		Set<N> networks = new HashSet<N>();
		
		// Perform removal operations
		if (!this.removeReferences.isEmpty()) {
			networks.addAll(removeComponents(this.removeReferences));
			this.removeReferences.clear();
		}
		
		// Perform put operations
		if (!this.putComponents.isEmpty()) {
			networks.addAll(putComponents(this.putComponents));
			this.putComponents.clear();
		}

		for (var n : networks) {
			if (n.listComponents().isEmpty()) continue;
			n.afterChange();
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
		
		for (var u : processingTickets)
			if (u.future() != null)
				u.future().complete(null);
		processingTickets.clear();
		
	}
	
}
