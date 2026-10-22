package de.m_marvin.genet.network;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface NetworkAccessor<N, E, Ei extends NetworkElement<N, E, Ni>, Ni extends NetworkSpace<N, E, Ei, Ni>.Network> {
	
	public IntStream getNodes();
	
	public IntStream getElements();
	
	public N getNodeName(Integer idn);
	
	public E getElementName(Integer ide);
	
	public Ei getElementInstance(Integer ide);
	
	public Stream<E> getElementNames();
	
	public Stream<Ei> getElementInstances();
	
	public Stream<N> getNodeNames();
	
	public IntStream getElementNodes(Integer ide);
	
	public Stream<N> getElementNodeNames(Integer ide);
	
	public IntStream getNodeElements(Integer idn);
	
	public Stream<E> getNodeElementNames(Integer idn);
	
	public Stream<Ei> getNodeElementInstances(Integer idn);
	
	public Integer getNodeId(N nodeName);

	public Integer getElementId(E elementName);
	
	default public Ei getElementInstance(E elementName) {
		return getElementInstance(getElementId(elementName));
	}
	
	default public IntStream getElementNodes(E elementName) {
		return getElementNodes(getElementId(elementName));
	}
	
	default public Stream<N> getElementNodeNames(E elementName) {
		return getElementNodeNames(getElementId(elementName));
	}
	
	default public IntStream getNodeElements(N nodeName) {
		return getNodeElements(getNodeId(nodeName));
	}
	
	default public Stream<E> getNodeElementNames(N nodeName) {
		return getNodeElementNames(getNodeId(nodeName));
	}
	
	default public Stream<Ei> getNodeElementInstances(N nodeName) {
		return getNodeElementInstances(getNodeId(nodeName));
	}
	
}
