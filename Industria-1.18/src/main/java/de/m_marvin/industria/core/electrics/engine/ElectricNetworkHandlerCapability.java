package de.m_marvin.industria.core.electrics.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.base.Objects;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.core.conduits.engine.ConduitEvent;
import de.m_marvin.industria.core.conduits.engine.ConduitEvent.ConduitBreakEvent;
import de.m_marvin.industria.core.conduits.engine.ConduitEvent.ConduitLoadEvent;
import de.m_marvin.industria.core.conduits.engine.ConduitEvent.ConduitPlaceEvent;
import de.m_marvin.industria.core.conduits.engine.ConduitEvent.ConduitUnloadEvent;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.conduits.IElectricConduit;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.IElectric;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricConnector;
import de.m_marvin.industria.core.registries.ModCapabilities;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ElectricNetworkHandlerCapability implements ICapabilitySerializable<ListTag> {
	
	/* Capability handling */
	
	private LazyOptional<ElectricNetworkHandlerCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
	private Level level;
	private HashMap<Object, Component<?, ?, ?>> pos2componentMap = new HashMap<Object, Component<?, ?, ?>>();
	private HashMap<NodePos, Set<Component<?, ?, ?>>> node2componentMap = new HashMap<NodePos, Set<Component<?, ?, ?>>>();
	private HashSet<ElectricNetwork> circuitNetworks = new HashSet<ElectricNetwork>();
	private HashMap<Component<?, ?, ?>, ElectricNetwork> component2circuitMap = new HashMap<Component<?, ?, ?>, ElectricNetwork>();
	
	@Override
	public ListTag serializeNBT() {
		ListTag nbt2 = new ListTag();
		for (ElectricNetwork circuitNetwork : this.circuitNetworks) {
			nbt2.add(circuitNetwork.saveNBT());
		}
		Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, "Saved " + nbt2.size() + " electric networks");
		
		return nbt2;
	}
	
	@Override
	public void deserializeNBT(ListTag nbt) {
		this.pos2componentMap.clear();
		this.node2componentMap.clear();
		this.circuitNetworks.clear();
		this.component2circuitMap.clear();
		
		for (int i = 0; i < nbt.size(); i++) {
			CompoundTag circuitTag = nbt.getCompound(i);
			ElectricNetwork circuitNetwork = new ElectricNetwork("ingame-level-circuit");
			circuitNetwork.loadNBT(level, circuitTag);
			if (!circuitNetwork.isPlotEmpty()) {
				if (circuitNetwork.getComponents().isEmpty()) continue;
				this.circuitNetworks.add(circuitNetwork);
				circuitNetwork.getComponents().forEach((component) -> {
					this.component2circuitMap.put(component, circuitNetwork);
					if (!this.pos2componentMap.containsValue(component)) {
						this.addToNetwork(component);
					}
				});
			}
		}
		Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, "Loaded " + this.circuitNetworks.size() + "/" + nbt.size() + " electric networks");
		Industria.LOGGER.log(org.apache.logging.log4j.Level.DEBUG, "Loaded " + this.pos2componentMap.size() + " electric components");
	}
	
	public ElectricNetworkHandlerCapability(Level level) {
		this.level = level;
	}
	
	/* Event handling */
	
//	@SubscribeEvent
//	public static void onWorldTick(WorldTickEvent event) {
//		ServerLevel level = (ServerLevel) event.world;
//		LazyOptional<ElectricNetworkHandlerCapability> networkHandler = level.getCapability(ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
//		if (networkHandler.isPresent()) {
//			networkHandler.resolve().get().tick();
//		}
//	}
	
