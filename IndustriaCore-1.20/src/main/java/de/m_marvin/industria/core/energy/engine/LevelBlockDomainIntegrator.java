package de.m_marvin.industria.core.energy.engine;

import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mojang.math.Axis;

import de.m_marvin.genet.NodeNet;
import de.m_marvin.genet.domains.TypeChain;
import de.m_marvin.genet.domains.TypeChainDomain;
import de.m_marvin.genet.nodal.CircuitSpace;
import de.m_marvin.genet.nodal.Device;
import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.energy.types.IBlockDeviceProvider;
import de.m_marvin.industria.core.registries.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
		if (side == null && cap == Capabilities.LEVEL_BLOCK_INTEGRATOR)
			this.holder.cast();
		return null;
	}
	
	public class BlockDevice extends Device<TypeChain, TypeChain> {
		
		/** represents the position of a device within the domain **/
		public static final TypeChain.Format BLOCK_DEVICE = TypeChain.registerFormat(IndustriaCore.MODID, "block_device", TypeChain.Format.of(ResourceLocation.class, BlockPos.class));
		/** represents a element within the device **/
		public static final TypeChain.Format BLOCK_DEVICE_ELEMENT = TypeChain.registerFormat(IndustriaCore.MODID, "block_device_element", BLOCK_DEVICE.sub(String.class));
		/** represents a node on the device within the domain **/
		public static final TypeChain.Format BLOCK_DEVICE_FACE_NODE = TypeChain.registerFormat(IndustriaCore.MODID, "block_device_face_node", BLOCK_DEVICE.sub(Axis.class));
		/** represents a node internal to the device on an element **/
		public static final TypeChain.Format BLOCK_DEVICE_ELEMENT_NODE = TypeChain.registerFormat(IndustriaCore.MODID, "block_device_element_node", BLOCK_DEVICE_ELEMENT.sub(String.class));
		// NOTE: Since an axis is used to represent nodes on the four sides of an block
		// each value (X Y Z) describes a node POSITIVE from the block position.
		// To represent a node on the opposite site, the position has to be decremented by 1 with the same axis value

		public static TypeChain deviceName(Level level, BlockPos position) {
			return BLOCK_DEVICE.of(level.dimensionTypeId().registry(), position);
		}
		
		public static TypeChain deviceNode(Level level, BlockPos position, Direction side) {
			if (side.getAxisDirection() == AxisDirection.POSITIVE)
				return BLOCK_DEVICE_FACE_NODE.of(level.dimensionTypeId().registry(), position, side.getAxis());
			else
				return BLOCK_DEVICE_FACE_NODE.of(level.dimensionTypeId().registry(), position.relative(side), side.getAxis());
		}
		
		private BlockState cachedState;
		private IBlockDeviceProvider cachedBlock;
		private TypeChain[] cachedNodes;
		
		public BlockDevice(BlockPos position) {
			super(deviceName(LevelBlockDomainIntegrator.this.getLevel(), position));
		}
		
		public BlockDevice(TypeChain name) {
			super(name);
			if (!syncFromLevel())
				throw new IllegalStateException("device block is not a block device: " + name);
		}
		
		public boolean syncFromLevel() {
			BlockState state = LevelBlockDomainIntegrator.this.getLevel().getBlockState(getPosition());
			if (state.getBlock() instanceof IBlockDeviceProvider blockDeviceProvider) {
				if (state.equals(this.cachedState))
					return true;
				
				// TODO remove previous elements
				
				this.cachedBlock.onDeviceChange(LevelBlockDomainIntegrator.this.getLevel(), getPosition(), this.cachedState, state);
				this.cachedState = state;
				this.cachedBlock = blockDeviceProvider;
				this.cachedNodes = Stream.of(Direction.values())
						.filter(side -> this.cachedBlock.hasDeviceConnection(LevelBlockDomainIntegrator.this.getLevel(), getPosition(), this.cachedState, side))
						.map(side -> deviceNode(LevelBlockDomainIntegrator.this.getLevel(), getPosition(), side))
						.toArray(TypeChain[]::new);
				return true;
			}
			return false;
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

		@Override
		public void installToCircuit(CircuitSpace<TypeChain, TypeChain> circuits) {
			this.cachedBlock.onDeviceChange(LevelBlockDomainIntegrator.this.getLevel(), getPosition(), null, getState());
		}
		
	}
	
	@SubscribeEvent
	public void onBlockStateChange(BlockEvent.NeighborNotifyEvent event) {
		TypeChain deviceName = BlockDevice.deviceName(getLevel(), event.getPos());
		if (event.getState().getBlock() instanceof IBlockDeviceProvider) {
			Device<TypeChain, TypeChain> device = getDevice(deviceName);
			if (device instanceof BlockDevice blockDevice)
				blockDevice.syncFromLevel();
			else
				insertDevice(new BlockDevice(deviceName));
		} else {
			removeDevice(deviceName);
		}
	}
	
	@SubscribeEvent
	public void onChunkStateChange(ChunkEvent event) {
		// Network loading and unloading
		
	}
	
}
