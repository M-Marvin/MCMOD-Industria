package de.m_marvin.industria.core.conduits.types.conduits;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.content.registries.ModParticleTypes;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.engine.particles.ConduitParticleOption;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.PlacedConduit;
import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.conduits.types.items.ConduitCableItem;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Conduit implements IForgeRegistryEntry<Conduit> {
	
	private ResourceLocation registryName;
	private ConduitType conduitType;
	private Item item;
	private ResourceLocation texture;
	private SoundType soundType;
	
	public Conduit(ConduitType type, Item item, ResourceLocation texture, SoundType sound) {
		this.conduitType = type;
		this.item = item;
		this.texture = texture;
		this.soundType = sound;
	}
	
	public SoundType getSoundType() {
		return soundType;
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
		return this.item;
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
	
	public int getColorAt(ClientLevel level, Vec3d nodePos, PlacedConduit conduitState) {
		return 0xFFFFFF;
	}
	
	public void onNodeStateChange(Level level, BlockPos nodePos, BlockState nodeState, PlacedConduit conduitState) {
		if (nodeState.getBlock() instanceof IConduitConnector) {
			int nodeId = conduitState.getNodeA().equals(nodePos) ? conduitState.getConnectionPointA() : conduitState.getConnectionPointB();
			if (((IConduitConnector) nodeState.getBlock()).getConduitNodes(level, nodePos, nodeState).length <= nodeId) {
				ConduitUtility.removeConduit(level, conduitState.getConduitPosition(), true);
			}
		} else {
			ConduitUtility.removeConduit(level, conduitState.getConduitPosition(), true);
		}
	}
	
	public void onPlace(Level level, ConduitPos position, PlacedConduit conduitState) {
		
		BlockPos nodeA = conduitState.getNodeA();
		BlockPos nodeB = conduitState.getNodeB();
		Vec3f middle = Vec3f.fromVec(nodeA).sub(Vec3f.fromVec(nodeB)).mul(0.5F).add(Vec3f.fromVec(nodeB));
		
		level.playLocalSound(middle.x, middle.y, middle.z, this.getSoundType().getBreakSound(), SoundSource.BLOCKS, this.getSoundType().getVolume(), this.getSoundType().getPitch(), false);
		
	}
	
	public void onBreak(Level level, ConduitPos position, PlacedConduit conduitState, boolean dropItems) {
		
		if (dropItems) {
			int conduitLength = (int) Math.round(Math.sqrt(conduitState.getNodeA().distSqr(conduitState.getNodeB())));
			int wireCost = (int) Math.ceil(conduitLength / (float) ConduitCableItem.BLOCKS_PER_WIRE_ITEM);
			BlockPos middle = conduitState.getNodeB().subtract(conduitState.getNodeA());
			middle = conduitState.getNodeA().offset(middle.getX() / 2, middle.getY() / 2, middle.getZ() / 2);
			for (int i = 0; i < wireCost; i++) {
				GameUtility.dropItem(level, new ItemStack(getItem()), Vec3f.fromVec(middle).add(new Vec3f(0.5F, 0.5F, 0.5F)), 0.5F, 0.1F);
			}
		}
		
		BlockPos nodeA = conduitState.getNodeA();
		BlockPos nodeB = conduitState.getNodeB();
		BlockPos cornerMin = new BlockPos(Math.min(nodeA.getX(), nodeB.getX()), Math.min(nodeA.getY(), nodeB.getY()), Math.min(nodeA.getZ(), nodeB.getZ()));
		Vec3f middle = Vec3f.fromVec(nodeA).sub(Vec3f.fromVec(nodeB)).mul(0.5F).add(Vec3f.fromVec(nodeB));
		
		level.playLocalSound(middle.x, middle.y, middle.z, this.getSoundType().getBreakSound(), SoundSource.BLOCKS, this.getSoundType().getVolume(), this.getSoundType().getPitch(), false);
		
		if (!level.isClientSide()) {
			for (Vec3d node : conduitState.getShape().nodes) {
				((ServerLevel) level).sendParticles(new ConduitParticleOption(ModParticleTypes.CONDUIT.get(), conduitState.getConduit()), node.x + cornerMin.getX(), node.y + cornerMin.getY(), node.z + cornerMin.getZ(), 10, 0.2F, 0.2F, 0.2F, 1);
			}
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
			this.stiffness = MathUtility.clamp(stiffness, 0, 1);
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
		BlockPos cornerMin = MathUtility.getMinCorner(nodeApos, nodeBpos);
		
		if ((nodeAstate.getBlock() instanceof IConduitConnector && nodeBstate.getBlock() instanceof IConduitConnector)) {
			
			ConduitNode nodeA = ((IConduitConnector) nodeAstate.getBlock()).getConduitNode(level, nodeApos, nodeAstate, conduit.getConnectionPointA());
			ConduitNode nodeB = ((IConduitConnector) nodeBstate.getBlock()).getConduitNode(level, nodeBpos, nodeBstate, conduit.getConnectionPointB());
			
			// TODO Random-Noise offset to the nodes for random wire placement.
			
			Vec3d pointStart = nodeA.getWorldPosition(level, nodeApos).sub(Vec3d.fromVec(cornerMin));
			Vec3d pointEnd = nodeB.getWorldPosition(level, nodeBpos).sub(Vec3d.fromVec(cornerMin));
			
			Vec3d connectionVec = pointEnd.copy().sub(pointStart);
			double conduitLength = connectionVec.length();
			double cornerSegments = conduitLength * nodesPerBlock;
			double beamLength = conduitLength / (cornerSegments + 1);
			connectionVec.normalizeI();
			
			List<Vec3d> nodes = new ArrayList<>();
			nodes.add(pointStart);
			for (int i = 1; i <= cornerSegments; i++) {
				nodes.add(connectionVec.mul(beamLength * i).add(pointStart));
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
		BlockPos origin = MathUtility.getMinCorner(conduit.getNodeA(), conduit.getNodeB());
		
		if (shape != null) {
			
			// Integrate nodes
			for (int i = 1; i < shape.nodes.length - 1; i++) {
				Vec3d temp = shape.nodes[i].copy();
				shape.nodes[i].addI(shape.nodes[i].copy().sub(shape.lastPos[i]));
				shape.lastPos[i] = temp;
			}
			
			for (int itteration = 1; itteration <= 10; itteration++) {

				// Solve beams
				for (int i = 1; i < shape.nodes.length; i++) {
					
					Vec3d nodeB = shape.nodes[i];
					Vec3d nodeA = shape.nodes[i - 1];
					
					// Calculate spring deformation
					Vec3d delta = nodeB.copy().sub(nodeA);
					double deltalength = delta.length(); // Math.sqrt(delta.dot(delta));
					double diff = (float) ((deltalength - shape.beamLength) / deltalength);
					
					// Reform spring
					double stiffness = conduit.getConduit().getConduitType().getStiffness() * 1.0F;
					double stiffnessLinear = (float) (1 - Math.pow((1 - stiffness), 1 / itteration));
					boolean oneStatic = i == 1 || i == shape.nodes.length - 1;
					nodeA.addI(delta.copy().mul(i == 1 ? 0 : (oneStatic ? 1 : 0.5)).mul(diff).mul(stiffnessLinear));
					nodeB.subI(delta.copy().mul(i == shape.nodes.length - 1 ? 0 : (oneStatic ? 1 : 0.5)).mul(diff).mul(stiffnessLinear));
					
				}
				
			}
			
			// Accumulate gravity
			for (int i = 1; i < shape.nodes.length - 1; i++) {
				shape.lastPos[i].addI(GameUtility.getWorldGravity(level).copy().mul(conduit.getConduit().getConduitType().getNodeMass()));
			}
			
			// Solve collision
			for (int i = 1; i < shape.nodes.length - 1; i++) {
				
				Vec3d nodePos = shape.nodes[i].copy().add(Vec3f.fromVec(origin));
				BlockPos nodeBlockPos = new BlockPos(nodePos.x, nodePos.y, nodePos.z);
				
				if (!nodeBlockPos.equals(conduit.getNodeA()) && !nodeBlockPos.equals(conduit.getNodeB())) {
					
					VoxelShape collisionShape = level.getBlockState(nodeBlockPos).getCollisionShape(level, nodeBlockPos);
					
					if (!collisionShape.isEmpty()) {
						
						AABB bounds = collisionShape.bounds().move(nodeBlockPos);
						
						Vec3d surface = nodePos.copy();
						double dist = 1;
						
						for (Direction d : Direction.values()) {
							
							Vec3d surfacePoint = nodePos.copy();
							if (d.getAxis() == Axis.X && d.getAxisDirection() == AxisDirection.POSITIVE && !level.getBlockState(nodeBlockPos.relative(d)).isCollisionShapeFullBlock(level, nodeBlockPos.relative(d))) surfacePoint.x = (float) bounds.maxX;
							if (d.getAxis() == Axis.X && d.getAxisDirection() == AxisDirection.NEGATIVE && !level.getBlockState(nodeBlockPos.relative(d)).isCollisionShapeFullBlock(level, nodeBlockPos.relative(d))) surfacePoint.x = (float) bounds.minX;
							if (d.getAxis() == Axis.Y && d.getAxisDirection() == AxisDirection.POSITIVE && !level.getBlockState(nodeBlockPos.relative(d)).isCollisionShapeFullBlock(level, nodeBlockPos.relative(d))) surfacePoint.y = (float) bounds.maxY;
							if (d.getAxis() == Axis.Y && d.getAxisDirection() == AxisDirection.NEGATIVE && !level.getBlockState(nodeBlockPos.relative(d)).isCollisionShapeFullBlock(level, nodeBlockPos.relative(d))) surfacePoint.y = (float) bounds.minY;
							if (d.getAxis() == Axis.Z && d.getAxisDirection() == AxisDirection.POSITIVE && !level.getBlockState(nodeBlockPos.relative(d)).isCollisionShapeFullBlock(level, nodeBlockPos.relative(d))) surfacePoint.z = (float) bounds.maxZ;
							if (d.getAxis() == Axis.Z && d.getAxisDirection() == AxisDirection.NEGATIVE && !level.getBlockState(nodeBlockPos.relative(d)).isCollisionShapeFullBlock(level, nodeBlockPos.relative(d))) surfacePoint.z = (float) bounds.minZ;
							
							double distance = nodePos.copy().sub(surfacePoint).length();
							
							if (distance < dist && distance > 0) {
								dist = distance;
								surface = surfacePoint;
							}
							
						}
						
						if (dist == 1F) surface.z = (float) bounds.maxZ;
						
						surface.subI(Vec3f.fromVec(origin));
						
						shape.nodes[i].setI(surface.x, surface.y, surface.z);
 						shape.lastPos[i] = shape.nodes[i];
						
					}
					
				}
				
			}
			
		}
		
	}
	
	public static class ConduitShape {
		public Vec3d[] nodes;
		public Vec3d[] lastPos;
		public double beamLength;
		
		public ConduitShape(List<Vec3d> nodes, double beamLength) {
			this.nodes = nodes.toArray(new Vec3d[] {});
			this.lastPos = nodes.toArray(new Vec3d[] {});
			this.beamLength = beamLength;
		}
		
	}
	
}
