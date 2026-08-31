package de.m_marvin.genet;

import java.util.Collection;

import de.m_marvin.genet.networks.Element;
import de.m_marvin.genet.networks.Network;
import de.m_marvin.genet.references.ElementReference;
import de.m_marvin.genet.references.NodeReference;
import de.m_marvin.genet.util.ufns.FriendlyFunctionalNetworkSpace;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class NetworkSpaceDomain extends FriendlyFunctionalNetworkSpace<ElementReference, Element, Network, NodeReference> {
	
	private final ResourceLocation name;
	
	public NetworkSpaceDomain(ResourceLocation name, int traceLimit) {
		super(traceLimit);
		
		this.name = name;
		
	}

	@Override
	protected Element findOrCreateComponentAt(ElementReference reference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<ParametrizedReference<ElementReference, NodeReference>> findConnectionsForComponent(
			Element component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CompoundTag serializeReference(ElementReference reference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ElementReference deserializeReference(CompoundTag tag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Network newNetwork() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Element newComponent() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
