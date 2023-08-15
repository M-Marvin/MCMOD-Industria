package de.m_marvin.industria.core.conduits.types;

import java.util.List;
import java.util.function.Predicate;

import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3i;
import de.m_marvin.univec.impl.Vec4f;
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

	public Vec3d getContraptionPosition(BlockPos pos) {
		return Vec3d.fromVec(pos).add(getOffsetBlocks());	
	}
	
	public int getMaxConnections() {
		return maxConnections;
	}
	
	public NodeType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "ConduitNode{offset=[" + this.offset.x + "," + this.offset.y + "," + this.offset.z + "],type=" + this.type.toString() + ",connections=" + this.maxConnections + "}";
	}
	
	public static class NodeType {
		
		private final Predicate<Conduit> conduitPredicate;
		private final Vec4f color;
		
		public boolean isValid(Conduit conduit) {
			return this.conduitPredicate.test(conduit);
		}
		
		protected NodeType(Predicate<Conduit> conduitPredicate, Vec4f color) {
			this.conduitPredicate = conduitPredicate;
			this.color = color;
		}
		
		public static NodeType fromList(List<Conduit> validConduits, Vec4f color) {
			return new NodeType(validConduits::contains, color);
		}
		
		public static NodeType fromPredicate(Predicate<Conduit> predicate, Vec4f color) {
			return new NodeType(predicate, color);
		}
	
		public Vec4f getColor() {
			return color;
		}
		
	}

}
