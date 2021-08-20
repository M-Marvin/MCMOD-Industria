package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Stream;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.handler.MathHelper;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMDiode extends BlockBase implements IBElectricConnectiveBlock, IBAdvancedBlockInfo {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<MaxCurrent> MAX_CURRENT = EnumProperty.create("max_current", MaxCurrent.class);	
	
	public BlockMDiode() {
		super("diode", Material.METAL, 2F, SoundType.METAL);
		this.registerDefaultState(this.stateDefinition.any().setValue(MAX_CURRENT, MaxCurrent.NO_LIMIT));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return VoxelHelper.rotateShape(Stream.of(
			Block.box(6, 9, 1, 10, 15, 7),
			Block.box(3, 0, 0, 13, 9, 16),
			Block.box(6, 9, 9, 10, 15, 15)
			).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get(), state.getValue(FACING));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, MAX_CURRENT);
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.diode"));
		};
	}

	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}

	@Override
	public Voltage getVoltage(World world, BlockPos pos, BlockState state, Direction side) {
		return getNetwork(world, pos, state.getValue(FACING).getOpposite()).getVoltage();
	}

	@Override
	public float getNeededCurrent(World world, BlockPos pos, BlockState state, Direction side) {
//		if (world.getBlockState(pos.relative(state.getValue(FACING))).getBlock() == this) return 0;
//		if (world.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).getBlock() == this) return 0;
		ElectricityNetwork network2 = ElectricityNetworkHandler.getHandlerForWorld(world).getNetworkState(world, pos, state.getValue(FACING), this);
		ElectricityNetwork network1 = ElectricityNetworkHandler.getHandlerForWorld(world).getNetworkState(world, pos, state.getValue(FACING).getOpposite(), this);
		float current = side != state.getValue(FACING) ? -network1.getCapacity() : network2.getNeedCurrent() - network2.getCurrent();
		float maxCurrent = state.getValue(MAX_CURRENT).getCurrent();
		float resultCurrent = maxCurrent > 0 ? (float) MathHelper.castBounds(maxCurrent, current) : current;
		return resultCurrent;
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult rayTraceResult) {
		if (player.isShiftKeyDown()) {
			MaxCurrent maxCurrent = state.getValue(MAX_CURRENT).next();
			worldIn.setBlockAndUpdate(pos, state.setValue(MAX_CURRENT, maxCurrent));
			player.displayClientMessage(new TranslationTextComponent("industria.block.info.diode." + (maxCurrent != MaxCurrent.NO_LIMIT ? "maxCurrent" : "unlimitedCurrent"), maxCurrent.getCurrent()), true);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}
	
	@Override
	public boolean canConnect(Direction side, World world, BlockPos pos, BlockState state) {
		return side.getAxis() == state.getValue(FACING).getAxis();
	}
	
	@Override
	public boolean isSwitchClosed(World worldIn, BlockPos pos, BlockState state) {
		return false;
	}
	
	@Override
	public DeviceType getDeviceType() {
		return DeviceType.SWITCH;
	}
	
	protected ElectricityNetwork getNetwork(World world, BlockPos pos, Direction side) {
		BlockPos networkPos = pos.relative(side);
		ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(world).getNetwork(networkPos);
		return network;
	}
	
	public static enum MaxCurrent implements IStringSerializable {
		NO_LIMIT("no_limit", -1),LOW("low", 4),NORMAL("normal", 8),MEDIUM("medium",16),HIGH("high",32),EXTREME("extreme",64);
		
		protected String name;
		protected int current;
		
		private MaxCurrent(String name, int current) {
			this.name = name;
			this.current = current;
		}
		
		@Override
		public String getSerializedName() {
			return this.name;
		}
		
		public int getCurrent() {
			return current;
		}
		
		public MaxCurrent next() {
			if (this == NO_LIMIT) {
				return LOW;
			} else if (this == LOW) {
				return NORMAL;
			} else if (this == NORMAL) {
				return MEDIUM;
			} else if (this == MEDIUM) {
				return HIGH;
			} else if (this == HIGH) {
				return EXTREME;
			}
			return NO_LIMIT;
		}
		
	}
	
}
