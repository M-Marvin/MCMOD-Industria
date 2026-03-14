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

import de.m_marvin.electronflow.nltisolver.elements.Current;
import de.m_marvin.electronflow.nltisolver.elements.Diode;
import de.m_marvin.electronflow.nltisolver.elements.Diode.DiodeModel;
import de.m_marvin.electronflow.nltisolver.elements.Element;
import de.m_marvin.electronflow.nltisolver.elements.Resistor;
import de.m_marvin.electronflow.nltisolver.elements.Voltage;
import de.m_marvin.electronflow.nltisolver.network.IndexedNetwork;
import de.m_marvin.electronflow.nltisolver.solver.NetworkSolver;
import de.m_marvin.electronflow.nltisolver.solver.NetworkSolverException;
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

public class ElectricNetwork extends SynchronizedFunctionalNetworkSpace.SynchronizedFunctionalNetwork<ElectricNetwork, ElectricReference, ElectricComponent<?, ?>, NodePos> {
	
	private final Supplier<Level> level;
	
	private MultiBiMap<Integer, NodePos> ref2nodeMap = new HashMultiBiMap<Integer, NodePos>();
	private PowerNetState state = PowerNetState.ACTIVE;
	
	private Map<CircuitElement, Circuit> element2circuitMap = new HashMap<CircuitElement, Circuit>();
	private Map<CircuitNode, Circuit> node2circuitMap = new HashMap<CircuitNode, Circuit>();

	private NetworkSolver networkSolver = NetworkSolver.standard().limSingular(1E-20).iterLim(500);
	
	public static record CircuitNode(NodePos conduitNode, ElectricReference componentReference, String nodeName) {
		
		public static CircuitNode node(NodePos nodePos, String nodeName) {
			return new CircuitNode(nodePos, null, nodeName);
		}
		
