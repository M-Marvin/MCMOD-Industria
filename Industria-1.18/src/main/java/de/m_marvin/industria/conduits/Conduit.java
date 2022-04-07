package de.m_marvin.industria.conduits;

import com.jozufozu.flywheel.repack.joml.Vector3f;

import de.m_marvin.industria.util.IFlexibleConnection;
import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.IFlexibleConnection.ConnectionPoint;
import de.m_marvin.industria.util.IFlexibleConnection.PlacedConduit;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Conduit implements IForgeRegistryEntry<Conduit> {
	
	private ResourceLocation registryName;
	private ConduitType conduitType;
	
	public Conduit(ConduitType type) {
		this.conduitType = type;
	}
	
	public ConduitType getConduitType() {
		return conduitType;
	}
	
	@Override
	public Conduit setRegistryName(ResourceLocation name) {
		this.registryName = name;
		return this;
	}
	
	@Override
	public ResourceLocation getRegistryName() {
		return this.registryName;
	}
	
	@Override
	public Class<Conduit> getRegistryType() {
		return Conduit.class;
	}
	
	public static class ConduitType implements IForgeRegistryEntry<ConduitType>{
		protected ResourceLocation registryName;
		protected float stiffness;
		protected int clampingLength;
		protected int thickness;

		public ConduitType(float stiffness, int clampingLength, int thickness) {
			this.stiffness = stiffness;
			this.clampingLength = clampingLength;
			this.thickness = thickness;
		}
		
		/*
		 * The stiffness of the wire, defines how much the wire can bend 
		 */
		public float getStiffness() {
			return stiffness;
		}
		
		/*
		 * The maximum clamping length in blocks
		 */
		public int getClampingLength() {
			return clampingLength;
		}
		
		/*
		 * The thickness of the wire in pixels (1/16 block)
		 */
		public int getThickness() {
			return thickness;
		}
		
		@Override
		public ConduitType setRegistryName(ResourceLocation name) {
			this.registryName = name;
			return this;
		}
		
		@Override
		public ResourceLocation getRegistryName() {
			return this.registryName;
		}
		
		@Override
		public Class<ConduitType> getRegistryType() {
			return ConduitType.class;
		}
	}
	
	public float[] calculateShape(BlockGetter level, PlacedConduit conduit) {
		
		BlockPos nodeApos = conduit.getNodeA();
		BlockState nodeAstate = level.getBlockState(nodeApos);
		BlockPos nodeBpos = conduit.getNodeB();
		BlockState nodeBstate = level.getBlockState(nodeBpos);
		BlockPos cornerMin = UtilityHelper.getMinCorner(nodeApos, nodeBpos);
		
		if (nodeAstate.getBlock() instanceof IFlexibleConnection && nodeBstate.getBlock() instanceof IFlexibleConnection) {

			ConnectionPoint pointA = ((IFlexibleConnection) nodeAstate.getBlock()).getConnectionPoints(level, nodeApos, nodeAstate)[conduit.getConnectionPointA()];
			ConnectionPoint pointB = ((IFlexibleConnection) nodeBstate.getBlock()).getConnectionPoints(level, nodeBpos, nodeBstate)[conduit.getConnectionPointB()];
			
			BlockPos bp1 = nodeApos.subtract(cornerMin);
			BlockPos bp2 = nodeBpos.subtract(cornerMin);
			return new float[] {bp1.getX() + pointA.offset().x / 16F, bp1.getY() + pointA.offset().y / 16F, bp1.getZ() + pointA.offset().z / 16F, 0, 0, 0,
			bp2.getX() + pointB.offset().x / 16F, bp2.getY() + pointB.offset().y / 16F, bp2.getZ() + pointB.offset().z / 16F, 0, 0, 0};
			
		}
		
		return new float[] {};
	}
	
}
