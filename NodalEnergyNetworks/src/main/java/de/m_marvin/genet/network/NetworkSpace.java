package de.m_marvin.genet.network;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.m_marvin.genet.misc.OutOfIdError;
import de.m_marvin.unimap.UniMap;
import de.m_marvin.unimap.types.BiMap;
import de.m_marvin.unimap.types.MultiBiMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public abstract class NetworkSpace<N, E, Ei extends NetworkElement<N, E>, Ni extends NetworkSpace<N, E, Ei, Ni>.Network> {//<N, E, Ei, Ni>> {
	
	// reference to id mappings
	protected final BiMap<N, Integer> node2idn = UniMap.HASH_FAST.newBiMap();
	protected final BiMap<E, Integer> element2ide = UniMap.HASH_FAST.newBiMap();
	
	protected int lastNodeId = 0;
	protected int lastElementId = 0;
	
	// network space wide element and network mappings
	protected final Int2ObjectMap<Ei> ide2instance = new Int2ObjectOpenHashMap<>();
	protected final BiMap<Integer, Ni> ide2network = UniMap.HASH_FAST.newBiMap();
	
	protected abstract Ni createNetwork(IntSet elements);
	
	protected static int supplyFreeId(int lastId, Predicate<Integer> usedCheck) {
		int free = lastId;
		while (usedCheck.test(free)) {
			free++;
			if (free == lastId)
				throw new OutOfIdError("network out of element ids"); // will this ever happen ? I hope not ...
		}
		return free;
	}
	
	protected int insertElementRef(E element) {
		Integer id = this.element2ide.get(element);
		if (id == null)
			this.element2ide.put(element, (id = supplyFreeId(this.lastElementId, this.element2ide::containsValue)));
		return id;
	}

	protected int insertNodeRef(N node) {
		Integer id = this.node2idn.get(node);
		if (id == null)
			this.node2idn.put(node, (id = supplyFreeId(this.lastElementId, this.element2ide::containsValue)));
		return id;
	}
	
	public Ei putElement(Ei instance) {
		// register new element reference
		int ide = insertElementRef(instance.reference());
		// register element nodes
		int[] idns = Stream.of(instance.nodes()).mapToInt(this::insertNodeRef).toArray();
		
		// remove element with same name before adding the new one
		Ei replaced = null;
		if (this.ide2instance.containsKey(ide))
			replaced = removeElement(this.element2ide.getKey(ide));
		
		// compile list of all networks connected to the nodes of the element
		Collection<Ni> networksToMerge = this.ide2network.values().stream()
				.distinct()
				.filter(network -> network.containsOneOfNodes(idns))
				.toList();
		
		// update mappings for new element and nodes
		this.ide2instance.put(ide, instance);
		
		// merge networks or create new one if no existing networks are touched
		Ni finalNetwork;
		if (networksToMerge.size() >= 1) {
			finalNetwork = networksToMerge.stream().limit(1).findAny().get();
			finalNetwork.merge(networksToMerge.stream().skip(1).toList(), ide);
		} else {
			finalNetwork = createNetwork(IntSet.of(ide));
		}
		
		// return the element that was replaced (or null if no such element exists)
		return replaced;
	}
	
	public Ei removeElement(E element) {
		// remove element from all maps, including its node mappings
		Integer ide = this.element2ide.remove(element);
		if (ide == null)
			return null;
		Ei removed = this.ide2instance.remove((int) ide);
		if (removed == null)
			return null;
		int[] idns = Stream.of(removed.nodes()).mapToInt(this.node2idn::remove).toArray();
		
		// remove network containing the element, and compute potential split networks
		Ni networkToSplit = this.ide2network.remove(ide);
		if (networkToSplit == null)
			return removed;
		
		// split networks if necessary
		Collection<Ni> finalNetworks = networkToSplit.split(idns);
		
		// update element to network mappings
		if (finalNetworks.size() > 1) // NOTE: a set size of 1 indicates the same network was returned (no splitting)
			for (Ni network : finalNetworks)
				network.getElements().forEach(ide2 -> this.ide2network.put(ide2, network));
		else
			// just update the one new element's mapping
			this.ide2network.put(ide, finalNetworks.stream().findAny().get());
		
		return removed;
	}

	public void reset() {
		this.element2ide.clear();
		this.node2idn.clear();
		this.lastElementId = 0;
		this.lastNodeId = 0;
		this.ide2instance.clear();
		this.ide2network.clear();
	}
	
	public abstract class Network {
		
		protected final IntSet elements = new IntOpenHashSet();
		protected final MultiBiMap<Integer, Integer> idn2ide = UniMap.HASH_FAST.newMultiBiMap();
		
		public Network(IntCollection elements) {
			insertElements(elements);;
		}
		
		protected abstract void afterElementsAdded(IntCollection elements);
		protected abstract void afterElementsRemoved();
		
		private void insertElements(IntCollection elements) {
			this.elements.addAll(elements);
			for (int ide : this.elements) {
				var instance = NetworkSpace.this.ide2instance.get(ide);
				Stream.of(instance.nodes()).map(NetworkSpace.this.node2idn::get).forEach(idn -> this.idn2ide.put(idn, ide));;	
			}
			afterElementsAdded(elements);
		}
		
		public boolean containsOneOfNodes(int[] idns) {
			for (int idn : idns)
				if (this.idn2ide.containsKey(idn))
					return true;
			return false;
		}

		public IntSet getElements() {
			return this.elements;
		}
		
		public void merge(Collection<Ni> networks, int element) {
			for (Ni network : networks) {
				this.elements.addAll(network.elements);
				this.idn2ide.putAll(network.idn2ide);
			}
			insertElements(IntList.of(element));
		}
		
		@SuppressWarnings("deprecation")
		public Collection<Ni> split(int[] nodes) {;
			IntSet tracedNodes = new IntOpenHashSet();
			List<IntSet> traces = new ArrayList<>();
			IntSet trace = new IntOpenHashSet();
			
			for (int idnStart : nodes) {
				if (tracedNodes.contains(idnStart))
					continue;
				
				// trace the nodes connected trough elements
				Queue<Integer> idnToTrace = new ArrayDeque<>();
				idnToTrace.add(idnStart);
				Integer idn;
				while ((idn = idnToTrace.poll()) != null) {
					tracedNodes.add((int) idn);
					
					// list all elements connected to the node
					for (int ide :this.idn2ide.getAll(idn)) {
						// add element to the current trace
						if (!trace.add(ide))
							continue; // element already added
						// update node to element map
						this.idn2ide.getKeys(ide).stream()
							.filter(Predicate.not(tracedNodes::contains))
							.forEach(idnToTrace::add);
					}
				}
				
				if (trace.isEmpty())
					continue;
				traces.add(trace);
				trace = new IntOpenHashSet();
			}
			
			// create networks, reuse this one for the first trace
			traces.stream().limit(1).forEach(elements -> {
				this.elements.clear();
				this.idn2ide.clear();
				afterElementsRemoved();
				insertElements(elements);
			});
			return traces.stream().skip(1).map(NetworkSpace.this::createNetwork).toList();
			
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.elements);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof NetworkSpace.Network other)
				return other.elements.equals(this.elements);
			return false;
		}
		
		@Override
		public String toString() {
			return "Network" + this.elements.toString();
		}
		
		/* >>>>>> Utility Methods <<<<<< */
		
		public IntStream getNodes() {
			return this.idn2ide.keySet().stream().mapToInt(Integer::intValue);
		}
		
		public N getNodeName(int idn) {
			return NetworkSpace.this.getNodeName(idn);
		}
		
		public E getElementName(int ide) {
			return NetworkSpace.this.getElementName(ide);
		}
		
		public Ei getElementInstance(int ide) {
			return NetworkSpace.this.getElementInstance(ide);
		}
		
		public Stream<E> getElementNames() {
			return getElements().intStream().mapToObj(this::getElementName);
		}
		
		public Stream<Ei> getElementInstances() {
			return getElements().intStream().mapToObj(this::getElementInstance);
		}
		
		public Stream<N> getNodeNames() {
			return getNodes().mapToObj(this::getNodeName);
		}
		
		public IntStream getElementNodes(int ide) {
			return this.idn2ide.getKeys(ide).stream().mapToInt(Integer::intValue);
		}
		
		public Stream<N> getElementNodeNames(int ide) {
			return getElementNodes(ide).mapToObj(this::getNodeName);
		}
		
		public IntStream getNodeElements(int idn) {
			return this.idn2ide.getAll(idn).stream().mapToInt(Integer::intValue);
		}
		
		public Stream<E> getNodeElementNames(int idn) {
			return getNodeElements(idn).mapToObj(this::getElementName);
		}
		
		public Stream<Ei> getNodeElementInstances(int idn) {
			return getNodeElements(idn).mapToObj(this::getElementInstance);
		}
		
		public int getNodeId(N nodeName) {
			return NetworkSpace.this.getNodeId(nodeName);
		}

		public int getElementId(E elementName) {
			return NetworkSpace.this.getElementId(elementName);
		}

		public N getNodeName(N nodeName) {
			return getNodeName(getNodeId(nodeName));
		}
		
		public E getElementName(E elementName) {
			return getElementName(getElementName(elementName));
		}
		
		public Ei getElementInstance(E elementName) {
			return getElementInstance(getElementName(elementName));
		}
		
		public IntStream getElementNodes(E elementName) {
			return getElementNodes(getElementName(elementName));
		}
		
		public Stream<N> getElementNodeNames(E elementName) {
			return getElementNodeNames(getElementName(elementName));
		}
		
		public IntStream getNodeElements(N nodeName) {
			return getNodeElements(getNodeId(nodeName));
		}
		
		public Stream<E> getNodeElementNames(N nodeName) {
			return getNodeElementNames(getNodeId(nodeName));
		}
		
		public Stream<Ei> getNodeElementInstances(N nodeName) {
			return getNodeElementInstances(getNodeId(nodeName));
		}
		
		
	}

	public N getNodeName(int idn) {
		return this.node2idn.getKey(idn);
	}
	
	public E getElementName(int ide) {
		return this.element2ide.getKey(ide);
	}

	public int getNodeId(N nodeName) {
		Integer idn = NetworkSpace.this.node2idn.get(nodeName);
		if (idn == null)
			throw new IllegalArgumentException("node is not defined: " + nodeName);
		return idn.intValue();
	}

	public int getElementId(E elementName) {
		Integer ide = NetworkSpace.this.element2ide.get(elementName);
		if (ide == null)
			throw new IllegalArgumentException("element is not defined: " + elementName);
		return ide.intValue();
	}
	
	public Ei getElementInstance(int ide) {
		return this.ide2instance.get(ide);
	}
	
	public Ni getElementNetwork(int ide) {
		return this.ide2network.get(ide);
	}
	
	public Ei getElementInstance(E elementName) {
		return getElementInstance(getElementId(elementName));
	}
	
	public Ni getElementNetwork(E elementName) {
		return getElementNetwork(getElementId(elementName));
	}
	
}
