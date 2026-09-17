package de.m_marvin.genet.nodal;

import de.m_marvin.genet.util.ufns.FunctionalNetworkSpace;
import de.m_marvin.tvnlnna.nodal.INodalElement;
import de.m_marvin.tvnlnna.nodal.NodalElementState;

public class Element<E> extends FunctionalNetworkSpace.Component<E> {
	
	private final NodalElementState<Node, E> state;
	
	public Element(E reference, INodalElement elementDef) {
		this.state = elementDef.newInstance(reference);
		
	}
	
	public NodalElementState<Node, E> getState() {
		return state;
	}
	
}
