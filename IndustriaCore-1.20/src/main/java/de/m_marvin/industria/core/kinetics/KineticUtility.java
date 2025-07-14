package de.m_marvin.industria.core.kinetics;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

import de.m_marvin.industria.core.kinetics.engine.KineticNetwork;
import de.m_marvin.industria.core.kinetics.engine.KineticNetworkSpaceCapability;
import de.m_marvin.industria.core.kinetics.engine.KineticNetworkSpaceCapability.KineticComponent;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.KineticReference;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.ufns.SynchronizedFunctionalNetworkSpace.UpdateType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

public class KineticUtility {
	
	private KineticUtility() {}
	
	/**
	 * Recalculates the network at the reference and triggers updates for its components, does not cause the network to be rebuild
	 */
	public static void updateNetworks(Level level, KineticReference reference) {
		KineticNetworkSpaceCapability handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_NETWORK_SPACE_CAPABILITY);
		handler.updateTicket(reference, UpdateType.NETWORK_UPDATE);
	}
	
	/**
	 * Returns all networks with components at the position
	 */
	public static Collection<KineticNetwork> findNetworkAt(Level level, BlockPos position) {
		KineticNetworkSpaceCapability handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_NETWORK_SPACE_CAPABILITY);
		return handler.findNetworksAt(position);
	}
	
	/**
	 * Returns the network with an component at the reference
	 */
	public static KineticNetwork findNetworkAt(Level level, KineticReference reference) {
		KineticNetworkSpaceCapability handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_NETWORK_SPACE_CAPABILITY);
		return handler.findNetworkAt(reference);
	}
	
	/**
	 * Returns all components at the position 
	 */
	public static Collection<KineticComponent> findComponentsAt(Level level, BlockPos position) {
		KineticNetworkSpaceCapability handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_NETWORK_SPACE_CAPABILITY);
		return handler.findComponentsAt(position);
	}
	
	/**
	 * Returns the component at the reference
	 */
	public static KineticComponent findComponentAt(Level level, KineticReference reference) {
		KineticNetworkSpaceCapability handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_NETWORK_SPACE_CAPABILITY);
		return handler.findComponentAt(reference);
	}
	
	/**
	 * Returns all components within the chunk
	 */
	public static Collection<KineticComponent> findComponentsInChunk(Level level, ChunkPos chunk) {
		KineticNetworkSpaceCapability handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_NETWORK_SPACE_CAPABILITY);
		return handler.findComponentsInChunk(chunk);
	}

	/**
	 * Send to all tracking the KineticNetwork in the Supplier {@link #with(Supplier)}
	 */
	public static final PacketDistributor<KineticNetwork> TRACKING_NETWORK = new PacketDistributor<>(KineticUtility::trackingNetwork, NetworkDirection.PLAY_TO_CLIENT);
	
	private static Consumer<Packet<?>> trackingNetwork(final PacketDistributor<KineticNetwork> distributor, final Supplier<KineticNetwork> networkSupplier) {
		return p -> {
			KineticNetwork network = networkSupplier.get();
			network.listComponents().stream()
				.map(c -> new ChunkPos(c.reference().pos()))
				.distinct()
				.flatMap(chunk -> ((ServerChunkCache) network.getLevel().getChunkSource()).chunkMap.getPlayers(chunk, false).stream())
				.distinct()
				.forEach(e -> e.connection.send(p));
		};
	}

}
