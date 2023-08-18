package de.m_marvin.industria.core.conduits.types.conduits;

import java.util.function.Consumer;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplateManager;
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
	public int getWireCount() {
		return this.wireCount;
	}
	
	@Override
	public String[] getWireLanes(Level level, ConduitPos pos, ConduitEntity instance, NodePos node) {
		if (instance instanceof ElectricConduitEntity entity) {
			return entity.getWireLanes();
		}
		return new String[] {};
	}
	
	@Override
	public void setWireLanes(Level level, ConduitPos pos, ConduitEntity instance, NodePos node, String[] laneLabels) {
		if (instance instanceof ElectricConduitEntity entity) {
			entity.setWireLanes(laneLabels);
		}
	}
	
	@Override
	public boolean isWire() {
		return true;
	}
	
	@Override
	public void plotCircuit(Level level, ConduitEntity instance, ConduitPos position, ElectricNetwork circuit, Consumer<ICircuitPlot> plotter) {
		CircuitTemplate template = CircuitTemplateManager.getInstance().getTemplate(new ResourceLocation(IndustriaCore.MODID, "resistor"));
		template.setProperty("resistance", 10); // TODO Wire resistance
		
		NodePos[] connections = getConnections(level, position, instance);
		for (String wireLabel : this.getWireLanes(level, position, instance, null)) {
			if (!wireLabel.isEmpty()) {
				template.setNetworkNode("NET1", connections[0], wireLabel);
				template.setNetworkNode("NET2", connections[1], wireLabel);
				plotter.accept(template);
			}
		}
	}
	
}