//	@SubscribeEvent
//	@SuppressWarnings("resource")
//	public static void onClientWorldTick(ClientTickEvent event) {
//		ClientLevel level = Minecraft.getInstance().level;
//		if (level != null) {
//			LazyOptional<ElectricNetworkHandlerCapability> networkHandler = level.getCapability(ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
//			if (networkHandler.isPresent()) {
//				networkHandler.resolve().get().tick();
//			}
//		}
//	}
	
	@SubscribeEvent
	public static void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		Level level = (Level) event.getWorld();
		ElectricNetworkHandlerCapability handler = GameUtility.getCapability(level, ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		if (event.getState().getBlock() instanceof IElectricConnector) {
			IElectricConnector block = (IElectricConnector) event.getState().getBlock();
			handler.addComponent(event.getPos(), block, event.getState());
		} else {
			handler.removeComponent(event.getPos(), event.getState());
		}
	}
	
	@SubscribeEvent
	public static void onConduitStateChange(ConduitEvent event) {
		Level level = (Level) event.getLevel();
		ElectricNetworkHandlerCapability handler = GameUtility.getCapability(level, ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		if (event.getConduitState().getConduit() instanceof IElectricConduit) {
			if (event instanceof ConduitPlaceEvent || event instanceof ConduitLoadEvent) {
				IElectricConduit conduit = (IElectricConduit) event.getConduitState().getConduit();
				handler.addComponent(event.getPosition(), conduit, event.getConduitState());
			} else if (event instanceof ConduitBreakEvent || event instanceof ConduitUnloadEvent) {
				handler.removeComponent(event.getPosition(), event.getConduitState());
			}
		}
	}
	
//	@SuppressWarnings("unchecked")
//	@SubscribeEvent
//	public static void onClientLoadsChunk(ChunkWatchEvent.Watch event) {
//		Level level = event.getPlayer().getLevel();
//		ConduitHandlerCapability conduitHandler = GameUtility.getCapability(level, ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
//		ElectricNetworkHandlerCapability electricHandler = GameUtility.getCapability(level, ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
//		for (PlacedConduit conduit : conduitHandler.getConduitsInChunk(event.getPos())) {
//			if (conduit.getConduit() instanceof @SuppressWarnings("rawtypes") IElectric electricConduit) {
//				electricHandler.addComponent(conduit.getPosition(), electricConduit, conduit);
//			}
//		}
//	}
//	
//	@SubscribeEvent
//	public static void onClientUnloadsChunk(ChunkWatchEvent.UnWatch event) {
//		Level level = event.getPlayer().getLevel();
//		ConduitHandlerCapability conduitHandler = GameUtility.getCapability(level, ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
//		ElectricNetworkHandlerCapability electricHandler = GameUtility.getCapability(level, ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
//		for (PlacedConduit conduit : conduitHandler.getConduitsInChunk(event.getPos())) {
//			if (conduit.getConduit() instanceof IElectric) {
//				electricHandler.removeComponent(conduit.getPosition(), conduit);;
//			}
//		}
//	}
	
	/* ElectricNetwork handling */
	
	/**
	 * Represents a component (can be a conduit or a block) in the electric networks
	 */
	public static class Component<I, P, T> {
		protected P pos;
		protected I instance;
		protected IElectric<I, P, T> type;
		
		public Component(P pos, IElectric<I, P, T> type, I instance) {
			this.type = type;
			this.instance = instance;
			this.pos = pos;
		}
		
		public P pos() {
			return pos;
		}
		
		public IElectric<I, P, T> type() {
			return type;
		}
		
		public I instance() {
			return instance;
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(this.type, this.pos);
		}
		
		@Override
		public String toString() {
			return "Component{pos=" + this.pos() + ",type=" + this.type.toString() + ",instance=" + this.instance.toString() + "}#hash=" + this.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj instanceof @SuppressWarnings("rawtypes") Component other) {
				return this.type.equals(other.type) && this.pos.equals(other.pos);
			}
			return false;
		}
		
		public void serializeNbt(CompoundTag nbt) {
			IElectric.Type componentType = IElectric.Type.getType(this.type);
			this.type.serializeNBT(instance, pos, nbt);
			nbt.putString("Type", this.type.getRegistryName().toString());
			nbt.putString("ComponentType", componentType.name().toLowerCase());
		}
		public static <I, P, T> Component<I, P, T> deserializeNbt(CompoundTag nbt) {
			IElectric.Type componentType = IElectric.Type.valueOf(nbt.getString("ComponentType").toUpperCase());
			ResourceLocation typeName = new ResourceLocation(nbt.getString("Type"));
			Object typeObject = componentType.getRegistry().getValue(typeName);
			if (typeObject instanceof IElectric) {
				@SuppressWarnings("unchecked")
				IElectric<I, P, T> type = (IElectric<I, P, T>) typeObject;
				I instance = type.deserializeNBTInstance(nbt);
				P position = type.deserializeNBTPosition(nbt);
				return new Component<I, P, T>(position, type, instance);
			}
			return null;
		}
		public void plotCircuit(Level level, ElectricNetwork circuit, Consumer<CircuitTemplate> plotter) {
			type.plotCircuit(level, instance, pos, circuit, plotter);
		}
		public NodePos[] getNodes(Level level) {
			return type.getConnections(level, pos, instance);
		}
		public void onNetworkChange(Level level) {
			type.onNetworkNotify(level, instance, pos);
		}
		public String[] getWireLanes(Level level, NodePos node) {
			return type.getWireLanes(pos, instance, node);
		}
		public void notifyRewired(Level level, Component<?, ?, ?> neighbor) {
			type.neighborRewired(level, instance, pos, neighbor);
		}
	}
