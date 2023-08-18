package de.m_marvin.industria.core.electrics.types.blockentities;

import java.util.List;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface IJunctionEdit {
	
	public default String[] getCableWireLabels(NodePos node) {
		List<String[]> laneLabels = ElectricUtility.getLaneLabels(this.getLevel(), node, Component::isWire);
		if (laneLabels.size() >= 1) {
			return laneLabels.get(0);
		} else {
			return new String[] {};
		}
	}

	public default void setCableWireLabels(NodePos node, String[] laneLabels) {
		ElectricUtility.setLaneLabels(this.getLevel(), node, Component::isWire, laneLabels);
	}
	
	public default String[] getInternalWireLabels(NodePos node) {
		List<String[]> laneLabels = ElectricUtility.getLaneLabels(getLevel(), node, c -> !c.isWire());
		if (laneLabels.size() >= 1) {
			return laneLabels.get(0);
		} else {
			return new String[0];
		}
	}
	
	public default void setInternalWireLabels(NodePos node, String[] laneLabels) {
		ElectricUtility.setLaneLabels(getLevel(), node, c -> !c.isWire(), laneLabels);
	}
	
	public Level getLevel();
	public NodePos[] getEditCableNodes(Direction playerFacing, Direction playerHorizontalFacing);
	
}
