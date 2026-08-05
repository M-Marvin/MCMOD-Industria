package de.m_marvin.industria.core.electrics;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.base.Predicate;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.ComponentCircuitContext;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.ElectricElement;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.ElectricNode;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkSpaceCapability;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkSpaceCapability.ElectricComponent;
import de.m_marvin.industria.core.electrics.types.IElectric.ElectricReference;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricBlock;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.registries.ElectricElements;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.PowerNetState;
import de.m_marvin.industria.core.util.ufns.SynchronizedFunctionalNetworkSpace.UpdateType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import tvnlnna.nodal.NodalElementState;

public class ElectricUtility {
	
	private ElectricUtility() {}
	
	/**
	 * Triggers an update for the network at the given reference
	 */
	public static <P> void updateNetwork(Level level, ElectricReference reference) {
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		networkSpace.updateTicket(reference, UpdateType.NETWORK_UPDATE);
	}

	/*
	 * Returns all components connected with the given node
	 */
	public static Collection<ElectricNetworkSpaceCapability.ElectricComponent<?, ?>> findComponentsOnNode(Level level, NodePos node) {
		ElectricNetwork network = findNetworkAt(level, ElectricReference.block(node.getBlock()));
		if (network == null) return Collections.emptySet();
		return network.findComponentsOnNode(node);
	}

	/*
	 * Returns all components located in the given chunk
	 */
	public static Collection<ElectricComponent<?, ?>> findComponentsInChunk(Level level, ChunkPos chunkPos) {
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		return networkSpace.findComponentsInChunk(chunkPos);
	}
	
	/*
	 * Returns the network for the given component
	 */
	public static ElectricNetwork findCircuitWithComponent(Level level, ElectricComponent<?, ?> component) {
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		return networkSpace.findNetworkAt(component);
	}
	
	/*
	 * Searches for a component at the given reference
	 */
	@SuppressWarnings("unchecked")
	public static <I, T> ElectricComponent<I, T> findComponentAt(Level level, ElectricReference reference) {
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		return (ElectricComponent<I, T>) networkSpace.findComponentAt(reference);
	}
	
	/**
	 * Checks if the component is registered as electric component and part of a valid network
	 */
	public static boolean isInNetwork(Level level, ElectricComponent<?, ?> component) {
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		return networkSpace.findNetworkAt(component) != null;
	}

	/**
	 * Checks if the component is registered as electric component and part of a valid network
	 */
	public static boolean isInNetwork(Level level, ElectricReference reference) {
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		return networkSpace.findNetworkAt(reference) != null;
	}
	
	/**
	 * Returns the electric network with an component at the given reference
	 */
	public static ElectricNetwork findNetworkAt(Level level, ElectricReference reference) {
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		return networkSpace.findNetworkAt(reference);
	}
	
