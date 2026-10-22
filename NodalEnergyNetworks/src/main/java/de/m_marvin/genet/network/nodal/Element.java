package de.m_marvin.genet.network.nodal;

import de.m_marvin.genet.network.NetworkElement;
import de.m_marvin.genet.network.NetworkSpace;
import de.m_marvin.tvnlnna.nodal.INodalElement;
import de.m_marvin.tvnlnna.nodal.NodalElementState;

public class Element<N, E> implements NetworkElement<N, E, Circuit<N, E>> {
	
	private final NodalElementState<N, E> state;
	
	public Element(E reference, INodalElement elementDef) {
		this.state = elementDef.newInstance(reference);
		// TODO
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInsertionToNetwork(Circuit<N, E> network) {
		// TODO Auto-generated method stub
		
	}

}