//	public static record Component<I, P, T>(P pos, IElectric<I, P, T> type, I instance) {
//		public void serializeNbt(CompoundTag nbt) {
//			IElectric.Type componentType = IElectric.Type.getType(this.type);
//			this.type.serializeNBT(instance, pos, nbt);
//			nbt.putString("Type", this.type.getRegistryName().toString());
//			nbt.putString("ComponentType", componentType.name().toLowerCase());
//		}
//		public static <I, P, T> Component<I, P, T> deserializeNbt(CompoundTag nbt) {
//			IElectric.Type componentType = IElectric.Type.valueOf(nbt.getString("ComponentType").toUpperCase());
//			ResourceLocation typeName = new ResourceLocation(nbt.getString("Type"));
//			Object typeObject = componentType.getRegistry().getValue(typeName);
//			if (typeObject instanceof IElectric) {
//				@SuppressWarnings("unchecked")
//				IElectric<I, P, T> type = (IElectric<I, P, T>) typeObject;
//				I instance = type.deserializeNBTInstance(nbt);
//				P position = type.deserializeNBTPosition(nbt);
//				return new Component<I, P, T>(position, type, instance);
//			}
//			return null;
//		}
//		public void plotCircuit(Level level, ElectricNetwork circuit) {
//			type.plotCircuit(level, instance, pos, circuit);
//		}
//		public NodePos[] getNodes(Level level) {
//			return type.getConnections(level, pos, instance);
//		}
//		public void onNetworkChange(Level level) {
//			type.onNetworkNotify(level, instance, pos);
//		}
//	}
	
	/**
	 * Returns the circuit which contains this components
	 */
	public ElectricNetwork getCircuit(Component<?, ?, ?> component) {
		return this.component2circuitMap.get(component);
	}
	
	/**
	 * Returns the component with the given position
	 */
	@SuppressWarnings("unchecked")
	public <I, P, T> Component<I, P, T> getComponent(P position) {
		return (Component<I, P, T>) this.pos2componentMap.get(position);
	}
	
	/**
	 * Returns the voltage for the node calculated last time the network was updated
	 */
	public double getVoltageAt(NodePos node) {
		Set<Component<?, ?, ?>> components = this.node2componentMap.get(node);
		if (components != null && components.size() > 0) {
			ElectricNetwork circuit = getCircuit(components.toArray(new Component<?, ?, ?>[] {})[0]);
			if (circuit != null) {
				double voltage =  circuit.getVoltage(node);
				return Double.isFinite(voltage) ? voltage : 0D;
			}
		}
		return 0D;
	}
	
