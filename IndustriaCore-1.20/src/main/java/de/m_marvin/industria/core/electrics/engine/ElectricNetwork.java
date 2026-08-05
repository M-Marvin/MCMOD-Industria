package de.m_marvin.industria.core.electrics.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkSpaceCapability.ElectricComponent;
import de.m_marvin.industria.core.electrics.engine.network.SUpdateElectricNetworkPackage;
import de.m_marvin.industria.core.electrics.events.ElectricNetworkEvent;
import de.m_marvin.industria.core.electrics.types.IElectric.ElectricReference;
import de.m_marvin.industria.core.util.types.EventStage;
import de.m_marvin.industria.core.util.types.PowerNetState;
import de.m_marvin.industria.core.util.ufns.SynchronizedFunctionalNetworkSpace;
import de.m_marvin.unimap.api.MultiBiMap;
import de.m_marvin.unimap.impl.HashMultiBiMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import tvnlnna.NetworkSolverException;
import tvnlnna.nodal.NodalElement;
import tvnlnna.nodal.NodalElementState;
import tvnlnna.nodal.NodalNetwork;
import tvnlnna.solver.NodalNetworkSolver;

public class ElectricNetwork extends SynchronizedFunctionalNetworkSpace.SynchronizedFunctionalNetwork<ElectricNetwork, ElectricReference, ElectricComponent<?, ?>, NodePos> {
	
	private final Supplier<Level> level;
	private final NodalNetworkSolver networkSolver; // TODO //NetworkSolver.standard().limSingular(1E-20).iterLim(500);
		
	private MultiBiMap<Integer, NodePos> ref2nodeMap = new HashMultiBiMap<Integer, NodePos>();
	private PowerNetState state = PowerNetState.ACTIVE;
	
	private Map<ElectricElement, ElectricCircuit> element2circuitMap = new HashMap<ElectricElement, ElectricCircuit>();
	private Map<ElectricNode, ElectricCircuit> node2circuitMap = new HashMap<ElectricNode, ElectricCircuit>();

	public static record ElectricNode(NodePos conduitNode, ElectricReference componentReference, String nodeName) {
		
		public static ElectricNode node(NodePos nodePos, String nodeName) {
			return new ElectricNode(nodePos, null, nodeName);
		}
		
		public static ElectricNode internal(ElectricReference componentReference, String nodeName) {
			return new ElectricNode(null, componentReference, nodeName);
		}
		
		public boolean isInternal() {
			return this.componentReference != null;
		}
		
		private static String nodeString(NodePos node) {
			return String.format("npos_%d_%d_%d_%d", node.getBlock().getX(), node.getBlock().getY(), node.getBlock().getZ(), node.getNode());
		}
		
		private static String referenceString(ElectricReference reference) {
			if (reference.isBlock())
				return String.format("block_%d_%d_%d", reference.block().getX(), reference.block().getY(), reference.block().getZ());
			else
				return String.format("conduit_%s_%s", nodeString(reference.conduit().getNodeA()), nodeString(reference.conduit().getNodeB()));
		}
		
		public String nodeString() {
			if (isInternal())
				return String.format("N_%s_lnm_%s", referenceString(componentReference), nodeName);
			else
				return String.format("N_%s_lnm_%s", nodeString(conduitNode), nodeName);
		}
		
	}
	
	public static record ElectricElement(ElectricReference componentReference, String elementName) {
		
		public static ElectricElement element(ElectricReference componentReference, String elementName) {
			return new ElectricElement(componentReference, elementName);
		}
		
		public String elementString() {
			return String.format("%s_%s", elementName, ElectricNode.referenceString(componentReference));
		}
		
	}
	
	private class ElectricCircuit {
		
		private NodalNetwork network = null;
		private Set<ElectricNode> nodes = new HashSet<ElectricNode>();
		private Map<ElectricElement, NodalElementState> elements = new HashMap<ElectricElement, NodalElementState>();
		
		public double getNodePotential(ElectricNode node) {
			return this.network.getNodePotential(node.nodeString());
		}
		
		public NodalElementState getElementState(ElectricElement element) {
			return this.elements.get(element);
		}
		
		@Override
		public String toString() {
			return this.network != null ? this.network.toString() : "EMPTY";
		}
		
	}

	@Override
	public String toString() {
		return this.element2circuitMap.values().stream()
				.distinct()
				.map(ElectricCircuit::toString)
				.reduce((a, b) -> a + "\n---\n" + b)
				.orElseGet(() -> "EMPTY");
	}
	
	public ElectricNetwork(Supplier<Level> level, NodalNetworkSolver solver) {
		this.level = level;
		this.networkSolver = solver;
	}
	
	public Level getLevel() {
		return level.get();
	}
	
	public NodalNetworkSolver getNetworkSolver() {
		return networkSolver;
	}
	
