package de.m_marvin.industria.conduits;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.items.ConduitCableItem;
import de.m_marvin.industria.particleoptions.ConduitParticleOption;
import de.m_marvin.industria.registries.ModItems;
import de.m_marvin.industria.registries.ModParticleTypes;
import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.block.IConduitConnector;
import de.m_marvin.industria.util.block.IConduitConnector.ConnectionPoint;
import de.m_marvin.industria.util.conduit.PlacedConduit;
import de.m_marvin.industria.util.types.ConduitPos;
import de.m_marvin.industria.util.unifiedvectors.Vec3f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Conduit implements IForgeRegistryEntry<Conduit> {
	
	private ResourceLocation registryName;
	private ConduitType conduitType;
	private ResourceLocation texture;
	
	public Conduit(ConduitType type, ResourceLocation texture) {
		this.conduitType = type;
		this.texture = texture;
	}
	
	public ConduitType getConduitType() {
		return conduitType;
	}
	
	public ResourceLocation getTexture() {
		return texture;
	}
	
	public ResourceLocation getTextureLoc() {
		return new ResourceLocation(this.texture.getNamespace(), "textures/" + this.texture.getPath() + ".png");
	}
	
	public Item getItem() {
		return ModItems.CONDUIT_TEST.get();
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
	
	public int getColorAt(ClientLevel level, Vec3f nodePos, PlacedConduit conduitState) {
		return 0xFFFFFF;
	}
	
	public void onNodeStateChange(Level level, BlockPos nodePos, BlockState nodeState, PlacedConduit conduitState) {
		if (nodeState.getBlock() instanceof IConduitConnector) {
			int nodeId = conduitState.getNodeA().equals(nodePos) ? conduitState.getConnectionPointA() : conduitState.getConnectionPointB();
			if (((IConduitConnector) nodeState.getBlock()).getConnectionPoints(level, nodePos, nodeState).length <= nodeId) {
				UtilityHelper.removeConduit(level, conduitState.getConduitPosition(), true);
			}
		} else {
			UtilityHelper.removeConduit(level, conduitState.getConduitPosition(), true);
		}
	}
	
	public void onBreak(Level level, ConduitPos position, PlacedConduit conduitState, boolean dropItems) {
		
		if (dropItems) {
			int conduitLength = (int) Math.round(Math.sqrt(conduitState.getNodeA().distSqr(conduitState.getNodeB())));
			int wireCost = (int) Math.ceil(conduitLength / (float) ConduitCableItem.BLOCKS_PER_WIRE_ITEM);
			BlockPos middle = conduitState.getNodeB().subtract(conduitState.getNodeA());
			middle = conduitState.getNodeA().offset(middle.getX() / 2, middle.getY() / 2, middle.getZ() / 2);
			for (int i = 0; i < wireCost; i++) {
				UtilityHelper.dropItem(level, new ItemStack(getItem()), new Vec3f(middle).add(new Vec3f(0.5F, 0.5F, 0.5F)), 0.5F, 0.1F);
			}
		}
		
		BlockPos nodeA = conduitState.getNodeA();
		BlockPos nodeB = conduitState.getNodeB();
		BlockPos cornerMin = new BlockPos(Math.min(nodeA.getX(), nodeB.getX()), Math.min(nodeA.getY(), nodeB.getY()), Math.min(nodeA.getZ(), nodeB.getZ()));
		
		for (Vec3f node : conduitState.getShape().nodes) {
			level.addParticle(new ConduitParticleOption(ModParticleTypes.CONDUIT.get(), conduitState.getConduit()), node.x + cornerMin.getX(), node.y + cornerMin.getY(), node.z + cornerMin.getZ(), 0.2F, 0.2F, 0.2F);
		}
		
	}
	
	public static class ConduitType implements IForgeRegistryEntry<ConduitType>{
		protected ResourceLocation registryName;
		protected float nodeMass;
		protected float stiffness;
		protected int clampingLength;
		protected int thickness;

		public ConduitType(float nodeMass, float stiffness, int clampingLength, int thickness) {
			this.nodeMass = nodeMass;
			this.stiffness = UtilityHelper.clamp(stiffness, 0, 1);
			this.clampingLength = clampingLength;
			this.thickness = thickness;
		}
		
		/*
		 * The mass of the individual nodes
		 */
		public float getNodeMass() {
			return nodeMass;
		}
		
		/*
		 * The stiffness of the wire beams
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
	
	public ConduitShape buildShape(Level level, PlacedConduit conduit, int nodesPerBlock) {
		
		BlockPos nodeApos = conduit.getNodeA();
		BlockState nodeAstate = level.getBlockState(nodeApos);
		BlockPos nodeBpos = conduit.getNodeB();
		BlockState nodeBstate = level.getBlockState(nodeBpos);
		BlockPos cornerMin = UtilityHelper.getMinCorner(nodeApos, nodeBpos);
		
		if ((nodeAstate.getBlock() instanceof IConduitConnector && nodeBstate.getBlock() instanceof IConduitConnector)) {
			
			ConnectionPoint pointA = ((IConduitConnector) nodeAstate.getBlock()).getConnectionPoints(level, nodeApos, nodeAstate)[conduit.getConnectionPointA()];
			ConnectionPoint pointB = ((IConduitConnector) nodeBstate.getBlock()).getConnectionPoints(level, nodeBpos, nodeBstate)[conduit.getConnectionPointB()];
			
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
			
			Vec3f connectionVec = pointEnd.copy().sub(pointStart);
			float conduitLength = (float) connectionVec.length();
			float cornerSegments = conduitLength * nodesPerBlock;
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
		
		Industria.LOGGER.log(org.apache.logging.log4j.Level.WARN, "Failed to build conduit shape at " + conduit.getNodeA() + "/" + conduit.getNodeB() + "!");
		
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
			
			for (int itteration = 1; itteration <= 10; itteration++) {

				// Solve beams
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
				shape.lastPos[i].add(UtilityHelper.getWorldGravity(level).copy().mul(conduit.getConduit().getConduitType().getNodeMass()));
			}
			
			// Solve collision
			for (int i = 1; i < shape.nodes.length - 1; i++) {
				
				Vec3f nodePos = shape.nodes[i].copy().add(new Vec3f(origin));
				BlockPos nodeBlockPos = new BlockPos(nodePos.x, nodePos.y, nodePos.z);
				
				if (!nodeBlockPos.equals(conduit.getNodeA()) && !nodeBlockPos.equals(conduit.getNodeB())) {
					
					VoxelShape collisionShape = level.getBlockState(nodeBlockPos).getCollisionShape(level, nodeBlockPos);
					
					if (!collisionShape.isEmpty()) {
						
						AABB bounds = collisionShape.bounds().move(nodeBlockPos);
						
						Vec3f surface = nodePos.copy();
						float dist = 1F;
						
						for (Direction d : Direction.values()) {
							
							Vec3f surfacePoint = nodePos.copy();
							if (d.getAxis() == Axis.X && d.getAxisDirection() == AxisDirection.POSITIVE && !level.getBlockState(nodeBlockPos.relative(d)).isCollisionShapeFullBlock(level, nodeBlockPos.relative(d))) surfacePoint.x = (float) bounds.maxX;
							if (d.getAxis() == Axis.X && d.getAxisDirection() == AxisDirection.NEGATIVE && !level.getBlockState(nodeBlockPos.relative(d)).isCollisionShapeFullBlock(level, nodeBlockPos.relative(d))) surfacePoint.x = (float) bounds.minX;
							if (d.getAxis() == Axis.Y && d.getAxisDirection() == AxisDirection.POSITIVE && !level.getBlockState(nodeBlockPos.relative(d)).isCollisionShapeFullBlock(level, nodeBlockPos.relative(d))) surfacePoint.y = (float) bounds.maxY;
							if (d.getAxis() == Axis.Y && d.getAxisDirection() == AxisDirection.NEGATIVE && !level.getBlockState(nodeBlockPos.relative(d)).isCollisionShapeFullBlock(level, nodeBlockPos.relative(d))) surfacePoint.y = (float) bounds.minY;
							if (d.getAxis() == Axis.Z && d.getAxisDirection() == AxisDirection.POSITIVE && !level.getBlockState(nodeBlockPos.relative(d)).isCollisionShapeFullBlock(level, nodeBlockPos.relative(d))) surfacePoint.z = (float) bounds.maxZ;
							if (d.getAxis() == Axis.Z && d.getAxisDirection() == AxisDirection.NEGATIVE && !level.getBlockState(nodeBlockPos.relative(d)).isCollisionShapeFullBlock(level, nodeBlockPos.relative(d))) surfacePoint.z = (float) bounds.minZ;
							
							float distance = (float) nodePos.copy().sub(surfacePoint).length();
							
							if (distance < dist && distance > 0) {
								dist = distance;
								surface = surfacePoint;
							}
							
						}
						
						if (dist == 1F) surface.z = (float) bounds.maxZ;
						
						surface.sub(new Vec3f(origin));
						
						shape.nodes[i].set(surface.x, surface.y, surface.z);
 						shape.lastPos[i] = shape.nodes[i];
									
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
