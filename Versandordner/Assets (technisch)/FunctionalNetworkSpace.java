package de.m_marvin.industria.core.util.systemnetworks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.collect.Queues;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class FunctionalNetworkSpace<R, C extends FunctionalNetworkSpace.Component<R>, N extends FunctionalNetworkSpace.FunctionalNetwork<R, C, A>, A> {

	public static abstract class Component<R> {
		
		protected final IntSet referencedComponents = new IntOpenHashSet();
		protected final R reference;
		
		public Component(R reference) {
			this.reference = reference;
		}
		
	}
	
	public static class FunctionalNetwork<R, C extends FunctionalNetworkSpace.Component<R>, A> {
		
		protected final Int2ObjectMap<C> components = new Int2ObjectOpenHashMap<>();
		
		public static <R, C extends FunctionalNetworkSpace.Component<R>, N extends FunctionalNetwork<R, C, A>, A> N combineNetwork(N network1, N network2) {
			network1.integrateNetwork(network2);
			return network1;
		}
		
		public void integrateNetwork(FunctionalNetwork<R, C, A> other) {
			this.components.putAll(other.components);
		}
		
		public void parametrizedConnection(int refId1, int refId2, A parameter) {}
		
	}

	public static record ParametrizedReference<R, A>(R reference, A paramter) {}
	
	protected final Supplier<N> networkFactory;
	protected final int traceLimit;
	
	protected final Object2IntMap<R> referenceIds = new Object2IntOpenHashMap<>();
	protected final Int2ObjectMap<C> components = new Int2ObjectOpenHashMap<>();
	protected final Int2ObjectMap<N> ref2network = new Int2ObjectOpenHashMap<>();
	
	private static final long MAX_IDS = 0xFFFFFFFFL;
	
	public FunctionalNetworkSpace() {
		// TODO Auto-generated constructor stub
	}
	
	private int newComponentId(R reference) {
		int id = reference.hashCode();
		if (components.size() == MAX_IDS)
			throw new RuntimeException("ID overflow");
		while (components.containsKey(id))
			id++;
		return id;
	}
	
//	public Collection<N> bulkPutComponents(Collection<C> components) {
//		
//		// Remove components currently placed at the components references
//		// This is essential to speed up the following put operations by ensuring no re-trace operations need to run for each individual component
//		removeComponents(components.stream().map(c -> c.reference).toList());
//		
//		
//		
//	}
	
	public Collection<N> putComponent(C component, @SuppressWarnings("unchecked") ParametrizedReference<R, A>... references) {
		Objects.requireNonNull(component);
		for (ParametrizedReference<R, A> parametrizedReference : references) {
			if (!this.referenceIds.containsKey(parametrizedReference.reference))
				return Collections.emptySet(); // Can't add component with invalid references
		}
		
		// Create and register new reference id
		int refId;
		if (this.referenceIds.containsKey(component.reference)) {
			refId = this.referenceIds.getInt(component.reference);
		} else {
			refId = newComponentId(component.reference);
			this.referenceIds.put(component.reference, refId);
		}
		
		// Put component in map
		C replacedComponent = this.components.put(refId, component);
		
		// Register references in new component
		Stream.of(references)
			.map(ParametrizedReference::reference)
			.mapToInt(this.referenceIds::getInt)
			.forEach(component.referencedComponents::add);;
		
		// Register references in other components
		component.referencedComponents.intStream().mapToObj(this.components::get).forEach(component2 -> {
			component.referencedComponents.add(refId);
		});
		
		// Check for networks and combine or create new network if no component had a network registered
		N combinedNetwork = component.referencedComponents.intStream()
				.mapToObj(this.ref2network::get)
				.filter(Objects::nonNull)
				.distinct()
				.reduce(FunctionalNetwork::combineNetwork)
				.orElseGet(this.networkFactory);
		
		// Add new component to network
		combinedNetwork.components.put(refId, component);
		
		// Update reference to network mappings
		combinedNetwork.components.forEach((netRefId, netComponent) -> {
			this.ref2network.put((int) netRefId, combinedNetwork);
		});
		
		
		
		if (replacedComponent != null) {
			
			// Get all references which are removed with the new component
			IntSet unreferencedIds = new IntOpenHashSet();
			replacedComponent.referencedComponents.intStream()
					.filter(i -> !component.referencedComponents.contains(i))
					.forEach(unreferencedIds::add);;
			
			// If no unreferenced id's exist, skip checking and return network
			if (unreferencedIds.isEmpty()) return Collections.singleton(combinedNetwork);
			
			// Check and split networks if required
			return traceAndSplit(unreferencedIds);
			
		} else {
			
			
			
		}
		
		return Collections.singleton(combinedNetwork);
	}
	
	public Collection<N> removeComponents(Collection<R> references) {
		Objects.requireNonNull(references);
		
		// Remove components and collect their references
		IntSet refIds = new IntOpenHashSet();
		for (R reference : references) {
			if (!this.referenceIds.containsKey(references)) continue;
			int refId = this.referenceIds.removeInt(reference);
			C component = this.components.remove(refId);
			N network = this.ref2network.remove(refId);
			network.components.remove(refId);
			if (component != null)
				refIds.addAll(component.referencedComponents);
		}
		
		// Trace the references and split the networks if required
		return traceAndSplit(refIds);
	}
	
	protected Collection<N> traceAndSplit(IntSet refIds) {
		Objects.requireNonNull(refIds);
		if (refIds.isEmpty()) return Collections.emptyList();
		
		List<IntSet> traces = new ArrayList<IntSet>();
		Queue<Integer> traceQueue = Queues.newArrayDeque();
		
		for (int refId : refIds) {
			
			// Check if already traced
			for (IntSet trace : traces)
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
				if (comp != null) {
					trace.add(ref);
					traceQueue.addAll(comp.referencedComponents);
				}
			}
			
			// Add to list of traces
			if (!traces.isEmpty()) traces.add(trace);
			
		}
		
		// Cleanup references, remove invalid references within the trace's components
		for (IntSet trace : traces) {
			trace.intStream().mapToObj(this.components).forEach(component -> {
				for (var itr = component.referencedComponents.iterator(); itr.hasNext();)
					if (!trace.contains(itr.nextInt())) itr.remove();
			});
		}
		
		// When there is only one trace, reuse existing network, otherwise make new networks
		if (traces.size() == 1) {
			IntSet trace = traces.get(0);
			N network = this.ref2network.get(trace.intStream().findAny().getAsInt());
			
			// Remove remaining untraced references, these seem to be invalid
			network.components.keySet().intStream().filter(refId -> !trace.contains(refId)).forEach(refId -> {
				N oldNetwork = this.ref2network.remove(refId);
				if (oldNetwork != null) oldNetwork.components.remove(refId);
			});
			
			return Collections.singleton(network);
		} else {
			
			// Assemble new networks for traces, and register them
			return traces.stream().map(trace -> {
				N network2 = this.networkFactory.get();
				network2.components.putAll(trace.intStream()
						.collect(Int2ObjectOpenHashMap::new, (map, refId) -> map.put(refId, this.components.get(refId)), Int2ObjectMap::putAll));
				trace.forEach(refId -> {
					N oldNetwork = this.ref2network.put(refId, network2);
					if (oldNetwork != null) oldNetwork.components.remove(refId);
				});
				return network2;
			}).toList();
			
		}
		
	}
	
}
