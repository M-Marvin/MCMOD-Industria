package de.m_marvin.industria.core.electrics.types.conduits;

import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.CircuitElement;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.CircuitNode;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.ComponentCircuitContext;
import de.m_marvin.industria.core.registries.NodeTypes;
import net.minecraft.world.level.Level;

public class ElectricConduit extends Conduit implements IElectricConduit {
	
	public final int wireCount;
	public final double resistance;
	
	public ElectricConduit(Properties properties, int wireCount, double resistance) {
		super(properties.validNodes(NodeTypes.ELECTRIC));
		this.wireCount = wireCount;
		this.resistance = resistance;
	}
	
	public double getResistancePerBlock() {
		return resistance;
	}
	
	@Override
	public ConduitEntity newConduitEntity(ConduitPos position, float length) {
		return new ElectricConduitEntity(position, length, this.wireCount);
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
	public String[] getWireLanes(Level level, ElectricReference reference, ConduitEntity instance, NodePos node) {
		if (instance instanceof ElectricConduitEntity entity) {
			return entity.getWireLanes();
		}
		return new String[] {};
	}
	
	@Override
	public void setWireLanes(Level level, ElectricReference reference, ConduitEntity instance, NodePos node, String[] laneLabels) {
		if (instance instanceof ElectricConduitEntity entity) {
			entity.setWireLanes(laneLabels);
		}
	}
	
	@Override
	public boolean isWire() {
		return true;
	}
	
	@Override
	public void installCircuitElements(Level level, ElectricReference reference, ConduitEntity instance, ComponentCircuitContext context) {
		NodePos[] connections = getElectricConnections(level, reference, instance);
		String[] wireLabels = this.getWireLanes(level, reference, instance, null);
		for (int i = 0; i < wireLabels.length; i++) {
			context.installResistor(CircuitElement.element(reference, "Rwire_" + i), CircuitNode.node(connections[0], wireLabels[i]), CircuitNode.node(connections[1], wireLabels[i]), this.getResistancePerBlock() * instance.getLength());
		}
	}
	
}
