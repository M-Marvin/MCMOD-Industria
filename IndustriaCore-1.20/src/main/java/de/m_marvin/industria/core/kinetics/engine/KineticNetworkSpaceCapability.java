package de.m_marvin.industria.core.kinetics.engine;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.Config;
import de.m_marvin.industria.core.kinetics.engine.network.SSyncKineticComponentsPackage;
import de.m_marvin.industria.core.kinetics.engine.network.SUpdateKineticNetworkPackage;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.KineticReference;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.TransmissionNode;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import de.m_marvin.industria.core.util.ufns.FriendlyFunctionalNetworkSpace;
import de.m_marvin.industria.core.util.ufns.FunctionalNetworkSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class KineticNetworkSpaceCapability extends FriendlyFunctionalNetworkSpace<KineticReference, KineticNetworkSpaceCapability.KineticComponent, KineticNetwork, Double> implements ICapabilitySerializable<CompoundTag> {
	
	/* Capability handling */
	
	private LazyOptional<KineticNetworkSpaceCapability> holder = LazyOptional.of(() -> this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == Capabilities.KINETIC_NETWORK_SPACE_CAPABILITY) {
			return holder.cast();
		}
		return LazyOptional.empty();
	}
	
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
	public void serializeNbt(CompoundTag nbt) {
		super.serializeNbt(nbt);
		
		IndustriaCore.LOGGER.info("Saved " + this.ref2network.values().stream().distinct().count() + " kinetic networks");
		IndustriaCore.LOGGER.info("Saved " + this.referenceIds.size() + " kinetic components");
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		super.deserializeNbt(nbt);

		IndustriaCore.LOGGER.info("Loaded " + this.ref2network.values().stream().distinct().count() + " kinetic networks");
		IndustriaCore.LOGGER.info("Loaded " + this.referenceIds.size() + " kinetic components");
	}
	
	public KineticNetworkSpaceCapability(Level level) {
		super(Config.KINETIC_NETWORK_TRACE_DEPTH.get());
		this.level = level;
	}
	
	
	
	/* Event handling */
	
	@SubscribeEvent
	public static void onLevelTick(TickEvent.LevelTickEvent event) {
		if (event.phase != Phase.END) return;
		KineticNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(event.level, Capabilities.KINETIC_NETWORK_SPACE_CAPABILITY);
		networkSpace.processUpdates();
	}
	
	@SubscribeEvent
	public static void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		Level level = (Level) event.getLevel();
		if (level.isClientSide()) return;
		KineticNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.KINETIC_NETWORK_SPACE_CAPABILITY);
		
		// Always remove components at this block pos, this prevents wrong connections from things like assembly to an compound
		for (KineticReference reference : networkSpace.listReferences()) {
			if (reference.pos().equals(event.getPos())) {
				networkSpace.updateTicket(reference, UpdateType.COMPONENT_REMOVE);
				KineticComponent component = networkSpace.findComponentAt(reference);
				if (component == null) continue;
				IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> (LevelChunk) level.getChunk(event.getPos())), new SSyncKineticComponentsPackage(component.reference(), new ChunkPos(event.getPos()), SyncRequestType.REMOVED));
			}
		}
		
		if (event.getState().getBlock() instanceof IKineticBlock kinetic) {
			Stream.of(kinetic.getTransmissionNodes(level, event.getPos(), event.getState()))
				.map(TransmissionNode::reference)
				.distinct()
				.forEach(ref -> {
					networkSpace.updateTicketCompletable(ref, UpdateType.COMPONENT_PUT).thenAccept(v -> {
						KineticComponent component = networkSpace.findComponentAt(ref);
						IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> (LevelChunk) level.getChunk(event.getPos())), new SSyncKineticComponentsPackage(component.reference(), new ChunkPos(event.getPos()), SyncRequestType.REMOVED));
					});
				});;
		}
	}
	
	@SubscribeEvent
	public static void onClientLoadsChunk(ChunkWatchEvent.Watch event) {
		Level level = event.getPlayer().level();
		KineticNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.KINETIC_NETWORK_SPACE_CAPABILITY);
		Collection<KineticComponent> components = networkSpace.findComponentsInChunk(event.getPos());
		
		if (!components.isEmpty()) {
			// We should not need this here, since the update network package already sends all components
//			IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(event::getPlayer), new SSyncKineticComponentsPackage(components, event.getPos(), SyncRequestType.ADDED));
			
			Collection<KineticNetwork> networks = components.stream().map(networkSpace::findNetworkAt).filter(Objects::nonNull).distinct().toList();
			for (var network : networks) {
				IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(event::getPlayer), new SUpdateKineticNetworkPackage(components, network));
			}
		}
	}
	
	@SubscribeEvent
	public static void onClientUnloadsChunk(ChunkWatchEvent.UnWatch event) {
		Level level = event.getPlayer().level();
		KineticNetworkSpaceCapability kinteticHandler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_NETWORK_SPACE_CAPABILITY);
		Collection<KineticComponent> components = kinteticHandler.findComponentsInChunk(event.getPos());
		
		if (!components.isEmpty()) {
			IndustriaCore.NETWORK.send(PacketDistributor.PLAYER.with(event::getPlayer), new SSyncKineticComponentsPackage(components.stream().map(KineticComponent::reference).toList(), event.getPos(), SyncRequestType.REMOVED));
		}
	}
	
	/* Kinetic handling */

	@Override
	protected KineticNetwork newNetwork() {
		return new KineticNetwork(() -> level);
	}

	@Override
	protected KineticComponent newComponent() {
		return new KineticComponent();
	}

	@Override
	protected CompoundTag serializeReference(KineticReference reference) {
		return reference.writeNbt();
	}

	@Override
	protected KineticReference deserializeReference(CompoundTag tag) {
		return KineticReference.readNbt(tag);
	}

	/**
	 * Represents a component in the kinetic networks
	 */
	public static class KineticComponent extends FunctionalNetworkSpace.Component<KineticReference> {
		protected BlockState instance;
		protected IKineticBlock type;
		
		public KineticComponent() {}
		
		public KineticComponent(KineticReference reference, IKineticBlock type, BlockState instance) {
			this.reference = reference;
			this.type = type;
			this.instance = instance;
		}
		
		@SuppressWarnings("deprecation")
		public boolean deserializeNbt(CompoundTag nbt) {
			super.deserializeNbt(nbt);
			ResourceLocation typeName = ResourceLocation.tryParse(nbt.getString("Type"));
			Block typeObject = ForgeRegistries.BLOCKS.getValue(typeName);
			if (typeObject instanceof IKineticBlock type) {
				this.reference = KineticReference.readNbt(nbt.getCompound("Reference"));
				this.type = type;
				this.instance = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt.getCompound("State"));
				return true;
			}
			return false;
		}
		
		@Override
		public void serializeNbt(CompoundTag nbt) {
			super.serializeNbt(nbt);
			nbt.put("Reference", reference().writeNbt());
			if (this.type instanceof Block typeBlock)
				nbt.putString("Type", ForgeRegistries.BLOCKS.getKey(typeBlock).toString());
			nbt.put("State", NbtUtils.writeBlockState(instance));
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
		public String toString() {
			return "Component{reference=" + this.reference() + ",type=" + this.type.toString() + ",instance=" + (this.instance(null) == null ? "N/A" : this.instance(null).toString()) + "}#hash=" + this.hashCode();
		}
		
		public TransmissionNode[] getTransmissionNodes(Level level) {
			return this.type.getTransmissionNodes(level, reference.pos(), instance);
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

	@Override
	protected Collection<ParametrizedReference<KineticReference, Double>> findConnectionsForComponent(KineticComponent component) {
		return Stream.of(component.getTransmissionNodes(level))
				.flatMap(node -> {
					return Stream.of(node.type().pos(node)).map(pos -> {
						BlockState state = this.level.getBlockState(pos);
						if (state.getBlock() instanceof IKineticBlock kinetic) {
							TransmissionNode[] nodes = kinetic.getTransmissionNodes(level, pos, state);
							for (TransmissionNode node2 : nodes) {
								double ratio = node.type().apply(node, node2);
								if (ratio != 0.0) return new ParametrizedReference<KineticReference, Double>(node2.reference(), ratio);
							}
						}
						return null;
					})
					.filter(Objects::nonNull)
					.distinct();
				})
				.filter(Objects::nonNull)
				.toList();
	}

	@Override
	protected KineticComponent findOrCreateComponentAt(KineticReference reference) {
		BlockState state = this.level.getBlockState(reference.pos());
		if (state.getBlock() instanceof IKineticBlock kinetic) {
			BlockState partState = kinetic.getPartState(level, reference.pos(), reference.partId(), state);
			if (partState.getBlock() instanceof IKineticBlock partBlock) {
				return new KineticComponent(reference, partBlock, partState);
			}
		}
		return null;
	}
	
	/**
	 * Returns the components at the given position
	 */
	public Collection<KineticComponent> findComponentsAt(BlockPos position) {
		return this.listComponents().stream()
				.filter(r -> r.reference().pos().equals(position))
				.toList();
	}
	
	/**
	 * Returns all networks with an component at the given position
	 */
	public Collection<KineticNetwork> findNetworksAt(BlockPos position) {
		Collection<KineticComponent> components = findComponentsAt(position);
		return components.stream()
			.map(r -> findNetworkAt(r.reference()))
			.distinct()
			.filter(Objects::nonNull)
			.toList();
	}

	/**
	 * Returns a set containing all components in the given chunk
	 */
	public Collection<KineticComponent> findComponentsInChunk(ChunkPos chunkPos) {
		return listComponents().stream()
				.filter(c -> new ChunkPos(c.reference().pos()).equals(chunkPos))
				.toList();
	}

	/**
	 * Updates all networks which have a component at the given position.
	 */
	public void markNetworksRecompute(BlockPos position) {
		findComponentsAt(position).forEach(c -> updateTicket(c.reference(), UpdateType.NETWORK_UPDATE));
	}
	
	/**
	 * Returns true if the component is already registered for an network
	 */
	public boolean isInNetwork(KineticComponent component) {
		return findNetworkAt(component) != null;
	}

}
