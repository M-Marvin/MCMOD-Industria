package de.m_marvin.industria.core.util.ufns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Supplier;

import com.google.common.collect.Queues;

import de.m_marvin.industria.IndustriaCore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class FunctionalNetworkSpace<R, C extends FunctionalNetworkSpace.Component<R>, N extends FunctionalNetworkSpace.FunctionalNetwork<N, R, C, A>, A> {

	public static abstract class Component<R> {
		
		protected final IntSet referencedComponents = new IntOpenHashSet();
		protected final R reference;
		
		public Component(R reference) {
			this.reference = reference;
		}
		
		public R reference() {
			return reference;
		}

		@Override
		public int hashCode() {
			return this.reference.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj instanceof Component other) {
				return other.reference.equals(this.reference);
			}
			return false;
		}
		
	}
	
	public static abstract class FunctionalNetwork<N extends FunctionalNetwork<N, R, C, A>, R, C extends FunctionalNetworkSpace.Component<R>, A> {
		
		protected final Int2ObjectMap<C> components = new Int2ObjectOpenHashMap<>();
		
		protected static <N extends FunctionalNetwork<N, ?, ?, ?>> N combineNetwork(N network1, N network2) {
			network1.integrateNetwork(network2.components.keySet(), network2);
			return network1;
		}
		
		protected void integrateNetwork(IntSet references, N other) {
			references.forEach(ref -> {
				C component = other.components.get(ref);
				if (component != null)
					this.components.put(ref, component);
			});
			afterIntegrateNetwork(references, other);
		}
		
		public Collection<C> listComponents() {
			return this.components.values();
		}
		
		@Override
		public int hashCode() {
			return components.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj instanceof FunctionalNetwork other) {
				return other.components.equals(this);
			}
			return false;
		}
		
		protected abstract void afterPutComponent(int refId);
		protected abstract void afterRemoveComponent(int refId);
		protected abstract boolean afterParametrizedConnection(int refId1, int refId2, A parameter);
		protected abstract void afterIntegrateNetwork(IntSet refIds, N other);
		
	}

	public static record ParametrizedReference<R, A>(R reference, A paramter) {}
	
	protected final Supplier<N> networkFactory;
	protected final int traceLimit;
	
	protected final Object2IntMap<R> referenceIds = new Object2IntOpenHashMap<>();
	protected final Int2ObjectMap<C> components = new Int2ObjectOpenHashMap<>();
	protected final Int2ObjectMap<N> ref2network = new Int2ObjectOpenHashMap<>();
	
	private static final long MAX_IDS = 0xFFFFFFFFL;
	
	public FunctionalNetworkSpace(Supplier<N> networkFactory, int traceLimit) {
		this.networkFactory = networkFactory;
		this.traceLimit = traceLimit;
	}
	
	public Collection<N> listNetworks() {
		return this.ref2network.values().stream().distinct().toList();
	}
	
	public Collection<C> listComponents() {
		return this.components.values();
	}
	
	public Collection<C> listComponentsOutsideNetworks() {
		return this.components.values().stream().filter(c -> !this.ref2network.containsKey(this.referenceIds.getInt(c.reference))).toList();
	}
	
	public Collection<R> listReferences() {
		return this.referenceIds.keySet();
	}
	
	private int newComponentId(R reference) {
		int id = reference.hashCode();
		if (components.size() == MAX_IDS)
			throw new RuntimeException("ID overflow");
		while (components.containsKey(id))
			id++;
		return id;
	}
	
	public Collection<N> putComponents(Map<C, Collection<ParametrizedReference<R, A>>> components) {
		
		// Create and register new reference ids
		Int2ObjectMap<C> newComponents = new Int2ObjectOpenHashMap<>();
		for (C component : components.keySet()) {
			if (this.referenceIds.containsKey(component.reference)) {
				newComponents.put(this.referenceIds.getInt(component.reference), component);
			} else {
				int refId = newComponentId(component.reference);
				this.referenceIds.put(component.reference, refId);
				newComponents.put(refId, component);
			}
		}
		
		// Register components in network space, collect replaced components and remove them from other components references
		List<C> replacedComponents = newComponents.int2ObjectEntrySet().stream().map(e -> this.components.put(e.getIntKey(), e.getValue())).filter(Objects::nonNull).toList();
		
		for (var entry : newComponents.int2ObjectEntrySet()) {
			// Register references in new component
			components.get(entry.getValue()).forEach(ref -> {
				if (this.referenceIds.containsKey(ref.reference()))
					entry.getValue().referencedComponents.add(this.referenceIds.getInt(ref.reference()));
			});

			// Register references in other components
			entry.getValue().referencedComponents.intStream().mapToObj(this.components::get).forEach(component2 -> {
				component2.referencedComponents.add(entry.getIntKey());
			});
		}
		
		// Check for referenced networks and combine them or create new network if no component had a network registered
		N combinedNetwork = components.keySet().stream().flatMapToInt(c -> c.referencedComponents.intStream())
				.mapToObj(this.ref2network::get)
				.filter(Objects::nonNull)
				.distinct()
				.reduce(FunctionalNetwork::combineNetwork)
				.orElseGet(this.networkFactory);
		
		// Add new components to network
		combinedNetwork.components.putAll(newComponents);
		
		// Update reference to network mappings for new components
		combinedNetwork.components.forEach((netRefId, netComponent) -> {
			this.ref2network.put((int) netRefId, combinedNetwork);
		});
		
		// Trigger put component event
		newComponents.keySet().forEach(combinedNetwork::afterPutComponent);
		
		// Trigger new connection event
		int toAdd = (int) components.values().stream().flatMap(Collection::stream).distinct().count();
		if (toAdd > 0) {
			List<ParametrizedReference<R, A>> addedRefs = new ArrayList<>();
			int attempts = toAdd + 1;
			while (toAdd > addedRefs.size() && attempts > 0) {
				for (var entry : components.entrySet()) {
					for (var pref : entry.getValue())
						if (!addedRefs.contains(pref))
							if (combinedNetwork.afterParametrizedConnection(this.referenceIds.getInt(entry.getKey().reference), this.referenceIds.getInt(pref.reference()), pref.paramter()))
								addedRefs.add(pref);
				}
				attempts--;
			}
			if (attempts == 0) {
				IndustriaCore.LOGGER.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				IndustriaCore.LOGGER.warn("Unable to insert all parametrized references, reached itterator limit!");
				IndustriaCore.LOGGER.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}
		}

		// Get a list of all new references and all potentially disconnected references
		IntSet referencesToTrace = new IntOpenHashSet();
		referencesToTrace.addAll(newComponents.keySet());
		replacedComponents.stream().flatMapToInt(c -> c.referencedComponents.intStream()).distinct().forEach(referencesToTrace::add);
		
		// Check and split network if required
		return traceAndSplit(referencesToTrace);
		
	}
	
	public Collection<N> removeComponents(Collection<R> references) {
		Objects.requireNonNull(references);
		
		// Remove components and collect their references
		IntSet refIds = new IntOpenHashSet();
		for (R reference : references) {
			if (!this.referenceIds.containsKey(reference)) continue;
			int refId = this.referenceIds.removeInt(reference);
			C component = this.components.remove(refId);
			N network = this.ref2network.remove(refId);
			if (network != null) {
				network.components.remove(refId);
				network.afterRemoveComponent(refId);
			}
			if (component != null)
				refIds.addAll(component.referencedComponents);
		}
		
		// Trace the references and split the networks if required
		return traceAndSplit(refIds);
	}
	
	protected Collection<N> traceAndSplit(IntSet refIds) {
		Objects.requireNonNull(refIds);
		if (refIds.isEmpty()) return Collections.emptyList();
		
		Map<IntSet, N> traces = new HashMap<>();
		Queue<Integer> traceQueue = Queues.newArrayDeque();
		
		for (int refId : refIds) {
			
			// Check if already traced
			for (IntSet trace : traces.keySet())
				if (trace.contains(refId)) continue;
			
			// Get network for reference, skip if no network available
			N network = this.ref2network.get(refId);
			if (network == null) continue;
			
			// Trace connections for reference in network
			IntSet trace = new IntOpenHashSet();
			traceQueue.add(refId);
			while (!traceQueue.isEmpty() && trace.size() < this.traceLimit) {
				int ref = traceQueue.poll();
				if (trace.contains(ref)) continue;
				C comp = network.components.get(ref);
				
				// Check network space should a component be missing in network, add it to the current network
				if (comp == null) {
					comp = this.components.get(ref);
					if (comp != null) {
						network.components.put(ref, comp);
						network.afterPutComponent(ref);
					}
				}
				
				if (comp != null) {
					trace.add(ref);
					traceQueue.addAll(comp.referencedComponents);
				}
			}
			
			// Add to list of traces
			if (!trace.isEmpty()) traces.put(trace, network);
			
		}
		
		// Cleanup references, remove invalid references within the trace's components
		for (IntSet trace : traces.keySet()) {
			trace.intStream().mapToObj(this.components).forEach(component -> {
				for (var itr = component.referencedComponents.iterator(); itr.hasNext();)
					if (!trace.contains(itr.nextInt())) itr.remove();
			});
		}
		
		// When there is only one trace, reuse existing network, otherwise make new networks
		if (traces.size() == 1) {
			IntSet trace = traces.keySet().stream().findAny().get();
			N network = traces.get(trace);
			
			// Remove remaining untraced references, these seem to be invalid
			network.components.keySet().intStream().filter(refId -> !trace.contains(refId)).forEach(refId -> {
				N oldNetwork = this.ref2network.remove(refId);
				if (oldNetwork != null) oldNetwork.components.remove(refId);
			});
			
			return Collections.singleton(network);
		} else {
			
			// Assemble new networks for traces, and register them
			return traces.entrySet().stream().map(traceEntry -> {
				N network2 = this.networkFactory.get();
				network2.integrateNetwork(traceEntry.getKey(), traceEntry.getValue());
				
				traceEntry.getKey().forEach(refId -> {
					N oldNetwork = this.ref2network.put(refId, network2);
					if (oldNetwork != null) oldNetwork.components.remove(refId);
				});
				return network2;
			}).toList();
			
		}
		
	}
	
}
