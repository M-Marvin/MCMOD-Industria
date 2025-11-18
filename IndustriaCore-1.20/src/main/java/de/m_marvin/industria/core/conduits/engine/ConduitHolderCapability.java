package de.m_marvin.industria.core.conduits.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.network.SSyncAddedConduits;
import de.m_marvin.industria.core.conduits.engine.network.SSyncRemovedConduits;
import de.m_marvin.industria.core.conduits.events.ConduitEvent;
import de.m_marvin.industria.core.conduits.types.ConduitHitResult;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitState;
import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.types.EventStage;
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
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

/*
 * Contains the conduits in a world (dimension), used on server and client side
 */
@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ConduitHolderCapability implements ICapabilitySerializable<ListTag> {
	
	/* Capability handling */
	
	private LazyOptional<ConduitHolderCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == Capabilities.CONDUIT_HOLDER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
	private List<ConduitEntity> conduits = new ArrayList<>();
	private Level level;
	private boolean preBuildLoad;
	
	@Override
	public ListTag serializeNBT() {
		ListTag tag = new ListTag();
		for (ConduitEntity con : conduits) {
			CompoundTag conTag = con.save();
			if (conTag != null) tag.add(conTag);
		}
		IndustriaCore.LOGGER.info("Saved " + tag.size() + " conduits");
		return tag;
	}

	@Override
	public void deserializeNBT(ListTag tag) {
		this.conduits.clear();
		for (int i = 0; i < tag.size(); i++) {
			CompoundTag conTag = tag.getCompound(i);
			ConduitEntity con = ConduitEntity.load(conTag);
			if (con != null) this.conduits.add(con);
		}
		this.preBuildLoad = true;
		IndustriaCore.LOGGER.info("Loaded " + this.conduits.size() + "/" + tag.size() + " conduits");
	}
	
	public ConduitHolderCapability(Level level) {
		this.level = level;
	}
	
	/* Event handling */

	@SubscribeEvent
	public static void onClientLoadsChunk(ChunkWatchEvent.Watch event) {
		ServerLevel level = event.getLevel();
		ConduitHolderCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
		
		List<ConduitEntity> conduits = handler.getConduitsInChunk(event.getPos(), true);	
		if (conduits.size() > 0) {
			IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(() -> event.getPlayer()), new SSyncAddedConduits(conduits, event.getPos()));
			for (ConduitEntity conduitEntity : conduits) {
				Event eventPost = new ConduitEvent.ConduitWatchEvent(level, conduitEntity.getPosition(), conduitEntity, EventStage.POST, event.getPlayer());
				MinecraftForge.EVENT_BUS.post(eventPost);
			}
		}
	}
	
	@SubscribeEvent
	public static void onClientUnloadChunk(ChunkWatchEvent.UnWatch event) {
		ServerLevel level = event.getLevel();
		ConduitHolderCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
		
		List<ConduitEntity> conduits = handler.getConduitsInChunk(event.getPos(), true);	
		if (conduits.size() > 0) {
			IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(() -> event.getPlayer()), new SSyncRemovedConduits(conduits.stream().map(ConduitEntity::getPosition).toList(), event.getPos()));
			for (ConduitEntity conduitEntity : conduits) {
				Event eventPost = new ConduitEvent.ConduitUnwatchEvent(level, conduitEntity.getPosition(), conduitEntity, EventStage.POST, event.getPlayer());
				MinecraftForge.EVENT_BUS.post(eventPost);
			}
		}
	}
	
	@SubscribeEvent
	// Ticking conduits on both sides
	public static void onWorldTick(LevelTickEvent event) {
		Level level = event.level;
		ConduitHolderCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HOLDER_CAPABILITY);
		
		handler.update();
	}
	
	@SubscribeEvent
	// Pass block updates to corresponding conduits
	public static void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		Level level = (Level) event.getLevel();
		LazyOptional<ConduitHolderCapability> conduitHolder = level.getCapability(Capabilities.CONDUIT_HOLDER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			BlockPos nodePos = event.getPos();
			List<ConduitEntity> conduitEntitys = conduitHolder.resolve().get().getConduitsAtBlock(nodePos);
			for (ConduitEntity con : conduitEntitys) {
				con.getConduitState().onNodeStateChange(nodePos, event.getState(), con);
			}
		}
	}
	
	/* Conduit handling */
	
	/*
	 * Removes the conduit at the given position if a conduit exists, and triggers events for particles, sound and other stuff
	 * Also sends the changes to the clients.
	 */
	public boolean breakConduit(ConduitPos position, boolean dropItems) {
		Optional<ConduitEntity> conduitToRemove = getConduit(position);
		if (conduitToRemove.isPresent()) {
			
			Event eventPre = new ConduitEvent.ConduitBreakEvent(this.level, position, conduitToRemove.get(), EventStage.PRE, dropItems);
			MinecraftForge.EVENT_BUS.post(eventPre);
			
			if (!eventPre.isCanceled()) {
				conduitToRemove.get().getConduitState().onBreak(conduitToRemove.get(), dropItems);
				if (!removeConduit(conduitToRemove.get())) return false;
				
				Event eventPost = new ConduitEvent.ConduitBreakEvent(this.level, position, conduitToRemove.get(), EventStage.POST, dropItems);
				MinecraftForge.EVENT_BUS.post(eventPost);
				
				if (!this.level.isClientSide()) {
					
					BlockPos middle = MathUtility.getMiddleBlock(position.getNodeApos(), position.getNodeBpos());

					IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(middle)), new SSyncRemovedConduits(position, level.getChunk(middle).getPos()));
					
				}
				
				return true;
			}
			
		}
		return false;
	}
	
	/*
	 * Places a new conduit in the world if both nodes are free, and triggers events for particles, sound and other stuff.
	 * Also sends the changes to the clients.
	 */
	public boolean placeConduit(ConduitPos position, ConduitState conduitState, float length) {
		if (conduitState.isNone())
			return false;
		
		BlockPos nodeApos = position.getNodeApos();
		BlockState nodeAstate = level.getBlockState(nodeApos);
		ConduitNode nodeA = nodeAstate.getBlock() instanceof IConduitConnector ? ((IConduitConnector) nodeAstate.getBlock()).getConduitNode(level, nodeApos, nodeAstate, position.getNodeAid()) : null; //.getConnectionPoints(position.getNodeApos(), nodeAstate) : null;
		
		BlockPos nodeBpos = position.getNodeBpos();
		BlockState nodeBstate = level.getBlockState(nodeBpos);
		ConduitNode nodeB = nodeBstate.getBlock() instanceof IConduitConnector ? ((IConduitConnector) nodeBstate.getBlock()).getConduitNode(level, nodeBpos, nodeBstate, position.getNodeBid()) : null;
		
		if (nodeA == null || nodeB == null || position.getNodeApos().equals(position.getNodeBpos())) return false;
		
		ConduitEntity conduitEntity = conduitState.getConduit().newConduitEntity(position, length);
		
		if (!nodeA.getType().canConnectWith(conduitState, conduitEntity) || !nodeB.getType().canConnectWith(conduitState, conduitEntity))
			return false;
		
		int conduitsAtNodeA = getConduitsAtNode(nodeApos, position.getNodeAid()).size();
		int conduitsAtNodeB = getConduitsAtNode(nodeBpos, position.getNodeBid()).size();
		if (conduitsAtNodeA >= nodeA.getMaxConnections() && conduitsAtNodeB >= nodeB.getMaxConnections())
			return false;
		
		conduitEntity.setConduitState(conduitState);
		conduitEntity.setLevel(this.level);
		
		Event eventPre = new ConduitEvent.ConduitPlaceEvent(this.level, position, conduitEntity, EventStage.PRE);
		MinecraftForge.EVENT_BUS.post(eventPre);
		
		if (!eventPre.isCanceled()) {
			if (!addConduit(conduitEntity)) {
				return false;
			}
			conduitEntity.getConduitState().onPlace(conduitEntity);
			
			Event eventPost = new ConduitEvent.ConduitPlaceEvent(this.level, position, conduitEntity, EventStage.POST);
			MinecraftForge.EVENT_BUS.post(eventPost);
			
			if (!this.level.isClientSide()) {
				BlockPos middle = MathUtility.getMiddleBlock(nodeApos, nodeBpos);
				IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(middle)), new SSyncAddedConduits(conduitEntity, level.getChunk(middle).getPos()));
			}
			return true;
		}
		
		return false;
	}
	
	/*
	 * Removes a conduit from the world, called on server AND client side to synchronize conduits
	 * Does not automatically sync the two sides!
	 */
	public boolean removeConduit(ConduitPos position) {
		Optional<ConduitEntity> conduitEntity = getConduit(position);
		if (conduitEntity.isEmpty()) return false;
		return removeConduit(conduitEntity.get());
	}

	/*
	 * Removes a conduit from the world, called on server AND client side to synchronize conduits
	 * Does not automatically sync the two sides!
	 */
	public boolean removeConduit(ConduitEntity conduitEntity) {
		if (level.isLoaded(conduitEntity.getPosition().getNodeApos()) && level.isLoaded(conduitEntity.getPosition().getNodeBpos())) {
			if (conduits.contains(conduitEntity)) {

				Event eventPre = new ConduitEvent.ConduitRemoveEvent(level, conduitEntity.getPosition(), conduitEntity, EventStage.PRE);
				MinecraftForge.EVENT_BUS.post(eventPre);
				
				if (this.conduits.remove(conduitEntity)) {
					conduitEntity.dismantle();
					conduitEntity.setLevel(null);

					Event eventPost = new ConduitEvent.ConduitRemoveEvent(level, conduitEntity.getPosition(), conduitEntity, EventStage.POST);
					MinecraftForge.EVENT_BUS.post(eventPost);
					
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
	public boolean addConduit(ConduitEntity conduitEntity) {
		if (level.isLoaded(conduitEntity.getPosition().getNodeApos()) && level.isLoaded(conduitEntity.getPosition().getNodeBpos())) {
			Optional<ConduitEntity> existingConduit = getConduit(conduitEntity.getPosition());
			
			if (existingConduit.isPresent()) {
				if (existingConduit.get() == conduitEntity) return false;
				removeConduit(existingConduit.get());
			}
			
			Event eventPre = new ConduitEvent.ConduitAddEvent(level, conduitEntity.getPosition(), conduitEntity, EventStage.PRE);
			MinecraftForge.EVENT_BUS.post(eventPre);
			
			conduitEntity.setLevel(level);
			conduitEntity.build();
			this.conduits.add(conduitEntity);
			
			Event eventPost = new ConduitEvent.ConduitAddEvent(level, conduitEntity.getPosition(), conduitEntity, EventStage.POST);
			MinecraftForge.EVENT_BUS.post(eventPost);
			
			return true;
		}
		return false;
	}
	
	/*
	 * Get all conduits with nodes in the given chunk
	 */
	public List<ConduitEntity> getConduitsInChunk(ChunkPos chunk, boolean includeExternal) {
		List<ConduitEntity> conduits = new ArrayList<ConduitEntity>();
 		for (ConduitEntity con : this.conduits) {
 			boolean na = MathUtility.isInChunk(chunk, con.getPosition().getNodeApos());
 			boolean nb = MathUtility.isInChunk(chunk, con.getPosition().getNodeBpos());
			if (includeExternal ? na || nb : na && nb) {
				conduits.add(con);
			}
		}
 		return conduits;
	}
	
	/*
	 * Get all conduits with **both** nodes within the bounds
	 */
	public List<ConduitEntity> getConduitsInBounds(BlockPos pos1, BlockPos pos2, boolean includeExternal) {
		BlockPos min = MathUtility.getMinCorner(pos1, pos2);
		BlockPos max = MathUtility.getMaxCorner(pos1, pos2);
		List<ConduitEntity> conduits = new ArrayList<ConduitEntity>();
 		for (ConduitEntity con : this.conduits) {
 			boolean na = MathUtility.isWithinInclusive(min, max, con.getPosition().getNodeApos());
 			boolean nb = MathUtility.isWithinInclusive(min, max, con.getPosition().getNodeBpos());
			if (includeExternal ? na || nb : na && nb) {
				conduits.add(con);
			}
		}
 		return conduits;
	}
	
	/*
	 * Gets the conduit at the given position
	 */
	public Optional<ConduitEntity> getConduit(ConduitPos position) {
		for (ConduitEntity con : this.conduits) {
			if (con.getPosition().equals(position)) {
				return Optional.of(con);
			}
		}
		return Optional.empty();
	}
	
	/*
	 * Try to get the conduit that is attached to the given node
	 */
	public Optional<ConduitEntity> getConduitAtNode(BlockPos block, int node) {
		for (ConduitEntity con : this.conduits) {
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
	public List<ConduitEntity> getConduitsAtNode(BlockPos block, int node) {
		List<ConduitEntity> conduits = new ArrayList<>();
		for (ConduitEntity con : this.conduits) {
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
	public List<ConduitEntity> getConduitsAtBlock(BlockPos block) {
		List<ConduitEntity> conduits = new ArrayList<>();
		for (ConduitEntity con : this.conduits) {
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
		ConduitEntity nearestConduit = null;
		Vec3d nearestHitPoint = null;
		int nodeIndex = 0;
		
		for (ConduitEntity conduit : this.conduits) {
			double distance = Math.max(
					conduit.getPosition().calculateWorldNodeA(level).dist(Vec3d.fromVec(context.getFrom())),
					conduit.getPosition().calculateWorldNodeB(level).dist(Vec3d.fromVec(context.getFrom()))
					);
			double maxRange = conduit.getConduitState().getClampingLength(conduit) + context.getTo().subtract(context.getFrom()).length();
			
			if (distance <= maxRange && conduit.getShape() != null) {
				Vec3d nodeApos = conduit.getPosition().calculateWorldNodeA(level);
				Vec3d nodeBpos = conduit.getPosition().calculateWorldNodeB(level);
				Vec3d cornerMin = MathUtility.getMinCorner(nodeApos, nodeBpos).sub(0.5, 0.5, 0.5);
				
				for (int i = 1; i < conduit.getShape().nodes.length; i++) {
					Vec3d nodeA = conduit.getShape().nodes[i - 1].copy().add(Vec3f.fromVec(cornerMin));
					Vec3d nodeB = conduit.getShape().nodes[i].copy().add(Vec3f.fromVec(cornerMin));
					Optional<Vec3d> hitPoint = MathUtility.getHitPoint(nodeA, nodeB, Vec3d.fromVec(context.getFrom()), Vec3d.fromVec(context.getTo()), conduit.getConduitState().getThickness(conduit) / 2F);
					
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
	public List<ConduitEntity> getConduits() {
		return this.conduits;
	}
	
	/*
	 * Called every game tick to update physics and send client data
	 */
	public void update() {
		
		for (ConduitEntity conduit : this.getConduits()) {
			if (this.preBuildLoad) {

				Event eventPre = new ConduitEvent.ConduitAddEvent(level, conduit.getPosition(), conduit, EventStage.PRE);
				MinecraftForge.EVENT_BUS.post(eventPre);
				
				conduit.setLevel(this.level);
				conduit.build();

				Event eventPost = new ConduitEvent.ConduitAddEvent(level, conduit.getPosition(), conduit, EventStage.POST);
				MinecraftForge.EVENT_BUS.post(eventPost);
				
			}
			conduit.updateShape();
		}
		this.preBuildLoad = false;
		
		if (level.isClientSide()) {
			this.conduits.removeIf((conduit) -> {
				Vec3d posA = conduit.getPosition().calculateWorldNodeA(level);
				Vec3d posB = conduit.getPosition().calculateWorldNodeB(level);
				return !level.isLoaded(MathUtility.toBlockPos(posA)) && !level.isLoaded(MathUtility.toBlockPos(posB));
			});
		}
		
	}
	
}
