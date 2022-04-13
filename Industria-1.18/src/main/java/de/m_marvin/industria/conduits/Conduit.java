package de.m_marvin.industria.conduits;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.conduit.IFlexibleConnection;
import de.m_marvin.industria.util.conduit.IFlexibleConnection.ConnectionPoint;
import de.m_marvin.industria.util.conduit.IFlexibleConnection.PlacedConduit;
import de.m_marvin.industria.util.unifiedvectors.Vec3f;
import de.m_marvin.industria.util.unifiedvectors.Vec3i;
import jnet.JNet;
import jnet.physic.PhysicSolver;
import jnet.physic.PhysicWorld;
import jnet.physic.SoftBody.Constrain;
import jnet.util.Vec2d;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Conduit implements IForgeRegistryEntry<Conduit> {
	
	public static final float GRAPHICAL_SEGMENT_LENGTH = 1F;
	
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
	
	public ConduitShape buildShape(BlockGetter level, PlacedConduit conduit) {
		
		BlockPos nodeApos = conduit.getNodeA();
		BlockState nodeAstate = level.getBlockState(nodeApos);
		BlockPos nodeBpos = conduit.getNodeB();
		BlockState nodeBstate = level.getBlockState(nodeBpos);
		BlockPos cornerMin = UtilityHelper.getMinCorner(nodeApos, nodeBpos);
		
		if (nodeAstate.getBlock() instanceof IFlexibleConnection && nodeBstate.getBlock() instanceof IFlexibleConnection) {
			
			ConnectionPoint pointA = ((IFlexibleConnection) nodeAstate.getBlock()).getConnectionPoints(level, nodeApos, nodeAstate)[conduit.getConnectionPointA()];
			ConnectionPoint pointB = ((IFlexibleConnection) nodeBstate.getBlock()).getConnectionPoints(level, nodeBpos, nodeBstate)[conduit.getConnectionPointB()];
			
			Vec3f pointStart = new Vec3f(
					nodeApos.getX() - cornerMin.getX(),
					nodeApos.getY() - cornerMin.getY(),
					nodeApos.getZ() - cornerMin.getZ()
				).add(pointA.offset().toFloat().mul(0.0625F));
			Vec3f pointEnd = new Vec3f(
					nodeBpos.getX() - cornerMin.getX(),
					nodeBpos.getY() - cornerMin.getY(),
					nodeBpos.getZ() - cornerMin.getZ()
				).add(pointB.offset().toFloat().mul(0.0625F));
			
			Vec3f rotationStart = UtilityHelper.rotationFromAxisAndAngle(pointA.attachmentFace().getAxis(), pointA.angle());
			Vec3f rotationEnd = UtilityHelper.rotationFromAxisAndAngle(pointB.attachmentFace().getAxis(), pointB.angle());
			float cornerSegments = conduit.getConduit().getConduitType().getStiffness() * 3;
			Vec3f connectionVec = pointEnd.copy().sub(pointStart);
			float conduitLength = (float) connectionVec.length();
			connectionVec.normalize();
			
			List<Vec3f> nodes = new ArrayList<>();
			nodes.add(pointStart);
			nodes.add(rotationStart);
			for (int i = 0; i < cornerSegments; i++) {
				nodes.add(new Vec3f(0, GRAPHICAL_SEGMENT_LENGTH, 0).add(nodes.get(nodes.size() - 2)));
				nodes.add(new Vec3f(0, 0, 0));
			}
			for (float f = 0; f < conduitLength; f += GRAPHICAL_SEGMENT_LENGTH) {
				nodes.add(connectionVec.copy().mul(GRAPHICAL_SEGMENT_LENGTH).add(nodes.get(nodes.size() - 2)));
				nodes.add(new Vec3f(0, 0, 0));
			}
			nodes.add(connectionVec.copy().mul(GRAPHICAL_SEGMENT_LENGTH).add(nodes.get(nodes.size() - 2)));
			nodes.add(new Vec3f(0, 0, 0));
			for (int i = 0; i < cornerSegments; i++) {
				nodes.add(new Vec3f(0, -GRAPHICAL_SEGMENT_LENGTH, 0).add(nodes.get(nodes.size() - 2)));
				nodes.add(new Vec3f(0, 0, 0));
			}
			nodes.add(pointEnd);
			nodes.add(rotationEnd);
			
			ConduitShape shape = new ConduitShape(nodes);
			return shape;
			
		}
		
		return null;
	}
	
	public void updatePhysicalNodes(BlockGetter level, PlacedConduit conduit) {
		
		Vec3f
		
	}
	
	public static class ConduitShape {
		public Vec3f[] nodes;
		public Vec3f[] angles;
		
		public ConduitShape(List<Vec3f> nodesAndAngles) {
			this.nodes = new Vec3f[nodesAndAngles.size() / 2];
			this.angles = new Vec3f[nodesAndAngles.size() / 2];
			for (int i = 0; i < nodesAndAngles.size(); i += 2) {
				this.nodes[i / 2] = nodesAndAngles.get(i);
				this.angles[i / 2] = nodesAndAngles.get(i + 1);
			}
		}
	}
	
}
