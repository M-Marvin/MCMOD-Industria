package de.m_marvin.genet.events;

import de.m_marvin.genet.networks.EnergyNetwork;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

public class EnergyNetworkEvent extends Event {

	private final EnergyNetwork network;
	
	public EnergyNetworkEvent(EnergyNetwork network) {
		this.network = network;
	}
	
	public EnergyNetwork getNetwork() {
		return network;
	}
	
	public static class TripedEvent extends EnergyNetworkEvent {
		
		public TripedEvent(EnergyNetwork network) {
			super(network);
		}
		
	}

	public static enum EventPhase {
		PRE,POST;
	}
	
	public static class StateChangeEvent extends EnergyNetworkEvent {
		
		private final EnergyNetwork.NetState state;
		private final EventPhase stage;
		
		public StateChangeEvent(EnergyNetwork network, EnergyNetwork.NetState newState, EventPhase stage) {
			super(network);
			this.state = newState;
			this.stage = stage;
		}
		
		public EnergyNetwork.NetState getNewState() {
			return state;
		}

		public EventPhase getStage() {
			return stage;
		}
		
		@Override
		public boolean isCancelable() {
			return this.stage == EventPhase.PRE;
		}
		
	}
	
}
