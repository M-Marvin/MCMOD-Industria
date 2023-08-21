package de.m_marvin.industria.core.electrics;

import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.world.level.Level;

public class ElectricUtility {
	
	public static <P> void updateNetwork(Level level, P position) {
		ElectricNetworkHandlerCapability handler = GameUtility.getCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		handler.updateNetwork(position);
	}
	
	public static Set<ElectricNetworkHandlerCapability.Component<?, ?, ?>> findComponentsOnNode(Level level, NodePos node) {
		ElectricNetworkHandlerCapability handler = GameUtility.getCapability(level,	Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.findComponentsOnNode(node);
	}
	
	public static List<String[]> getLaneLabels(Level level, NodePos node, Predicate<Component<?, ?, ?>> componentPredicate) {
		return findComponentsOnNode(level, node).stream().filter(componentPredicate).map(component -> component.getWireLanes(level, node)).toList();
	}
	
	public static void setLaneLabels(Level level, NodePos node, Predicate<Component<?, ?, ?>> componentPredicate, List<String[]> laneLabels) {
		List<Component<?, ?, ?>> cables = findComponentsOnNode(level, node).stream().filter(componentPredicate).toList();
		for (int i = 0; i < Math.min(cables.size(), laneLabels.size()); i++) {
			cables.get(i).setWireLanes(level, node, laneLabels.get(i));
		}
	}

	public static void setLaneLabels(Level level, NodePos node, Predicate<Component<?, ?, ?>> componentPredicate, String[]... laneLabels) {
		List<Component<?, ?, ?>> cables = findComponentsOnNode(level, node).stream().filter(componentPredicate).toList();
		for (int i = 0; i < Math.min(cables.size(), laneLabels.length); i++) {
			cables.get(i).setWireLanes(level, node, laneLabels[i]);
		}
	}
	
	public static double getFloatingNodeVoltage(Level level, NodePos node, String lane) {
		ElectricNetworkHandlerCapability handler = GameUtility.getCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.getFloatingNodeVoltage(node, lane);
	}
	
	
	// TODO Add all functions
	
}