//	/*
//	 * Returns the serial resistance between two nodes calculated last time the network was updated
//	 */
//	public double getSerialResistance(NodePos nodeA, NodePos nodeB) {
//		Set<Component<?, ?, ?>> components = this.node2componentMap.get(nodeA);
//		if (components != null && components.size() > 0) {
//			ElectricNetwork circuit = getCircuit(components.stream().findAny().get()); // .toArray(new Component<?, ?, ?>[] {})[0]
//			if (circuit != null) {
//				double resistance =  circuit.getSerialResistance(nodeA, nodeB);
//				return Double.isFinite(resistance) ? resistance : Double.MAX_VALUE;
//			}
//		}
//		return Double.MAX_VALUE;
//	}
	
//	/*
//	 * Returns the parallel resistance (resistance to ground) for the node calculated last time the network was updated
//	 * Ignores the resistance of other components connected to the node via a wire
//	 */
//	public double getParallelResistance(NodePos node) {
//		Set<Component<?, ?, ?>> components = this.node2componentMap.get(node);
//		if (components != null && components.size() > 0) {
//			ElectricNetwork circuit = getCircuit(components.toArray(new Component<?, ?, ?>[] {})[0]);
//			if (circuit != null) {
//				double resistance =  circuit.getParallelResistance(node);
//				return Double.isFinite(resistance) ? resistance : Double.MAX_VALUE;
//			}
//		}
//		return Double.MAX_VALUE;
//	}
	
