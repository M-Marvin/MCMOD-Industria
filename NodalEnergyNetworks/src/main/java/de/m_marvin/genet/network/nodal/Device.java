package de.m_marvin.genet.network.nodal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.m_marvin.genet.network.NetworkElement;

public abstract class Device<N, E> implements NetworkElement<N, E, Grid<N, E>> {
	
	protected final E name;
	protected final Map<E, Element<N, E>> elements = new HashMap<E, Element<N,E>>();
	protected CircuitSpace<N, E> circuits;
	
	public Device(E name) {
		this.name = name;
	}
	
	@Override
	public E name() {
		return this.name;
	}
	
	public Element<N, E> install(Element<N, E> element) {
		Element<N, E> replaced = this.elements.put(element.name(), element);
		if (this.circuits != null)
			this.circuits.insertElements(element);
		return replaced;
	}
	
	public Element<N, E> remove(E name) {
		Element<N, E> removed = this.elements.remove(name);
		if (this.circuits != null)
			this.circuits.removeElements(name);
		return removed;
	}
	
	public void removeAllElements() {
		if (this.circuits != null)
			this.circuits.removeElements(this.elements.keySet());
		this.elements.clear();
	}
	
	public static enum DeviceChangeResult {
		REMOVAL,REINSERT,NO_CHANGE;
	}
	
	public abstract DeviceChangeResult onDeviceChange();
	
	@Override
	public void onRemovalFromNetwork() {
		if (this.circuits == null)
			return;
		
		this.circuits.removeElements(this.elements.keySet());
		this.circuits = null;
	}
	
	@Override
	public void onInsertionToNetwork(Grid<N, E> network) {
		if (this.circuits != null)
			throw new IllegalStateException("device is already inserted into a network: " + name());
		
		this.circuits = network.getCircuits();
		this.circuits.insertElements(this.elements.values());
	}

	@Override
	public int hashCode() {
		return Objects.hash(name());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Element other)
			return other.name().equals(this.name());
		return false;
	}
	
	@Override
	public String toString() {
		return "Device{" + name() + "}";
	}
	
}
