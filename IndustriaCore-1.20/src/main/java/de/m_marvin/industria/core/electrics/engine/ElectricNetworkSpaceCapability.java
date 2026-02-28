package de.m_marvin.industria.core.electrics.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.CircuitElement;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.CircuitNode;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkSpaceCapability.ElectricComponent;
import de.m_marvin.industria.core.electrics.engine.network.SSyncElectricComponentsPackage;
import de.m_marvin.industria.core.electrics.engine.network.SUpdateElectricNetworkPackage;
import de.m_marvin.industria.core.electrics.types.IElectric;
import de.m_marvin.industria.core.electrics.types.IElectric.ElectricReference;
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
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ElectricNetworkSpaceCapability extends FriendlyFunctionalNetworkSpace<ElectricReference, ElectricComponent<?, ?>, ElectricNetwork, NodePos> implements ICapabilitySerializable<CompoundTag> {
	
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
		super(() -> new ElectricNetwork(() -> level), () -> new ElectricComponent(null, null, null), Config.ELECTRIC_NETWORK_TRACE_DEPTH.get());
		this.level = level;
	}
	
	/* Event handling */

	@SubscribeEvent
	public static void onLevelTick(TickEvent.LevelTickEvent event) {
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(event.level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		if (event.phase == Phase.START && !event.level.isClientSide()) {
			networkSpace.stepElectrics();
		} else if (event.phase == Phase.END) {
			networkSpace.processUpdates();
		}
	}
	
	@SubscribeEvent
	public static void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		Level level = (Level) event.getLevel();
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		
		ElectricReference reference = ElectricReference.block(event.getPos());
		if (event.getState().getBlock() instanceof IElectricBlock electric && electric.getConnectorMasterPos(level, event.getPos(), event.getState()).equals(event.getPos())) {
			networkSpace.updateTicketCompletable(reference, UpdateType.COMPONENT_PUT).thenAccept(v -> {
				ElectricComponent<?, ?> component = networkSpace.findComponentAt(reference);
				IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> (LevelChunk) level.getChunk(event.getPos())), new SSyncElectricComponentsPackage(component, new ChunkPos(event.getPos()), SyncRequestType.ADDED));
			});
		} else {
			ElectricComponent<?, ?> component = networkSpace.findComponentAt(reference);
			if (component == null) return;
			IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> (LevelChunk) level.getChunk(event.getPos())), new SSyncElectricComponentsPackage(component, new ChunkPos(event.getPos()), SyncRequestType.REMOVED));
			networkSpace.updateTicket(reference, UpdateType.COMPONENT_REMOVE);
		}
	}
	
	@SubscribeEvent
	public static void onConduitEntityChange(ConduitEvent event) { // NOTE Fired on client and server
		Level level = (Level) event.getLevel();
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		
		ElectricReference reference = ElectricReference.conduit(event.getPosition());
		if (event.getConduitEntity().getConduitState().getConduit() instanceof IElectricConduit) {
			// We don't need to send SSyncElectricComponentsPackage packages, since this event also triggers on the client by default
			if (event instanceof ConduitPlaceEvent) {
				networkSpace.updateTicket(reference, UpdateType.COMPONENT_PUT);
			} else if (event instanceof ConduitBreakEvent) {
				networkSpace.updateTicket(reference, UpdateType.COMPONENT_REMOVE);
			}
		}
	}
	
	@SubscribeEvent
	public static void onClientLoadsChunk(ChunkWatchEvent.Watch event) {
		Level level = event.getPlayer().level();
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		Collection<ElectricComponent<?, ?>> components = networkSpace.findComponentsInChunk(event.getPos());
		
		if (!components.isEmpty()) {
			// We should not need this here, since the update network package already sends all components
//			IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> event.getChunk()), new SSyncElectricComponentsPackage(componentsInChunk, event.getPos(), SyncRequestType.ADDED));
			
			Collection<ElectricNetwork> networks = components.stream().map(networkSpace::findNetworkAt).filter(Objects::nonNull).distinct().toList();
			for (var network : networks) {
				IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(event::getPlayer), new SUpdateElectricNetworkPackage(components, network));
			}
		}
	}
	
	@SubscribeEvent
	public static void onClientUnloadsChunk(ChunkWatchEvent.UnWatch event) {
		Level level = event.getPlayer().level();
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		Collection<ElectricComponent<?, ?>> components = networkSpace.findComponentsInChunk(event.getPos());
		
		if (!components.isEmpty()) {
			IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(event::getPlayer), new SSyncElectricComponentsPackage(components.stream().map(ElectricComponent::reference).toList(), event.getPos(), SyncRequestType.REMOVED));
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
	protected CompoundTag serializeReference(ElectricReference reference) {
		return reference.writeNbt();
	}
	
	@Override
	protected ElectricReference deserializeReference(CompoundTag tag) {
		return ElectricReference.readNbt(tag);
	}
	
	@Override
	protected ElectricComponent<?, ?> findOrCreateComponentAt(ElectricReference reference) {
		
		if (reference.isBlock()) {
			BlockState blockState = this.level.getBlockState(reference.block());
			if (blockState.getBlock() instanceof IElectricBlock electricBlock) {
				return new ElectricComponent<BlockState, Block>(reference, electricBlock, blockState);
			}
		} else if (reference.isConduit()) {
			Optional<ConduitEntity> conduitEntity = ConduitUtility.getConduit(this.level, reference.conduit());
			if (conduitEntity.isPresent() && conduitEntity.get().getConduitState().getConduit() instanceof IElectricConduit electricConduit) {
				return new ElectricComponent<ConduitEntity, Conduit>(ElectricReference.conduit(reference.conduit()), electricConduit, conduitEntity.get());
			}
		}
		return null;
	}

	@Override
	protected Collection<ParametrizedReference<ElectricReference, NodePos>> findConnectionsForComponent(ElectricComponent<?, ?> component) {
		
		if (component.isBlock()) {
			
			IElectricBlock block = component.asBlock();
			BlockPos position = component.asBlockPos();
			BlockState state = component.asBlockState(this.level);
			
			NodePos[] nodes = block.getElectricConnections(this.level, ElectricReference.block(position), state);
			
			Set<ParametrizedReference<ElectricReference, NodePos>> connections = new HashSet<>();
			for (NodePos node : nodes) {
				for (ConduitEntity conduit : ConduitUtility.getConduitsAtNode(level, node)) {
					if (conduit.getConduitState().getConduit() instanceof IElectricConduit) {
						connections.add(new ParametrizedReference<ElectricReference, NodePos>(ElectricReference.conduit(conduit.getPosition()), node));
					}
				}
			}
			return connections;
			
		} else if (component.isConduit()) {
			
			ConduitPos position = component.asConduitPos();
			
			NodePos[] nodes = new NodePos[] { position.getNodeA(), position.getNodeB() };
			
			Set<ParametrizedReference<ElectricReference, NodePos>> connections = new HashSet<>();
			for (NodePos node : nodes) {
				BlockPos blockPosition = node.getBlock();
				BlockState blockState = this.level.getBlockState(blockPosition);
				if (blockState.getBlock() instanceof IElectricBlock) {
					connections.add(new ParametrizedReference<ElectricReference, ConduitPos.NodePos>(ElectricReference.block(blockPosition), node));
				}
			}
			return connections;
			
		}
		
		return Collections.emptyList();
		
	}
	
	/**
	 * Represents a component (can be a conduit or a block) in the electric networks
	 */
	public static class ElectricComponent<I, T> extends FunctionalNetworkSpace.Component<ElectricReference> {
		protected boolean hasChanged;
		protected I instance;
		protected IElectric<I, T> type;
		
		public ElectricComponent(ElectricReference pos, IElectric<I, T> type, I instance) {
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
				this.type = (IElectric<I, T>) typeObject;
				this.instance = type.deserializeNBT(nbt);
				this.reference = ElectricReference.readNbt(nbt.getCompound("Position"));
				return true;
			}
			return false;
		}
		
		@Override
		public void serializeNbt(CompoundTag nbt) {
			super.serializeNbt(nbt);
			
			IElectric.Type componentType = IElectric.Type.getType(this.type);
			nbt.put("Position", reference.writeNbt());
			nbt.putString("Type", componentType.getRegistry().getKey(this.type).toString());
			nbt.putString("ComponentType", componentType.name().toLowerCase());
			this.type.serializeNBT(instance, nbt);
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
			return reference().block();
		}
		
		public ConduitPos asConduitPos() {
			return reference().conduit();
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
		
		public IElectric<I, T> type() {
			return type;
		}

		public I instance(Level level) {
			if ((this.hasChanged || !this.type.isInstanceValid(level, instance)) && level != null) {
				Optional<I> instanceLoaded = this.type.getInstance(level, reference);
				if (instanceLoaded.isPresent()) {
					this.instance = instanceLoaded.get();
					this.hasChanged = false;
				}
			}
			return instance;
		}
		
		public void installCircuitElements(Level level, ElectricNetwork.ComponentCircuitContext context) {
			type.installCircuitElements(level, reference, instance(level), context);
		}
		public void stepCircuitElements(Level level, ElectricNetwork.ComponentCircuitContext context) {
			type.stepCircuitElements(level, reference, instance(level), context);
		}
		public void afterNetworkStep(Level level, ElectricNetwork network) {
			type.afterNetworkStep(level, reference, instance(level), network);
		}
		
		public NodePos[] getNodes(Level level) {
			return type.getElectricConnections(level, reference, instance(level));
		}
		public String[] getWireLanes(Level level, NodePos node) {
			return type.getWireLanes(level, reference, instance(level), node);
		}
		public void setWireLanes(Level level, NodePos node, String[] laneLabels) {
			String[] oldLanes = type.getWireLanes(level, reference, instance(level), node);
			type.setWireLanes(level, reference, instance(level), node, laneLabels);
			for (int i = 0; i < oldLanes.length && i < laneLabels.length; i++) {
				if (!oldLanes[i].equals(laneLabels[i])) {
					ElectricUtility.updateNetwork(level, reference);
					return;
				}
			}
		}
		public boolean isWire() {
			return type.isWire();
		}
		public ChunkPos getAffectedChunk(Level level) {
			return type.getAffectedChunk(level, reference);
		}
//		public double getMaxPowerGeneration(Level level) {
//			return type.getMaxPowerGeneration(level, reference, this.instance(level));
//		}
//		public double getCurrentPower(Level level) {
//			return type.getCurrentPower(level, reference, this.instance(level));
//		}
	}
	
	public void stepElectrics() {
		
		for (var network : listNetworks()) {
			
			network.stepElectrics();
//			// TODO multi-threadding
//			listComponents().forEach(c -> c.afterNetworkStep(level, network));
			
		}
		
	}
	
	/**
	 * Returns a set containing all components in the given chunk
	 */
	public Collection<ElectricComponent<?, ?>> findComponentsInChunk(ChunkPos chunkPos) {
		return listComponents().stream().filter(c -> c.getAffectedChunk(level).equals(chunkPos)).toList();
	}
	
	/**
	 * Returns the floating voltage currently available on the given node.
	 * NOTE: Floating means that the voltage is referenced to "global ground", meaning a second voltage is required to calculate the actual difference (the voltage) between the two nodes.
	 */
	public double getFloatingNodeVoltage(CircuitNode node) {
		if (node.isInternal()) {
			ElectricNetwork network = findNetworkAt(node.componentReference());
			if (network != null) {
				return network.getFloatingNodeVoltage(node);
			}
		} else {
			ElectricNetwork network = findNetworkAt(ElectricReference.block(node.conduitNode().getBlock()));
			if (network != null) {
				return network.getFloatingNodeVoltage(node);
			}
		}
		return 0.0;
	}
	
	public double getElementCurrent(CircuitElement element) {
		ElectricNetwork network = findNetworkAt(element.componentReference());
		if (network != null) {
			return network.getElementCurrent(element);
		}
		return 0.0;
	}
	
}
