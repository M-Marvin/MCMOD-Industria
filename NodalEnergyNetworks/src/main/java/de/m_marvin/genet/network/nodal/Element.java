package de.m_marvin.genet.network.nodal;

import java.util.Objects;

import de.m_marvin.genet.network.NetworkElement;
import de.m_marvin.tvnlnna.nodal.INodalElement;
import de.m_marvin.tvnlnna.nodal.NodalElementState;
import de.m_marvin.tvnlnna.nodal.NodalNetwork;

public class Element<N, E> implements NetworkElement<N, E, Circuit<N, E>> {
	
	private NodalNetwork<N, E> network;
	private final NodalElementState<N, E> state;
	
	public Element(E reference, INodalElement elementDef) {
		this.state = elementDef.newInstance(reference);
	}
	
	public NodalElementState<N, E> getState() {
		return state;
	}
	
	@Override
	public E name() {
		return this.state.name();
	}

	@Override
	public N[] nodes() {
		return this.state.getNodeNames();
	}

	@Override
	public void onRemovalFromNetwork() {
		if (this.network == null)
			return;
		
		this.network.removeElement(this.state);
		this.network = null;
	}

	@Override
	public void onInsertionToNetwork(Circuit<N, E> circuit) {
		if (this.network != null)
			throw new IllegalStateException("element is already inserted into a network: " + name());

		this.network = circuit.getNodal();
		this.network.addElement(this.state);
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
		return this.getClass().getSimpleName() + "{" + name() + "}";
	}
	
}
