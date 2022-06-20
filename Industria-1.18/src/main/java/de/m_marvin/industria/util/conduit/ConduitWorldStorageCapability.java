package de.m_marvin.industria.util.conduit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.network.SSyncPlacedConduit;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.block.IConduitConnector;
import de.m_marvin.industria.util.block.IConduitConnector.ConnectionPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

/*
 * Contains the conduits in a world (dimension), used on server and client side
 */
@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ConduitWorldStorageCapability implements ICapabilitySerializable<ListTag> {
	
	/* Capability handling */
	
	private LazyOptional<ConduitWorldStorageCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ModCapabilities.CONDUIT_HOLDER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
	private boolean preBuildLoad = false;
	private List<PlacedConduit> conduits = new ArrayList<>();
	private Level level;
	
	@Override
	public ListTag serializeNBT() {
		ListTag tag = new ListTag();
		for (PlacedConduit con : conduits) {
			CompoundTag conTag = con.save();
			if (conTag != null) tag.add(conTag);
		}
		Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG ,"Saved " + tag.size() + " Conduits");
		return tag;
	}

	@Override
	public void deserializeNBT(ListTag tag) {
		this.conduits.clear();
		this.preBuildLoad = true;
		for (int i = 0; i < tag.size(); i++) {
			CompoundTag conTag = tag.getCompound(i);
			PlacedConduit con = PlacedConduit.load(conTag);
			if (con != null) this.conduits.add(con);
		}
		Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG ,"Loaded " + this.conduits.size() + "/" + tag.size() + "  Conduits");
	}
	
	public ConduitWorldStorageCapability(Level level) {
		this.level = level;
	}
	
	/* Event handling */

	@SubscribeEvent
	// Send corresponding conduits when server detects loading of a chunk on client side
	public static void onClientLoadsChunk(ChunkWatchEvent.Watch event) {
		ServerLevel level = event.getWorld();
		LazyOptional<ConduitWorldStorageCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			List<PlacedConduit> conduits = conduitHolder.resolve().get().getConduitsInChunk(event.getPos());
			
			if (conduits.size() > 0) {
				Industria.NETWORK.send(PacketDistributor.PLAYER.with(() -> event.getPlayer()), new SSyncPlacedConduit(conduits, event.getPos(), false));
			}
		}
		
	}

	@SuppressWarnings("resource")
	@SubscribeEvent
	// Clear conduits on client side if chunk gets unloaded (out of render distance)
	public static void onClientUnloadsChunk(ChunkEvent.Unload event) {
		if (event.getWorld().isClientSide()) {
			ClientLevel level = Minecraft.getInstance().level;
			LazyOptional<ConduitWorldStorageCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
			if (conduitHolder.isPresent()) {
				List<PlacedConduit> loadedConduits = conduitHolder.resolve().get().getConduitsInChunk(event.getChunk().getPos());
				
				if (loadedConduits.size() > 0) {
					conduitHolder.resolve().get().getConduits().removeAll(loadedConduits);
				}
			}
		}
	}
	
	@SubscribeEvent
	// Ticking conduits on client side
	public static void onClientWorldTick(ClientTickEvent event) {
		@SuppressWarnings("resource")
		ClientLevel level = Minecraft.getInstance().level;
		if (level != null) {
			LazyOptional<ConduitWorldStorageCapability> conduitCap = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
			if (conduitCap.isPresent()) {
				conduitCap.resolve().get().update(level);
			}
		}
	}
	
	@SubscribeEvent
	// Ticking conduits on server side
	public static void onWorldTick(WorldTickEvent event) {
		ServerLevel level = (ServerLevel) event.world;
		LazyOptional<ConduitWorldStorageCapability> conduitCap = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
		if (conduitCap.isPresent()) {
			conduitCap.resolve().get().update(level);
		}
	}
	
	@SubscribeEvent
	// Pass block updates to corresponding conduits
	public static void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		ServerLevel level = (ServerLevel) event.getWorld();
		LazyOptional<ConduitWorldStorageCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
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
	 * Removes the conduit at the given position if a conduit exists
	 */
	public boolean breakConduit(ConduitPos position) {
		PlacedConduit conduitToRemove = null;
		for (PlacedConduit con : this.conduits) {
			if (con.getConduitPosition().equals(position)) {
				conduitToRemove = con;
				break;
			}
		}
		if (conduitToRemove != null) {
			removeConduit(conduitToRemove);
			if (!level.isClientSide()) {
				Industria.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(position.getNodeApos())), new SSyncPlacedConduit(conduitToRemove, new ChunkPos(position.getNodeApos()), true));
				Industria.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(position.getNodeBpos())), new SSyncPlacedConduit(conduitToRemove, new ChunkPos(position.getNodeBpos()), true));
			}
			return true;
		}
		return false;
	}
	
	/*
	 * Places a new conduit in the world if both nodes are free, and sends the changes to clients
	 */
	public boolean placeConduit(ConduitPos position, Conduit conduit, int nodesPerBlock) {
		BlockState nodeAstate = level.getBlockState(position.getNodeApos());
		ConnectionPoint[] nodesA = nodeAstate.getBlock() instanceof IConduitConnector ? ((IConduitConnector) nodeAstate.getBlock()).getConnectionPoints(level, position.getNodeApos(), nodeAstate) : null;
		ConnectionPoint nodeA = nodesA != null && nodesA.length > position.getNodeAid() ? nodesA[position.getNodeAid()] : null;
		BlockState nodeBstate = level.getBlockState(position.getNodeBpos());
		ConnectionPoint[] nodesB = nodeBstate.getBlock() instanceof IConduitConnector ? ((IConduitConnector) nodeBstate.getBlock()).getConnectionPoints(level, position.getNodeBpos(), nodeBstate) : null;
		ConnectionPoint nodeB = nodesB != null && nodesB.length > position.getNodeBid() ? nodesB[position.getNodeBid()] : null;
		
		if (nodeA == null || nodeB == null || position.getNodeBpos().equals(position.getNodeApos())) return false;
		
		if (getConduitAtNode(nodeA).isPresent() || getConduitAtNode(nodeB).isPresent()) {
			return false;
		} else {
			PlacedConduit conduitState = new PlacedConduit(position, conduit, nodesPerBlock);
			addConduit(conduitState);
			if (!level.isClientSide()) {
				Industria.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(nodeA.position())), new SSyncPlacedConduit(conduitState, new ChunkPos(nodeA.position()), false));
				Industria.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(nodeB.position())), new SSyncPlacedConduit(conduitState, new ChunkPos(nodeB.position()), false));
			}
			return true;
		}
	}
	
	/*
	 * Removes a conduit from the world, called on server AND client side to synchronize conduits
	 */
	public void removeConduit(PlacedConduit conduitState) {
		if (level.isLoaded(conduitState.getNodeA()) && level.isLoaded(conduitState.getNodeB())) {
			if (conduits.contains(conduitState)) {
				this.conduits.remove(conduitState);
				conduitState.setShape(null);
			}
		}
	}
	
	/*
	 * Adds a conduit to the world, called on server AND client side to synchronize conduits
	 */
	public void addConduit(PlacedConduit conduitState) {
		if (level.isLoaded(conduitState.getNodeA()) && level.isLoaded(conduitState.getNodeB())) {
			if (!conduits.contains(conduitState)) {
				conduitState.build(level);
				this.conduits.add(conduitState);
			}
		}
	}
	
	/*
	 * Get all conduits with nodes in the given chunk
	 */
	public List<PlacedConduit> getConduitsInChunk(ChunkPos chunk) {
		List<PlacedConduit> conduits = new ArrayList<PlacedConduit>();
 		for (PlacedConduit con : this.conduits) {
			if (UtilityHelper.isInChunk(chunk, con.getNodeA()) || UtilityHelper.isInChunk(chunk, con.getNodeB())) {
				conduits.add(con);
			}
		}
 		return conduits;
	}
	
	/*
	 * Gets the conduit at the given position
	 */
	public Optional<PlacedConduit> getConduit(ConduitPos position) {
		BlockState nodeAstate = level.getBlockState(position.getNodeApos());
		ConnectionPoint[] nodesA = nodeAstate instanceof IConduitConnector ? ((IConduitConnector) nodeAstate).getConnectionPoints(level, position.getNodeApos(), nodeAstate) : null;
		ConnectionPoint nodeA = nodesA != null && nodesA.length > position.getNodeAid() ? nodesA[position.getNodeAid()] : null;
		BlockState nodeBstate = level.getBlockState(position.getNodeBpos());
		ConnectionPoint[] nodesB = nodeBstate instanceof IConduitConnector ? ((IConduitConnector) nodeBstate).getConnectionPoints(level, position.getNodeBpos(), nodeBstate) : null;
		ConnectionPoint nodeB = nodesB != null && nodesB.length > position.getNodeAid() ? nodesA[position.getNodeAid()] : null;
		
		Optional<PlacedConduit> conduitA = getConduitAtNode(nodeA);
		Optional<PlacedConduit> conduitB = getConduitAtNode(nodeB);
		
		if (conduitA.isPresent() && conduitB.isPresent() && conduitA.get() == conduitB.get()) {
			return conduitA;
		}
		
		return Optional.empty();
	}
	
	/*
	 * Try to get the conduit that is attached to the given node
	 */
	public Optional<PlacedConduit> getConduitAtNode(ConnectionPoint node) {
		for (PlacedConduit con : this.conduits) {
			if (	(con.getNodeA().equals(node.position()) && con.getConnectionPointA() == node.connectionId()) ||
					(con.getNodeB().equals(node.position()) && con.getConnectionPointB() == node.connectionId())) {
				return Optional.of(con);
			}
		}
		return Optional.empty();
	}
	
	/*
	 * Try to get all conduits connected with the given block
	 */
	public List<PlacedConduit> getConduitsAtBlock(BlockPos blockPos) {
		List<PlacedConduit> conduits = new ArrayList<>();
		for (PlacedConduit con : this.conduits) {
			if (con.getNodeA().equals(blockPos) || con.getNodeB().equals(blockPos)) {
				conduits.add(con);
			}
		}
		return conduits;
	}
	
	/*
	 * Get all conduits currently aviable on the current side
	 */
	public List<PlacedConduit> getConduits() {
		return this.conduits;
	}
	
	/*
	 * Called every game tick to update physics
	 */
	public void update(BlockGetter level) {
		for (PlacedConduit conduit : this.getConduits()) {
			if (this.preBuildLoad) {
				conduit.build(this.level);
			} else {
				conduit.updateShape(level); // TODO server-conduit-physic config
			}
		}
		if (this.preBuildLoad) this.preBuildLoad = false;
	}
		
}
