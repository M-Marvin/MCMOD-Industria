package de.m_marvin.genet.domains.integrators;

import java.util.Arrays;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.m_marvin.genet.NodeNet;
import de.m_marvin.genet.domains.TypeChain;
import de.m_marvin.genet.domains.TypeChainDomain;
import de.m_marvin.genet.domains.integrators.IDeviceImplementor.FaceConnection;
import de.m_marvin.genet.network.nodal.Device;
import de.m_marvin.genet.network.nodal.Device.DeviceChangeResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.Type;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class LevelBlockDomainIntegrator extends TypeChainDomain.DomainIntegrator implements ICapabilityProvider {
	
	private final LazyOptional<LevelBlockDomainIntegrator> holder = LazyOptional.of(() -> this);
	private final ServerLevel level;
	
	public LevelBlockDomainIntegrator(ServerLevel level) {
		NodeNet.GLOBAL.get().super();
		MinecraftForge.EVENT_BUS.register(this);
		this.level = level;
	}
	
	public ServerLevel getLevel() {
		return level;
	}
	
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (side == null && cap == NodeNet.LEVEL_BLOCK_INTEGRATOR)
			this.holder.cast();
		return null;
	}

	public class BlockDevice extends Device<TypeChain, TypeChain> {
		
		/** represents the position of a device within the domain **/
		public static final TypeChain.Format BLOCK_DEVICE = TypeChain.registerFormat(NodeNet.MODID, "block_device", TypeChain.Format.of(ResourceLocation.class, BlockPos.class));
		/** represents a element within the device **/
		public static final TypeChain.Format BLOCK_DEVICE_ELEMENT = TypeChain.registerFormat(NodeNet.MODID, "block_device_element", BLOCK_DEVICE.sub(String.class));
		/** represents a node internal to the device **/
		public static final TypeChain.Format BLOCK_DEVICE_INNER_NODE = TypeChain.registerFormat(NodeNet.MODID, "block_device_element_node", BLOCK_DEVICE.sub(String.class));
		/** represents a node of the device which can connect to other devices **/
		public static final TypeChain.Format BLOCK_DEVICE_OUTER_NODE = TypeChain.registerFormat(NodeNet.MODID, "block_device_face_node", BLOCK_DEVICE.sub(Axis.class));
		// NOTE: Since an axis is used to represent nodes on the four sides of an block
		// each value (X Y Z) describes a node POSITIVE from the block position.
		// To represent a node on the opposite site, the position has to be decremented by 1 with the same axis value

		public static TypeChain deviceName(Level level, BlockPos position) {
			return BLOCK_DEVICE.of(level.dimensionTypeId().location(), position);
		}
		
		public TypeChain elementName(String name) {
			return BLOCK_DEVICE_ELEMENT.subChain(name(), name);
		}
		
		public TypeChain innerNode(String name) {
			return BLOCK_DEVICE_INNER_NODE.subChain(name(), name);
		}
		
		public TypeChain outerNode(Direction side) {
			if (side.getAxisDirection() == AxisDirection.POSITIVE)
				return BLOCK_DEVICE_OUTER_NODE.of(getLevel().dimensionTypeId().location(), getPosition(), side.getAxis());
			else
				return BLOCK_DEVICE_OUTER_NODE.of(getLevel().dimensionTypeId().location(), getPosition().relative(side), side.getAxis());
		}
		
		private BlockState cachedState = Blocks.VOID_AIR.defaultBlockState();
		private IDeviceImplementor cachedBlock = null;
		private TypeChain[] cachedNodes = new TypeChain[0];
		
		public BlockDevice(BlockPos position) {
			super(deviceName(LevelBlockDomainIntegrator.this.getLevel(), position));
		}
		
		public BlockDevice(TypeChain name) {
			super(name);
//			if (!syncFromLevel())
//				throw new IllegalStateException("device block is not a block device: " + name);
		}
		
		public Level getLevel() {
			return LevelBlockDomainIntegrator.this.getLevel();
		}
		
		@Override
		public DeviceChangeResult onDeviceChange() {
			
			BlockState state = LevelBlockDomainIntegrator.this.getLevel().getBlockState(getPosition());
			if (state.getBlock() instanceof IDeviceImplementor implementor) {
				
				TypeChain[] nodes = Stream.of(Direction.values())
						.filter(side -> implementor.hasDeviceConnection(getLevel(), getPosition(), state, side) == FaceConnection.OPEN)
						.map(side -> outerNode(side))
						.toArray(TypeChain[]::new);
				boolean nodesChanged = !Arrays.equals(this.cachedNodes, nodes);
				
				if (nodesChanged || !implementor.doesDeviceKeepElements(getLevel(), getPosition(), this.cachedState, state)) {
					removeAllElements();
					this.cachedState = state;
					this.cachedBlock = implementor;
					this.cachedNodes = nodes;
					implementor.installDeviceElements(this);
				} else {
					this.cachedState = state;
					this.cachedBlock = implementor;
					this.cachedNodes = nodes;
					implementor.updateDeviceElements(this);
				}
				
				return nodesChanged ? DeviceChangeResult.REINSERT : DeviceChangeResult.NO_CHANGE;
				
			} else {
				
				return DeviceChangeResult.REMOVAL;
				
			}
			
		}
		
		public ResourceLocation getDimension() {
			return (ResourceLocation) name().npos()[0];
		}
		
		public BlockPos getPosition() {
			return (BlockPos) name().npos()[1];
		}
		
		public BlockState getState() {
			return this.cachedState;
		}
		
		@Override
		public TypeChain[] nodes() {
			return this.cachedNodes;
		}
		
	}
	
	// TODO this should happen somewhere else ... idealy where save/load is handled later on ...
	@SubscribeEvent
	public void onLevelTick(TickEvent.LevelTickEvent event) {
		if (event.phase == Phase.END && event.type == Type.LEVEL && event.side == LogicalSide.SERVER)
			NodeNet.GLOBAL.get().finalizeQueuedChanges();
	}
	
	@SubscribeEvent
	public void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		if (event.getLevel() != this.level) return; // TODO make this static ?
		TypeChain deviceName = BlockDevice.deviceName(getLevel(), event.getPos());
		Device<TypeChain, TypeChain> device = getDevice(deviceName);
		
		if (device instanceof BlockDevice blockDevice) {
			DeviceChangeResult result = blockDevice.onDeviceChange();
			if (result == DeviceChangeResult.REMOVAL) {
				removeDevice(deviceName);
			} else if (result == DeviceChangeResult.REINSERT) {
				insertDevice(blockDevice);
			}
			return;
		} else if (device != null) {
			removeDevice(deviceName);
		}
		
		BlockDevice blockDevice = new BlockDevice(event.getPos());
		if (blockDevice.onDeviceChange() != DeviceChangeResult.REMOVAL)
			insertDevice(blockDevice);
	}
	
	@SubscribeEvent
	public void onChunkStateChange(ChunkEvent event) {
		// Network loading and unloading
		
	}
	
}
