package de.m_marvin.industria.core.electrics;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.google.common.base.Predicate;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.circuits.Circuits;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.types.IElectric.ICircuitPlot;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricConnector;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ElectricUtility {
	
	public static <P> void updateNetwork(Level level, P position) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		handler.updateNetwork(position);
	}
	
	public static Set<ElectricNetworkHandlerCapability.Component<?, ?, ?>> findComponentsOnNode(Level level, NodePos node) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level,	Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.findComponentsOnNode(node);
	}

	public static String[] getLaneLabelsSummarized(Level level, NodePos node) {
		List<String[]> laneLabels = getLaneLabels(level, node, Component::isWire);
		int laneCount = laneLabels.stream().mapToInt(l -> l.length).max().orElseGet(() -> 0);
		String[] lanes = new String[laneCount];
		
		for (int i = 0; i < laneCount; i++) {
			for (String[] le : laneLabels) {
				if (le.length > i) {
					if (lanes[i] == null || lanes[i].isEmpty()) {
						lanes[i] = le[i];
					} else if (!lanes[i].equals(le[i])) {
						lanes[i] = "?";
					}
				}
			}
		}
		
		return lanes;
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

	public static void setLaneLabelsEqueal(Level level, NodePos node, Predicate<Component<?, ?, ?>> componentPredicate, String[] laneLabels) {
		List<Component<?, ?, ?>> cables = findComponentsOnNode(level, node).stream().filter(componentPredicate).toList();
		for (int i = 0; i < cables.size(); i++) {
			cables.get(i).setWireLanes(level, node, laneLabels);
		}
	}
	
	public static double getFloatingNodeVoltage(Level level, NodePos node, int laneId, String lane) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.getFloatingNodeVoltage(node, laneId, lane);
	}
	
	public static double getVoltageBetween(Level level, NodePos nodeP, NodePos nodeN, int laneIdP, int laneIdN, String laneP, String laneN) {
		double v1 = ElectricUtility.getFloatingNodeVoltage(level, nodeN, laneIdN, laneN);
		double v2 = ElectricUtility.getFloatingNodeVoltage(level, nodeP, laneIdP, laneP);
		return v2 - v1;
	}
	
	public static double getPowerPercentage(double power, double targetPower) {
		return power / targetPower;
	}
	
	public static double getPowerOvershoot(double voltage, double targetVoltage) {
		return Math.max(voltage - targetVoltage, 0) / targetVoltage;
	}
	
	public static void plotJoinTogether(Consumer<ICircuitPlot> plotter, Level level, IElectricConnector block, BlockPos position, BlockState instance, int innerLaneIdP, String innerLaneP, int innerLaneIdN, String innerLaneN) {
		NodePos[] nodes = block.getConnections(level, position, instance);
		List<String[]> lanes = Stream.of(nodes).map(node -> getLaneLabelsSummarized(level, node)).toList();
		
		CircuitTemplate template = CircuitTemplateManager.getInstance().getTemplate(Circuits.JUNCTION_RESISTOR);
		
		for (int i = 0; i < nodes.length; i++) {
			String[] wireLanes = lanes.get(i);
			for (int i1 = 0; i1 < wireLanes.length; i1++) {
				if (wireLanes[i1].equals(innerLaneP) && (i1 != innerLaneIdP || nodes[i].getNode() != 0)) {
					template.setNetworkNode("NET1", nodes[i], i1, wireLanes[i1]);
					template.setNetworkNode("NET2", new NodePos(position, 0), innerLaneIdP, innerLaneP);
					plotter.accept(template);
				} else if (wireLanes[i1].equals(innerLaneN) && (i1 != innerLaneIdN || nodes[i].getNode() != 0)) {
					template.setNetworkNode("NET1", nodes[i], i1, wireLanes[i1]);
					template.setNetworkNode("NET2", new NodePos(position, 0), innerLaneIdN, innerLaneN);
					plotter.accept(template);
				}
			}
		}
	}
	
}
