package de.m_marvin.industria.util.electricity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.block.IElectricConnector;
import de.m_marvin.industria.util.conduit.ConduitEvent;
import de.m_marvin.industria.util.conduit.ConduitEvent.ConduitPlaceEvent;
import de.m_marvin.industria.util.conduit.IElectricConduit;
import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
	private HashMap<ConnectionPoint, Set<Component<?, ?, ?>>> node2componentMap = new HashMap<ConnectionPoint, Set<Component<?, ?, ?>>>();
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
			if (!circuitNetwork.isEmpty()) {
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
		if (!event.getWorld().isClientSide()) {
			ServerLevel level = (ServerLevel) event.getWorld();
			LazyOptional<ElectricNetworkHandlerCapability> networkHandler = level.getCapability(ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
			if (networkHandler.isPresent()) {
				if (event.getState().getBlock() instanceof IElectricConnector) {
					IElectricConnector block = (IElectricConnector) event.getState().getBlock();
					networkHandler.resolve().get().addComponent(event.getPos(), block, event.getState());
				} else {
					networkHandler.resolve().get().removeComponent(event.getPos(), event.getState());
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onConduitStateChange(ConduitEvent event) {
		if (!event.getLevel().isClientSide()) {
			ServerLevel level = (ServerLevel) event.getLevel();
			LazyOptional<ElectricNetworkHandlerCapability> networkHandler = level.getCapability(ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
			if (networkHandler.isPresent()) {
				if (event.getConduitState().getConduit() instanceof IElectricConduit && event instanceof ConduitPlaceEvent) {
					IElectricConduit conduit = (IElectricConduit) event.getConduitState().getConduit();
					networkHandler.resolve().get().addComponent(event.getPosition(), conduit, event.getConduitState());
				} else {
					networkHandler.resolve().get().removeComponent(event.getPosition(), event.getConduitState());
				}
			}
		}
	}
	
	/* ElectricNetwork handling */
	
	/*
	 * Represents a component (can be a conduit or a block) in the electric networks
	 */
	public static record Component<I, P, T>(P pos, IElectric<I, P, T> type, I instance) {
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
		public void plotCircuit(Level level, ElectricNetwork circuit) {
			type.plotCircuit(level, instance, pos, circuit);
		}
		public ConnectionPoint[] getNodes(Level level) {
			return type.getConnections(level, pos, instance);
		}
		public void onNetworkChange(Level level) {
			type.onNetworkNotify(level, instance, pos);
		}
	}
	
	/*
	 * Returns the circuit which contains this components
	 */
	public ElectricNetwork getCircuit(Component<?, ?, ?> component) {
		return this.component2circuitMap.get(component);
	}
	
	/*
	 * Returns the voltage for the node calculated last time the network was updated
	 */
	public double getVoltageAt(ConnectionPoint node) {
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
	
	/*
	 * Returns the serial resistance between two nodes calculated last time the network was updated
	 */
	public double getSerialResistance(ConnectionPoint nodeA, ConnectionPoint nodeB) {
		Set<Component<?, ?, ?>> components = this.node2componentMap.get(nodeA);
		if (components != null && components.size() > 0) {
			ElectricNetwork circuit = getCircuit(components.toArray(new Component<?, ?, ?>[] {})[0]);
			if (circuit != null) {
				double resistance =  circuit.getSerialResistance(nodeA, nodeB);
				return Double.isFinite(resistance) ? resistance : Double.MAX_VALUE;
			}
		}
		return Double.MAX_VALUE;
	}
	
	/*
	 * Returns the parallel resistance (resistance to ground) for the node calculated last time the network was updated
	 * Ignores the resistance of other components connected to the node via a wire
	 */
	public double getParallelResistance(ConnectionPoint node) {
		Set<Component<?, ?, ?>> components = this.node2componentMap.get(node);
		if (components != null && components.size() > 0) {
			ElectricNetwork circuit = getCircuit(components.toArray(new Component<?, ?, ?>[] {})[0]);
			if (circuit != null) {
				double resistance =  circuit.getParallelResistance(node);
				return Double.isFinite(resistance) ? resistance : Double.MAX_VALUE;
			}
		}
		return Double.MAX_VALUE;
	}
	
	/*
	 * Returns the current flowing between two nodes calculated last time the network was updated
	 */
	public double getCurrent(Component<?, ?, ?> component, ConnectionPoint nodeA, ConnectionPoint nodeB) {
		double voltageA = getVoltageAt(nodeA);
		double voltageB = getVoltageAt(nodeB);
		double resistance = getSerialResistance(nodeA, nodeB);
		return Math.abs(voltageA - voltageB) / resistance;
	}
	
	/*
	 * Removes a component from the network and updates it and its components
	 */
	public <I, P, T> void removeComponent(P pos, I state) {
		if (this.pos2componentMap.containsKey(pos)) {
			Component<?, ?, ?> component = removeFromNetwork(pos);
			
			if (component != null) {
				Set<Component<?, ?, ?>> componentsToUpdate = new HashSet<Component<?, ?, ?>>();
				ConnectionPoint[] nodes = component.getNodes(level);
				for (int i = 0; i < nodes.length; i++) {
					ConnectionPoint node = nodes[i];
					componentsToUpdate.addAll(this.node2componentMap.get(node));
				}
				if (componentsToUpdate.isEmpty()) {
					ElectricNetwork emptyNetwork = this.component2circuitMap.remove(component);
					if (emptyNetwork != null) this.circuitNetworks.remove(emptyNetwork);
				}
				componentsToUpdate.forEach((comp) -> updateNetwork(comp.pos()));
			}
		}
	}
	
	/*
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
		updateNetwork(pos);
	}
	
	/*
	 * Adds a component to the network but does not couse any updates
	 */
	public <I, P, T> void addToNetwork(Component<I, P, T> component) {
		this.pos2componentMap.put(component.pos, component);
		for (ConnectionPoint node : component.type().getConnections(this.level, component.pos, component.instance())) {
			Set<Component<?, ?, ?>> componentSet = this.node2componentMap.getOrDefault(node, new HashSet<Component<?, ?, ?>>());
			componentSet.add(component);
			this.node2componentMap.put(node, componentSet);
		}
	}
	
	/*
	 * Removes a component from the network but does not couse any updates
	 */
	public <I, P, T> Component<I, P, T> removeFromNetwork(P pos) {
		@SuppressWarnings("unchecked")
		Component<I, P, T> component = (Component<I, P, T>) this.pos2componentMap.remove(pos);
		for (ConnectionPoint node : component.type().getConnections(this.level, component.pos, component.instance())) {
			Set<Component<?, ?, ?>> componentSet = this.node2componentMap.getOrDefault(node, new HashSet<Component<?, ?, ?>>());
			componentSet.remove(component);
			this.node2componentMap.put(node, componentSet);
		}
		return component;
	}
	
	/*
	 * Updates the network which has a component at the given position
	 */
	public <P> void updateNetwork(P position) {
		
		Component<?, ?, ?> component = this.pos2componentMap.get(position);
		
		if (component != null) {
			
			ElectricNetwork circuit = this.component2circuitMap.getOrDefault(component, new ElectricNetwork("ingame-level-circuit"));
			if (!circuit.getComponents().contains(component)) circuit = new ElectricNetwork("ingame-level-circuit");
			if (circuit.updatedInFrame(this.level.getGameTime())) return;
			circuit.reset();
			buildCircuit(component, circuit);
			if (circuit.isEmpty()) return;
			this.circuitNetworks.add(circuit);
			
			final ElectricNetwork circuitFinalized = circuit;
			SPICE.processCircuit(circuit);
			SPICE.vectorData().keySet().stream().filter((s) -> s.startsWith("node")).forEach((hashName) ->  {
				Set<ConnectionPoint> nodes = circuitFinalized.getNodes(hashName);
				if (nodes != null) {
					nodes.forEach((node) -> circuitFinalized.nodeVoltages.put(node, SPICE.vectorData().get(hashName)));
				}
			});
			
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
	
	/*
	 * Builds a ngspice netlist for the given network begining from the given gomponent
	 */
	private void buildCircuit(Component<?, ?, ?> component, ElectricNetwork circuit) {
		buildCircuit0(component, null, circuit);
		circuit.complete(this.level.getGameTime());
	}
	private void buildCircuit0(Component<?, ?, ?> component, ConnectionPoint node, ElectricNetwork circuit) {
		
		if (circuit.getComponents().contains(component)) return;
		circuit.getComponents().add(component);
		component.plotCircuit(level, circuit);
		
		for (ConnectionPoint node2 : component.getNodes(level)) {
			if (node2.equals(node)) continue; 
			for (Component<?, ?, ?> component2 : this.node2componentMap.get(node2)) {
				buildCircuit0(component2, node2, circuit);
			}
		}
		
	}
	
}
