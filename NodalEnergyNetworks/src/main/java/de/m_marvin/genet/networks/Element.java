package de.m_marvin.genet.networks;

import de.m_marvin.genet.references.ElementReference;
import de.m_marvin.genet.util.ufns.FunctionalNetworkSpace.Component;
import tvnlnna.nodal.INodalElement;
import tvnlnna.nodal.NodalElementState;

public class Element extends Component<ElementReference>  {

	private final ElementReference reference;
	private final NodalElementState state;
	
	public Element(ElementReference reference, NodalElementState state) {
		super();
		this.reference = reference;
		this.state = state;
	}
	
	public ElementReference getReference() {
		return reference;
	}
	
	public INodalElement getModel() {
		return this.state.type();
	}
	
	public NodalElementState getState() {
		return state;
	}
	
}