		public static CircuitNode internal(ElectricReference componentReference, String nodeName) {
			return new CircuitNode(null, componentReference, nodeName);
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
	
	public static record CircuitElement(ElectricReference componentReference, String elementName) {
		
		public static CircuitElement element(ElectricReference componentReference, String elementName) {
			return new CircuitElement(componentReference, elementName);
		}
		
		public String elementString() {
			return String.format("%s_%s", elementName, CircuitNode.referenceString(componentReference));
		}
		
	}
	
	private class Circuit {
		
		private IndexedNetwork indexedNetwork = null;
		private Set<CircuitNode> nodes = new HashSet<CircuitNode>();
		private Map<CircuitElement, Element> elements = new HashMap<CircuitElement, Element>();
		private boolean changed = false;
		
		public double getNodePotential(CircuitNode node) {
			try {
				return this.indexedNetwork.getNodePotential(node.nodeString());
			} catch (IllegalStateException e) {
				return 0.0;
			}
		}
		
		public double getElementCurrent(CircuitElement element) {
			try {
				double[] currents = this.indexedNetwork.getElementCurrents(element.elementString());
				return currents.length > 0 ? currents[0] : 0.0;
			} catch (IllegalStateException e) {
				return 0.0;
			}
		}

		@Override
		public String toString() {
			return this.indexedNetwork != null ? this.indexedNetwork.toString() : "EMPTY";
		}
		
	}

	@Override
	public String toString() {
		return this.element2circuitMap.values().stream()
				.distinct()
				.map(Circuit::toString)
				.reduce((a, b) -> a + "\n---\n" + b)
				.orElseGet(() -> "EMPTY");
	}
	
	public ElectricNetwork(Supplier<Level> level) {
		this.level = level;
	}
	
	public Level getLevel() {
		return level.get();
	}
	
	public NetworkSolver getNetworkSolver() {
		return networkSolver;
	}
	
	@Override
	public void serializeNbt(CompoundTag nbt) {
		super.serializeNbt(nbt);
		
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
	
	protected Circuit circuitOfNode(CircuitNode node) {
		return this.node2circuitMap.get(node);
	}
	
	protected Circuit circuitOfElement(CircuitElement element) {
		return this.element2circuitMap.get(element);
	}
	
	public class ComponentCircuitContext {
		
		private final ElectricComponent<?, ?> component;
		private final boolean allowInstall;
		
		public ComponentCircuitContext(ElectricComponent<?, ?> component, boolean allowInstall) {
			this.component = component;
			this.allowInstall = allowInstall;
		}
		
		public void install(Element element, CircuitElement elementKey, CircuitNode... nodes) {
			if (!this.allowInstall)
				throw new IllegalStateException("can not change circuit outside of install phase");
			Circuit circuit = Stream.of(nodes).map(ElectricNetwork.this::circuitOfNode).filter(o -> o != null).distinct().reduce((circuitA, circuitB) -> {
					circuitA.nodes.addAll(circuitB.nodes);
					circuitB.nodes.forEach(node -> {
						ElectricNetwork.this.node2circuitMap.put(node, circuitA);
					});
					circuitA.elements.putAll(circuitB.elements);
					circuitB.elements.keySet().forEach(elementKey2 -> {
						ElectricNetwork.this.element2circuitMap.put(elementKey2, circuitA);
					});
					return circuitA;
			}).orElseGet(Circuit::new);
			circuit.elements.put(elementKey, element);
			circuit.nodes.addAll(List.of(nodes));
			circuit.changed = true;
			ElectricNetwork.this.element2circuitMap.put(elementKey, circuit);
			for (var node : nodes)
				ElectricNetwork.this.node2circuitMap.put(node, circuit);
		}
		
		public void installResistor(CircuitElement element, CircuitNode nodeA, CircuitNode nodeB, double value) {
			install(new Resistor(element.elementString(), nodeA.nodeString(), nodeB.nodeString(), value), element, nodeA, nodeB);
		}
		
		public void installVoltage(CircuitElement element, CircuitNode nodeA, CircuitNode nodeB, double value) {
			install(new Voltage(element.elementString(), nodeA.nodeString(), nodeB.nodeString(), value), element, nodeA, nodeB);
		}
		
		public void installCurrent(CircuitElement element, CircuitNode nodeA, CircuitNode nodeB, double value) {
			install(new Current(element.elementString(), nodeA.nodeString(), nodeB.nodeString(), value), element, nodeA, nodeB);
		}
		
		public void installDiode(CircuitElement element, CircuitNode nodeA, CircuitNode nodeB, DiodeModel model) {
			install(new Diode(element.elementString(), nodeA.nodeString(), nodeB.nodeString(), model), element, nodeA, nodeB);
		}
		
		public void changeVoltage(CircuitElement element, double value) {
			Circuit circuit = circuitOfElement(element);
			if (circuit != null && circuit.elements.get(element) instanceof Voltage vsource) {
				vsource.setVoltage(value);
				circuit.changed = true;
			}
		}

		public void changeCurrent(CircuitElement element, double value) {
			Circuit circuit = circuitOfElement(element);
			if (circuit != null && circuit.elements.get(element) instanceof Current isource) {
				isource.setCurrent(value);
				circuit.changed = true;
			}
		}

		public void changeResistance(CircuitElement element, double value) {
			Circuit circuit = circuitOfElement(element);
			if (circuit != null && circuit.elements.get(element) instanceof Resistor resistor) {
				resistor.setResistance(value);
				circuit.changed = true;
			}
		}
		

		public void changeDiode(CircuitElement element, DiodeModel value) {
			Circuit circuit = circuitOfElement(element);
			if (circuit != null && circuit.elements.get(element) instanceof Diode diode) {
				diode.setModel(value);
				circuit.changed = true;
			}
		}
		
		public double readVoltage(CircuitElement element) {
			Circuit circuit = circuitOfElement(element);
			if (circuit != null && circuit.elements.get(element) instanceof Voltage vsource) {
				return vsource.voltage();
			}
			return 0.0;
		}

		public double readCurrent(CircuitElement element) {
			Circuit circuit = circuitOfElement(element);
			if (circuit != null && circuit.elements.get(element) instanceof Current isource) {
				return isource.current();
			}
			return 0.0;
		}

		public double readResistance(CircuitElement element) {
			Circuit circuit = circuitOfElement(element);
			if (circuit != null && circuit.elements.get(element) instanceof Resistor resistor) {
				return resistor.resistance();
			}
			return 0.0;
		}

		public DiodeModel readDiode(CircuitElement element) {
			Circuit circuit = circuitOfElement(element);
			if (circuit != null && circuit.elements.get(element) instanceof Diode diode) {
				return diode.getModel();
			}
			return null;
		}
		
		public double nodePotential(CircuitNode node) {
			Circuit circuit = circuitOfNode(node);
			if (circuit != null)
				return circuit.getNodePotential(node);
			return 0.0;
		}
		
		public double elementCurrent(CircuitElement element) {
			Circuit circuit = circuitOfElement(element);
			if (circuit != null)
				return circuit.getElementCurrent(element);
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
				component.installCircuitElements(getLevel(), new ComponentCircuitContext(component, true));
			} catch (Exception e) {
				IndustriaCore.LOGGER.warn("Exception while installing circuit component: " + component.reference(), e);
			}
		}
		
		this.element2circuitMap.values().stream().distinct().forEach(circuit -> {
			circuit.indexedNetwork = new IndexedNetwork(circuit.elements.values());
		});
		
	}

	public void stepElectrics() {
		
		if (isOnline() && !this.element2circuitMap.isEmpty()) {

			for (var component : listComponents()) {
				try {
					component.stepCircuitElements(getLevel(), new ComponentCircuitContext(component, false));
				} catch (Exception e) {
					IndustriaCore.LOGGER.warn("Exception while stepping circuit component: " + component.reference(), e);
					tripFuse();
				}
			}
			
			this.element2circuitMap.values().stream().distinct().forEach(circuit -> {
				if (circuit.changed) {
					try {
						networkSolver.solve(circuit.indexedNetwork);
						circuit.changed = false;
					} catch (NetworkSolverException e) {						
						this.tripFuse();
					}
				}
			});
			
		}
		
		listComponents().forEach(c -> c.afterNetworkStep(level.get(), this));
		
	}
	
	@Override
	protected void afterPutComponent(int refId) {
		resetCircuits();
	}

	@Override
	protected void afterRemoveComponent(int refId) {
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
	
	public double getFloatingNodeVoltage(CircuitNode node) {
		if (!isOnline())
			return 0.0;
		Circuit circuit = circuitOfNode(node);
		if (circuit == null)
			return 0.0;
		return circuit.getNodePotential(node);
	}
	
	public double getElementCurrent(CircuitElement element) {
		if (!isOnline())
			return 0.0;
		Circuit circuit = circuitOfElement(element);
		if (circuit == null)
			return 0.0;
		return circuit.getElementCurrent(element);
	}
	
}