	@Override
	public void serializeNbt(CompoundTag nbt) {
		
		ListTag nodesNbt = new ListTag();
		for (var e : this.ref2nodeMap.entrySet()) {
			CompoundTag nodeNbt = new CompoundTag();
			nodeNbt.putInt("RefId", e.getKey());
			nodeNbt.put("Node", e.getValue().writeNBT(new CompoundTag()));
			nodesNbt.add(nodeNbt);
		}
		nbt.put("Nodes", nodesNbt);
		
		// TODO save circuit state
//		CompoundTag voltagesNbt = new CompoundTag();
//		for (var e : this.nodeVoltages.entrySet()) {
//			voltagesNbt.putDouble(e.getKey(), e.getValue());
//		}
//		nbt.put("Voltages", voltagesNbt);
		
		nbt.putString("State", this.state.name().toLowerCase());
	}
	
	@Override
	public void deserializeNbt(CompoundTag nbt) {
		super.deserializeNbt(nbt);

		ListTag nodesNbt = nbt.getList("Nodes", 10);
		this.ref2nodeMap.clear();
		for (int i = 0; i < nodesNbt.size(); i++) {
			CompoundTag nodeNbt = nodesNbt.getCompound(i);
			int refId = nodeNbt.getInt("RefId");
			NodePos node = NodePos.readNBT(nodeNbt.getCompound("Node"));
			this.ref2nodeMap.put(refId, node);
		}
		
//		CompoundTag voltagesNbt = nbt.getCompound("Voltages");
//		this.nodeVoltages.clear();
//		for (String node : voltagesNbt.getAllKeys()) {
//			this.nodeVoltages.put(node, voltagesNbt.getDouble(node));
//		}
		
		this.state = PowerNetState.valueOf(nbt.getString("State").toUpperCase());
	}
	
	@Override
	public void afterChange() {
		rebuildCircuits();
	}

	@Override
	public void onUpdate() {
		rebuildCircuits();
	}
	
	protected ElectricCircuit circuitOfNode(ElectricNode node) {
		return this.node2circuitMap.get(node);
	}
	
	protected ElectricCircuit circuitOfElement(ElectricElement element) {
		return this.element2circuitMap.get(element);
	}
	
	public class ComponentCircuitContext {
		
		private final ElectricComponent<?, ?> component;
		private final boolean allowInstall;
		
		public ComponentCircuitContext(ElectricComponent<?, ?> component, boolean allowInstall) {
			this.component = component;
			this.allowInstall = allowInstall;
		}
		
		public NodalElementState getOrInstall(NodalElement elementDef, ElectricElement element, ElectricNode... nodes)  {
			NodalElementState state = getElement(element);
			if (state == null)
				state = install(elementDef, element, nodes);
			return state;
		}
		
		public NodalElementState install(NodalElement elementDef, ElectricElement element, ElectricNode... nodes) {
			if (!this.allowInstall)
				throw new IllegalStateException("can not change circuit outside of install phase");
			NodalElementState elementState = elementDef.newInstance(element.elementString());
			elementState.setNodeNames(Stream.of(nodes).map(ElectricNode::nodeString).toArray(String[]::new));
			ElectricCircuit circuit = Stream.of(nodes).map(ElectricNetwork.this::circuitOfNode).filter(o -> o != null).distinct().reduce((circuitA, circuitB) -> {
					circuitA.nodes.addAll(circuitB.nodes);
					circuitB.nodes.forEach(node -> {
						ElectricNetwork.this.node2circuitMap.put(node, circuitA);
					});
					circuitA.elements.putAll(circuitB.elements);
					circuitB.elements.keySet().forEach(elementKey2 -> {
						ElectricNetwork.this.element2circuitMap.put(elementKey2, circuitA);
					});
					circuitA.network = new NodalNetwork(circuitA.elements.values(), circuitA.nodes.stream().findAny().get().nodeString());
					return circuitA;
			}).orElseGet(ElectricCircuit::new);
			circuit.elements.put(element, elementState);
			circuit.nodes.addAll(List.of(nodes));
			ElectricNetwork.this.element2circuitMap.put(element, circuit);
			for (var node : nodes)
				ElectricNetwork.this.node2circuitMap.put(node, circuit);
			return elementState;
		}
		
		public NodalElementState getElement(ElectricElement element) {
			ElectricCircuit circuit = circuitOfElement(element);
			if (circuit != null)
				return circuit.elements.get(element);
			return null;
		}
		
		public double nodePotential(ElectricNode node) {
			ElectricCircuit circuit = circuitOfNode(node);
			if (circuit != null)
				return circuit.getNodePotential(node);
			return 0.0;
		}
		
		public ElectricComponent<?, ?> getComponent() {
			return component;
		}
		
	}
	
