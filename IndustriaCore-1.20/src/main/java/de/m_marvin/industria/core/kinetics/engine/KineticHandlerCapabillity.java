package de.m_marvin.industria.core.kinetics.engine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.OptionalDouble;
import java.util.Queue;
import java.util.Set;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import com.google.common.base.Objects;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.kinetics.engine.network.SSyncKineticComponentsPackage;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.KineticReference;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.TransmissionNode;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.PowerNetState;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class KineticHandlerCapabillity implements ICapabilitySerializable<ListTag> {
	
	/* Capability handling */
	
	private LazyOptional<KineticHandlerCapabillity> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == Capabilities.KINETIC_HANDLER_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
	private final Level level;
	private final HashMap<KineticReference, Component> pos2componentMap = new HashMap<KineticReference, Component>();
	private final HashSet<KineticNetwork> kineticNetworks = new HashSet<KineticNetwork>();
	private final HashMap<Component, KineticNetwork> component2kineticMap = new HashMap<Component, KineticNetwork>();
	
	public Level getLevel() {
		return level;
	}
	
	@Override
	public ListTag serializeNBT() {
		ListTag networksNbt = new ListTag();
		
		int componentCount = 0;
		for (KineticNetwork kineticNetwork : this.kineticNetworks) {
			kineticNetwork.removeInvalidComponents();
			if (kineticNetwork.isEmpty()) continue;
			networksNbt.add(kineticNetwork.saveNBT(this));
			componentCount += kineticNetwork.getComponents().size();
		}
		
		IndustriaCore.LOGGER.info("Saved " + networksNbt.size() + " kinetic networks");
		IndustriaCore.LOGGER.info("Saved " + componentCount + " kinetic components");
		return networksNbt;
	}
	
	@Override
	public void deserializeNBT(ListTag nbt) {
		this.pos2componentMap.clear();
		this.kineticNetworks.clear();
		this.component2kineticMap.clear();
		
		for (int i = 0; i < nbt.size(); i++) {
			CompoundTag kineticTag = nbt.getCompound(i);
			KineticNetwork kineticNetwork = new KineticNetwork(() -> this.level);
			kineticNetwork.loadNBT(this, kineticTag);
			if (!kineticNetwork.isEmpty()) {
				this.kineticNetworks.add(kineticNetwork);
				kineticNetwork.getComponents().forEach((component) -> {
					this.component2kineticMap.put(component, kineticNetwork);
					if (!this.pos2componentMap.containsValue(component)) {
						this.addToNetwork(component);
					}
				});
			}
		}
		
		IndustriaCore.LOGGER.info("Loaded " + this.kineticNetworks.size() + "/" + nbt.size() + " kinetic networks");
		IndustriaCore.LOGGER.info("Loaded " + this.pos2componentMap.size() + " kinetic components");
	}
	
	public KineticHandlerCapabillity(Level level) {
		this.level = level;
	}
	
	/* Event handling */
	
	@SubscribeEvent
	public static void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		Level level = (Level) event.getLevel();
		KineticHandlerCapabillity handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_HANDLER_CAPABILITY);
		
		if (event.getState().getBlock() instanceof IKineticBlock) {
			handler.updateNetworks(event.getPos());
		} else {
			for (Component component : handler.findComponentsAt(event.getPos())) {
				handler.removeComponent(component.reference());
			}
		}
	}
	
	@SubscribeEvent
	public static void onClientLoadsChunk(ChunkWatchEvent.Watch event) {
		Level level = event.getPlayer().level();
		KineticHandlerCapabillity kineticHandler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_HANDLER_CAPABILITY);
		Set<Component> components = kineticHandler.findComponentsInChunk(event.getPos());
		
		if (!components.isEmpty()) {
			IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(event::getPlayer), new SSyncKineticComponentsPackage(components, event.getChunk().getPos(), SyncRequestType.ADDED));
		}
	}
	
	@SubscribeEvent
	public static void onClientUnloadsChunk(ChunkWatchEvent.UnWatch event) {
		Level level = event.getPlayer().level();
		KineticHandlerCapabillity electricHandler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_HANDLER_CAPABILITY);
		Set<Component> components = electricHandler.findComponentsInChunk(event.getPos());
		if (!components.isEmpty()) {
			IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(event::getPlayer), new SSyncKineticComponentsPackage(components, event.getPos(), SyncRequestType.REMOVED));
		}
	}
	
	/* Kinetic handling */
	
	/**
	 * Represents a component in the kinetic networks
	 */
	public static class Component {
		protected KineticReference reference;
		protected BlockState instance;
		protected IKineticBlock type;
		
		public Component(KineticReference reference, IKineticBlock type, BlockState instance) {
			this.type = type;
			this.instance = instance;
			this.reference = reference;
		}
		
		public KineticReference reference() {
			return reference;
		}
		
		public IKineticBlock type() {
			return type;
		}
		
		public BlockState instance(Level level) {
			if (level != null) {
				this.instance = reference.state(level);
				if (!this.instance.isAir()) {
				}
			}
			return instance;
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(this.type, this.reference);
		}
		
		@Override
		public String toString() {
			return "Component{reference=" + this.reference() + ",type=" + this.type.toString() + ",instance=" + (this.instance(null) == null ? "N/A" : this.instance(null).toString()) + "}#hash=" + this.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj instanceof Component other) {
				return this.type.equals(other.type) && this.reference.equals(other.reference);
			}
			return false;
		}
		
		public void serializeNbt(CompoundTag nbt) {
			nbt.put("Reference", reference.serialize());
			if (this.type instanceof Block typeBlock) nbt.putString("Type", ForgeRegistries.BLOCKS.getKey(typeBlock).toString());
			nbt.put("State", NbtUtils.writeBlockState(instance));
		}
		public static Component deserializeNbt(CompoundTag nbt) {
			ResourceLocation typeName = new ResourceLocation(nbt.getString("Type"));
			Block typeObject = ForgeRegistries.BLOCKS.getValue(typeName);
			if (typeObject instanceof IKineticBlock type) {
				KineticReference reference = KineticReference.deserialize(nbt.getCompound("Reference"));
				@SuppressWarnings("deprecation")
				BlockState instance = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt.getCompound("State"));
				return new Component(reference, type, instance);
			}
			return null;
		}
		
		public double getSourceSpeed(Level level) {
			return this.type.getSourceSpeed(level, reference.pos(), reference.partId(), instance);
		}
		public double getTorque(Level level) {
			return this.type.getTorque(level, reference.pos(), reference.partId(), instance);
		}
		public void setRPM(Level level, double rpm) {
			this.type.setRPM(level, reference.pos(), reference.partId(), instance, rpm);
		}
		public double getRPM(Level level) {
			return this.type.getRPM(level, reference.pos(), reference.partId(), instance);
		}
	}

	/**
	 * Returns the component with the given position
	 */
	public Collection<Component> findComponentsAt(BlockPos position) {
		return this.pos2componentMap.keySet().stream()
				.filter(r -> r.pos().equals(position))
				.map(this::findComponentAt)
				.toList();
	}

	/**
	 * Returns the component with the given position
	 */
	public Component findComponentAt(KineticReference reference) {
		return this.pos2componentMap.get(reference);
	}
	
	/**
	 * Returns all networks with an component at the given position
	 */
	public Collection<KineticNetwork> getNetworksAt(BlockPos position) {
		Collection<Component> components = findComponentsAt(position);
		return components.stream()
			.map(c -> this.component2kineticMap.get(c))
			.distinct()
			.filter(n -> n != null)
			.toList();
	}

	/**
	 * Returns the networks with an component at the given reference
	 */
	public KineticNetwork getNetworkAt(KineticReference reference) {
		Component component = findComponentAt(reference);
		if (component == null) return null;
		return this.component2kineticMap.get(component);
	}

	/**
	 * Returns a set containing all components in the given chunk
	 */
	public Set<Component> findComponentsInChunk(ChunkPos chunkPos) {
		Set<Component> components = new HashSet<>();
		for (Entry<KineticReference, Component> componentEntry : this.pos2componentMap.entrySet()) {
			if (new ChunkPos(componentEntry.getKey().pos()).equals(chunkPos)) components.add(componentEntry.getValue());
		}
		return components;
	}

	/**
	 * Updates all networks which have a component at the given position.
	 */
	public Collection<KineticNetwork> updateNetworks(BlockPos position) {
		
		List<KineticReference> references =
				getNetworksAt(position).stream()
				.flatMap(n -> n.getComponents().stream())
				.map(Component::reference)
				.distinct()
				.toList();
		
		if (references.isEmpty()) {
			BlockState state = level.getBlockState(position);
			if (state.getBlock() instanceof IKineticBlock block) {
				references = Stream.of(block.getTransmissionNodes(level, position, state))
					.map(TransmissionNode::reference)
					.distinct()
					.toList();
			}
		}
		
		Set<KineticNetwork> networks = new HashSet<>();
		Set<KineticReference> processed = new HashSet<>();
		for (KineticReference reference : references) {
			if (processed.contains(reference)) continue;
			Collection<KineticNetwork> network = updateNetwork(reference);
			if (network == null) continue;
			processed.addAll(network.stream().map(KineticNetwork::getComponents).flatMap(Collection::stream).map(Component::reference).distinct().toList());
			networks.addAll(network);
		}
		
		return networks;
		
	}
	
	/**
	 * Updates the network which has a component at the given reference.
	 */
	public Collection<KineticNetwork> updateNetwork(KineticReference reference) {
		
		// We have to ensure that we start the updates with references of valid sub blocks, not the compounds!
		BlockState refState = reference.state(this.level);
		if (refState.getBlock() instanceof IKineticBlock refKinetic) {
			TransmissionNode[] refNodes = refKinetic.getTransmissionNodes(this.level, reference.pos(), refState);
			
			List<KineticReference> blockReferences = Stream.of(refNodes)
				.map(TransmissionNode::reference)
				.distinct()
				.toList();
			
			List<KineticNetwork> networks = new ArrayList<KineticNetwork>();
			for (KineticReference blockReference : blockReferences) {
				KineticNetwork network = makeNetwork(blockReference);
				if (network == null) return null;
				
				recalculateNetwork(network);
				
				networks.add(network);
			}
			
			return networks;
		}
		
		return Collections.emptyList();
	}
	
	/**
	 * Recalculates torque and speeds in the network, without rebuilding it
	 */
	public void recalculateNetwork(KineticNetwork network) {
		
		// Check for opposite rotations, if so, skip calculations
		if (network.isLocked()) {
			network.setNetworkSpeed(0.0);
		} else {
			
			// Calculate source speeds
			double[] sources = network.getComponents().stream()
				.mapToDouble(c -> c.getSourceSpeed(this.level) * network.getTransmission(c))
				.distinct()
				.toArray();
			
			// Find fastest source-speed in network (in both directions)
			OptionalDouble maxSpeedH = DoubleStream.of(sources).filter(s -> s > 0).max();
			OptionalDouble maxSpeedL = DoubleStream.of(sources).filter(s -> s < 0).min();
			
			// Check if any source available
			if (maxSpeedH.isEmpty() && maxSpeedL.isEmpty()) {
				network.setNetworkSpeed(0.0);
				network.setState(PowerNetState.INACTIVE);
			} 
			
			// Check for reversed sources
			else if (maxSpeedH.isPresent() && maxSpeedL.isPresent()) {
				network.setNetworkSpeed(0.0);
				network.setState(PowerNetState.INACTIVE);
			}
			
			else {
				
				double speed = maxSpeedL.orElseGet(() -> maxSpeedH.getAsDouble());
				
				// Calculate available torque
				double torque = network.getComponents().stream()
					.filter(c -> c.getSourceSpeed(this.level) == 0)
					.mapToDouble(c -> c.getTorque(level) / network.getTransmission(c))
					.sum();
				
				// Calculate total load
				double load = network.getComponents().stream()
						.filter(c -> c.getSourceSpeed(this.level) == 0)
						.mapToDouble(c -> c.getTorque(level) / network.getTransmission(c))
						.sum();
				
				// Check for overload
				if (load > torque) {
					network.setNetworkSpeed(0.0);
					network.setState(PowerNetState.INACTIVE);
				} 
				
				else {
					
					// Set network rotation speed
					network.setNetworkSpeed(speed);
					network.setState(PowerNetState.ACTIVE);
					
				}
				
			}
			
		}
		
		// Update rotation speeds
		for (Component c : network.getComponents()) {
			double ratio = network.getTransmission(c);
			double cspeed = ratio == 0.0 ? 0.0 : (network.getSpeed() / ratio);
			c.setRPM(level, cspeed);
		}
		
		// Trigger updates
		network.getComponents().stream()
			.map(c -> c.reference().pos())
			.distinct()
			.forEach(pos -> GameUtility.triggerClientSync(level, pos));
		
	}
	
	/**
	 * Removes a component from the network and updates it and its components
	 */
	public void removeComponent(KineticReference reference) {
		if (this.pos2componentMap.containsKey(reference)) {
			Component component = removeFromNetwork(reference);
			if (component != null) {
				if (!this.level.isClientSide) {
					ChunkPos chunkPos = new ChunkPos(component.reference().pos());
					IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunk(chunkPos.x, chunkPos.z)), new SSyncKineticComponentsPackage(component, chunkPos, SyncRequestType.REMOVED));
				}
				KineticNetwork network = this.component2kineticMap.remove(component);
				if (network != null) {
					Queue<Component> componentsToUpdate = new ArrayDeque<KineticHandlerCapabillity.Component>(network.getComponents());
					componentsToUpdate.forEach(this.component2kineticMap::remove);
					while (componentsToUpdate.size() > 0) {
						Component componentToUpdate = componentsToUpdate.poll();
						if (componentToUpdate == component) continue;
						Collection<KineticNetwork> networks2 = updateNetworks(componentToUpdate.reference().pos());
						networks2.forEach(network2 -> componentsToUpdate.removeAll(network2.getComponents()));
					}
					this.kineticNetworks.remove(network);
				}
			}
		}
	}
	
	/**
	 * Adds a component to the network and updates it and its components
	 */
	public void addComponent(KineticReference reference, IKineticBlock type, BlockState instance) {
		
		Component component = this.pos2componentMap.get(reference);
		if (component != null) {
			if (!component.type.equals(type)) {
				removeFromNetwork(reference);
			}
		}
		Component component2 = new Component(reference, type, instance);
		addToNetwork(component2);
		if (!this.level.isClientSide) {
			ChunkPos chunkPos = new ChunkPos(component2.reference().pos());
			IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunk(chunkPos.x, chunkPos.z)), new SSyncKineticComponentsPackage(component2, chunkPos, SyncRequestType.ADDED));
		}

		updateNetwork(reference);

	}
	
	/**
	 * Removes a component from the network but does not cause any updates
	 */
	public Component removeFromNetwork(KineticReference reference) {
		Component component = this.pos2componentMap.remove(reference);
		return component;
	}
	
	/**
	 * Returns true if the component is already registered for an network
	 */
	public boolean isInNetwork(Component component) {
		if (component.instance(level) == null) return false;
		return this.component2kineticMap.containsKey(component) && this.pos2componentMap.containsKey(component.reference());
	}
	
	/**
	 * Returns the internal collection of all electric components
	 */
	public Collection<Component> getComponents() {
		return this.pos2componentMap.values();
	}
	
	/**
	 * Adds a component to the network but does not cause any updates
	 */
	public void addToNetwork(Component component) {
		if (component.instance(level) == null) return;
		this.pos2componentMap.put(component.reference(), component);
	}
	
	/**
	 * Rebuilds the network from scratch, registers blocks which are not yet registered as kinetic components automatically
	 */
	public KineticNetwork makeNetwork(KineticReference startReference) {
		
		KineticNetwork network = null;
		Set<KineticReference> tnd = new HashSet<>();
		Queue<KineticReference> neighbors = new ArrayDeque<>();
		neighbors.add(startReference);
		
		// Process queued component references
		while (!neighbors.isEmpty()) {
			
			KineticReference ref = neighbors.poll();
			
			// TODO kinetic update optimization
			//System.out.println(neighbors.size() + " " + tnd.size());
			
			// Get block at reference
			BlockPos pos1a = ref.pos();
			BlockState state1a = this.level.getBlockState(pos1a);
			
			// Check if valid kinetic block
			if (state1a.getBlock() instanceof IKineticBlock kinetic1a) {
				
				// Iterate over all nodes that match the reference
				for (TransmissionNode node1 : kinetic1a.getTransmissionNodes(level, pos1a, state1a)) {
					
					// Ignore nodes not part of this component (possible other sub blocks)
					if (!node1.reference().equals(ref)) continue;

					// Get block of node (might differ from reference, since it could be a sub block)
					BlockState state1 = node1.reference().state(level);
					
					// Check if block is a valid kinetic block
					if (state1.getBlock() instanceof IKineticBlock kinetic1) {

						// Add reference to list of already processed references, to avoid endless loop
						tnd.add(node1.reference());
						
						// Get component, add new one if not yet registered
						Component component1 = findComponentAt(node1.reference());
						if (component1 == null) {
							component1 = new Component(node1.reference(), kinetic1, state1);
							addToNetwork(component1);
							
							if (!this.level.isClientSide()) {
								ChunkPos chunkPos = new ChunkPos(component1.reference().pos());
								IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunk(chunkPos.x, chunkPos.z)), new SSyncKineticComponentsPackage(component1, chunkPos, SyncRequestType.ADDED));
							}
						}
						
						// Create or add to network
						if (network == null) {
							network = this.component2kineticMap.get(component1);
							if (network == null) {
								network = new KineticNetwork(() -> this.level);
								this.component2kineticMap.put(component1, network); 
							} else {
								network.reset();
							}
						} else {
							KineticNetwork previousNetwork = this.component2kineticMap.put(component1, network);
							if (previousNetwork != null && previousNetwork != network) {
								this.kineticNetworks.remove(previousNetwork);
							}
						}
						
						network.addComponent(component1);
						
						// Iterate over all possible positions this node could connect to
						for (BlockPos pos2a : node1.type().pos(node1)) {
							
							// Get block at possible connection position
							BlockState state2a = this.level.getBlockState(pos2a);
							
							// Check if block is valid kinetic block
							if (state2a.getBlock() instanceof IKineticBlock kinetic2a) {
								
								// Iterate over the blocks transmission nodes
								for (TransmissionNode node2 : kinetic2a.getTransmissionNodes(level, pos2a, state2a)) {
									
									// If node was already processed, skip to avoid endless loop
									if (tnd.contains(node2.reference())) continue;
									
									// Get block of node, might differ from previous if its an sub block
									BlockState state2 = node2.reference().state(level);
									
									// Check if block is a valid kinetic block
									if (state2.getBlock() instanceof IKineticBlock kinetic2) {
										
										// Calculate transmission ratio to the block, skip if zero (no connection)
										double transmission = node1.type().apply(node1, node2);
										if (transmission == 0.0) continue;
										
										// Add component to network
										Component component2 = findComponentAt(node2.reference());
										if (component2 == null) {
											component2 = new Component(node2.reference(), kinetic2, state2);
											addToNetwork(component1);
										}
										
										// Set transmission ratio in network
										if (!network.addTransmission(component1, component2, transmission))
											network.setLocked();
										
										// Add this nodes reference to queue of references to process
										if (!neighbors.contains(node2.reference())) neighbors.add(node2.reference());
										
									}
									
								}
								
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		
		if (network != null)
			this.kineticNetworks.add(network);
		
		return network;
		
	}
	
}
