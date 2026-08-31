package de.m_marvin.genet.networks;

import java.util.UUID;

import de.m_marvin.genet.references.ElementReference;
import de.m_marvin.genet.references.NodeReference;
import de.m_marvin.genet.util.ufns.FunctionalNetworkSpace;
import de.m_marvin.unimap.api.MultiBiMap;
import de.m_marvin.unimap.impl.HashMultiBiMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.nbt.CompoundTag;
import tvnlnna.nodal.NodalNetwork;

public class Network extends FunctionalNetworkSpace.FunctionalNetwork<Network, ElementReference, Element, NodeReference> {

	private final UUID uuid;
	private final NodalNetwork net = new NodalNetwork();
	private final MultiBiMap<Integer, NodeReference> ref2nodeMap = new HashMultiBiMap<Integer, NodeReference>();
	
	private NetState state = NetState.ACTIVE;
	
	public static enum NetState {
		ACTIVE,INACTIVE,TRIPPED;
	}
	
	private Network(UUID uuid) {
		this.uuid = uuid;
	}
	
	public static Network newEmptyNetwork() {
		return new Network(UUID.randomUUID());
	}
	
	public static Network deserializeNetwork(CompoundTag tag) {
		UUID uuid = tag.getUUID("uuid");
		Network network = new Network(uuid);
		
		
		return network;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public NetState getState() {
		return state;
	}
	
	public void setState(NetState state) {
		if (state != this.state) {
//			var stateChangeEvent = new EnergyNetworkEvent.StateChangeEvent(this, this.state, EventPhase.PRE);
//			MinecraftForge.EVENT_BUS.post(stateChangeEvent);
//			if (stateChangeEvent.isCanceled()) return;
			
			this.state = state;

//			if (hasTripped())
//				MinecraftForge.EVENT_BUS.post(new EnergyNetworkEvent.TripedEvent(this));
//			MinecraftForge.EVENT_BUS.post(new EnergyNetworkEvent.StateChangeEvent(this, this.state, EventPhase.POST));
		}
		
		this.state = state;
	}
	
	public boolean isActive() {
		return this.state == NetState.ACTIVE;
	}
	
	public boolean hasTripped() {
		return this.state == NetState.TRIPPED;
	}
	
	public void trip() {
		setState(NetState.TRIPPED);
	}
	
	@Override
	protected void afterPutComponent(int refId, Element component) {
		this.net.addElement(component.getState());
	}

	@Override
	protected void afterRemoveComponent(int refId, Element component) {
		this.net.removeElement(component.getState());
	}

	@Override
	protected void afterParametrizedConnection(int refId1, int refId2, NodeReference parameter) {
		this.ref2nodeMap.put(refId1, parameter);
		this.ref2nodeMap.put(refId2, parameter);
		
	}
	
	@Override
	protected void afterIntegrateNetwork(IntSet refIds, Network other) {
		if (!isActive())
			this.state = other.state;
		
		for (int refId : refIds)
			if (other.ref2nodeMap.containsKey(refId))
				this.ref2nodeMap.putAll(refId, other.ref2nodeMap.getAll(refId));
	}
	
	
	
}
