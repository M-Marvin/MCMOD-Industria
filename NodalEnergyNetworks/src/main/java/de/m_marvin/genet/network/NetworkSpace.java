package de.m_marvin.genet.network;

import java.util.function.Predicate;
import java.util.stream.Stream;

import de.m_marvin.genet.misc.OutOfIdError;
import de.m_marvin.unimap.api.BiMap;
import de.m_marvin.unimap.impl.HashBiMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class NetworkSpace<N, E, I extends NetworkElement<N, E>> {
	
	protected final BiMap<N, Integer> node2idn = new HashBiMap<>();
	protected final BiMap<E, Integer> element2ide = new HashBiMap<>();
	
	protected int lastNodeId = 0;
	protected int lastElementId = 0;
	
	protected final Int2ObjectMap<I> ide2instance = new Int2ObjectOpenHashMap<>();
	protected final BiMap<Integer, Network<N, E, I>> ide2network = new HashBiMap<>();
	
	protected static int supplyFreeId(int lastId, Predicate<Integer> usedCheck) {
		int free = lastId;
		while (usedCheck.test(free)) {
			free++;
			if (free == lastId)
				throw new OutOfIdError("network out of element ids");
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
	
	public I putElement(I instance) {
		int ide = insertElementRef(instance.reference());
		int[] idn = Stream.of(instance.nodes()).mapToInt(this::insertNodeRef).toArray();
		I replaced = this.ide2instance.put(ide, instance);
		
		// TODO network generation
		
		return replaced;
	}
	
	public I removeElement(E element) {
		// TODO network generation
		
		Integer ide = this.element2ide.remove(element);
		if (ide == null)
			return null;
		I instance = this.ide2instance.remove((int) ide);
		if (instance == null)
			return null;
		for (N node : instance.nodes())
			this.node2idn.remove(node);
		return instance;
	}
	
	
	
}
