package de.m_marvin.genet.network;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import de.m_marvin.genet.NodeNet;
import de.m_marvin.genet.misc.OutOfIdError;
import de.m_marvin.stdextras.Pair;
import de.m_marvin.unimap.UniMap;
import de.m_marvin.unimap.types.BiMap;
import de.m_marvin.unimap.types.MultiBiMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public abstract class NetworkSpace<N, E, Ei extends NetworkElement<N, E, Ni>, Ni extends NetworkSpace<N, E, Ei, Ni>.Network> implements NetworkAccessor<N, E, Ei, Ni> {
	
	// reference to id mappings
	protected final BiMap<N, Integer> node2idn = UniMap.HASH_FAST.newBiMap();
	protected final BiMap<E, Integer> element2ide = UniMap.HASH_FAST.newBiMap();
	
	protected int lastNodeId = 0;
	protected int lastElementId = 0;
	
	// network space wide element and network mappings
	protected final MultiBiMap<Integer, Integer> idn2ide = UniMap.HASH_FAST.newMultiBiMap();
	protected final Int2ObjectMap<Ei> ide2instance = new Int2ObjectOpenHashMap<>();
	protected final BiMap<Integer, Ni> ide2network = UniMap.HASH_FAST.newBiMap();
	
	protected abstract Ni createNetwork(IntCollection elements);
	
	protected final static int supplyFreeId(int lastId, Predicate<Integer> usedCheck) {
		int free = lastId;
		while (usedCheck.test(free)) {
			free++;
			if (free == lastId)
				throw new OutOfIdError("network out of element ids"); // will this ever happen ? I hope not ...
		}
		return free;
	}
	
	protected final int insertElementRef(E element) {
		Integer id = this.element2ide.get(element);
		if (id == null)
			this.element2ide.put(element, (id = supplyFreeId(this.lastElementId, this.element2ide::containsValue)));
		return id;
	}

	protected final int insertNodeRef(N node) {
		Integer id = this.node2idn.get(node);
		if (id == null)
			this.node2idn.put(node, (id = supplyFreeId(this.lastNodeId, this.node2idn::containsValue)));
		return id;
	}
	
	public final Collection<Ei> directRemoveElements(Collection<E> names) {

		// remove elements from the network space to free up id space and collect idn and ide values
		Set<Ei> removed = new HashSet<Ei>();
		List<Pair<Collection<Integer>, Integer>> idnsAndIde = names.stream()
			.map(this.element2ide::remove)
			.filter(Objects::nonNull)
			.peek(ide -> removed.add(this.ide2instance.remove(ide.intValue())))
			.map(ide -> new Pair<>(this.idn2ide.removeValues(ide), ide))
			.toList();
		
		// remove element to network mappings and collect networks to split
		Collection<Ni> networksToSplit = idnsAndIde.stream()
			.map(pair -> this.ide2network.remove(pair.second()))
			.filter(Objects::nonNull)
			.distinct()
			.toList();
		
		// collect list of nodes to try splitting
		IntList nodesToSplit = IntArrayList.toList(idnsAndIde.stream()
				.flatMapToInt(pair -> pair.first().stream().mapToInt(Integer::intValue))
				.distinct());
		
		// remove unused idn values to free up id space
		nodesToSplit.intStream()
			.filter(idn -> this.idn2ide.getAll(idn).isEmpty())
			.forEach(this.node2idn::removeValue);
		
		// call the network remove event on the removed devices, since they will not be called by the network
		removed.forEach(NetworkElement::onRemovalFromNetwork);
		
		// split networks and update ide to network mappings
		networksToSplit.stream()
			.flatMap(network -> network.split(nodesToSplit).stream())
			.forEach(network -> network.elements.forEach(ide -> this.ide2network.put(ide, network)));
		
		return removed;
		
	}
	
	public final void directInsertElements(Collection<Ei> elements) {
		
		// register elements in the network space and collect ide values
		List<Integer> idns = elements.stream()
			.flatMap(instance -> {
				
				// register element reference in network space
				int ide = insertElementRef(instance.name());

				// register element nodes in network space
				Stream<Integer> idns2 = Stream.of(instance.nodes())
						.map(this::insertNodeRef)
						.peek(idn -> this.idn2ide.put(idn, ide));
				
				// update reference to instance mapping in network space
				if (this.ide2instance.put(ide, instance) != null)
					throw new IllegalStateException("illegal element insertion, conflicting elements: " + instance.name() + " as ide:" + ide);
				// NOTE: this code assumes a previous conflicting element to be already removed by the synchronizer code
				
				return idns2;
				
			}).distinct().toList();
		
		// trace connected elements and merge networks
		for (IntSet traceIdes : Tracer.trace(this.idn2ide, idns)) {
			
			// find networks contained within the trace, and separate elements without any network
			Set<Ni> networksToMerge = new HashSet<Ni>();
			IntList netlessIdes = IntArrayList.toList(traceIdes.intStream()
				.filter(ide -> {
					Ni network = this.ide2network.get(ide);
					if (network != null) {
						networksToMerge.add(network);
						return false;
					}
					return true;
				}));
			
			// merge networks and add new elements
			Optional<Ni> network = networksToMerge.stream().max((n1, n2) -> Integer.compare(n1.elements.size(), n2.elements.size()));
			if (network.isPresent())
				network.get().merge(networksToMerge.stream().filter(n -> n != network.get()).toList(), netlessIdes);
			Ni mergedNetwork = network.orElseGet(() -> createNetwork(netlessIdes));
			
			// update element network mappings
			mergedNetwork.elements.forEach(ide -> this.ide2network.put(ide, mergedNetwork));
			
		}
		
	}
	
	private final Set<Ei> insertionQueue = new LinkedHashSet<Ei>();
	private final Set<E> removalQueue = new LinkedHashSet<E>();
	
	public final void insertElements(Collection<Ei> elements) {
		this.insertionQueue.addAll(elements);
	}

	@SafeVarargs
	public final void insertElements(Ei... elements) {
		insertElements(Arrays.asList(elements));
	}

	public final void removeElements(Collection<E> names) {
		this.removalQueue.addAll(names);
	}

	@SafeVarargs
	public final void removeElements(E... names) {
		removeElements(Arrays.asList(names));
	}
	
	public void finalizeQueuedChanges() {
		
		// check for conflicting elements which have to be removed before insertion
		Stream<E> removeForReplace = this.insertionQueue.stream()
			.map(Ei::name)
			.filter(name -> !this.removalQueue.contains(name))
			.filter(this.element2ide::containsKey);
		
		// build list of elements to remove
		Collection<E> toRemove = Stream.concat(removeForReplace, this.removalQueue.stream()).toList();
		
		// remove elements
		directRemoveElements(toRemove);
		
		// insert elements
		directInsertElements(this.insertionQueue);
		
		// clear queue
		this.removalQueue.clear();
		this.insertionQueue.clear();
		
	}
	
	public void reset() {
		this.element2ide.clear();
		this.node2idn.clear();
		this.lastElementId = 0;
		this.lastNodeId = 0;
		this.ide2instance.clear();
		this.ide2network.clear();
		this.removalQueue.clear();
		this.insertionQueue.clear();
	}
	
	public abstract class Network implements NetworkAccessor<N, E, Ei, Ni> {
		
		protected final IntSet elements = new IntOpenHashSet();
		
		public Network(IntCollection elements) {
			insertElements(elements);
		}
		
		public Network() {}
		
		protected abstract void afterElementsAdded(Collection<Ei> elements);
		protected abstract void afterElementsRemoved(Collection<Ei> elements);
		
		@SuppressWarnings("unchecked")
		protected final void insertElements(IntCollection elements) {
			Collection<Ei> instances = elements.intStream()
				.mapToObj(ide -> new Pair<>(ide, NetworkSpace.this.ide2instance.get(ide)))
				.filter(pair -> pair.second() != null)
				.peek(pair -> this.elements.add(pair.first().intValue()))
				.peek(pair -> {
					try {
						pair.second().onInsertionToNetwork((Ni) this);
					} catch (Exception e) {
						NodeNet.LOGGER.warn("exception while inserting element to network: {} as ide:{}", pair.second().name(), pair.first(), e);
					}
				})
				.map(Pair::second)
				.toList();
			afterElementsAdded(instances);
		}
		
		protected final void removeElements(IntCollection element) {
			Collection<Ei> instances = elements.intStream()
				.peek(this.elements::remove)
				.mapToObj(ide -> new Pair<>(ide, NetworkSpace.this.ide2instance.get(ide)))
				.filter(pair -> pair.second() != null)
				.peek(pair -> {
					try {
						pair.second().onRemovalFromNetwork();
					} catch (Exception e) {
						NodeNet.LOGGER.warn("exception while removing element from network: {} as ide:{}", pair.second().name(), pair.first(), e);
					}
				})
				.map(Pair::second)
				.toList();
			afterElementsRemoved(instances);
		}
		
		protected void clearNetwork() {
			this.elements.clear();
		}
		
		protected void merge(Collection<Ni> networks, IntCollection elements) {
			IntStream insertions = IntStream.concat(
				networks.stream()
					.flatMapToInt(network -> {
						IntList ides = IntArrayList.toList(network.getElements());
						network.removeElements(ides);
						return ides.intStream();
					})
					.distinct(),
				elements.intStream()
			).distinct();
			insertElements(IntArrayList.toList(insertions));
		}
		
		@SuppressWarnings({ "unchecked" })
		protected Collection<Ni> split(IntCollection nodes) {;
			List<IntSet> traces = Tracer.trace(NetworkSpace.this.idn2ide, nodes).stream()
					.peek(ides -> {
						ides.intStream()
							.mapToObj(NetworkSpace.this.ide2network::get)
							.filter(Objects::nonNull)
							.distinct()
							.forEach(network -> network.removeElements(ides));
					}).toList();
			clearNetwork();
			if (traces.isEmpty())
				return Collections.emptyList();
			traces.stream().limit(1).forEach(elements -> {
				insertElements(elements);
			});
			return Streams.concat(Stream.of((Ni) this),  traces.stream().skip(1).map(NetworkSpace.this::createNetwork)).toList();
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
		
		@Override
		public IntStream getNodes() {
			return this.elements.intStream()
					.mapToObj(NetworkSpace.this.idn2ide::getKeys)
					.flatMap(Collection::stream)
					.distinct()
					.mapToInt(Integer::intValue);
		}
		
		@Override
		public IntStream getElements() {
			return this.elements.intStream();
		}
		
		@Override
		public N getNodeName(Integer idn) {
			return NetworkSpace.this.getNodeName(idn);
		}
		
		@Override
		public E getElementName(Integer ide) {
			return NetworkSpace.this.getElementName(ide);
		}

		@Override
		public Ei getElementInstance(Integer ide) {
			return NetworkSpace.this.getElementInstance(ide);
		}

		@Override
		public Stream<E> getElementNames() {
			return getElements().mapToObj(this::getElementName);
		}

		@Override
		public Stream<Ei> getElementInstances() {
			return getElements().mapToObj(this::getElementInstance);
		}

		@Override
		public Stream<N> getNodeNames() {
			return getNodes().mapToObj(this::getNodeName);
		}
		
		@Override
		public IntStream getElementNodes(Integer ide) {
			return NetworkSpace.this.getElementNodes(ide);
		}

		@Override
		public Stream<N> getElementNodeNames(Integer ide) {
			return NetworkSpace.this.getElementNodeNames(ide);
		}

		@Override
		public IntStream getNodeElements(Integer idn) {
			return NetworkSpace.this.getNodeElements(idn);
		}

		@Override
		public Stream<E> getNodeElementNames(Integer idn) {
			return NetworkSpace.this.getNodeElementNames(idn);
		}

		@Override
		public Stream<Ei> getNodeElementInstances(Integer idn) {
			return NetworkSpace.this.getNodeElementInstances(idn);
		}
		
		@Override
		public Integer getNodeId(N nodeName) {
			return NetworkSpace.this.getNodeId(nodeName);
		}
		
		@Override
		public Integer getElementId(E elementName) {
			return NetworkSpace.this.getElementId(elementName);
		}
		
	}
	
	@Override
	public IntStream getNodes() {
		return this.node2idn.values().stream().mapToInt(Integer::intValue);
	}
	
	@Override
	public IntStream getElements() {
		return this.element2ide.values().stream().mapToInt(Integer::intValue);
	}
	
	@Override
	public N getNodeName(Integer idn) {
		return idn == null ? null : this.node2idn.getKey(idn);
	}
	
	@Override
	public E getElementName(Integer ide) {
		return ide == null ? null : this.element2ide.getKey(ide);
	}

	@Override
	public Ei getElementInstance(Integer ide) {
		return ide == null ? null : this.ide2instance.get(ide.intValue());
	}

	@Override
	public Stream<E> getElementNames() {
		return this.element2ide.keySet().stream();
	}
	
	@Override
	public Stream<Ei> getElementInstances() {
		return this.ide2instance.values().stream();
	}
	
	@Override
	public Stream<N> getNodeNames() {
		return this.node2idn.keySet().stream();
	}
	
	@Override
	public IntStream getElementNodes(Integer ide) {
		return ide == null ? null : this.idn2ide.getKeys(ide).stream().mapToInt(Integer::intValue);
	}

	@Override
	public Stream<N> getElementNodeNames(Integer ide) {
		return ide == null ? null : getElementNodes(ide).mapToObj(this::getNodeName);
	}

	@Override
	public IntStream getNodeElements(Integer idn) {
		return idn == null ? null : this.idn2ide.getAll(idn).stream().mapToInt(Integer::intValue);
	}

	@Override
	public Stream<E> getNodeElementNames(Integer idn) {
		return idn == null ? null : getNodeElements(idn).mapToObj(this::getElementName);
	}

	@Override
	public Stream<Ei> getNodeElementInstances(Integer idn) {
		return idn == null ? null : getNodeElements(idn).mapToObj(this::getElementInstance);
	}
	
	@Override
	public Integer getNodeId(N nodeName) {
		return this.node2idn.get(nodeName);
	}
	
	@Override
	public Integer getElementId(E elementName) {
		return this.element2ide.get(elementName);
	}
	
	public Ni getElementNetwork(Integer ide) {
		return ide == null ? null : this.ide2network.get(ide);
	}
	
	public Ni getElementNetwork(E elementName) {
		return getElementNetwork(getElementId(elementName));
	}
	
}
