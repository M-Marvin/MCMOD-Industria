package de.m_marvin.industria.core.conduits.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.core.conduits.engine.ConduitEvent.ConduitBreakEvent;
import de.m_marvin.industria.core.conduits.engine.ConduitEvent.ConduitPlaceEvent;
import de.m_marvin.industria.core.conduits.engine.network.SSyncPlacedConduit;
import de.m_marvin.industria.core.conduits.engine.network.SSyncPlacedConduit.Status;
import de.m_marvin.industria.core.conduits.registry.Conduits;
import de.m_marvin.industria.core.conduits.types.ConduitHitResult;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.PlacedConduit;
import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.registries.ModCapabilities;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

/*
 * Contains the conduits in a world (dimension), used on server and client side
 */
@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ConduitHandlerCapability implements ICapabilitySerializable<ListTag> {
	
	/* Capability handling */
	
	private LazyOptional<ConduitHandlerCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ModCapabilities.CONDUIT_HANDLER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
	private List<PlacedConduit> conduits = new ArrayList<>();
	private Level level;
	private boolean preBuildLoad;
	
	@Override
	public ListTag serializeNBT() {
		ListTag tag = new ListTag();
		for (PlacedConduit con : conduits) {
			CompoundTag conTag = con.save();
			if (conTag != null) tag.add(conTag);
		}
		Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG ,"Saved " + tag.size() + " conduits");
		return tag;
	}

	@Override
	public void deserializeNBT(ListTag tag) {
		this.conduits.clear();
		for (int i = 0; i < tag.size(); i++) {
			CompoundTag conTag = tag.getCompound(i);
			PlacedConduit con = PlacedConduit.load(conTag);
			if (con != null) this.conduits.add(con);
		}
		this.preBuildLoad = true;
		Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG ,"Loaded " + this.conduits.size() + "/" + tag.size() + " conduits");
	}
	
	public ConduitHandlerCapability(Level level) {
		this.level = level;
	}
	
	/* Event handling */

	@SubscribeEvent
	// Send corresponding conduits when server detects loading of a chunk trough a client
	public static void onClientLoadsChunk(ChunkWatchEvent.Watch event) {
		ServerLevel level = event.getWorld();
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			List<PlacedConduit> conduits = conduitHolder.resolve().get().getConduitsInChunk(event.getPos());	
			if (conduits.size() > 0) {
				Industria.NETWORK.send(PacketDistributor.PLAYER.with(() -> event.getPlayer()), new SSyncPlacedConduit(conduits, event.getPos(), Status.ADDED, false, false));
			}
		}
	}
	
	@SubscribeEvent
	// Ticking conduits on both sides
	public static void onWorldTick(WorldTickEvent event) {
		Level level = event.world;
		LazyOptional<ConduitHandlerCapability> conduitCap = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitCap.isPresent()) {
			conduitCap.resolve().get().update();
		}
	}
	
	@SubscribeEvent
	// Pass block updates to corresponding conduits
	public static void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		Level level = (Level) event.getWorld();
		LazyOptional<ConduitHandlerCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			BlockPos nodePos = event.getPos();
			List<PlacedConduit> conduitStates = conduitHolder.resolve().get().getConduitsAtBlock(nodePos);
			for (PlacedConduit con : conduitStates) {
				con.getConduit().onNodeStateChange(level, nodePos, event.getState(), con);
			}
		}
	}
	
	/* Conduit handling */
	
	/*
	 * Removes the conduit at the given position if a conduit exists, and sends the changes to clients
	 */
	public boolean breakConduit(ConduitPos position, boolean dropItems) {
		if (level.isClientSide()) return false;
		
		PlacedConduit conduitToRemove = null;
		for (PlacedConduit con : this.conduits) {
			if (con.getPosition().equals(position)) {
				conduitToRemove = con;
				break;
			}
		}
		if (conduitToRemove != null) {
			
			Event event = new ConduitBreakEvent(this.level, position, conduitToRemove);
			MinecraftForge.EVENT_BUS.post(event);
			
			if (!event.isCanceled()) {
				Vec3d middlePos = MathUtility.getMiddle(position.calculateWorldNodeA(level), position.calculateWorldNodeB(level));
				Industria.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(new BlockPos(middlePos.x, middlePos.y, middlePos.z))), new SSyncPlacedConduit(conduitToRemove, new ChunkPos(new BlockPos(middlePos.x, middlePos.y, middlePos.z)), Status.REMOVED, true, dropItems));
				conduitToRemove.getConduit().onBreak(level, position, conduitToRemove, dropItems);
				if (!removeConduit(conduitToRemove)) {
					return false;
				}
				return true;
			}
			
		}
		return false;
	}
	
	/*
	 * Places a new conduit in the world if both nodes are free, and sends the changes to clients
	 */
	public boolean placeConduit(ConduitPos position, Conduit conduit, double length) {
		if (level.isClientSide()) return false;
		
		if (conduit == Conduits.NONE.get()) {
			return false;
		}
		
		BlockPos nodeApos = position.getNodeApos();
		BlockState nodeAstate = level.getBlockState(nodeApos);
		ConduitNode nodeA = nodeAstate.getBlock() instanceof IConduitConnector ? ((IConduitConnector) nodeAstate.getBlock()).getConduitNode(level, nodeApos, nodeAstate, position.getNodeAid()) : null; //.getConnectionPoints(position.getNodeApos(), nodeAstate) : null;
		
		BlockPos nodeBpos = position.getNodeBpos();
		BlockState nodeBstate = level.getBlockState(nodeBpos);
		ConduitNode nodeB = nodeBstate.getBlock() instanceof IConduitConnector ? ((IConduitConnector) nodeBstate.getBlock()).getConduitNode(level, nodeBpos, nodeBstate, position.getNodeBid()) : null;
		
		if (nodeA == null || nodeB == null || position.getNodeApos().equals(position.getNodeBpos())) return false;
		
		if (!nodeA.getType().isValid(conduit) || !nodeB.getType().isValid(conduit)) {
			return false;
		}
		
		int conduitsAtNodeA = getConduitsAtNode(nodeApos, position.getNodeAid()).size();
		int conduitsAtNodeB = getConduitsAtNode(nodeBpos, position.getNodeBid()).size();
		
		if (conduitsAtNodeA >= nodeA.getMaxConnections() && conduitsAtNodeB >= nodeB.getMaxConnections()) {
			return false;
		}
		
		PlacedConduit conduitState = new PlacedConduit(position, conduit, length);
		
		Event event = new ConduitPlaceEvent(this.level, position, conduitState);
		MinecraftForge.EVENT_BUS.post(event);
		
		if (!event.isCanceled()) {
			if (!addConduit(conduitState)) {
				return false;
			}
			conduitState.getConduit().onPlace(level, position, conduitState);
			Vec3d middlePos = MathUtility.getMiddle(position.calculateWorldNodeA(level), position.calculateWorldNodeB(level));
			Industria.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(new BlockPos(middlePos.x, middlePos.y, middlePos.z))), new SSyncPlacedConduit(conduitState, new ChunkPos(new BlockPos(middlePos.x, middlePos.y, middlePos.z)), Status.ADDED, true, false));
			return true;
		}
		
		return false;
	}
	
	/*
	 * Removes a conduit from the world, called on server AND client side to synchronize conduits
	 * Does not automatically sync the two sides!
	 */
	public boolean removeConduit(PlacedConduit conduitState) {
		if (level.isLoaded(conduitState.getPosition().getNodeApos()) && level.isLoaded(conduitState.getPosition().getNodeBpos())) {
			if (conduits.contains(conduitState)) {
				if (this.conduits.remove(conduitState)) {
					conduitState.dismantle(level);
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * Adds a conduit to the world, called on server AND client side to synchronize conduits
	 * Does not automatically sync the two sides!
	 */
	public boolean addConduit(PlacedConduit conduitState) {
		if (level.isLoaded(conduitState.getPosition().getNodeApos()) && level.isLoaded(conduitState.getPosition().getNodeBpos())) {
			
			if (conduits.contains(conduitState)) {
				System.err.println("THIS MAKES NO SENSE!!!!!");
				conduits.remove(conduitState);
			}
			
			if (!conduits.contains(conduitState)) {
				conduitState.build(level);
				this.conduits.add(conduitState);
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Get all conduits with nodes in the given chunk
	 */
	public List<PlacedConduit> getConduitsInChunk(ChunkPos chunk) {
		List<PlacedConduit> conduits = new ArrayList<PlacedConduit>();
 		for (PlacedConduit con : this.conduits) {
			if (MathUtility.isInChunk(chunk, con.getPosition().getNodeApos()) || MathUtility.isInChunk(chunk, con.getPosition().getNodeBpos())) {
				conduits.add(con);
			}
		}
 		return conduits;
	}
	
	/*
	 * Gets the conduit at the given position
	 */
	public Optional<PlacedConduit> getConduit(ConduitPos position) {
		for (PlacedConduit con : this.conduits) {
			if (con.getPosition().equals(position)) {
				return Optional.of(con);
			}
		}
		return Optional.empty();
	}
	
	/*
	 * Try to get the conduit that is attached to the given node
	 */
	public Optional<PlacedConduit> getConduitAtNode(BlockPos block, int node) {
		for (PlacedConduit con : this.conduits) {
			if (	(con.getPosition().getNodeApos().equals(block) && con.getPosition().getNodeAid() == node) ||
					(con.getPosition().getNodeBpos().equals(block) && con.getPosition().getNodeBid() == node)) {
				return Optional.of(con);
			}
		}
		return Optional.empty();
	}

	/*
	 * Try to get all conduits connected with the given node
	 */
	public List<PlacedConduit> getConduitsAtNode(BlockPos block, int node) {
		List<PlacedConduit> conduits = new ArrayList<>();
		for (PlacedConduit con : this.conduits) {
			if (	(con.getPosition().getNodeApos().equals(block) && con.getPosition().getNodeAid() == node) ||
					(con.getPosition().getNodeBpos().equals(block) && con.getPosition().getNodeBid() == node)) {
				conduits.add(con);
			}
		}
		return conduits;
	}
	
	/*
	 * Try to get all conduits connected with the given block
	 */
	public List<PlacedConduit> getConduitsAtBlock(BlockPos block) {
		List<PlacedConduit> conduits = new ArrayList<>();
		for (PlacedConduit con : this.conduits) {
			if (con.getPosition().getNodeApos().equals(block) || con.getPosition().getNodeBpos().equals(block)) {
				conduits.add(con);
			}
		}
		return conduits;
	}
	
	/*
	 * Runs a raytrace to determine the first conduit on this ray
	 */
	public ConduitHitResult clipConduits(ClipContext context) {
		
		double distanceToOriging = context.getFrom().distanceTo(context.getTo());
		PlacedConduit nearestConduit = null;
		Vec3d nearestHitPoint = null;
		int nodeIndex = 0;
		
		for (PlacedConduit conduit : this.conduits) {
			double distance = Math.max(
					conduit.getPosition().calculateWorldNodeA(level).dist(Vec3d.fromVec(context.getFrom())),
					conduit.getPosition().calculateWorldNodeB(level).dist(Vec3d.fromVec(context.getFrom()))
					);
			double maxRange = conduit.getConduit().getConduitType().getClampingLength() + context.getTo().subtract(context.getFrom()).length();
			
			if (distance <= maxRange && conduit.getShape() != null) {
				Vec3d nodeApos = conduit.getPosition().calculateWorldNodeA(level);
				Vec3d nodeBpos = conduit.getPosition().calculateWorldNodeB(level);
				Vec3d cornerMin = MathUtility.getMinCorner(nodeApos, nodeBpos).sub(0.5, 0.5, 0.5);
				
				for (int i = 1; i < conduit.getShape().nodes.length; i++) {
					Vec3d nodeA = conduit.getShape().nodes[i - 1].copy().add(Vec3f.fromVec(cornerMin));
					Vec3d nodeB = conduit.getShape().nodes[i].copy().add(Vec3f.fromVec(cornerMin));
					Optional<Vec3d> hitPoint = MathUtility.getHitPoint(nodeA, nodeB, Vec3d.fromVec(context.getFrom()), Vec3d.fromVec(context.getTo()), conduit.getConduit().getConduitType().getThickness() / 32F);
					
					if (hitPoint.isPresent()) {
						double conduitDistance = Vec3f.fromVec(context.getFrom()).dist(hitPoint.get());
						if (conduitDistance < distanceToOriging) {
							distanceToOriging = conduitDistance;
							nearestConduit = conduit;
							nearestHitPoint = hitPoint.get();
							nodeIndex = i;
							break;
						}
					}
				}
			}
		}
		if (nearestConduit != null) return ConduitHitResult.hit(nearestConduit, nearestHitPoint, nodeIndex - 1, nodeIndex);
		
		return ConduitHitResult.miss();
	}
	
	/*
	 * Get all conduits currently aviable on the current side
	 */
	public List<PlacedConduit> getConduits() {
		return this.conduits;
	}
	
	/*
	 * Called every game tick to update physics and send client data
	 */
	public void update() {
		
		for (PlacedConduit conduit : this.getConduits()) {
			if (this.preBuildLoad) conduit.build(level);
			conduit.updateShape(level);
		}
		this.preBuildLoad = false;
		
		if (level.isClientSide()) {
			this.conduits.removeIf((conduit) -> {
				Vec3d posA = conduit.getPosition().calculateWorldNodeA(level);
				Vec3d posB = conduit.getPosition().calculateWorldNodeB(level);
				return !level.isLoaded(new BlockPos(posA.x, posA.y, posA.z)) && !level.isLoaded(new BlockPos(posB.x, posB.y, posB.z));
			});
		}
		
	}
	
}
