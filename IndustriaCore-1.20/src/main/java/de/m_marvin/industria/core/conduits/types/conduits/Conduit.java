package de.m_marvin.industria.core.conduits.types.conduits;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import org.joml.Vector3d;
import org.valkyrienskies.core.apigame.constraints.VSConstraint;
import org.valkyrienskies.core.apigame.constraints.VSRopeConstraint;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.engine.particles.ConduitParticleOption;
import de.m_marvin.industria.core.conduits.types.ConduitBehavior;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitNode.NodeType;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitState;
import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.conduits.types.items.AbstractConduitItem;
import de.m_marvin.industria.core.contraptions.ContraptionUtility;
import de.m_marvin.industria.core.contraptions.engine.types.contraption.ServerContraption;
import de.m_marvin.industria.core.registries.Conduits;
import de.m_marvin.industria.core.registries.ParticleTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.NBTUtility;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
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
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Conduit extends ConduitBehavior implements ItemLike {

	public static final int BLOCKS_PER_WIRE_ITEM = 2;
	
	private final StateDefinition<Conduit, ConduitState> stateDefinition;
	private final ConduitState defaultConduitState;
	private Item item;
	
	public Conduit(ConduitBehavior.Properties properties) {
		super(properties);
		
		StateDefinition.Builder<Conduit, ConduitState> builder = new StateDefinition.Builder<Conduit, ConduitState>(this);
		this.createStateDefinition(builder);
		this.stateDefinition = builder.create(Conduit::defaultConduitState, ConduitState::new);
		this.defaultConduitState = this.stateDefinition.any();
	}

	public StateDefinition<Conduit, ConduitState> getStateDefinition() {
		return this.stateDefinition;
	}
	
	public float getNodeMass(ConduitState state, ConduitEntity conduitEntity) {
		return getNodeMass();
	}
	
	public float getStiffness(ConduitState state, ConduitEntity conduitEntity) {
		return getStiffness();
	}
	
	public float getTension(ConduitState state, ConduitEntity conduitEntity) {
		return getTension();
	}
	
	public int getClampingLength(ConduitState state, ConduitEntity conduitEntity) {
		return getClampingLength();
	}
	
	public float getThickness(ConduitState state, ConduitEntity conduitEntity) {
		return getThickness();
	}
	
	public float getSegmentLength(ConduitState state, ConduitEntity conduitEntity) {
		return getSegmentLength();
	}
	
	public double getConstraintCompensation(ConduitState state, ConduitEntity conduitEntitiy) {
		return getConstraintCompensation();
	}

	public double getConstraintForce(ConduitState state, ConduitEntity conduitEntitiy) {
		return getConstraintForce();
	}
	
	public SoundType getSoundType(ConduitState state, ConduitEntity conduitEntity) {
		return getSoundType();
	}
	
	public NodeType[] getValidNodeTypes(ConduitState state, ConduitEntity conduitEntity) {
		return getValidNodeTypes();
	}

	public ConduitEntity newConduitEntity(ConduitPos position, float length) {
		 return new ConduitEntity(position, length);
	}

	public Component getName() {
		ResourceLocation conduitKey = Conduits.CONDUITS_REGISTRY.get().getKey(this);
		return Component.translatable("conduit." + conduitKey.getNamespace() + "." + conduitKey.getPath());
	}

	@Override
	public String toString() {
		ResourceLocation conduitKey = Conduits.CONDUITS_REGISTRY.get().getKey(this);
		return "Conduit{" + conduitKey.toString() + "}";
	}
	
	protected void createStateDefinition(StateDefinition.Builder<Conduit, ConduitState> builder) {}

	public final ConduitState defaultConduitState() {
		return this.defaultConduitState;
	}
	
	@Override
	public Item asItem() {
		if (this.item == null) {
			this.item = AbstractConduitItem.byConduit(this);
		}
		return this.item;
	}

	public void appendHoverText(List<Component> tooltip, TooltipFlag flags) {}
	
	@SuppressWarnings("deprecation")
	public List<ItemStack> getDrops(ConduitState state, ConduitEntity conduitEntity) {
		Level level = conduitEntity.getLevel();
		int conduitCost = (int) Math.ceil(conduitEntity.getLength() / (float) BLOCKS_PER_WIRE_ITEM);
		Item conduitItem = asItem();
		if (conduitItem == null) return List.of();
		int stacks = level.getRandom().nextIntBetweenInclusive(conduitCost / conduitItem.getMaxStackSize() + 1, conduitCost);
		List<ItemStack> drops = new ArrayList<ItemStack>();
		for (int i = 1; i <= stacks; i++) {
			int maxCount = Math.min(conduitItem.getMaxStackSize(), conduitCost - (stacks - i));
			int minCount = Math.max(1, conduitCost - (stacks - i) * conduitItem.getMaxStackSize());
			int count = level.getRandom().nextIntBetweenInclusive(minCount, maxCount);
			conduitCost -= count;
			drops.add(new ItemStack(conduitItem, count));
		}
		return drops;
	}
	
	public boolean collidesWithBlock(BlockPos pos, ConduitState state, ConduitEntity conduitEntity) {
		BlockState blockState = conduitEntity.getLevel().getBlockState(pos);
		VoxelShape blockShape = blockState.getCollisionShape(conduitEntity.getLevel(), pos);
		if (blockShape.isEmpty()) return false;
		AABB bounds = blockShape.bounds();
		return bounds.getXsize() == 1.0 || bounds.getYsize() == 1.0 || bounds.getZsize() == 1.0;
	}
	
	public void onNodeStateChange(BlockPos nodePos, BlockState nodeState, ConduitState state, ConduitEntity conduitEntity) {
		Level level = conduitEntity.getLevel();
		if (nodeState.getBlock() instanceof IConduitConnector) {
			int nodeId = conduitEntity.getPosition().getNodeApos().equals(nodePos) ? conduitEntity.getPosition().getNodeAid() : conduitEntity.getPosition().getNodeBid();
			if (((IConduitConnector) nodeState.getBlock()).getConduitNodes(level, nodePos, nodeState).length <= nodeId) {
				ConduitUtility.removeConduit(level, conduitEntity.getPosition(), true);
			}
		} else {
			ConduitUtility.removeConduit(level, conduitEntity.getPosition(), true);
		}
	}
	
	public void onDismantle(ConduitState state, ConduitEntity conduitEntity) {}
	public void onBuild(ConduitState state, ConduitEntity conduitEntity) {}
	
	public ConduitState beforePlace(Level level, BlockPos nodePosA, BlockPos nodePosB, float length) {
		return defaultConduitState();
	}
	
	public void onPlace(ConduitState state, ConduitEntity conduitEntity) {
		Level level = conduitEntity.getLevel();
		Vec3d nodeA = Vec3d.fromVec(ContraptionUtility.ensureWorldBlockCoordinates(level, conduitEntity.getPosition().getNodeApos(), conduitEntity.getPosition().getNodeApos()));
		Vec3d nodeB = Vec3d.fromVec(ContraptionUtility.ensureWorldBlockCoordinates(level, conduitEntity.getPosition().getNodeBpos(), conduitEntity.getPosition().getNodeBpos()));
		Vec3d middle = nodeA.sub(nodeB).mul(0.5).add(nodeB);
		SoundType soundType = state.getSoundType(conduitEntity);
		level.playLocalSound(middle.x, middle.y, middle.z, soundType.getBreakSound(), SoundSource.BLOCKS, soundType.getVolume(), soundType.getPitch(), false);
	}
	
	public void onBreak(ConduitState state, ConduitEntity conduitEntity, boolean dropItems) {

		Level level = conduitEntity.getLevel();
		Vec3d nodeA = Vec3d.fromVec(ContraptionUtility.ensureWorldBlockCoordinates(level, conduitEntity.getPosition().getNodeApos(), conduitEntity.getPosition().getNodeApos()));
		Vec3d nodeB = Vec3d.fromVec(ContraptionUtility.ensureWorldBlockCoordinates(level, conduitEntity.getPosition().getNodeBpos(), conduitEntity.getPosition().getNodeBpos()));
		Vec3d middle = nodeA.sub(nodeB).mul(0.5).add(nodeB);
		Vec3d nodeOrigin = MathUtility.getMinCorner(nodeA, nodeB);
		
		if (dropItems && !level.isClientSide() && level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
			List<ItemStack> drops = state.getDrops(conduitEntity);
			for (int i = 0; i < drops.size(); i++) {
				Vec3f position = new Vec3f(nodeA.add(nodeB.sub(nodeA).mul(i / (double) drops.size())));
				GameUtility.dropItem(level, drops.get(i), position, 0.5F, 0.1F);
			}
		}
		
		level.playLocalSound(middle.x, middle.y, middle.z, this.getSoundType().getBreakSound(), SoundSource.BLOCKS, this.getSoundType().getVolume(), this.getSoundType().getPitch(), false);
		
		if (!level.isClientSide()) {
			for (Vec3d node : conduitEntity.getShape().nodes) {
				((ServerLevel) level).sendParticles(new ConduitParticleOption(ParticleTypes.CONDUIT.get(), state), node.x + nodeOrigin.x, node.y + nodeOrigin.y, node.z + nodeOrigin.z, 10, 0.2F, 0.2F, 0.2F, 1);
			}
		}
		
	}
	
	public void dismantleShape(ConduitState state, ConduitEntity conduit) {
		
 		if (conduit.getShape().constraint.isPresent() && !conduit.getLevel().isClientSide())
 			ContraptionUtility.removeConstraint(conduit.getLevel(), conduit.getShape().constraint.getAsInt());
		
	}
	
	public ConduitShape buildShape(ConduitState state, ConduitEntity conduitEntity) {
		
		ConduitShape shape = conduitEntity.getShape();
		Level level = conduitEntity.getLevel();
		
		if (shape == null) {
			
			Vec3d pointStart = conduitEntity.getPosition().calculateWorldNodeA(level);
			Vec3d pointEnd = conduitEntity.getPosition().calculateWorldNodeB(level);
			Vec3d origin = new Vec3d(Math.min(pointStart.x, pointEnd.x), Math.min(pointStart.y, pointEnd.y), Math.min(pointStart.z, pointEnd.z)).sub(0.5, 0.5, 0.5);
			pointStart.subI(origin);
			pointEnd.subI(origin);

			float segmentLength = state.getSegmentLength(conduitEntity);
			Vec3d connectionVec = pointEnd.sub(pointStart);
			Double spanDistance = connectionVec.length();
			int segmentCount = (int) Math.round(conduitEntity.getLength() / segmentLength);
			segmentLength = conduitEntity.getLength() / segmentCount;
			double segmentPlacementLength = spanDistance / segmentCount;
			connectionVec.normalizeI();
			
			List<Vec3d> nodes = new ArrayList<>();
			for (int i = 0; i <= segmentCount; i++)
				nodes.add(connectionVec.mul(segmentPlacementLength * i).add(pointStart));
			
			shape = ConduitShape.construct(nodes, segmentLength);
			
		}
		
		if (!conduitEntity.getLevel().isClientSide()) {
			shape.contraptionA = ContraptionUtility.getContraptionOfBlock(level, conduitEntity.getPosition().getNodeApos());
			shape.contraptionB = ContraptionUtility.getContraptionOfBlock(level, conduitEntity.getPosition().getNodeBpos());
			long contraptionIdA = shape.contraptionA == null ? ContraptionUtility.getGroundBodyId(level) : shape.contraptionA.getId();
			long contraptionIdB = shape.contraptionB == null ? ContraptionUtility.getGroundBodyId(level) : shape.contraptionB.getId();
			
			double comp = state.getConstraintCompensation(conduitEntity);
			double force = state.getConstraintForce(conduitEntity);
			Vec3d contraptionNodePosA = conduitEntity.getPosition().calculateContraptionNodeA(level);
			Vec3d contraptionNodePosB = conduitEntity.getPosition().calculateContraptionNodeB(level);
			VSConstraint constraint = new VSRopeConstraint(contraptionIdA, contraptionIdB, comp, contraptionNodePosA.writeTo(new Vector3d()), contraptionNodePosB.writeTo(new Vector3d()), force, conduitEntity.getLength());
				shape.constraint = OptionalInt.of(ContraptionUtility.addConstraint(level, constraint));
		}
		
		return shape;
		
	}
	
	public void updateShape(ConduitState state, ConduitEntity conduit) {
		
		Level level = conduit.getLevel();
		BlockPos nodeApos = conduit.getPosition().getNodeApos();
		BlockState nodeAstate = level.getBlockState(nodeApos);
		BlockPos nodeBpos = conduit.getPosition().getNodeBpos();
		BlockState nodeBstate = level.getBlockState(nodeBpos);

		if ((nodeAstate.getBlock() instanceof IConduitConnector && nodeBstate.getBlock() instanceof IConduitConnector)) {
			
			ConduitNode nodeA = ((IConduitConnector) nodeAstate.getBlock()).getConduitNode(level, nodeApos, nodeAstate, conduit.getPosition().getNodeAid());
			ConduitNode nodeB = ((IConduitConnector) nodeBstate.getBlock()).getConduitNode(level, nodeBpos, nodeBstate, conduit.getPosition().getNodeBid());
			
			if (nodeA == null || nodeB == null) {
				IndustriaCore.LOGGER.warn("Invalid conduit at " + nodeApos + " - " + nodeBpos);
				return;
			}
			
			// update the world position of the start and end point in the shape data
			conduit.getShape().shapeNodeA = nodeA.getWorldPosition(level, nodeApos);
			conduit.getShape().shapeNodeB = nodeB.getWorldPosition(level, nodeBpos);
			conduit.getShape().contraptionNodeA = nodeA.getContraptionPosition(nodeApos);
			conduit.getShape().contraptionNodeB = nodeB.getContraptionPosition(nodeBpos);
			
		}
		
		conduit.getShape().stepPhysics(state, conduit);
		
	}
	
	public static class ConduitShape {
		public int breakingPoint;
		public Vec3d[] nodes;
		public Vec3d[] lastPos;
		public double segmentLength;
		
		// Temporary data that gets not saved
		public OptionalInt constraint = OptionalInt.empty();
		public ServerContraption contraptionA;
		public ServerContraption contraptionB;
		public Vec3d contraptionNodeA = new Vec3d();
		public Vec3d contraptionNodeB = new Vec3d();
		public Vec3d shapeNodeA = new Vec3d();
		public Vec3d shapeNodeB = new Vec3d();
		
		private ConduitShape() {}
		
		public static ConduitShape construct(List<Vec3d> nodes, double segmentLength) {
			ConduitShape shape = new ConduitShape();
			shape.nodes = nodes.toArray(Vec3d[]::new);
			shape.lastPos = nodes.toArray(Vec3d[]::new);
			shape.segmentLength = segmentLength;
			shape.breakingPoint = -1;
			return shape;
		}
		
		public static ConduitShape load(CompoundTag tag) {
			ConduitShape shape = new ConduitShape();
			shape.segmentLength = tag.getDouble("SegmentLength");
			shape.breakingPoint = tag.getInt("BreakingPoint");
			ListTag nodesTag = tag.getList("Nodes", 10);
			if (nodesTag == null) return null;
			shape.lastPos = new Vec3d[nodesTag.size()];
			shape.nodes = new Vec3d[nodesTag.size()];
			for (int i = 0; i < shape.nodes.length; i++) {
				CompoundTag nodeTag = nodesTag.getCompound(i);
				shape.lastPos[i] = NBTUtility.loadVector3d(nodeTag.getCompound("LastPos"));
				shape.nodes[i] = NBTUtility.loadVector3d(nodeTag.getCompound("Node"));
			}
			if (shape.lastPos.length < 3) return null;
			return shape;
		}
		
		public CompoundTag save() {
			CompoundTag tag = new CompoundTag();
			tag.putDouble("SegmentLength", this.segmentLength);
			tag.putInt("BreakingPoint", this.breakingPoint);
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
			this.segmentLength = buff.readDouble();
			this.breakingPoint = buff.readInt();
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
			buff.writeDouble(this.segmentLength);
			buff.writeInt(this.breakingPoint);
			buff.writeInt(this.nodes.length);
			for (int i = 0; i < this.nodes.length; i++) {
				NBTUtility.writeVector3d(this.nodes[i], buff);
				NBTUtility.writeVector3d(this.lastPos[i], buff);
			}
		}
		
		public void stepPhysics(ConduitState state, ConduitEntity conduitEntity) {
			
			Level level = conduitEntity.getLevel();
			double stiffness = state.getStiffness(conduitEntity);
			double nodeMass = state.getNodeMass(conduitEntity);
	
			Vec3d pointStart = this.shapeNodeA.copy();
			Vec3d pointEnd = this.shapeNodeB.copy();
			Vec3d origin = new Vec3d(Math.min(pointStart.x, pointEnd.x), Math.min(pointStart.y, pointEnd.y), Math.min(pointStart.z, pointEnd.z)).sub(0.5, 0.5, 0.5);
			pointStart.subI(origin);
			pointEnd.subI(origin);
			
			// integrate nodes
			for (int i = 0; i < this.nodes.length - 0; i++) {
				Vec3d temp = this.nodes[i].copy();
				this.nodes[i].addI(this.nodes[i].sub(this.lastPos[i]));
				this.lastPos[i] = temp;
			}
			
			double inverseTension = 1.0 / state.getTension(conduitEntity);
			for (int itteration = 1; itteration <= 10; itteration++) {

				// solve beams
				for (int i = 1; i < this.nodes.length; i++) {
					
					Vec3d node1 = this.nodes[i];
					Vec3d node2 = this.nodes[i - 1];
					
					// calculate spring deformation
					Vec3d delta = node1.sub(node2);
					double deltalength = delta.length();
					double diff = (float) ((deltalength - (this.segmentLength * inverseTension)) / deltalength);
					
					// the outer nodes are fixed, apply all correction force to the inner nodes
					double f1 = 0.5;
					double f2 = 0.5;
					if (i == 1) {
						f1 = 1.0;
						f2 = 0.0;
					} else if (i == this.nodes.length - 1) {
						f1 = 0.0;
						f2 = 1.0;
					}
					
					// reform spring
					node2.addI(delta.mul(diff * f2).mul(stiffness));
					node1.subI(delta.mul(diff * f1).mul(stiffness));
					
				}
				
			}

			// re-attach first and last node
			this.nodes[0].setI(pointStart);
			this.nodes[this.nodes.length - 1].setI(pointEnd);
			
			// accumulate gravity
			for (int i = 1; i < this.nodes.length - 1; i++) {
				this.nodes[i].subI(new Vec3d(GameUtility.getWorldGravity(level)).mul(nodeMass));
			}
			
			// solve collision TODO limit max velocity
			for (int i = 1; i < this.nodes.length - 1; i++) {

				Vec3d nodePos = this.nodes[i].add(Vec3f.fromVec(origin));
				BlockPos nodeBlockPos = MathUtility.toBlockPos(nodePos);
				
				if (state.collidesWithBlock(nodeBlockPos, conduitEntity)) {

					VoxelShape collisionShape = level.getBlockState(nodeBlockPos).getCollisionShape(level, nodeBlockPos);
					AABB bounds = collisionShape.bounds().move(nodeBlockPos);
					
					Vec3d surface = nodePos.copy();
					double dist = 1;
					
					// find closest point outside of the blocks bounds, which is not inside an another solid block
					for (Direction d : Direction.values()) {
						
						Vec3d surfacePoint = nodePos.copy();
						boolean collidesWithBlock = state.collidesWithBlock(nodeBlockPos.relative(d), conduitEntity);
						
						if (!collidesWithBlock) {
						
							if (d.getAxis() == Axis.X && d.getAxisDirection() == AxisDirection.POSITIVE)
								surfacePoint.x = (float) bounds.maxX;
							if (d.getAxis() == Axis.X && d.getAxisDirection() == AxisDirection.NEGATIVE)
								surfacePoint.x = (float) bounds.minX;
							if (d.getAxis() == Axis.Y && d.getAxisDirection() == AxisDirection.POSITIVE)
								surfacePoint.y = (float) bounds.maxY;
							if (d.getAxis() == Axis.Y && d.getAxisDirection() == AxisDirection.NEGATIVE)
								surfacePoint.y = (float) bounds.minY;
							if (d.getAxis() == Axis.Z && d.getAxisDirection() == AxisDirection.POSITIVE)
								surfacePoint.z = (float) bounds.maxZ;
							if (d.getAxis() == Axis.Z && d.getAxisDirection() == AxisDirection.NEGATIVE)
								surfacePoint.z = (float) bounds.minZ;

							double distance = nodePos.dist(surfacePoint);
							
							if (distance < dist) {
								dist = distance;
								surface = surfacePoint;
							}
							
						}
						
					}
					
					// if the block was completely surrounded, just move up
					if (dist == 1) {
						surface.setI(nodePos);
						surface.setY((double) bounds.maxY);
						dist = nodePos.dist(surface);
					}
					
					// move to surface
					surface.subI(Vec3f.fromVec(origin));
					this.nodes[i].setI(surface.x, surface.y, surface.z);
					
					// cancel momentum, acts as friction
					this.lastPos[i].setI(surface);
					
				}
				
			}
			
		}
		
	}
	
}
