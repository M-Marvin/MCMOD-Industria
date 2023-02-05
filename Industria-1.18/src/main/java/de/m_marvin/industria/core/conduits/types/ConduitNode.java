package de.m_marvin.industria.core.conduits.types;

import de.m_marvin.industria.core.conduits.engine.NodeType;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ConduitNode {
	
	private Vec3i offset;
	private final int maxConnections;
	private final NodeType type;
	
	public ConduitNode(NodeType type, int maxConnections, Vec3i offset) {
		this.offset = offset;
		this.maxConnections = maxConnections;
		this.type = type;
	}
	
	public Vec3i getOffset() {
		return offset;
	}
	
	public Vec3d getOffsetBlocks() {
		return new Vec3d(offset).div(16.0);
	}
	
	public void changeOffset(Vec3i offset) {
		this.offset = offset;
	}
	
	public Vec3d getWorldPosition(Level level, BlockPos pos) {
		return PhysicUtility.ensureWorldCoordinates(level, pos, Vec3d.fromVec(pos).add(getOffsetBlocks()));	
	}
	
	public int getMaxConnections() {
		return maxConnections;
	}
	
	public NodeType getType() {
		return type;
	}
	
}
