package de.m_marvin.industria.core.kinetics;

import java.util.Collection;

import de.m_marvin.industria.core.kinetics.engine.KineticHandlerCapabillity;
import de.m_marvin.industria.core.kinetics.engine.KineticHandlerCapabillity.KineticComponent;
import de.m_marvin.industria.core.kinetics.engine.KineticNetwork;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.KineticReference;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class KineticUtility {
	
	private KineticUtility() {}
	
//	/**
//	 * Triggers an update for each network at the position
//	 */
//	public static Collection<KineticNetwork> updateNetworks(Level level, BlockPos position) {
//		KineticHandlerCapabillity handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_HANDLER_CAPABILITY);
//		return handler.updateNetworks(position);
//	}
//	
//	/**
//	 * Triggers an update for the network at the reference
//	 */
//	public static Collection<KineticNetwork> updateNetwork(Level level, KineticReference reference) {
//		KineticHandlerCapabillity handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_HANDLER_CAPABILITY);
//		return handler.updateNetwork(reference);
//	}
//	
//	/**
//	 * Recalculates the network at the reference and triggers updates for its components, does not cause the network to be rebuild
//	 */
//	public static void recalculateNetwork(Level level, KineticReference reference) {
//		KineticHandlerCapabillity handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_HANDLER_CAPABILITY);
//		handler.recalculateNetwork(handler.getNetworkAt(reference));
//	}
//	
//	/**
//	 * Returns all networks with components at the position
//	 */
//	public static Collection<KineticNetwork> findNetworkAt(Level level, BlockPos position) {
//		KineticHandlerCapabillity handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_HANDLER_CAPABILITY);
//		return handler.getNetworksAt(position);
//	}
//	
//	/**
//	 * Returns the network with an component at the reference
//	 */
//	public static KineticNetwork findNetworkAt(Level level, KineticReference reference) {
//		KineticHandlerCapabillity handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_HANDLER_CAPABILITY);
//		return handler.getNetworkAt(reference);
//	}
//	
//	/**
//	 * Returns all components at the position 
//	 */
//	public static Collection<KineticComponent> findComponentsAt(Level level, BlockPos position) {
//		KineticHandlerCapabillity handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_HANDLER_CAPABILITY);
//		return handler.findComponentsAt(position);
//	}
//	
//	/**
//	 * Returns the component at the reference
//	 */
//	public static KineticComponent findComponentAt(Level level, KineticReference reference) {
//		KineticHandlerCapabillity handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_HANDLER_CAPABILITY);
//		return handler.findComponentAt(reference);
//	}
//	
//	/**
//	 * Returns all components within the chunk
//	 */
//	public static Collection<KineticComponent> findComponentsInChunk(Level level, ChunkPos chunk) {
//		KineticHandlerCapabillity handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_HANDLER_CAPABILITY);
//		return handler.findComponentsInChunk(chunk);
//	}
	
}