	public void resetCircuits() {
		this.element2circuitMap.clear();
		this.node2circuitMap.clear();
	}
	
	public void rebuildCircuits() {
		
		resetCircuits();
	
		for (var component : listComponents()) {
			try {
				component.updateElectricElements(getLevel(), new ComponentCircuitContext(component, true), true);
			} catch (Exception e) {
				IndustriaCore.LOGGER.warn("Exception while installing circuit component: " + component.reference(), e);
			}
		}
		
		this.element2circuitMap.values().stream().distinct().forEach(circuit -> {
			circuit.network = new NodalNetwork(circuit.elements.values(), circuit.nodes.stream().findAny().get().nodeString());
		});
		
	}

	public void stepElectrics(double timestep) {
		
		if (this.element2circuitMap.isEmpty())
			rebuildCircuits();
		
		if (isOnline()) {

			for (var component : listComponents()) {
				try {
					component.updateElectricElements(getLevel(), new ComponentCircuitContext(component, false), false);
				} catch (Exception e) {
					IndustriaCore.LOGGER.warn("Exception while stepping circuit component: " + component.reference(), e);
					tripFuse();
				}
			}
			
			double t0 = this.networkSolver.getSimulationTime();
			this.element2circuitMap.values().stream().distinct().forEach(circuit -> {
				try {
					networkSolver.setNetwork(circuit.network);
					if (circuit.network.getSystemMatrix_x() == null) {
						networkSolver.resetAndInitSimulation();
						networkSolver.step(0.0);
					}
					networkSolver.setSimulationTime(t0);
					networkSolver.step(timestep);
				} catch (NetworkSolverException e) {						
					this.tripFuse();
				}
			});
			
		}
		
	}
	
	@Override
	protected void afterPutComponent(int refId, ElectricComponent<?, ?> component) {
		resetCircuits();
	}

	@Override
	protected void afterRemoveComponent(int refId, ElectricComponent<?, ?> component) {
		this.ref2nodeMap.remove(refId);
		resetCircuits();
	}
	
	@Override
	protected void afterParametrizedConnection(int refId1, int refId2, NodePos parameter) {
		this.ref2nodeMap.put(refId1, parameter);
		this.ref2nodeMap.put(refId2, parameter);
	}
	
	public Collection<ElectricComponent<?, ?>> findComponentsOnNode(NodePos node) {
		List<ElectricComponent<?, ?>> components = new ArrayList<>();
		for (Integer refId : this.ref2nodeMap.getKeys(node)) {
			ElectricComponent<?, ?> component = this.components.get(refId.intValue());
			if (component != null) components.add(component);
		}
		return components;
	}

	@Override
	protected void afterIntegrateNetwork(IntSet refIds, ElectricNetwork other) {
		if (!isOnline())
			this.state = other.state;
		for (int refId : refIds) {
			if (other.ref2nodeMap.containsKey(refId))
				this.ref2nodeMap.putAll(refId, other.ref2nodeMap.getAll(refId));
		}
		this.node2circuitMap.putAll(other.node2circuitMap);
		this.element2circuitMap.putAll(other.element2circuitMap);
	}
	
	public void tripFuse() {
		setState(PowerNetState.FAILED);
	}
	
	public void setState(PowerNetState state) {
		if (this.state != state) {
			var stateChangeEvent = new ElectricNetworkEvent.StateChangeEvent(getLevel(), this, this.state, EventStage.PRE);
			MinecraftForge.EVENT_BUS.post(stateChangeEvent);
			if (stateChangeEvent.isCanceled()) return;
			
			this.state = state;
			
			if (isTripped())
				MinecraftForge.EVENT_BUS.post(new ElectricNetworkEvent.FuseTripedEvent(getLevel(), this));
			MinecraftForge.EVENT_BUS.post(new ElectricNetworkEvent.StateChangeEvent(getLevel(), this, this.state, EventStage.POST));
		}
		
		if (!getLevel().isClientSide())
			IndustriaCore.NETWORK.send(ElectricUtility.TRACKING_NETWORK.with(() -> this), new SUpdateElectricNetworkPackage(this));
	}
	
	public PowerNetState getState() {
		return state;
	}
	
	public boolean isTripped() {
		return this.state == PowerNetState.FAILED;
	}
	
	public boolean isOnline() {
		return this.state == PowerNetState.ACTIVE;
	}
	
	public double getFloatingNodeVoltage(ElectricNode node) {
		if (!isOnline())
			return 0.0;
		ElectricCircuit circuit = circuitOfNode(node);
		if (circuit == null)
			return 0.0;
		return circuit.getNodePotential(node);
	}
	
	public NodalElementState getElementState(ElectricElement element) {
		ElectricCircuit circuit = circuitOfElement(element);
		if (circuit == null)
			return null;
		return circuit.getElementState(element);
	}
	
}
