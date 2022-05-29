package de.m_marvin.industria.conduits;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.conduit.IFlexibleConnection;
import de.m_marvin.industria.util.conduit.IFlexibleConnection.ConnectionPoint;
import de.m_marvin.industria.util.conduit.PlacedConduit;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
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
			
//			Vec3f rotationStart = UtilityHelper.rotationFromAxisAndAngle(pointA.attachmentFace().getAxis(), pointA.angle());
//			Vec3f rotationEnd = UtilityHelper.rotationFromAxisAndAngle(pointB.attachmentFace().getAxis(), pointB.angle());
			Vec3f connectionVec = pointEnd.copy().sub(pointStart);
			float conduitLength = (float) connectionVec.length();
			float cornerSegments = conduitLength * 3F;
			float beamLength = conduitLength / (cornerSegments + 1);
			connectionVec.normalize();
			
			List<Vec3f> nodes = new ArrayList<>();
			nodes.add(pointStart);
			for (int i = 1; i <= cornerSegments; i++) {
				nodes.add(connectionVec.copy().mul(beamLength * i).add(pointStart));
			}
			nodes.add(pointEnd);
			
			ConduitShape shape = new ConduitShape(nodes, beamLength);
			return shape;
			
		}
		
		return null;
	}
	
	public void updatePhysicalNodes(BlockGetter level, PlacedConduit conduit) {
		
		ConduitShape shape = conduit.getShape();
		BlockPos origin = UtilityHelper.getMinCorner(conduit.getNodeA(), conduit.getNodeB());
		
		if (shape != null) {
			
			// Integrate nodes
			for (int i = 1; i < shape.nodes.length - 1; i++) {
				Vec3f temp = shape.nodes[i].copy();
				shape.nodes[i].add(shape.nodes[i].copy().sub(shape.lastPos[i]));
				shape.lastPos[i] = temp;
			}
			
			// Solve beams
			for (int itteration = 1; itteration <= 10; itteration++) {
				for (int i = 1; i < shape.nodes.length; i++) {
					
					Vec3f nodeB = shape.nodes[i];
					Vec3f nodeA = shape.nodes[i - 1];
					
					// Calculate spring deformation
					Vec3f delta = nodeB.copy().sub(nodeA);
					double deltalength = delta.length(); // Math.sqrt(delta.dot(delta));
					float diff = (float) ((deltalength - shape.beamLength) / deltalength);
					
					// Reform spring
					float stiffness = conduit.getConduit().getConduitType().getStiffness() * 1.0F;
					float stiffnessLinear = (float) (1 - Math.pow((1 - stiffness), 1 / itteration));
					boolean oneStatic = i == 1 || i == shape.nodes.length - 1;
					nodeA.add(delta.copy().mul(i == 1 ? 0 : (oneStatic ? 1 : 0.5F)).mul(diff).mul(stiffnessLinear));
					nodeB.sub(delta.copy().mul(i == shape.nodes.length - 1 ? 0 : (oneStatic ? 1 : 0.5F)).mul(diff).mul(stiffnessLinear));
					
				}
			}
			
			// Accumulate gravity
			for (int i = 1; i < shape.nodes.length - 1; i++) {
				shape.lastPos[i].add(UtilityHelper.getWorldGravity(level));
			}

			// Solve collision
			for (int i = 1; i < shape.nodes.length - 1; i++) {
				
				Vec3f nodePos = shape.nodes[i].copy().add(new Vec3f(origin));
				BlockPos nodeBlockPos = new BlockPos(nodePos.x, nodePos.y, nodePos.z);
				
				if (!nodeBlockPos.equals(conduit.getNodeA()) && !nodeBlockPos.equals(conduit.getNodeB())) {
					
					VoxelShape collisionShape = level.getBlockState(nodeBlockPos).getCollisionShape(level, nodeBlockPos);
					
					if (!collisionShape.isEmpty()) {
						
						AABB bounds = collisionShape.bounds().move(nodeBlockPos);
						
						if (bounds.maxY + 0.04F - nodePos.y <= 0.5F) {
							if (!level.getBlockState(nodeBlockPos.above()).isCollisionShapeFullBlock(level, nodeBlockPos)) {
								shape.nodes[i].y = (float) bounds.maxY - origin.getY() + 0.04F;
								shape.lastPos[i] = shape.nodes[i];
								continue;
							}
						}
						
						if (bounds.maxZ - nodePos.z >= 0.5F) {
							if (!level.getBlockState(nodeBlockPos.north()).isCollisionShapeFullBlock(level, nodeBlockPos)) {
								shape.nodes[i].z = (float) bounds.minZ - origin.getZ();
								shape.lastPos[i] = shape.nodes[i];
								continue;
							} else if (!level.getBlockState(nodeBlockPos.south()).isCollisionShapeFullBlock(level, nodeBlockPos)) {
								shape.nodes[i].z = (float) bounds.maxZ - origin.getZ();
								shape.lastPos[i] = shape.nodes[i];
								continue;
							}
						} else {
							if (!level.getBlockState(nodeBlockPos.south()).isCollisionShapeFullBlock(level, nodeBlockPos)) {
								shape.nodes[i].z = (float) bounds.maxZ - origin.getZ();
								shape.lastPos[i] = shape.nodes[i];
								continue;
							} else if (!level.getBlockState(nodeBlockPos.north()).isCollisionShapeFullBlock(level, nodeBlockPos)) {
								shape.nodes[i].z = (float) bounds.minZ - origin.getZ();
								shape.lastPos[i] = shape.nodes[i];
								continue;
							}
						}
						
						if (bounds.maxX - nodePos.x >= 0.5F) {
							if (!level.getBlockState(nodeBlockPos.west()).isCollisionShapeFullBlock(level, nodeBlockPos)) {
								shape.nodes[i].x = (float) bounds.minX - origin.getX();
								shape.lastPos[i] = shape.nodes[i];
								continue;
							} else if (!level.getBlockState(nodeBlockPos.east()).isCollisionShapeFullBlock(level, nodeBlockPos)) {
								shape.nodes[i].x = (float) bounds.maxX - origin.getX();
								continue;
							}
						} else {
							if (!level.getBlockState(nodeBlockPos.east()).isCollisionShapeFullBlock(level, nodeBlockPos)) {
								shape.nodes[i].x = (float) bounds.maxX - origin.getX();
								shape.lastPos[i] = shape.nodes[i];
								continue;
							} else if (!level.getBlockState(nodeBlockPos.west()).isCollisionShapeFullBlock(level, nodeBlockPos)) {
								shape.nodes[i].x = (float) bounds.minX - origin.getX();
								shape.lastPos[i] = shape.nodes[i];
								continue;
							}
						}
						
						if (bounds.maxY - nodePos.y >= 0.5F) {
							if (!level.getBlockState(nodeBlockPos.below()).isCollisionShapeFullBlock(level, nodeBlockPos)) {
								shape.nodes[i].y = (float) bounds.minY - origin.getY();
								shape.lastPos[i] = shape.nodes[i];
								continue;
							}
						}
						
						shape.nodes[i].y = (float) bounds.maxY + 0.04F - origin.getY();
											
					}
					
				}
				
			}
			
		}
		
	}
	
	public static class ConduitShape {
		public Vec3f[] nodes;
		public Vec3f[] lastPos;
		public float beamLength;
		
		public ConduitShape(List<Vec3f> nodes, float beamLength) {
			this.nodes = nodes.toArray(new Vec3f[] {});
			this.lastPos = nodes.toArray(new Vec3f[] {});
			this.beamLength = beamLength;
		}
	}
	
}
