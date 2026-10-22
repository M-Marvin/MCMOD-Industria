package de.m_marvin.genet.network;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

import de.m_marvin.unimap.types.MultiBiMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class Tracer {
	
	@SuppressWarnings("deprecation")
	public static List<IntSet> trace(MultiBiMap<Integer, Integer> node2element, Collection<Integer> startNodes) {
		
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
						continue; // element already added
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
