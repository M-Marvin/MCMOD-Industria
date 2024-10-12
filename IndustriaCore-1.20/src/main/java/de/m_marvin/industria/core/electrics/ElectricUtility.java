package de.m_marvin.industria.core.electrics;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.base.Predicate;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.types.CircuitTemplate.Plotter;
import de.m_marvin.industria.core.electrics.types.IElectric.ICircuitPlot;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricBlock;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.registries.Circuits;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

public class ElectricUtility {
	
	public static <P> void updateNetwork(Level level, P position) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		handler.updateNetwork(position);
	}
	
	public static Set<ElectricNetworkHandlerCapability.Component<?, ?, ?>> findComponentsOnNode(Level level, NodePos node) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.findComponentsOnNode(node);
	}

	public static Set<Component<?, ?, ?>> findComponentsInChunk(Level level, ChunkPos chunkPos) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.findComponentsInChunk(chunkPos);
	}
	
	public static Set<Component<?, ?, ?>> findComponentsConnectedWith(Level level, Component<?, ?, ?>... components) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.findComponentsConnectedWith(components);
	}
	
	public static ElectricNetwork getCircuitWithComponent(Level level, Component<?, ?, ?> component) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.getCircuitWithComponent(component);
	}
	
	public static <I, P, T> Component<I, P, T> getComponentAt(Level level, P position) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.getComponentAt(position);
	}
	
	public static boolean isInNetwork(Level level, Component<?, ?, ?> component) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.isInNetwork(component);
	}

	public static boolean isInNetwork(Level level, Object pos) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.isInNetwork(pos);
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
	
	public static Optional<Double> getFloatingNodeVoltage(Level level, NodePos node, int laneId, String lane) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.getFloatingNodeVoltage(node, laneId, lane);
	}
	
	public static Optional<Double> getVoltageBetween(Level level, NodePos nodeP, NodePos nodeN, int laneIdP, int laneIdN, String laneP, String laneN) {
		Optional<Double> v1 = ElectricUtility.getFloatingNodeVoltage(level, nodeN, laneIdN, laneN);
		Optional<Double> v2 = ElectricUtility.getFloatingNodeVoltage(level, nodeP, laneIdP, laneP);
		if (v1.isEmpty() || v2.isEmpty()) return Optional.empty();
		return Optional.of(v2.get() - v1.get());
	}
	
	public static double getPowerPercentage(double power, double targetPower) {
		return power / targetPower;
	}
	
	public static double getPowerOvershoot(double voltage, double targetVoltage) {
		return Math.max(voltage - targetVoltage, 0) / targetVoltage;
	}

	public static void plotJoinTogether(Consumer<ICircuitPlot> plotter, Level level, IElectricBlock block, BlockPos position, BlockState instance, int innerLaneId, String innerLane) {
		NodePos[] nodes = block.getConnections(level, position, instance);
		plotJoinTogether(plotter, level, block, position, instance, nodes, innerLaneId, innerLane);
	}

	public static void plotJoinTogether(Consumer<ICircuitPlot> plotter, Level level, IElectricBlock block, BlockPos position, BlockState instance, int innerLaneIdP, String innerLaneP, int innerLaneIdN, String innerLaneN) {
		NodePos[] nodes = block.getConnections(level, position, instance);
		plotJoinTogether(plotter, level, block, position, instance, nodes, innerLaneIdP, innerLaneP, innerLaneIdN, innerLaneN);
	}
	
	public static void plotJoinTogether(Consumer<ICircuitPlot> plotter, Level level, IElectricBlock block, BlockPos position, BlockState instance, NodePos[] nodes, int innerLaneIdL, String innerLaneL, int innerLaneIdN, String innerLaneN) {
		plotJoinTogether(plotter, level, block, position, instance, nodes, nodes[0], innerLaneIdL, innerLaneL, innerLaneIdN, innerLaneN);
	}

	public static void plotJoinTogether(Consumer<ICircuitPlot> plotter, Level level, IElectricBlock block, BlockPos position, BlockState instance, NodePos[] nodes, int innerLaneId, String innerLane) {
		plotJoinTogether(plotter, level, block, position, instance, nodes, nodes[0], innerLaneId, innerLane);
	}
	
	public static void plotJoinTogether(Consumer<ICircuitPlot> plotter, Level level, IElectricBlock block, BlockPos position, BlockState instance, NodePos[] nodes, NodePos jointNode, int innerLaneIdL, String innerLaneL, int innerLaneIdN, String innerLaneN) {
		plotJoinTogether(plotter, level, block, position, instance, nodes, jointNode, innerLaneIdL, innerLaneL);
		plotJoinTogether(plotter, level, block, position, instance, nodes, jointNode, innerLaneIdN, innerLaneN);
	}
	
	public static void plotJoinTogether(Consumer<ICircuitPlot> plotter, Level level, IElectricBlock block, BlockPos position, BlockState instance, NodePos[] nodes, NodePos jointNode, int innerLaneId, String innerLane) {
		List<String[]> lanes = Stream.of(nodes).map(node -> getLaneLabelsSummarized(level, node)).toList();
		
		Plotter template = CircuitTemplateManager.getInstance().getTemplate(Circuits.JUNCTION_RESISTOR).plotter();
		
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].equals(jointNode)) continue;
			String[] wireLanes = lanes.get(i);
			for (int i1 = 0; i1 < wireLanes.length; i1++) {
				if (wireLanes[i1].equals(innerLane) && (i1 != innerLaneId || nodes[i].getNode() != 0)) {
					template.setNetworkNode("NET1", nodes[i], i1, wireLanes[i1]);
					template.setNetworkNode("NET2", jointNode, innerLaneId, innerLane);
					plotter.accept(template);
				}
			}
		}
	}
	
	public static void plotConnectEquealNamed(Consumer<ICircuitPlot> plotter, Level level, IElectricBlock block, BlockPos position, BlockState instance) {
		NodePos[] nodes = block.getConnections(level, position, instance);
		List<String[]> lanes = Stream.of(nodes).map(node -> ElectricUtility.getLaneLabelsSummarized(level, node)).toList();
		
		Plotter template = CircuitTemplateManager.getInstance().getTemplate(Circuits.JUNCTION_RESISTOR).plotter();
		
		for (int i = 0; i < nodes.length; i++) {
			String[] wireLanes = lanes.get(i);
			for (int i1 = 0; i1 < wireLanes.length; i1++) {
				String wireLabel = wireLanes[i1];
				if (!wireLabel.isEmpty()) {
					template.setNetworkNode("NET1", nodes[i], i1, wireLabel);
					template.setNetworkNode("NET2", new NodePos(position, 0), 0, "junction_" + wireLabel);
					plotter.accept(template);
				}
			}
		}
	}
	
	/**
	 * Send to all tracking the ElectricNetwork in the Supplier
	 * <br/>
	 * {@link #with(Supplier)} ElectricNetwork
	 */
	public static final PacketDistributor<ElectricNetwork> TRACKING_NETWORK = new PacketDistributor<>(ElectricUtility::trackingNetwork, NetworkDirection.PLAY_TO_CLIENT);
	
	private static Consumer<Packet<?>> trackingNetwork(final PacketDistributor<ElectricNetwork> distributor, final Supplier<ElectricNetwork> networkSupplier) {
		return p -> {
			ElectricNetwork network = networkSupplier.get();
			network.getComponents().stream()
					.map(Component::pos)
					.filter(b -> b instanceof BlockPos)
					.map(pos -> network.getLevel().getChunkAt((BlockPos) pos))
					.distinct()
					.flatMap(chunk -> ((ServerChunkCache)chunk.getLevel().getChunkSource()).chunkMap.getPlayers(chunk.getPos(), false).stream())
					.distinct()
					.forEach(e -> e.connection.send(p));
		};
	}
	
}
