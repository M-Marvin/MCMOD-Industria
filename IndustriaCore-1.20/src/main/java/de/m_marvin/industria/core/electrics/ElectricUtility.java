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
	
	/*
	 * Returns all components connected with the given node
	 */
	public static Set<ElectricNetworkHandlerCapability.Component<?, ?, ?>> findComponentsOnNode(Level level, NodePos node) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.findComponentsOnNode(node);
	}

	/*
	 * Returns all components located in the given chunk
	 */
	public static Set<Component<?, ?, ?>> findComponentsInChunk(Level level, ChunkPos chunkPos) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.findComponentsInChunk(chunkPos);
	}
	
	/*
	 * List all components connected with the given components
	 */
	public static Set<Component<?, ?, ?>> findComponentsConnectedWith(Level level, Component<?, ?, ?>... components) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.findComponentsConnectedWith(components);
	}
	
	/*
	 * Returns the network for the given component
	 */
	public static ElectricNetwork getCircuitWithComponent(Level level, Component<?, ?, ?> component) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.getCircuitWithComponent(component);
	}
	
	/*
	 * Searches for a component at the given position
	 */
	public static <I, P, T> Component<I, P, T> getComponentAt(Level level, P position) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.getComponentAt(position);
	}
	
	/**
	 * Checks if the component is registered as electric component and part of a valid network
	 */
	public static boolean isInNetwork(Level level, Component<?, ?, ?> component) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.isInNetwork(component);
	}

	/**
	 * Checks if the component is registered as electric component and part of a valid network
	 */
	public static boolean isInNetwork(Level level, Object pos) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.isInNetwork(pos);
	}
	
	/**
	 * Summarizes the lanes of all components connected to the node.
	 * The length of the array equals the number of lanes of the component with the most lanes.
	 * Lane id 0 of all nodes aligns with element 0 in the array returned by this function.
	 * If lane N has equal names on all components, this name will be put into the Nth element of the array.
	 * If a lane has different names on the components, an "?" is placed in the array.
	 * @param level Level of the node
	 * @param node The node to search for components on
	 * @return A summarized list of the lane nodes of all components
	 */
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

	/**
	 * Collects a list (one entry per component connected to the node) of lists of lane labels of the components connected to the given node.
	 * @param level Level of the node
	 * @param node The node to search for components on
	 * @param componentPredicate A predicate for the components to look for
	 * @return A list of all lane names, sorted per component
	 */
	public static List<String[]> getLaneLabels(Level level, NodePos node, Predicate<Component<?, ?, ?>> componentPredicate) {
		return findComponentsOnNode(level, node).stream().filter(componentPredicate).map(component -> component.getWireLanes(level, node)).toList();
	}
	
	/**
	 * Sets the lanes of the components connected to the node and matching the predicate to the supplied values.
	 * Gives a warning if one of the component had a mismatching number of lanes.
	 * @param level The level of the node
	 * @param node The node to look for components
	 * @param componentPredicate The predicate for the components
	 * @param laneLabels The lane labels to set
	 */
	public static void setLaneLabels(Level level, NodePos node, Predicate<Component<?, ?, ?>> componentPredicate, String[] laneLabels) {
		List<Component<?, ?, ?>> cables = findComponentsOnNode(level, node).stream().filter(componentPredicate).toList();
		for (int i = 0; i < cables.size(); i++) {
			cables.get(i).setWireLanes(level, node, laneLabels);
		}
	}
	
	/**
	 * Returns the node potential of the node (the voltage relative to "network global ground")
	 * @param level The level of the node
	 * @param node The position of the node
	 * @param laneId The lane id of the node
	 * @param lane The lane name of the node
	 * @return The potential of the node relative to the network ground potential
	 */
	public static Optional<Double> getFloatingNodeVoltage(Level level, NodePos node, int laneId, String lane) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.getFloatingNodeVoltage(node, laneId, lane);
	}

	/**
	 * Returns the node potential of the local node lane of the component at the given position (the voltage relative to "network global ground")
	 * @param level The level of the node
	 * @param position The position of the component
	 * @param lane The lane name of the local node
	 * @param group Group id of the local node
	 * @return The potential of the node relative to the network ground potential
	 */
	public static Optional<Double> getFloatingLocalNodeVoltage(Level level, BlockPos position, String lane, int group) {
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		return handler.getFloatingLocalNodeVoltage(position, lane, group);
	}
	
	/**
	 * Returns the voltage between two nodes
	 * @param level The level of the two nodes
	 * @param nodeP The first nodes position
	 * @param nodeN The second nodes position
	 * @param laneIdP The first nodes lane id
	 * @param laneIdN The second nodes lane id
	 * @param laneP The first nodes lane name
	 * @param laneN The second nodes lane name
	 * @return The voltage between the two nodes (first node potential - second node potential)
	 */
	public static Optional<Double> getVoltageBetween(Level level, NodePos nodeP, NodePos nodeN, int laneIdP, int laneIdN, String laneP, String laneN) {
		Optional<Double> v1 = ElectricUtility.getFloatingNodeVoltage(level, nodeN, laneIdN, laneN);
		Optional<Double> v2 = ElectricUtility.getFloatingNodeVoltage(level, nodeP, laneIdP, laneP);
		if (v1.isEmpty() || v2.isEmpty()) return Optional.empty();
		return Optional.of(v2.get() - v1.get());
	}
	
	/**
	 * Returns the node voltage between two the local node lanes of the component at the given position
	 * @param level The level of the node
	 * @param position The position of the component
	 * @param laneP The first lane name of the local node
	 * @param laneN The second lane name of the local node
	 * @param groupP Group id of the first local node
	 * @param groupN Group id of the second local node
	 * @return The voltage between the two nodes (first node potential - second node potential)
	 */
	public static Optional<Double> getVoltageBetweenLocal(Level level, BlockPos position, String laneP, int groupP, String laneN, int groupN) {
		Optional<Double> v1 = ElectricUtility.getFloatingLocalNodeVoltage(level, position, laneN, groupN);
		Optional<Double> v2 = ElectricUtility.getFloatingLocalNodeVoltage(level, position, laneP, groupP);
		if (v1.isEmpty() || v2.isEmpty()) return Optional.empty();
		return Optional.of(v2.get() - v1.get());
	}
	
	/**
	 * Plots junction resistors to connect all node lanes of the supplied nodes to the internal lane and node (if the names match).
	 * @param plotter Plotter to use for plotting the junction resistors
	 * @param level Level of the electric component
	 * @param block Block of the electric component
	 * @param position Position of the electric component
	 * @param instance State of the electric component
	 * @param group Group id of the local node
	 * @param localLanes The internal node lane names
	 */
	public static void plotJoinTogether(Consumer<ICircuitPlot> plotter, Level level, IElectricBlock block, BlockPos position, BlockState instance, int group, String... localLanes) {
		NodePos[] nodes = block.getConnections(level, position, instance);
		plotJoinTogether(plotter, level, block, position, instance, nodes, group, localLanes);
	}
	
	/**
	 * Plots junction resistors to connect all node lanes of the supplied nodes to the internal lane and node (if the names match).
	 * @param plotter Plotter to use for plotting the junction resistors
	 * @param level Level of the electric component
	 * @param block Block of the electric component
	 * @param position Position of the electric component
	 * @param instance State of the electric component
	 * @param nodes Nodes to connect with inner lane
	 * @param group Group id of the local node
	 * @param localLanes The internal node lane names
	 */
	public static void plotJoinTogether(Consumer<ICircuitPlot> plotter, Level level, IElectricBlock block, BlockPos position, BlockState instance, NodePos[] nodes, int group, String... localLanes) {
		List<String[]> lanes = Stream.of(nodes).map(node -> getLaneLabelsSummarized(level, node)).toList();
		
		Plotter template = CircuitTemplateManager.getInstance().getTemplate(Circuits.JUNCTION_RESISTOR).plotter();
		
		for (int i = 0; i < nodes.length; i++) {
			String[] wireLanes = lanes.get(i);
			for (int i1 = 0; i1 < wireLanes.length; i1++) {
				for (String localLaneName : localLanes) {
					if (wireLanes[i1].equals(localLaneName)) {
						template.setNetworkNode("NET1", nodes[i], i1, wireLanes[i1]);
						template.setNetworkLocalNode("NET2", position, localLaneName, group);
						plotter.accept(template);
					}
				}
			}
		}
	}

	/**
	 * Plots junction resistors to connect all equally named lanes of the conduits connected to the given component
	 * @param plotter Plotter to use for plotting the junction resistors
	 * @param level Level of the electric component
	 * @param block Block of the electric component
	 * @param position Position of the electric component
	 * @param instance State of the electric component
	 */
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
	 * Send to all tracking the ElectricNetwork in the Supplier {@link #with(Supplier)} ElectricNetwork
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
