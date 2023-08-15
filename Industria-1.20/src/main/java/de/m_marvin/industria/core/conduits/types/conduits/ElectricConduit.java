package de.m_marvin.industria.core.conduits.types.conduits;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

public abstract class ElectricConduit extends Conduit implements IElectricConduit {
	
	public final int wireCount;
	
	public ElectricConduit(ConduitType type, Item item, ResourceLocation texture, SoundType sound, int wireCount) {
		super(type, item, texture, sound);
		this.wireCount = wireCount;
	}
	
	@Override
	public void onBuild(Level level, ConduitPos position, ConduitEntity conduitState) {
		super.onBuild(level, position, conduitState);

//		if (conduitState.getDataStorage("Wires") == null) {
//			
//			if (level != null) {
//				
//				NodePos[] connections = getConnections(level, position, conduitState);
//				Set<ElectricNetworkHandlerCapability.Component<?, ?, ?>> neighborsA = ElectricUtility.findComponentsOnNode(level, connections[0]);
//				Set<ElectricNetworkHandlerCapability.Component<?, ?, ?>> neighborsB = ElectricUtility.findComponentsOnNode(level, connections[1]);
//				
//				Set<String> availableLabels = new HashSet<>();
//				for (Component<?, ?, ?> component : neighborsA) {
//					for (String label : component.getWireLanes(level, connections[0])) availableLabels.add(label);
//				}
//				for (Component<?, ?, ?> component : neighborsB) {
//					for (String label : component.getWireLanes(level, connections[1])) availableLabels.add(label);
//				}
//				
//				String[] labels = availableLabels.toArray(i -> new String[i]);
//				//for (int i = 0; i < Math.min(initialLabels.length, availableLabels.size()); i++) initialLabels[i] = labels[i];
//				
//			}
//
//			
//		}
		
	}
	
	@Override
	public ConduitEntity newConduitEntity(ConduitPos position, Conduit conduit, double length) {
		return new ElectricConduitEntity(position, conduit, length, this.wireCount);
	}
	
	protected int searchForLabel(String[] lables, String label) {
		int freeSpot = -(lables.length + 1);
		for (int i = 0; i < lables.length; i++) {
			if (lables[i].equals(label)) return i;
			if (freeSpot < 0 && lables[i].isEmpty()) freeSpot = i;
		}
		return -(freeSpot + 1);
	}
	
	@Override
	public void neighborRewired(Level level, ConduitEntity instance, ConduitPos position, Component<?, ?, ?> neighbor) {
		if (instance instanceof ElectricConduitEntity entity) {
			
			// TODO
			NodePos[] connections = getConnections(level, position, instance);
			NodePos[] connectionsNeighbor = neighbor.getNodes(level);
			
			Set<String> newLabels = new HashSet<>();
			for (int i = 0; i < connections.length; i++) {
				for (int i2 = 0; i2 < connectionsNeighbor.length; i2++) {
					if (connections[i].equals(connectionsNeighbor[i2])) {
						for (String label : neighbor.getWireLanes(level, connectionsNeighbor[i2])) newLabels.add(label);
					}
				}
			}
			
			String[] currentLabels = entity.getWireLanes();
			boolean changed = false;
			for (String label : newLabels) {
				int i = searchForLabel(currentLabels, label);
				if (i >= 0) continue;
				if (-i > currentLabels.length) break;
				currentLabels[-(i + 1)] = label;
				changed = true;
			}
			
			if (changed) ElectricUtility.notifyRewired(level, position);
			
		}
	}
	
	@Override
	public int getWireCount() {
		return this.wireCount;
	}
	
	@Override
	public String[] getWireLanes(ConduitPos pos, ConduitEntity instance, NodePos node) {
		if (instance instanceof ElectricConduitEntity entity) {
			return entity.getWireLanes();
		}
		return new String[] {};
	}
	
	@Override
	public void setWireLanes(ConduitPos pos, ConduitEntity instance, NodePos node, String[] laneLabels) {
		if (instance instanceof ElectricConduitEntity entity) {
			entity.setWireLanes(laneLabels);
		}
	}
	
	@Override
	public boolean isWire() {
		return true;
	}
	
	@Override
	public void plotCircuit(Level level, ConduitEntity instance, ConduitPos position, ElectricNetwork circuit, Consumer<CircuitTemplate> plotter) {
		CircuitTemplate template = CircuitTemplateManager.getInstance().getTemplate(new ResourceLocation(IndustriaCore.MODID, "resistor"));
		template.setProperty("resistance", 10); // TODO Wire resistance
		
		NodePos[] connections = getConnections(level, position, instance);
		for (String wireLabel : this.getWireLanes(position, instance, null)) {
			template.setNetworkNode("NET1", connections[0], wireLabel);
			template.setNetworkNode("NET2", connections[1], wireLabel);
			plotter.accept(template);
		}
	}
	
}