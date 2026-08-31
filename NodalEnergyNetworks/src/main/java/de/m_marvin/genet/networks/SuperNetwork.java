package de.m_marvin.genet.networks;

import de.m_marvin.genet.util.ufns.SynchronizedFunctionalNetworkSpace;
import it.unimi.dsi.fastutil.ints.IntSet;

public class SuperNetwork extends SynchronizedFunctionalNetworkSpace.SynchronizedFunctionalNetwork<SuperNetwork, SuperElementReference, SuperElement, SuperNodeReference> {
	
	@Override
	public void afterChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void afterPutComponent(int refId, SuperElement component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void afterRemoveComponent(int refId, SuperElement component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void afterParametrizedConnection(int refId1, int refId2, SuperNodeReference parameter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void afterIntegrateNetwork(IntSet refIds, SuperNetwork other) {
		// TODO Auto-generated method stub
		
	}
	
}
