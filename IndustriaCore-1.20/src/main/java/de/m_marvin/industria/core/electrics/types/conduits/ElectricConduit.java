package de.m_marvin.industria.core.electrics.types.conduits;

import java.util.function.Consumer;

import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.ConduitType;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.electrics.engine.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.CircuitTemplate.Plotter;
import de.m_marvin.industria.core.registries.Circuits;
import de.m_marvin.industria.core.registries.NodeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;

public abstract class ElectricConduit extends Conduit implements IElectricConduit {
	
	public final int wireCount;
	public final double resistance;
	
	public ElectricConduit(ConduitType type, Item item, ResourceLocation texture, SoundType sound, int wireCount, double resistance) {
		super(type, item, texture, sound, NodeTypes.ELECTRIC);
		this.wireCount = wireCount;
		this.resistance = resistance;
	}
	
	public double getResistancePerBlock() {
		return resistance;
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
		Plotter template = CircuitTemplateManager.getInstance().getTemplate(Circuits.RESISTOR).plotter();
		template.setProperty("resistance", this.resistance * instance.getLength());
		
		NodePos[] connections = getConnections(level, position, instance);
		String[] wireLabels = this.getWireLanes(level, position, instance, null);
		for (int i = 0; i < wireLabels.length; i++) {
			// TODO maybe add some sort of filter to preven "singular matrix" warning ?
			if (!wireLabels[i].isBlank()) {
				template.setNetworkNode("NET1", connections[0], i, wireLabels[i]);
				template.setNetworkNode("NET2", connections[1], i, wireLabels[i]);
				plotter.accept(template);
			}
		}
	}
	
}