//	/*
//	 * Returns the current flowing between two nodes calculated last time the network was updated
//	 */
//	public double getCurrent(Component<?, ?, ?> component, NodePos nodeA, NodePos nodeB) {
//		double voltageA = getVoltageAt(nodeA);
//		double voltageB = getVoltageAt(nodeB);
//		double resistance = getSerialResistance(nodeA, nodeB);
//		return Math.abs(voltageA - voltageB) / resistance;
//	}
	
	/**
	 * Returns a Set containing all components attached to the given node
	 */
	public Set<Component<?, ?, ?>> findComponentsOnNode(NodePos node) {
		return this.node2componentMap.getOrDefault(node, new HashSet<>());
	}
	
	/**
	 * Notifies all neighbors that the given component has changed its wire lane configuration
	 */
	public void notifyRewired(Component<?, ?, ?> component) {
		Set<Component<?, ?, ?>> toNotify = new HashSet<>();
		for (NodePos node : component.getNodes(level)) toNotify.addAll(findComponentsOnNode(node));
		for (Component<?, ?, ?> neighbor : toNotify) {
			if (neighbor != component) component.notifyRewired(level, component);
		}
	}
	
	/**
	 * Removes a component from the network and updates it and its components
	 */
	public <I, P, T> void removeComponent(P pos, I state) {
		if (this.pos2componentMap.containsKey(pos)) {
			Component<?, ?, ?> component = removeFromNetwork(pos);
			
			if (component != null) {
				Set<Component<?, ?, ?>> componentsToUpdate = new HashSet<Component<?, ?, ?>>();
				NodePos[] nodes = component.getNodes(level);
				for (int i = 0; i < nodes.length; i++) {
					Set<Component<?, ?, ?>> components = this.node2componentMap.get(nodes[i]);
					if (components != null) componentsToUpdate.addAll(components);
				}
				if (componentsToUpdate.isEmpty()) {
					ElectricNetwork emptyNetwork = this.component2circuitMap.remove(component);
					if (emptyNetwork != null) this.circuitNetworks.remove(emptyNetwork);
				}
				componentsToUpdate.forEach((comp) -> updateNetwork(comp.pos()));
			}
		}
	}
	
	/**
	 * Adds a component to the network and updates it and its components
	 */
	public <I, P, T> void addComponent(P pos, IElectric<I, P, T> type, I instance) {
		Component<?, ?, ?> component = this.pos2componentMap.get(pos);
		if (component != null) {
			if (component.type.equals(type) && component.instance.equals(instance)) {
				return;
			} else {
				removeFromNetwork(pos);
			}
		}
		Component<I, P, T> component2 = new Component<I, P, T>(pos, type, instance);
		addToNetwork(component2);
		notifyRewired(component2);
		updateNetwork(pos);
	}
	
	/**
	 * Adds a component to the network but does not cause any updates
	 */
	public <I, P, T> void addToNetwork(Component<I, P, T> component) {
		this.pos2componentMap.put(component.pos, component);
		for (NodePos node : component.type().getConnections(this.level, component.pos, component.instance())) {
			Set<Component<?, ?, ?>> componentSet = this.node2componentMap.getOrDefault(node, new HashSet<Component<?, ?, ?>>());
			componentSet.add(component);
			this.node2componentMap.put(node, componentSet);
		}
	}
	
	/**
	 * Removes a component from the network but does not cause any updates
	 */
	public <I, P, T> Component<I, P, T> removeFromNetwork(P pos) {
		@SuppressWarnings("unchecked")
		Component<I, P, T> component = (Component<I, P, T>) this.pos2componentMap.remove(pos);
		if (component != null) {
			for (NodePos node : component.type().getConnections(this.level, component.pos, component.instance())) {
				Set<Component<?, ?, ?>> componentSet = this.node2componentMap.getOrDefault(node, new HashSet<Component<?, ?, ?>>());
				componentSet.remove(component);
				if (componentSet.isEmpty()) {
					this.node2componentMap.remove(node);
				} else {
					this.node2componentMap.put(node, componentSet);
				}
			}
		}
		return component;
	}
	
	/**
	 * Updates the network which has a component at the given position
	 */
	public <P> void updateNetwork(P position) {
		
		Component<?, ?, ?> component = this.pos2componentMap.get(position);
		
		if (component != null) {
			
			ElectricNetwork circuit = this.component2circuitMap.get(component);
			
			if (circuit == null || !circuit.getComponents().contains(component)) circuit = new ElectricNetwork("ingame-level-circuit");
			if (circuit.updatedInFrame(this.level.getGameTime())) return;
			circuit.reset();
			buildCircuit(component, circuit);
			if (circuit.isEmpty()) return;
			if (!this.circuitNetworks.contains(circuit)) this.circuitNetworks.add(circuit);
			
			final ElectricNetwork circuitFinalized = circuit;
//			if (!circuitFinalized.isPlotEmpty()) {
//				SPICE.processCircuit(circuit);
//				SPICE.vectorData().keySet().stream().filter((s) -> s.startsWith("node")).forEach((hashName) ->  {
//					Set<NodePos> nodes = circuitFinalized.getNodes(hashName);
//					if (nodes != null) {
//						nodes.forEach((node) -> circuitFinalized.getNodeVoltages().put(node, SPICE.vectorData().get(hashName)));
//					}
//				});
//			}
			
			circuit.getComponents().forEach((comp) -> {
				ElectricNetwork previousNetwork = this.component2circuitMap.put(comp, circuitFinalized);
				if (previousNetwork != null && previousNetwork != circuitFinalized) {
					previousNetwork.getComponents().remove(comp);
					if (previousNetwork.getComponents().isEmpty()) this.circuitNetworks.remove(previousNetwork);
				}
				comp.onNetworkChange(level);
			});
			
		}
		
	}
	
	/**
	 * Builds a ngspice netlist for the given network begining from the given gomponent
	 */
	private void buildCircuit(Component<?, ?, ?> component, ElectricNetwork circuit) {
		buildCircuit0(component, null, circuit);
		circuit.complete(this.level.getGameTime());
	}
	private void buildCircuit0(Component<?, ?, ?> component, NodePos node, ElectricNetwork circuit) {
		
		if (circuit.getComponents().contains(component)) return;
		circuit.getComponents().add(component);
		component.plotCircuit(level, circuit, template -> circuit.plotTemplate(component, template));
		
		for (NodePos node2 : component.getNodes(level)) {
			if (node2.equals(node) || this.node2componentMap.get(node2) == null) continue; 
			for (Component<?, ?, ?> component2 : this.node2componentMap.get(node2)) {
				buildCircuit0(component2, node2, circuit);
			}
		}
		
	}
	
}
