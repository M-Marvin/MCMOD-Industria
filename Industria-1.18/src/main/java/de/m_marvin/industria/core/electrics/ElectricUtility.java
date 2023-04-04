package de.m_marvin.industria.core.electrics;

import java.util.Set;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.core.registries.ModCapabilities;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ElectricUtility {

	public static void updateNetwork(Level level, BlockPos worldPosition) {
		ElectricNetworkHandlerCapability handler = GameUtility.getCapability(level, ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		handler.updateNetwork(worldPosition);
	}
	
	public static Set<ElectricNetworkHandlerCapability.Component<?, ?, ?>> findComponentsOnNode(Level level, NodePos node) {
		ElectricNetworkHandlerCapability handler = GameUtility.getCapability(level,	ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.findComponentsOnNode(node);
	}
	
	public static void notifyRewired(Level level, Object position) {
		ElectricNetworkHandlerCapability handler = GameUtility.getCapability(level, ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		ElectricNetworkHandlerCapability.Component<?, ?, ?> component = handler.getComponent(position);
		if (component != null) handler.notifyRewired(component);		
	}
	
	public static void notifyRewired(Level level, ElectricNetworkHandlerCapability.Component<?, ?, ?> component) {
		ElectricNetworkHandlerCapability handler = GameUtility.getCapability(level, ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		handler.notifyRewired(component);
	}
	
	// TODO Add all functions
	
}
