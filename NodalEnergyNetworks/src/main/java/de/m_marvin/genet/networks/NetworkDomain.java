package de.m_marvin.genet.networks;

import java.util.Collection;

import de.m_marvin.genet.util.ufns.FriendlyFunctionalNetworkSpace;
import net.minecraft.nbt.CompoundTag;

public class NetworkDomain extends FriendlyFunctionalNetworkSpace<SuperElementReference, SuperElement, SuperNetwork, SuperNodeReference> {

	public NetworkDomain(int traceLimit) {
		super(traceLimit);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected SuperElement findOrCreateComponentAt(SuperElementReference reference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<ParametrizedReference<SuperElementReference, SuperNodeReference>> findConnectionsForComponent(
			SuperElement component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CompoundTag serializeReference(SuperElementReference reference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SuperElementReference deserializeReference(CompoundTag tag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SuperNetwork newNetwork() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected SuperElement newComponent() {
		// TODO Auto-generated method stub
		return null;
	}

}