	/**
	 * Changes the state of the network with an component at the given reference
	 * Runs necessary updates and triggers events
	 */
	public static void setNetworkState(Level level, ElectricReference reference, PowerNetState state) {
		ElectricNetwork network = findNetworkAt(level, reference);
		if (network != null) {
			network.setState(state);
			updateNetwork(level, reference);
		}
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
		List<String[]> laneLabels = getLaneLabels(level, node, ElectricComponent::isWire);
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
	 * Collects a list (one entry per component connected to the node) of arrays of lane labels of the components connected to the given node.
	 * @param level Level of the node
	 * @param node The node to search for components on
	 * @param componentPredicate A predicate for the components to look for
	 * @return A list of all lane names, sorted per component
	 */
	public static List<String[]> getLaneLabels(Level level, NodePos node, Predicate<ElectricComponent<?, ?>> componentPredicate) {
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
	public static void setLaneLabels(Level level, NodePos node, Predicate<ElectricComponent<?, ?>> componentPredicate, String[] laneLabels) {
		List<ElectricComponent<?, ?>> cables = findComponentsOnNode(level, node).stream().filter(componentPredicate).toList();
		for (int i = 0; i < cables.size(); i++) {
			cables.get(i).setWireLanes(level, node, laneLabels);
		}
	}
	
	public static double getFloatingNodeVoltage(Level level, ElectricNode node) {
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		return networkSpace.getFloatingNodeVoltage(node);
	}
	
	/**
	 * Returns the voltage between two nodes
	 * @param level The level of the two nodes
	 * @param nodeP The first nodes reference
	 * @param nodeN The second nodes reference
	 * @param laneIdP The first nodes lane id
	 * @param laneIdN The second nodes lane id
	 * @param laneP The first nodes lane name
	 * @param laneN The second nodes lane name
	 * @return The voltage between the two nodes (first node potential - second node potential)
	 */
	public static double getVoltageBetween(Level level, ElectricNode nodeA, ElectricNode nodeB) {
		double v1 = ElectricUtility.getFloatingNodeVoltage(level, nodeA);
		double v2 = ElectricUtility.getFloatingNodeVoltage(level, nodeB);
		return v1 - v2;
	}
	
	public static NodalElementState getElementState(Level level, ElectricElement element) {
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		return networkSpace.getElement(element);
	}
	
	// TODO
//	/**
//	 * Plots junction resistors to connect all node lanes of the supplied nodes to the internal lane and node (if the names match).
//	 * @param plotter Plotter to use for plotting the junction resistors
//	 * @param level Level of the electric component
//	 * @param block Block of the electric component
//	 * @param reference Reference of the electric component
//	 * @param instance State of the electric component
//	 * @param group Group id of the local node
//	 * @param localLanes The internal node lane names
//	 */
	public static void installJunctionResistors(Level level, ComponentCircuitContext context, IElectricBlock block, ElectricReference reference, BlockState instance, double junctionResistance, ElectricNode[] targetNodes) {
		NodePos[] conduitNodes = block.getElectricConnections(level, reference, instance);
		installJunctionResistors(level, context, reference, junctionResistance, conduitNodes, targetNodes);
	}

	// TODO
//	/**
//	 * Plots junction resistors to connect all node lanes of the supplied nodes to the internal lane and node (if the names match).
//	 * @param plotter Plotter to use for plotting the junction resistors
//	 * @param level Level of the electric component
//	 * @param block Block of the electric component
//	 * @param reference Reference of the electric component
//	 * @param instance State of the electric component
//	 * @param nodes Nodes to connect with inner lane
//	 * @param group Group id of the local node
//	 * @param localLanes The internal node lane names
//	 */
	public static void installJunctionResistors(Level level, ComponentCircuitContext context, ElectricReference reference, double junctionResistance, NodePos[] conduitNodes, ElectricNode[] targetNodes) {
		int i = 0;
		for (NodePos conduitNode : conduitNodes) {
			for (String lane : getLaneLabelsSummarized(level, conduitNode)) {
				for (ElectricNode targetNode : targetNodes) {
					if (targetNode.nodeName().equals(lane)) {
//						context.installResistor(ElectricElement.element(reference, "junction_" + i++), targetNode, ElectricNode.node(conduitNode, lane), 0.0);
						NodalElementState resistorState = context.install(
								ElectricElements.RESISTOR.get(), ElectricElement.element(reference, "junction_" + i++), 
								ElectricNode.internal(reference, "junction_" + lane), ElectricNode.node(conduitNode, lane));
						resistorState.setParameter("R", 0.01); // TODO
					}
				}
			}
		}
	}
	
	public static void installJunctionResistors(Level level, ComponentCircuitContext context, IElectricBlock block, ElectricReference reference, BlockState instance, double junctionResistance) {
		NodePos[] conduitNodes = block.getElectricConnections(level, reference, instance);
		installJunctionResistors(level, context, reference, junctionResistance, conduitNodes);
	}
	
//	/**
//	 * Plots junction resistors to connect all equally named lanes of the conduits connected to the given component
//	 * @param plotter Plotter to use for plotting the junction resistors
//	 * @param level Level of the electric component
//	 * @param block Block of the electric component
//	 * @param reference Reference of the electric component
//	 * @param instance State of the electric component
//	 */
	public static void installJunctionResistors(Level level, ComponentCircuitContext context, ElectricReference reference, double junctionResistance, NodePos[] conduitNodes) {
		for (NodePos conduitNode : conduitNodes) {
			for (String lane : getLaneLabelsSummarized(level, conduitNode)) {
//				context.installResistor(ElectricElement.element(reference, "Rjunction_" + lane), ElectricNode.internal(reference, "junction_" + lane), ElectricNode.node(conduitNode, lane), 0.0);
				NodalElementState resistorState = context.install(
						ElectricElements.RESISTOR.get(), ElectricElement.element(reference, "junction_" + lane), 
						ElectricNode.internal(reference, "junction_" + lane), ElectricNode.node(conduitNode, lane));
				resistorState.setParameter("R", 0.01); // TODO
			}
		}
	}
	
//	/**
//	 * Plots junction resistors to connect all node lanes of the supplied nodes to the internal lane and node (if the names match).
//	 * @param plotter Plotter to use for plotting the junction resistors
//	 * @param level Level of the electric component
//	 * @param block Block of the electric component
//	 * @param reference Reference of the electric component
//	 * @param instance State of the electric component
//	 * @param nodes Nodes to connect with inner lane
//	 * @param group Group id of the local node
//	 * @param localLanes The internal node lane names
//	 */
//	public static void plotJoinTogether(Consumer<ICircuitPlot> plotter, Level level, IElectricBlock block, ElectricReference reference, BlockState instance, NodePos[] nodes, int group, String... localLanes) {
//		List<String[]> lanes = Stream.of(nodes).map(node -> getLaneLabelsSummarized(level, node)).toList();
//		
//		Plotter template = CircuitTemplateManager.getInstance().getTemplate(Circuits.JUNCTION_RESISTOR).plotter();
//		
//		for (int i = 0; i < nodes.length; i++) {
//			String[] wireLanes = lanes.get(i);
//			for (int i1 = 0; i1 < wireLanes.length; i1++) {
//				for (String localLaneName : localLanes) {
//					if (wireLanes[i1].equals(localLaneName)) {
//						template.setNetworkNode("NET1", nodes[i], i1, wireLanes[i1]);
//						template.setNetworkLocalNode("NET2", reference.block(), localLaneName, group);
//						plotter.accept(template);
//					}
//				}
//			}
//		}
//	}
//
//	/**
//	 * Plots junction resistors to connect all equally named lanes of the conduits connected to the given component
//	 * @param plotter Plotter to use for plotting the junction resistors
//	 * @param level Level of the electric component
//	 * @param block Block of the electric component
//	 * @param reference Reference of the electric component
//	 * @param instance State of the electric component
//	 */
//	public static void plotConnectEquealNamed(Consumer<ICircuitPlot> plotter, Level level, IElectricBlock block, ElectricReference reference, BlockState instance) {
//		NodePos[] nodes = block.getElectricConnections(level, reference, instance);
//		List<String[]> lanes = Stream.of(nodes).map(node -> ElectricUtility.getLaneLabelsSummarized(level, node)).toList();
//		
//		Plotter template = CircuitTemplateManager.getInstance().getTemplate(Circuits.JUNCTION_RESISTOR).plotter();
//		
//		for (int i = 0; i < nodes.length; i++) {
//			String[] wireLanes = lanes.get(i);
//			for (int i1 = 0; i1 < wireLanes.length; i1++) {
//				String wireLabel = wireLanes[i1];
//				if (!wireLabel.isEmpty()) {
//					template.setNetworkNode("NET1", nodes[i], i1, wireLabel);
//					template.setNetworkNode("NET2", new NodePos(reference.block(), 0), 0, "junction_" + wireLabel);
//					plotter.accept(template);
//				}
//			}
//		}
//	}
	
	/**
	 * Send to all tracking the ElectricNetwork in the Supplier {@link #with(Supplier)}
	 */
	public static final PacketDistributor<ElectricNetwork> TRACKING_NETWORK = new PacketDistributor<>(ElectricUtility::trackingNetwork, NetworkDirection.PLAY_TO_CLIENT);
	
	private static Consumer<Packet<?>> trackingNetwork(final PacketDistributor<ElectricNetwork> distributor, final Supplier<ElectricNetwork> networkSupplier) {
		return p -> {
			ElectricNetwork network = networkSupplier.get();
			network.listComponents().stream()
				.map(c -> c.getAffectedChunk(network.getLevel()))
				.distinct()
				.flatMap(chunk -> ((ServerChunkCache) network.getLevel().getChunkSource()).chunkMap.getPlayers(chunk, false).stream())
				.distinct()
				.forEach(e -> e.connection.send(p));
		};
	}
	
}
