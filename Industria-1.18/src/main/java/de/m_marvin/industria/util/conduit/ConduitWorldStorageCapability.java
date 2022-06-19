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
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

/*
 * Contains the conduits in a world (dimension), used on seber and client side
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
	
	private List<PlacedConduit> conduits = new ArrayList<>();
	
	@Override
	public ListTag serializeNBT() {
		ListTag tag = new ListTag();
		for (PlacedConduit con : conduits) {
			CompoundTag conTag = con.save();
			if (conTag != null) tag.add(conTag);
		}
		return tag;
	}

	@Override
	public void deserializeNBT(ListTag tag) {
		//this.conduits.clear();
		for (int i = 0; i < tag.size(); i++) {
			CompoundTag conTag = tag.getCompound(i);
			PlacedConduit con = PlacedConduit.load(conTag);
			// FIXME if (con != null) addConduit(con);
		}
	}
	
	/* Event handling */

	@SubscribeEvent
	public static void onClientLoadsChunk(ChunkWatchEvent.Watch event) {
		ServerLevel level = event.getWorld();
		LazyOptional<ConduitWorldStorageCapability> conduitHolder = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
		if (conduitHolder.isPresent()) {
			List<PlacedConduit> conduits = conduitHolder.resolve().get().getConduitsInChunk(event.getPos());
			
			if (conduits.size() > 0) {
				Industria.NETWORK.send(PacketDistributor.PLAYER.with(() -> event.getPlayer()), new SSyncPlacedConduit(conduits, event.getPos()));
			}
		}
		
	}
	
	@SubscribeEvent
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
	public static void onWorldTick(WorldTickEvent event) {
		
		Level level = event.world;
		
		LazyOptional<ConduitWorldStorageCapability> conduitCap = level.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
		if (conduitCap.isPresent()) {
			conduitCap.resolve().get().update(level);
		}
		
	}
	
	/* Conduit handling */
	
	/*
	 * Places a new conduit in the world if both nodes are free, and sends the changes to clients
	 */
	public boolean placeConduit(Level level, ConduitPos position, Conduit conduit, int nodesPerBlock) {
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
			PlacedConduit conduitState = new PlacedConduit(nodeA.position(), nodeA.connectionId(), nodeB.position(), nodeB.connectionId(), conduit, nodesPerBlock);
			addConduit(level, conduitState);
			if (!level.isClientSide()) {
				Industria.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(nodeA.position())), new SSyncPlacedConduit(conduitState, new ChunkPos(nodeA.position())));
				Industria.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(nodeB.position())), new SSyncPlacedConduit(conduitState, new ChunkPos(nodeB.position())));
			}
			return true;
		}
	}
	
	/*
	 * Adds a conduit to the world, called on server AND client side to synchronize conduits
	 */
	public void addConduit(Level level, PlacedConduit conduitState) {
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
	public Optional<PlacedConduit> getConduit(Level level, ConduitPos position) {
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
			if (con.getNodeA().equals(node.position())) {
				return Optional.of(con);
			}
		}
		return Optional.empty();
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
			conduit.updateShape(level);
		}
	}
		
}
