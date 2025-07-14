package de.m_marvin.industria.core.electrics.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.Config;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.events.ConduitEvent;
import de.m_marvin.industria.core.conduits.events.ConduitEvent.ConduitBreakEvent;
import de.m_marvin.industria.core.conduits.events.ConduitEvent.ConduitPlaceEvent;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkSpaceCapability.ElectricComponent;
import de.m_marvin.industria.core.electrics.engine.network.SSyncElectricComponentsPackage;
import de.m_marvin.industria.core.electrics.engine.network.SUpdateElectricNetworkPackage;
import de.m_marvin.industria.core.electrics.types.IElectric;
import de.m_marvin.industria.core.electrics.types.IElectric.ICircuitPlot;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricBlock;
import de.m_marvin.industria.core.electrics.types.conduits.IElectricConduit;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import de.m_marvin.industria.core.util.ufns.FriendlyFunctionalNetworkSpace;
import de.m_marvin.industria.core.util.ufns.FunctionalNetworkSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ElectricNetworkSpaceCapability extends FriendlyFunctionalNetworkSpace<Object, ElectricComponent<?, Object, ?>, ElectricNetwork, NodePos> implements ICapabilitySerializable<CompoundTag> {
	
	/* Capability handling */
	
	private LazyOptional<ElectricNetworkSpaceCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
	private static SimulationProcessor simulationProcessor;
	
	private final Level level;
	
	public Level getLevel() {
		return level;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		serializeNbt(tag);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		super.deserializeNbt(nbt);
		
		IndustriaCore.LOGGER.info("Loaded " + this.ref2network.values().stream().distinct().count() + " electric networks");
		IndustriaCore.LOGGER.info("Loaded " + this.referenceIds.size() + " electric components");
	}
	
	@Override
	public void serializeNbt(CompoundTag nbt) {
		super.serializeNbt(nbt);
		
		IndustriaCore.LOGGER.info("Saved " + this.ref2network.values().stream().distinct().count() + " electric networks");
		IndustriaCore.LOGGER.info("Saved " + this.referenceIds.size() + " electric components");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ElectricNetworkSpaceCapability(Level level) {
		super(() -> new ElectricNetwork(() -> level), () -> new ElectricComponent(null, null, null), 1024);
		this.level = level;
	}
	
	/* Event handling */

	@SubscribeEvent
	public static void onLevelTick(TickEvent.LevelTickEvent event) {
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(event.level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		networkSpace.processUpdates();
	}
	
	@SubscribeEvent
	public static void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		Level level = (Level) event.getLevel();
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		
		if (event.getState().getBlock() instanceof IElectricBlock electric && electric.getConnectorMasterPos(level, event.getPos(), event.getState()).equals(event.getPos())) {
			networkSpace.updateTicketCompletable(event.getPos(), UpdateType.COMPONENT_PUT).thenAccept(v -> {
				ElectricComponent<?, Object, ?> component = networkSpace.findComponentAt(event.getPos());
				IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> (LevelChunk) level.getChunk(event.getPos())), new SSyncElectricComponentsPackage(component, new ChunkPos(event.getPos()), SyncRequestType.ADDED));
			});
		} else {
			ElectricComponent<?, Object, ?> component = networkSpace.findComponentAt(event.getPos());
			if (component == null) return;
			IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> (LevelChunk) level.getChunk(event.getPos())), new SSyncElectricComponentsPackage(component, new ChunkPos(event.getPos()), SyncRequestType.REMOVED));
			networkSpace.updateTicket(event.getPos(), UpdateType.COMPONENT_REMOVE);
		}
	}
	
	@SubscribeEvent
	public static void onConduitStateChange(ConduitEvent event) { // NOTE Fired on client and server
		Level level = (Level) event.getLevel();
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		
		if (event.getConduitState().getConduit() instanceof IElectricConduit) {
			// We don't need to send SSyncElectricComponentsPackage packages, since this event also triggers on the client by default
			if (event instanceof ConduitPlaceEvent) {
				networkSpace.updateTicket(event.getPosition(), UpdateType.COMPONENT_PUT);
			} else if (event instanceof ConduitBreakEvent) {
				networkSpace.updateTicket(event.getPosition(), UpdateType.COMPONENT_REMOVE);
			}
		}
	}
	
	@SubscribeEvent
	public static void onClientLoadsChunk(ChunkWatchEvent.Watch event) {
		Level level = event.getPlayer().level();
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		Collection<ElectricComponent<?, Object, ?>> components = networkSpace.findComponentsInChunk(event.getPos());
		
		if (!components.isEmpty()) {
			// We should not need this here, since the update network package already sends all components
//			IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> event.getChunk()), new SSyncElectricComponentsPackage(componentsInChunk, event.getPos(), SyncRequestType.ADDED));
			
			Collection<ElectricNetwork> networks = components.stream().map(networkSpace::findNetworkAt).distinct().toList();
			for (var network : networks) {
				IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(event::getPlayer), new SUpdateElectricNetworkPackage(network));
			}
		}
	}
	
	@SubscribeEvent
	public static void onClientUnloadsChunk(ChunkWatchEvent.UnWatch event) {
		Level level = event.getPlayer().level();
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		Collection<ElectricComponent<?, Object, ?>> components = networkSpace.findComponentsInChunk(event.getPos());
		
		if (!components.isEmpty()) {
			IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(event::getPlayer), new SSyncElectricComponentsPackage(components, event.getPos(), SyncRequestType.REMOVED));
		}
	}
	
	/* SPICE worker thread */

	public static SimulationProcessor getSimulationProcessor() {
		if (!hasProcessor()) startupProcessor();
		return simulationProcessor;
	}
	
	public static boolean hasProcessor() {
		return simulationProcessor != null && simulationProcessor.isRunning();
	}
	
	public static void startupProcessor() {
		if (hasProcessor()) {
			IndustriaCore.LOGGER.log(org.apache.logging.log4j.Level.WARN, "Electric network processor already running, this is not right!");
		}
		if (simulationProcessor == null) simulationProcessor = new SimulationProcessor(Config.ELECTIRC_SIMULATION_THREADS.get());
		simulationProcessor.start();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownProcessor()));
	}
	
	public static void shutdownProcessor() {
		simulationProcessor.shutdown();
	}
	
	/* ElectricNetwork handling */
	
	@Override
	protected CompoundTag serializeReference(Object reference) {
		if (reference instanceof BlockPos blockPos) {
			return NbtUtils.writeBlockPos(blockPos);
		} else if (reference instanceof ConduitPos conduitPos) {
			return conduitPos.writeNBT(new CompoundTag());
		} else {
			throw new IllegalArgumentException("Not a valid electric network reference: " + reference.getClass());
		}
	}
	
	@Override
	protected Object deserializeReference(CompoundTag tag) {
		if (tag.contains("NodeA")) {
			return ConduitPos.readNBT(tag);
		} else {
			return NbtUtils.readBlockPos(tag);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected ElectricComponent<?, Object, ?> findOrCreateComponentAt(Object reference) {
		
		if (reference instanceof BlockPos blockPosition) {
			BlockState blockState = this.level.getBlockState(blockPosition);
			if (blockState.getBlock() instanceof IElectricBlock electricBlock) {
				// Yeah, this is not optimal, but BlockPos and ConduitPos have no common Interface or Super-Class
				return (ElectricComponent) new ElectricComponent<BlockState, BlockPos, Block>(blockPosition, electricBlock, blockState);
			}
		} else if (reference instanceof ConduitPos conduitPosition) {
			Optional<ConduitEntity> conduitEntity = ConduitUtility.getConduit(this.level, conduitPosition);
			if (conduitEntity.isPresent() && conduitEntity.get().getConduit() instanceof IElectricConduit electricConduit) {
				// Yeah, this is not optimal, but BlockPos and ConduitPos have no common Interface or Super-Class
				return (ElectricComponent) new ElectricComponent<ConduitEntity, ConduitPos, Conduit>(conduitPosition, electricConduit, conduitEntity.get());
			}
		}
		return null;
	}

	@Override
	protected Collection<ParametrizedReference<Object, NodePos>> findConnectionsForComponent(ElectricComponent<?, Object, ?> component) {
		
		if (component.isBlock()) {
			
			IElectricBlock block = component.asBlock();
			BlockPos position = component.asBlockPos();
			BlockState state = component.asBlockState(this.level);
			
			NodePos[] nodes = block.getElectricConnections(this.level, position, state);
			
			Set<ParametrizedReference<Object, NodePos>> connections = new HashSet<>();
			for (NodePos node : nodes) {
				for (ConduitEntity conduit : ConduitUtility.getConduitsAtNode(level, node)) {
					if (conduit instanceof IElectricConduit) {
						connections.add(new ParametrizedReference<Object, NodePos>(conduit.getPosition(), node));
					}
				}
			}
			return connections;
			
		} else if (component.isConduit()) {
			
			ConduitPos position = component.asConduitPos();
			
			NodePos[] nodes = new NodePos[] { position.getNodeA(), position.getNodeB() };
			
			Set<ParametrizedReference<Object, NodePos>> connections = new HashSet<>();
			for (NodePos node : nodes) {
				BlockPos blockPosition = node.getBlock();
				BlockState blockState = this.level.getBlockState(blockPosition);
				if (blockState.getBlock() instanceof IElectricBlock) {
					connections.add(new ParametrizedReference<Object, ConduitPos.NodePos>(blockPosition, node));
				}
			}
			return connections;
			
		}
		
		return Collections.emptyList();
		
	}
	
	/**
	 * Represents a component (can be a conduit or a block) in the electric networks
	 */
	public static class ElectricComponent<I, P, T> extends FunctionalNetworkSpace.Component<Object> {
		protected boolean hasChanged;
		protected I instance;
		protected IElectric<I, P, T> type;
		
		public ElectricComponent(P pos, IElectric<I, P, T> type, I instance) {
			this.reference = pos;
			this.type = type;
			this.instance = instance;
			this.hasChanged = true;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean deserializeNbt(CompoundTag nbt) {
			super.deserializeNbt(nbt);
			
			IElectric.Type componentType = IElectric.Type.valueOf(nbt.getString("ComponentType").toUpperCase());
			ResourceLocation typeName = new ResourceLocation(nbt.getString("Type"));
			Object typeObject = componentType.getRegistry().getValue(typeName);
			if (typeObject instanceof IElectric) {
				this.type = (IElectric<I, P, T>) typeObject;
				this.instance = type.deserializeNBTInstance(nbt);
				this.reference = type.deserializeNBTPosition(nbt);
				return true;
			}
			return false;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void serializeNbt(CompoundTag nbt) {
			super.serializeNbt(nbt);
			
			IElectric.Type componentType = IElectric.Type.getType(this.type);
			this.type.serializeNBTPosition((P) reference(), nbt);
			nbt.putString("Type", componentType.getRegistry().getKey(this.type).toString());
			nbt.putString("ComponentType", componentType.name().toLowerCase());
			this.type.serializeNBTInstance(instance, nbt);
		}
		
		@Override
		public String toString() {
			return "Component{pos=" + this.reference() + ",type=" + this.type.toString() + ",instance=" + (this.instance(null) == null ? "N/A" : this.instance(null).toString()) + "}#hash=" + this.hashCode();
		}
		
		public void setChanged() {
			this.hasChanged = true;
		}
		
		public boolean isBlock() {
			return type instanceof IElectricBlock;
		}
		
		public boolean isConduit() {
			return type instanceof IElectricConduit;
		}
		
		public BlockPos asBlockPos() {
			return (BlockPos) reference();
		}
		
		public ConduitPos asConduitPos() {
			return (ConduitPos) reference();
		}
		
		public BlockState asBlockState(Level level) {
			return (BlockState) instance(level);
		}
		
		public ConduitEntity asConduitEntity(Level level) {
			return (ConduitEntity) instance(level);
		}
		
		public IElectricBlock asBlock() {
			return (IElectricBlock) type();
		}
		
		public IElectricConduit asCondutit() {
			return (IElectricConduit) type();
		}
		
		public IElectric<I, P, T> type() {
			return type;
		}

		@SuppressWarnings("unchecked")
		public I instance(Level level) {
			if ((this.hasChanged || !this.type.isInstanceValid(level, instance)) && level != null) {
				Optional<I> instanceLoaded = this.type.getInstance(level, (P) this.reference());
				if (instanceLoaded.isPresent()) {
					this.instance = instanceLoaded.get();
					this.hasChanged = false;
				}
			}
			return instance;
		}
		@SuppressWarnings("unchecked")
		public void plotCircuit(Level level, ElectricNetwork circuit, Consumer<ICircuitPlot> plotter) {
			type.plotCircuit(level, instance(level), (P) reference(), circuit, plotter);
		}
		@SuppressWarnings("unchecked")
		public NodePos[] getNodes(Level level) {
			return type.getElectricConnections(level, (P) reference(), instance(level));
		}
		@SuppressWarnings("unchecked")
		public void onNetworkChange(Level level) {
			type.onNetworkNotify(level, instance(level), (P) reference());
		}
		@SuppressWarnings("unchecked")
		public String[] getWireLanes(Level level, NodePos node) {
			return type.getWireLanes(level, (P) reference(), instance(level), node);
		}
		@SuppressWarnings("unchecked")
		public void setWireLanes(Level level, NodePos node, String[] laneLabels) {
			String[] oldLanes = type.getWireLanes(level, (P) reference(), instance(level), node);
			type.setWireLanes(level, (P) reference(), instance(level), node, laneLabels);
			for (int i = 0; i < oldLanes.length && i < laneLabels.length; i++) {
				if (!oldLanes[i].equals(laneLabels[i])) {
					ElectricUtility.updateNetwork(level, (P) reference());
					return;
				}
			}
		}
		public boolean isWire() {
			return type.isWire();
		}
		@SuppressWarnings("unchecked")
		public ChunkPos getAffectedChunk(Level level) {
			return type.getAffectedChunk(level, (P) reference());
		}
		@SuppressWarnings("unchecked")
		public double getMaxPowerGeneration(Level level) {
			return type.getMaxPowerGeneration(level, (P) reference(), this.instance(level));
		}
		@SuppressWarnings("unchecked")
		public double getCurrentPower(Level level) {
			return type.getCurrentPower(level, (P) reference(), this.instance(level));
		}
	}
	
	/**
	 * Returns a set containing all components in the given chunk
	 */
	public Collection<ElectricComponent<?, Object, ?>> findComponentsInChunk(ChunkPos chunkPos) {
		return listComponents().stream().filter(c -> c.getAffectedChunk(level).equals(chunkPos)).toList();
	}
	
	/**
	 * Returns the floating voltage currently available on the given node.
	 * NOTE: Floating means that the voltage is referenced to "global ground", meaning a second voltage is required to calculate the actual difference (the voltage) between the two nodes.
	 */
	public Optional<Double> getFloatingNodeVoltage(NodePos node, int laneId, String lane) {
		ElectricNetwork network = findNetworkAt(node.getBlock());
		if (network != null) {
			return network.getFloatingNodeVoltage(node, laneId, lane);
		}
		return Optional.empty();
	}

	/**
	 * Returns the floating voltage currently available on the given node.
	 * NOTE: Floating means that the voltage is referenced to "global ground", meaning a second voltage is required to calculate the actual difference (the voltage) between the two nodes.
	 */
	public Optional<Double> getFloatingLocalNodeVoltage(BlockPos position, String lane, int group) {
		ElectricNetwork network = findNetworkAt(position);
		if (network != null) {
			return network.getFloatingLocalNodeVoltage(position, lane, group);
		}
		return Optional.empty();
	}
	
}
