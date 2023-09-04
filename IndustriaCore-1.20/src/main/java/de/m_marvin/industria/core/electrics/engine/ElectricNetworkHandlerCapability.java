package de.m_marvin.industria.core.electrics.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.base.Objects;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.ConduitEvent;
import de.m_marvin.industria.core.conduits.engine.ConduitEvent.ConduitBreakEvent;
import de.m_marvin.industria.core.conduits.engine.ConduitEvent.ConduitPlaceEvent;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.conduits.IElectricConduit;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.network.SSyncComponentsPackage;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.IElectric;
import de.m_marvin.industria.core.electrics.types.IElectric.ICircuitPlot;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricConnector;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.SyncRequestType;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ElectricNetworkHandlerCapability implements ICapabilitySerializable<ListTag> {
	
	public static final String CIRCUIT_FILE_NAME = "circuit_";
	public static final String CIRCUIT_FILE_EXTENSION = ".net";
	
	/* Capability handling */
	
	private LazyOptional<ElectricNetworkHandlerCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
	private final Level level;
	private final HashMap<Object, Component<?, ?, ?>> pos2componentMap = new HashMap<Object, Component<?, ?, ?>>();
	private final HashMap<NodePos, Set<Component<?, ?, ?>>> node2componentMap = new HashMap<NodePos, Set<Component<?, ?, ?>>>();
	private final HashSet<ElectricNetwork> circuitNetworks = new HashSet<ElectricNetwork>();
	private final HashMap<Component<?, ?, ?>, ElectricNetwork> component2circuitMap = new HashMap<Component<?, ?, ?>, ElectricNetwork>();
	private int circuitFileCounter;
	
	public Level getLevel() {
		return level;
	}
	
	@Override
	public ListTag serializeNBT() {
		this.circuitFileCounter = 0;
		
		ListTag nbt2 = new ListTag();
		int componentCount = 0;
		for (ElectricNetwork circuitNetwork : this.circuitNetworks) {
			circuitNetwork.removeInvalidComponents();
			if (circuitNetwork.isEmpty()) continue;
			nbt2.add(circuitNetwork.saveNBT(this));
			componentCount += circuitNetwork.getComponents().size();
		}
		cleanupUnusedCircuitFiles();
		
		IndustriaCore.LOGGER.info("Saved " + nbt2.size() + " electric networks");
		IndustriaCore.LOGGER.info("Saved " + componentCount + " electric components");
		return nbt2;
	}
	
	@Override
	public void deserializeNBT(ListTag nbt) {
		terminateNetworks();
		this.pos2componentMap.clear();
		this.node2componentMap.clear();
		this.circuitNetworks.clear();
		this.component2circuitMap.clear();
		
		for (int i = 0; i < nbt.size(); i++) {
			CompoundTag circuitTag = nbt.getCompound(i);
			ElectricNetwork circuitNetwork = new ElectricNetwork("ingame-level-circuit");
			circuitNetwork.loadNBT(this, circuitTag);
			if (!circuitNetwork.isEmpty()) {
				this.circuitNetworks.add(circuitNetwork);
				circuitNetwork.getComponents().forEach((component) -> {
					this.component2circuitMap.put(component, circuitNetwork);
					if (!this.pos2componentMap.containsValue(component)) {
						this.addToNetwork(component);
					}
				});
			}
		}
		
		startNetworks();
		IndustriaCore.LOGGER.info("Loaded " + this.circuitNetworks.size() + "/" + nbt.size() + " electric networks");
		IndustriaCore.LOGGER.info("Loaded " + this.pos2componentMap.size() + " electric components");
	}
	
	public String saveCircuit(String netList) {
		if (this.level instanceof ServerLevel serverLevel) {
			String circuit = CIRCUIT_FILE_NAME + circuitFileCounter++;
			File file = new File(((ServerLevel) this.level).getDataStorage().dataFolder, circuit + CIRCUIT_FILE_EXTENSION);
			
			try {
				OutputStream outputStream = new FileOutputStream(file);
				outputStream.write(netList.getBytes());
				outputStream.close();
				return circuit;
			} catch (IOException e) {
				IndustriaCore.LOGGER.error("Could not save circuit net '" + circuit + "' to file!");
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public void cleanupUnusedCircuitFiles() {
		File dataFolder = ((ServerLevel) this.level).getDataStorage().dataFolder;
		File circuitFile;
		int counter = this.circuitFileCounter;
		while ((circuitFile = new File(dataFolder, CIRCUIT_FILE_NAME + counter++ + CIRCUIT_FILE_EXTENSION)).isFile()) {
			try {
				Files.delete(circuitFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String loadCircuit(String circuit) {
		if (this.level instanceof ServerLevel serverLevel) {
			File file = new File(((ServerLevel) this.level).getDataStorage().dataFolder, circuit + CIRCUIT_FILE_EXTENSION);
			
			try {
				InputStream inputStream = new FileInputStream(file);
				String netList = new String(inputStream.readAllBytes());
				inputStream.close();
				return netList;
			} catch (IOException e) {
				IndustriaCore.LOGGER.error("Could not load circuit net '" + circuit + "' from file!");
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public ElectricNetworkHandlerCapability(Level level) {
		this.level = level;
	}
	
	/* Event handling */
	
	@SubscribeEvent
	public static void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		Level level = (Level) event.getLevel();
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
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
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		if (event.getConduitState().getConduit() instanceof IElectricConduit) {
			if (event instanceof ConduitPlaceEvent) {
				IElectricConduit conduit = (IElectricConduit) event.getConduitState().getConduit();
				handler.addComponent(event.getPosition(), conduit, event.getConduitState());
			} else if (event instanceof ConduitBreakEvent) {
				handler.removeComponent(event.getPosition(), event.getConduitState());
			}
		}
	}
	
	@SubscribeEvent
	public static void onClientLoadsChunk(ChunkWatchEvent.Watch event) {
		Level level = event.getPlayer().level();
		ElectricNetworkHandlerCapability electricHandler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		Set<Component<?, ?, ?>> componentsInChunk = electricHandler.findComponentsInChunk(event.getPos());
		Set<Component<?, ?, ?>> components = electricHandler.findComponentsConnectedWith(componentsInChunk.toArray(i -> new Component[i]));
		if (!components.isEmpty()) {
			IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> event.getChunk()), new SSyncComponentsPackage(components, event.getChunk().getPos(), SyncRequestType.ADDED));
		}
	}
	
	@SubscribeEvent
	public static void onClientUnloadsChunk(ChunkWatchEvent.UnWatch event) {
		Level level = event.getPlayer().level();
		ElectricNetworkHandlerCapability electricHandler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		Set<Component<?, ?, ?>> components = electricHandler.findComponentsInChunk(event.getPos());
		if (!components.isEmpty()) {
			 IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunk(event.getPos().x, event.getPos().z)), new SSyncComponentsPackage(components, event.getPos(), SyncRequestType.REMOVED));
		}
	}
	
	@SubscribeEvent
	public static void onWorldUnloadEvent(LevelEvent.Unload event) {
		if (!event.getLevel().isClientSide()) {
			ElectricNetworkHandlerCapability electricHandler = GameUtility.getLevelCapability((ServerLevel) event.getLevel(), Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
			electricHandler.terminateNetworks();
		}
	}
	
	/* ElectricNetwork handling */
	
	/**
	 * Represents a component (can be a conduit or a block) in the electric networks
	 */
	public static class Component<I, P, T> {
		protected P pos;
		protected boolean preLoadedInstance;
		protected I instance;
		protected IElectric<I, P, T> type;
		
		public Component(P pos, IElectric<I, P, T> type, I instance) {
			this.type = type;
			this.instance = instance;
			this.pos = pos;
			this.preLoadedInstance = true;
		}
		
		public P pos() {
			return pos;
		}
		
		public IElectric<I, P, T> type() {
			return type;
		}
		
		public I instance(Level level) {
			if (this.preLoadedInstance && level != null) {
				Optional<I> instanceLoaded = this.type.getInstance(level, this.pos);
				if (instanceLoaded.isPresent()) {
					this.instance = instanceLoaded.get();
					this.preLoadedInstance = false;
				}
			}
			return instance;
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(this.type, this.pos);
		}
		
		@Override
		public String toString() {
			return "Component{pos=" + this.pos() + ",type=" + this.type.toString() + ",instance=" + (this.instance(null) == null ? "N/A" : this.instance(null).toString()) + "}#hash=" + this.hashCode();
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
			this.type.serializeNBTPosition(pos, nbt);
			nbt.putString("Type", componentType.getRegistry().getKey(this.type).toString());
			nbt.putString("ComponentType", componentType.name().toLowerCase());
			this.type.serializeNBTInstance(instance, nbt);
		}
		public static <I, P, T> Component<I, P, T> deserializeNbt(CompoundTag nbt) {
			IElectric.Type componentType = IElectric.Type.valueOf(nbt.getString("ComponentType").toUpperCase());
			ResourceLocation typeName = new ResourceLocation(nbt.getString("Type"));
			Object typeObject = componentType.getRegistry().getValue(typeName);
			if (typeObject instanceof IElectric) {
				@SuppressWarnings("unchecked")
				IElectric<I, P, T> type = (IElectric<I, P, T>) typeObject;
				P position = type.deserializeNBTPosition(nbt);
				I instance = type.deserializeNBTInstance(nbt);
				return new Component<I, P, T>(position, type, instance);
			}
			return null;
		}
		public void plotCircuit(Level level, ElectricNetwork circuit, Consumer<ICircuitPlot> plotter) {
			type.plotCircuit(level, instance(level), pos, circuit, plotter);
		}
		public NodePos[] getNodes(Level level) {
			return type.getConnections(level, pos, instance(level));
		}
		public void onNetworkChange(Level level) {
			type.onNetworkNotify(level, instance(level), pos);
		}
		public String[] getWireLanes(Level level, NodePos node) {
			return type.getWireLanes(level, pos, instance(level), node);
		}
		public void setWireLanes(Level level, NodePos node, String[] laneLabels) {
			String[] oldLanes = type.getWireLanes(level, pos, instance(level), node);
			type.setWireLanes(level, pos, instance(level), node, laneLabels);
			for (int i = 0; i < oldLanes.length && i < laneLabels.length; i++) {
				if (!oldLanes[i].equals(laneLabels[i])) {
					ElectricUtility.updateNetwork(level, pos);
				}
			}
		}
		public boolean isWire() {
			return type.isWire();
		}
		public ChunkPos getAffectedChunk(Level level) {
			return type.getAffectedChunk(level, pos);
		}
	}
	
	/**
	 * Returns the circuit which contains this components
	 */
	public ElectricNetwork getCircuitWithComponent(Component<?, ?, ?> component) {
		return this.component2circuitMap.get(component);
	}
	
	/**
	 * Returns the component with the given position
	 */
	@SuppressWarnings("unchecked")
	public <I, P, T> Component<I, P, T> getComponentAt(P position) {
		return (Component<I, P, T>) this.pos2componentMap.get(position);
	}
	
	/**
	 * Returns a set containing all components attached to the given node
	 */
	public Set<Component<?, ?, ?>> findComponentsOnNode(NodePos node) {
		return this.node2componentMap.getOrDefault(node, new HashSet<>());
	}
	
	/**
	 * Returns a set containing all components in the given chunk
	 */
	public Set<Component<?, ?, ?>> findComponentsInChunk(ChunkPos chunkPos) {
		Set<Component<?, ?, ?>> components = new HashSet<>();
		for (Entry<Object, Component<?, ?, ?>> componentEntry : this.pos2componentMap.entrySet()) {
			if (componentEntry.getValue().getAffectedChunk(level).equals(chunkPos)) components.add(componentEntry.getValue());
		}
		return components;
	}
	
	/**
	 * Returns a set containing all components connected with the given components
	 */
	public Set<Component<?, ?, ?>> findComponentsConnectedWith(Component<?, ?, ?>... components) {
		Set<ElectricNetwork> networks = new HashSet<>();
		Set<Component<?, ?, ?>> componentsConnected = new HashSet<>();
		for (Component<?, ?, ?> component : components) {
			ElectricNetwork circuit = this.component2circuitMap.get(component);
			if (!networks.contains(circuit)) {
				networks.add(circuit);
				componentsConnected.addAll(circuit.getComponents());
			}
		}
		return componentsConnected;
	}
	
	/**
	 * Returns the floating voltage currently available on the given node.
	 * NOTE: Floating means that the voltage is referenced to "global ground", meaning a second voltage is required to calculate the actual difference (the voltage) between the two nodes.
	 */
	public double getFloatingNodeVoltage(NodePos node, int laneId, String lane) {
		Set<Component<?, ?, ?>> components = this.node2componentMap.get(node);
		if (components == null) return 0.0;
		Component<?, ?, ?> component = components.stream().findAny().orElseGet(() -> this.pos2componentMap.get(node.getBlock()));
		if (component != null) {
			ElectricNetwork network = this.component2circuitMap.get(component);
			if (network != null) {
				return network.getFloatingNodeVoltage(node, laneId, lane);
			}
		}
		return 0.0;
	}
	
	/**
	 * Updates the network which has a component at the given position
	 */
	public <P> void updateNetwork(P position) {
		
		Component<?, ?, ?> component = this.pos2componentMap.get(position);
		
		if (component != null) {
			
			ElectricNetwork circuit = this.component2circuitMap.get(component);
			
			if (circuit == null || !circuit.getComponents().contains(component)) circuit = new ElectricNetwork("ingame-level-circuit");
			//if (circuit.updatedInFrame(this.level.getGameTime())) return;
			circuit.reset();
			buildCircuit(component, circuit);
			if (circuit.isEmpty()) return;
			
			if (!this.circuitNetworks.contains(circuit)) this.circuitNetworks.add(circuit);
			
			final ElectricNetwork circuitFinalized = circuit;
			circuit.getComponents().forEach((comp) -> {
				ElectricNetwork previousNetwork = this.component2circuitMap.put(comp, circuitFinalized);
				if (previousNetwork != null && previousNetwork != circuitFinalized) {
					previousNetwork.getComponents().remove(comp);
					if (previousNetwork.getComponents().isEmpty()) {
						previousNetwork.terminateExecution();
						this.circuitNetworks.remove(previousNetwork);
					}
				}
			});
			
			circuit.updateSimulation();
			
			circuit.getComponents().forEach((comp) -> { 
				comp.onNetworkChange(level);
			});
			
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
					if (emptyNetwork != null) {
						emptyNetwork.terminateExecution();
						this.circuitNetworks.remove(emptyNetwork);
					}
				}
				if (!this.level.isClientSide) {
					ChunkPos chunkPos = component.getAffectedChunk(level);
					IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunk(chunkPos.x, chunkPos.z)), new SSyncComponentsPackage(component, chunkPos, SyncRequestType.REMOVED));
				}
				for (Component<?, ?, ?> comp : componentsToUpdate) {
					updateNetwork(comp.pos());
				}
			}
		}
	}
	
	/**
	 * Adds a component to the network and updates it and its components
	 */
	public <I, P, T> void addComponent(P pos, IElectric<I, P, T> type, I instance) {
		Component<?, ?, ?> component = this.pos2componentMap.get(pos);
		if (component != null) {
			if (component.type.equals(type)) {
				return;
			} else {
				removeFromNetwork(pos);
			}
		}
		Component<I, P, T> component2 = new Component<I, P, T>(pos, type, instance);
		addToNetwork(component2);
		if (!this.level.isClientSide) {
			ChunkPos chunkPos = component2.getAffectedChunk(level);
			IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunk(chunkPos.x, chunkPos.z)), new SSyncComponentsPackage(component2, chunkPos, SyncRequestType.ADDED));
		}
		updateNetwork(pos);
	}
	
	/**
	 * Returns true if the component is already registered for an network
	 */
	public boolean isInNetwork(Component<?, ?, ?> component) {
		if (component.instance(level) == null) return false;
		return this.component2circuitMap.containsKey(component);
	}
	
	/**
	 * Adds a component to the network but does not cause any updates
	 */
	public <I, P, T> void addToNetwork(Component<I, P, T> component) {
		if (component.instance(level) == null) return;
		this.pos2componentMap.put(component.pos, component);
		for (NodePos node : component.type().getConnections(this.level, component.pos, component.instance(level))) {
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
			for (NodePos node : component.type().getConnections(this.level, component.pos, component.instance(level))) {
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
	 * Builds a ngspice netlist for the given network beginning from the given component
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
	
	/**
	 * Terminates all running simulations and detaches all nglink instances
	 */
	private void terminateNetworks() {
		for (ElectricNetwork network : this.circuitNetworks) {
			network.terminateExecution();
		}
	}
	
	/**
	 * Attaches nglink instances for all loaded networks and starts the initial simulation
	 */
	private void startNetworks() {
		for (ElectricNetwork network : this.circuitNetworks) {
			network.updateSimulation();
		}
	}
	
}
