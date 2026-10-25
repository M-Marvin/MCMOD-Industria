package de.m_marvin.genet.misc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

import de.m_marvin.unimap.types.MultiBiMap;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * A helper class which provides a algorithm for building network sets from a node2element map and a set of starting nodes.
 */
public final class Tracer {
	
	private Tracer() {}
	
	@SuppressWarnings("deprecation")
	public static List<IntSet> traceFromElements(MultiBiMap<Integer, Integer> node2element, IntCollection elements, Collection<Integer> startNodes) {
		Collection<Integer> validNodes = startNodes.stream()
			.filter(idn -> node2element.getAll(idn).stream().filter(elements::contains).count() > 0)
			.toList();
		return traceFromNodes(node2element, validNodes);
	}
	
	@SuppressWarnings("deprecation")
	public static List<IntSet> traceFromNodes(MultiBiMap<Integer, Integer> node2element, Collection<Integer> startNodes) {
		
		IntSet tracedNodes = new IntOpenHashSet();
		List<IntSet> traces = new ArrayList<>();
		IntSet trace = new IntOpenHashSet();
		
		for (int idnStart : startNodes) {
			if (tracedNodes.contains(idnStart))
				continue;
			
			// trace the nodes connected trough elements
			Queue<Integer> idnToTrace = new ArrayDeque<>();
			idnToTrace.add(idnStart);
			Integer idn;
			while ((idn = idnToTrace.poll()) != null) {
				tracedNodes.add((int) idn);
				
				// list all elements connected to the node
				for (int ide : node2element.getAll(idn)) {
					// add element to the current trace
					if (!trace.add(ide))
						continue; // element already added or invalid
					// update node to element map
					node2element.getKeys(ide).stream()
						.filter(Predicate.not(tracedNodes::contains))
						.forEach(idnToTrace::add);
				}
			}
			
			if (trace.isEmpty())
				continue;
			traces.add(trace);
			trace = new IntOpenHashSet();
		}
		
		return traces;
		
	}
	
}
