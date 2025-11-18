package de.m_marvin.industria.core.conduits.types;

import java.util.function.Predicate;

import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.contraptions.ContraptionUtility;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
		return ContraptionUtility.ensureWorldCoordinates(level, pos, Vec3d.fromVec(pos).add(getOffsetBlocks()));	
	}

	public Vec3d getWorldRenderPosition(Level level, BlockPos pos) {
		return ContraptionUtility.optionalContraptionRenderTransform(level, pos, ContraptionUtility::toWorldPos, Vec3d.fromVec(pos).add(getOffsetBlocks()));	
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
		
		private final Predicate<NodeType> typePredicate;
		private final ChatFormatting color;
		private final ResourceLocation symbolTexture;
		
		protected NodeType(Predicate<NodeType> typePredicate, ChatFormatting color, ResourceLocation symbolTexture) {
			this.typePredicate = typePredicate;
			this.color = color;
			this.symbolTexture = symbolTexture;
		}
		
		public static NodeType fromPredicate(ChatFormatting color, ResourceLocation symbolTexture, Predicate<NodeType> typePredicate) {
			return new NodeType(typePredicate, color, symbolTexture);
		}
	
		public ChatFormatting getColor() {
			return color;
		}
		
		public ResourceLocation getSymbolTexture() {
			return symbolTexture;
		}

		public boolean canConnectWith(NodeType type) {
			return this.typePredicate.test(type);
		}

		public boolean canConnectWith(NodeType[] validTypes) {
			for (NodeType type : validTypes) {
				if (canConnectWith(type)) return true;
			}
			return false;
		}
		
		public boolean canConnectWith(ConduitState state, ConduitEntity entity) {
			return canConnectWith(state.getValidNodeTypes(entity));
		}

		public boolean canConnectWith(ConduitState state) {
			return canConnectWith(state.getConduit().getValidNodeTypes());
		}
		
	}

}
