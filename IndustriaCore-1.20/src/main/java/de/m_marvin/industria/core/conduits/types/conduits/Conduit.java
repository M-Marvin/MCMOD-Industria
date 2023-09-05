package de.m_marvin.industria.core.conduits.types.conduits;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import javax.annotation.Nullable;

import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.apigame.constraints.VSRopeConstraint;

import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.engine.particles.ConduitParticleOption;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.registries.Conduits;
import de.m_marvin.industria.core.registries.ParticleTypes;
import de.m_marvin.industria.core.util.Formatter;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.NBTUtility;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Conduit {

	public static final int BLOCKS_PER_WIRE_ITEM = 2;
	
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
	
	public void appendHoverText(Level level, List<Component> tooltip, TooltipFlag flags) {
		tooltip.add(Formatter.build().appand(Component.translatable("industriacore.tooltip.conduit.name", this.getName())).withStyle(ChatFormatting.GRAY).component());
		tooltip.add(Formatter.build().appand(Component.translatable("industriacore.tooltip.conduit.maxClampDistance", this.getConduitType().getClampingLength())).withStyle(ChatFormatting.GRAY).component());
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
	
	public Item getItem() {
		return this.item;
	}
	
	public int getColorAt(ClientLevel level, Vec3d nodePos, ConduitEntity conduitState) {
		return 0xFFFFFF;
	}
	
	public void onNodeStateChange(Level level, BlockPos nodePos, BlockState nodeState, ConduitEntity conduitState) {
		if (nodeState.getBlock() instanceof IConduitConnector) {
			int nodeId = conduitState.getPosition().getNodeApos().equals(nodePos) ? conduitState.getPosition().getNodeAid() : conduitState.getPosition().getNodeBid();
			if (((IConduitConnector) nodeState.getBlock()).getConduitNodes(level, nodePos, nodeState).length <= nodeId) {
				ConduitUtility.removeConduit(level, conduitState.getPosition(), true);
			}
		} else {
			ConduitUtility.removeConduit(level, conduitState.getPosition(), true);
		}
	}
	
	public void onDismantle(@Nullable Level level, ConduitPos position, ConduitEntity conduitState) {}
	public void onBuild(@Nullable Level level, ConduitPos position, ConduitEntity conduitState) {}
	
	public void onPlace(Level level, ConduitPos position, ConduitEntity conduitState) {
		
		Vec3d nodeA = Vec3d.fromVec(PhysicUtility.ensureWorldBlockCoordinates(level, conduitState.getPosition().getNodeApos(), conduitState.getPosition().getNodeApos()));
		Vec3d nodeB = Vec3d.fromVec(PhysicUtility.ensureWorldBlockCoordinates(level, conduitState.getPosition().getNodeBpos(), conduitState.getPosition().getNodeBpos()));
		Vec3d middle = nodeA.sub(nodeB).mul(0.5).add(nodeB);
		
		level.playLocalSound(middle.x, middle.y, middle.z, this.getSoundType().getBreakSound(), SoundSource.BLOCKS, this.getSoundType().getVolume(), this.getSoundType().getPitch(), false);
		
	}
	
	public void onBreak(Level level, ConduitPos position, ConduitEntity conduitState, boolean dropItems) {
		
		Vec3d nodeA = Vec3d.fromVec(PhysicUtility.ensureWorldBlockCoordinates(level, conduitState.getPosition().getNodeApos(), conduitState.getPosition().getNodeApos()));
		Vec3d nodeB = Vec3d.fromVec(PhysicUtility.ensureWorldBlockCoordinates(level, conduitState.getPosition().getNodeBpos(), conduitState.getPosition().getNodeBpos()));
		Vec3d middle = nodeA.sub(nodeB).mul(0.5).add(nodeB);
		Vec3d nodeOrigin = MathUtility.getMinCorner(nodeA, nodeB);
		
		if (dropItems) {
			int wireCost = (int) Math.ceil(conduitState.getLength() / (float) BLOCKS_PER_WIRE_ITEM);
			for (int i = 0; i < wireCost; i++) {
				GameUtility.dropItem(level, new ItemStack(getItem()), Vec3f.fromVec(middle).add(new Vec3f(0.5F, 0.5F, 0.5F)), 0.5F, 0.1F);
			}
		}
		
		level.playLocalSound(middle.x, middle.y, middle.z, this.getSoundType().getBreakSound(), SoundSource.BLOCKS, this.getSoundType().getVolume(), this.getSoundType().getPitch(), false);
		
		if (!level.isClientSide()) {
			for (Vec3d node : conduitState.getShape().nodes) {
				((ServerLevel) level).sendParticles(new ConduitParticleOption(ParticleTypes.CONDUIT.get(), conduitState.getConduit()), node.x + nodeOrigin.x, node.y + nodeOrigin.y, node.z + nodeOrigin.z, 10, 0.2F, 0.2F, 0.2F, 1);
			}
		}
		
	}
	
	public static class ConduitType {
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
		
	}
	
	public void dismantleShape(Level level, ConduitEntity conduit) {
		
		ConduitShape shape = conduit.getShape();
 		if (shape.constraint.isPresent() && !level.isClientSide()) PhysicUtility.removeConstraint(level, shape.constraint.getAsInt());
		
	}
	
	public ConduitShape buildShape(Level level, ConduitEntity conduit) {
		
		Vec3d pointStart = conduit.getPosition().calculateWorldNodeA(level);
		Vec3d pointEnd = conduit.getPosition().calculateWorldNodeB(level);
		Vec3d origin = new Vec3d(Math.min(pointStart.x, pointEnd.x), Math.min(pointStart.y, pointEnd.y), Math.min(pointStart.z, pointEnd.z)).sub(0.5, 0.5, 0.5);
		pointStart.subI(origin);
		pointEnd.subI(origin);
		
		ConduitShape shape = conduit.getShape();
		
		if (shape == null) {

			int nodesPerBlock = conduit.getNodeCount();
			
			Vec3d connectionVec = pointEnd.copy().sub(pointStart);
			double spanDistance = connectionVec.length();
			double cornerSegments = conduit.getLength() * nodesPerBlock;
			double beamLength = conduit.getLength() / (cornerSegments + 1);
			double beamPlacementLength = spanDistance / (cornerSegments + 1);
			connectionVec.normalizeI();
			
			List<Vec3d> nodes = new ArrayList<>();
			nodes.add(pointStart);
			for (int i = 1; i <= cornerSegments; i++) {
				nodes.add(connectionVec.mul(beamPlacementLength * i).add(pointStart));
			}
			nodes.add(pointEnd);
			
			shape = new ConduitShape(nodes, beamLength);
			
		}
		
	  	return shape;
		
	}
	
	public void updatePhysicalNodes(Level level, ConduitEntity conduit) {
		ConduitShape shape = conduit.getShape();
		BlockPos nodeApos = conduit.getPosition().getNodeApos();
		BlockState nodeAstate = level.getBlockState(nodeApos);
		BlockPos nodeBpos = conduit.getPosition().getNodeBpos();
		BlockState nodeBstate = level.getBlockState(nodeBpos);
		
		if ((nodeAstate.getBlock() instanceof IConduitConnector && nodeBstate.getBlock() instanceof IConduitConnector)) {
			
			ConduitNode nodeA = ((IConduitConnector) nodeAstate.getBlock()).getConduitNode(level, nodeApos, nodeAstate, conduit.getPosition().getNodeAid());
			ConduitNode nodeB = ((IConduitConnector) nodeBstate.getBlock()).getConduitNode(level, nodeBpos, nodeBstate, conduit.getPosition().getNodeBid());
			Vec3d pointStart = nodeA.getWorldPosition(level, nodeApos);
			Vec3d pointEnd = nodeB.getWorldPosition(level, nodeBpos);
			Vec3d origin = new Vec3d(Math.min(pointStart.x, pointEnd.x), Math.min(pointStart.y, pointEnd.y), Math.min(pointStart.z, pointEnd.z)).sub(0.5, 0.5, 0.5);
			pointStart.subI(origin);
			pointEnd.subI(origin);
			
			if (shape != null) {
								
				// Integrate nodes
				for (int i = 0; i < shape.nodes.length - 0; i++) {
					Vec3d temp = shape.nodes[i].copy();
					shape.nodes[i].addI(shape.nodes[i].copy().sub(shape.lastPos[i]));
					shape.lastPos[i] = temp;
				}
				
				for (int itteration = 1; itteration <= 10; itteration++) {
	
					// Solve beams
					for (int i = 1; i < shape.nodes.length; i++) {
						
						Vec3d node1 = shape.nodes[i];
						Vec3d node2 = shape.nodes[i - 1];
						
						// Calculate spring deformation
						double constraintCompensation = pointStart.dist(pointEnd) / conduit.getLength()* 0.1F;
						Vec3d delta = node1.copy().sub(node2);
						double deltalength = delta.length(); // Math.sqrt(delta.dot(delta));
						double diff = (float) ((deltalength - shape.beamLength + constraintCompensation) / deltalength);
						
						// Reform spring
						double stiffness = conduit.getConduit().getConduitType().getStiffness();
						double stiffnessLinear = (float) (1 - Math.pow((1 - stiffness), 1 / itteration));
						node2.addI(delta.copy().mul(diff * 0.5).mul(stiffnessLinear));
						node1.subI(delta.copy().mul(diff * 0.5).mul(stiffnessLinear));
						
					}
					
				}
				
				// Accumulate gravity
				for (int i = 1; i < shape.nodes.length - 1; i++) {
					shape.lastPos[i].addI(GameUtility.getWorldGravity(level).copy().mul(conduit.getConduit().getConduitType().getNodeMass()));
				}
				
				// Solve collision
				for (int i = 1; i < shape.nodes.length - 1; i++) {
					
					Vec3d nodePos = shape.nodes[i].copy().add(Vec3f.fromVec(origin));
					BlockPos nodeBlockPos = MathUtility.toBlockPos(nodePos);
					
					if (!nodeBlockPos.equals(conduit.getPosition().getNodeApos()) && !nodeBlockPos.equals(conduit.getPosition().getNodeBpos())) {
						
						VoxelShape collisionShape = level.getBlockState(nodeBlockPos).getCollisionShape(level, nodeBlockPos);
						
						if (!collisionShape.isEmpty()) {
							
							AABB bounds = collisionShape.bounds().move(nodeBlockPos);
							
							Vec3d surface = nodePos.copy();
							double dist = 1;
							
							for (Direction d :
								Direction.values()) {
								
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
				
				// Re-Attach nodes to conduit ends
				shape.nodes[0].setI(pointStart);
				shape.nodes[shape.nodes.length - 1].setI(pointEnd);
				
				// Update temporary fields
				shape.shapeNodeA = shape.nodes[0].add(origin);
				shape.shapeNodeB = shape.nodes[shape.nodes.length - 1].add(origin);
				shape.contraptionNodeA = conduit.getPosition().calculateContraptionNodeA(level);
				shape.contraptionNodeB = conduit.getPosition().calculateContraptionNodeB(level);
				
				// Create constraint if not already existing
				if (!level.isClientSide() && shape.constraint.isEmpty()) {
					
					shape.contraptionA = (ServerShip) PhysicUtility.getContraptionOfBlock(level, nodeApos);
					shape.contraptionB = (ServerShip) PhysicUtility.getContraptionOfBlock(level, nodeBpos);
					
					long contraptionIdA = shape.contraptionA == null ? PhysicUtility.getGroundBodyId(level) : shape.contraptionA.getId();
					long contraptionIdB = shape.contraptionB == null ? PhysicUtility.getGroundBodyId(level) : shape.contraptionB.getId();
					
					double comp = 1e-10;
					double force = 1e10;
					VSConstraint constraint = new VSRopeConstraint(contraptionIdA, contraptionIdB, comp, shape.contraptionNodeA.writeTo(new Vector3d()), shape.contraptionNodeB.writeTo(new Vector3d()), force, conduit.getLength() + 1);
 					shape.constraint = OptionalInt.of(PhysicUtility.addConstraint(level, constraint));
					
				}
				
			}
			
		}
		
	}
	
	// TODO Remove if not able to fix
//	public void updateContraptionForces(Level level, PhysShip contraption, PlacedConduit conduit, int nodeId) {
//
////		double constraint = conduit.getShape().shapeNodeA.dist(conduit.getShape().shapeNodeB) / conduit.getLength();
////		
////		if (constraint > 0.8F) {
////			if (conduit.getShape().contraptionA == conduit.getShape().contraptionB) return;
////			
////			updatePhysicalNodes(level, conduit);
////			
////			ServerShip serverContraption = nodeId == 0 ? conduit.getShape().contraptionA : conduit.getShape().contraptionB;
////			Vec3d nodePos = nodeId == 0 ? conduit.getShape().contraptionNodeA : conduit.getShape().contraptionNodeB;
////			
////			if (nodePos == null) return;
////			
////			Vec3d massCenter = PhysicUtility.toContraptionPos(contraption.getTransform(), Vec3d.fromVec(contraption.getTransform().getPositionInWorld()));
////			
////			
////			
////			double strength = 120 * serverContraption.getInertiaData().getMass();
////			
////			Vec3d nodeInWorldPos = PhysicUtility.toWorldPos(contraption.getTransform(), nodePos);
////			Vec3d conduitEnd = nodeId == 0 ? conduit.getShape().shapeNodeA : conduit.getShape().shapeNodeB;
////			
////			Vec3d force = nodeInWorldPos.sub(conduitEnd).mul(-strength).clampI(-strength, strength);
////
////			conduitEnd.setI(nodeInWorldPos);
////			
////			contraption.applyInvariantForceToPos(force.writeTo(new Vector3d()), nodePos.sub(massCenter).writeTo(new Vector3d()));
////			
////			if (nodeId == 0) {
////				conduit.getShape().contraptionNodeA = null; 
////			} else {
////				conduit.getShape().contraptionNodeB = null;
////			}
////		}
//		
//	}
	
	public static class ConduitShape {
		public Vec3d[] nodes;
		public Vec3d[] lastPos;
		public double beamLength;
		
		// Temporary data that gets not saved
		public OptionalInt constraint = OptionalInt.empty();
		public ServerShip contraptionA;
		public ServerShip contraptionB;
		public Vec3d contraptionNodeA = new Vec3d();
		public Vec3d contraptionNodeB = new Vec3d();
		public Vec3d shapeNodeA = new Vec3d();
		public Vec3d shapeNodeB = new Vec3d();
		
		public ConduitShape(List<Vec3d> nodes, double beamLength) {
			this.nodes = nodes.toArray(new Vec3d[] {});
			this.lastPos = nodes.toArray(new Vec3d[] {});
			this.beamLength = beamLength;
		}
		
		public ConduitShape(Vec3d[] nodes, Vec3d[] lastPos, double beamLength) {
			this.nodes = nodes;
			this.lastPos = lastPos;
			this.beamLength = beamLength;
		}
		
		public static ConduitShape load(CompoundTag tag) {
			double beamLength = tag.getDouble("SegmentLength");
			ListTag nodesTag = tag.getList("Nodes", 10);
			if (nodesTag == null) return null;
			Vec3d[] nodes = new Vec3d[nodesTag.size()];
			Vec3d[] lastPos = new Vec3d[nodesTag.size()];
			for (int i = 0; i < nodes.length; i++) {
				CompoundTag nodeTag = nodesTag.getCompound(i);
				lastPos[i] = NBTUtility.loadVector3d(nodeTag.getCompound("LastPos"));
				nodes[i] = NBTUtility.loadVector3d(nodeTag.getCompound("Node"));
			}
			if (nodes.length < 3) return null;
			return new ConduitShape(nodes, lastPos, beamLength);
		}
		
		public CompoundTag save() {
			CompoundTag tag = new CompoundTag();
			tag.putDouble("SegmentLength", this.beamLength);
			ListTag nodes = new ListTag();
			for (int i = 0; i < this.nodes.length; i++) {
				CompoundTag nodeTag = new CompoundTag();
				nodeTag.put("LastPos", NBTUtility.writeVector3d(this.lastPos[i]));
				nodeTag.put("Node", NBTUtility.writeVector3d(this.nodes[i]));
				nodes.add(nodeTag);
			}
			tag.put("Nodes", nodes);
			return tag;
		}
		
		public void readUpdateData(FriendlyByteBuf buff) {
			this.beamLength = buff.readDouble();
			int nodeCount = buff.readInt();
			if (this.nodes == null || nodeCount != this.nodes.length) {
				this.nodes = new Vec3d[nodeCount];
				this.lastPos = new Vec3d[nodeCount];
			}
			for (int i = 0; i < nodeCount; i++) {
				this.nodes[i] = NBTUtility.readVector3d(buff);
				this.lastPos[i] = NBTUtility.readVector3d(buff);
			}
		}
		
		public void writeUpdateData(FriendlyByteBuf buff) {
			buff.writeDouble(beamLength);
			buff.writeInt(this.nodes.length);
			for (int i = 0; i < this.nodes.length; i++) {
				NBTUtility.writeVector3d(this.nodes[i], buff);
				NBTUtility.writeVector3d(this.lastPos[i], buff);
			}
		}
		
	}

	public ConduitEntity newConduitEntity(ConduitPos position, Conduit conduit, double length) {
		 return new ConduitEntity(position, conduit, length);
	}

	public Component getName() {
		ResourceLocation conduitKey = Conduits.CONDUITS_REGISTRY.get().getKey(this);
		return Component.translatable("conduit." + conduitKey.getNamespace() + "." + conduitKey.getPath());
	}
	
}
