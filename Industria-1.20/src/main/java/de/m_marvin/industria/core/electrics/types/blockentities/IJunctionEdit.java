package de.m_marvin.industria.core.electrics.types.blockentities;

import java.util.List;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface IJunctionEdit {
	
	public default String[] getCableWireLabels(NodePos node) {
		List<String[]> cableLaneLabels = ElectricUtility.getLaneLabels(this.getLevel(), node);
		if (cableLaneLabels.size() >= 1) {
			return cableLaneLabels.get(0);
		} else {
			return new String[] {};
		}
	}

	public default void setCableWireLabels(NodePos node, String[] laneLabels) {
		ElectricUtility.setLaneLabels(this.getLevel(), node, laneLabels);
	}
	
	public default String[] getInternalWireLabels(int node) {
		return new String[] {};
	}
	
	public default void setInternalWireLabels(int id, String[] laneLabels) {}
	
	public Level getLevel();
	public NodePos[] getEditCableNodes(Direction playerFacing, Direction playerHorizontalFacing);
	
}
