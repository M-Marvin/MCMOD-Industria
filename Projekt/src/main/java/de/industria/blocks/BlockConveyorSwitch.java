package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.blocks.BlockConveyorBelt.BeltState;
import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.tileentity.TileEntityConveyorBelt;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockConveyorSwitch extends BlockContainerBase implements IBAdvancedBlockInfo {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final EnumProperty<BeltState> RIGHT = EnumProperty.create("right", BeltState.class);
	public static final EnumProperty<BeltState> LEFT = EnumProperty.create("left", BeltState.class);
	public static final	BooleanProperty ENABLED = BooleanProperty.create("enabled");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public BlockConveyorSwitch(String name) {
		super(name, Material.METAL, 1.5F, SoundType.LADDER);
	}
	
	public BlockConveyorSwitch() {
		super("conveyor_switch", Material.METAL, 1.5F, SoundType.LADDER, true);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(RIGHT, BeltState.CLOSE).setValue(LEFT, BeltState.CLOSE).setValue(WATERLOGGED, false));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource().defaultFluidState() : Fluids.EMPTY.defaultFluidState();
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Direction facing = state.getValue(FACING); 
		if (facing.getAxis() == Axis.Z) {
			boolean connectedWest = facing == Direction.NORTH ? (state.getValue(LEFT) == BeltState.OPEN) : (state.getValue(RIGHT) == BeltState.OPEN);
			boolean connectedEast = facing == Direction.NORTH ? (state.getValue(RIGHT) == BeltState.OPEN) : (state.getValue(LEFT) == BeltState.OPEN);
			VoxelShape shapeWest = connectedWest ? VoxelShapes.or(Block.box(1, 0, 0, 2, 2, 16), Block.box(1, 2, 0, 2, 3, 1), Block.box(1, 2, 15, 2, 3, 16)) : Block.box(1, 0, 0, 2, 3, 16);
			VoxelShape shapeEast = connectedEast ? VoxelShapes.or(Block.box(14, 0, 0, 15, 2, 16), Block.box(14, 2, 0, 15, 3, 1), Block.box(14, 2, 15, 15, 3, 16)) : Block.box(14, 0, 0, 15, 3, 16);
			return VoxelShapes.or(Block.box(2, 1, 0, 14, 2, 16), shapeWest, shapeEast);
		} else {
			boolean connectedNorth = facing == Direction.EAST ? (state.getValue(LEFT) == BeltState.OPEN) : (state.getValue(RIGHT) == BeltState.OPEN);
			boolean connectedSouth = facing == Direction.EAST ? (state.getValue(RIGHT) == BeltState.OPEN) : (state.getValue(LEFT) == BeltState.OPEN);
			VoxelShape shapeNorth = connectedNorth ? VoxelShapes.or(Block.box(0, 0, 1, 16, 2, 2), Block.box(0, 2, 1, 1, 3, 2), Block.box(15, 2, 1, 16, 3, 2)) : Block.box(0, 0, 1, 16, 3, 2);
			VoxelShape shapeSouth = connectedSouth ? VoxelShapes.or(Block.box(0, 0, 14, 16, 2, 15), Block.box(0, 2, 14, 1, 3, 15), Block.box(15, 2, 14, 16, 3, 15)) : Block.box(0, 0, 14, 16, 3, 15);
			return VoxelShapes.or(Block.box(0, 1, 2, 16, 2, 14), shapeNorth, shapeSouth);
		}
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, RIGHT, LEFT, WATERLOGGED, ENABLED);
	}
	
	public BlockState updateState(World world, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(FACING);
		BlockState stateRight = world.getBlockState(pos.relative(facing.getClockWise()));
		BlockState stateLeft = world.getBlockState(pos.relative(facing.getCounterClockWise()));
		state = state.setValue(RIGHT, canConnectWithState(stateRight, facing.getClockWise()) ? BeltState.OPEN : BeltState.CLOSE);
		state = state.setValue(LEFT, canConnectWithState(stateLeft, facing.getCounterClockWise()) ? BeltState.OPEN : BeltState.CLOSE);
		boolean powered = world.hasNeighborSignal(pos) || world.getBestNeighborSignal(pos) > 0;
		state = state.setValue(ENABLED, powered);
		return state;
	}
	
	public boolean canConnectWithState(BlockState state, Direction side) {
		if (state.getBlock() instanceof BlockConveyorSpliter) {
			Direction facing = state.getValue(FACING);
			if (state.getValue(RIGHT) == BeltState.OPEN) {
				return side == facing.getCounterClockWise() || side == facing.getOpposite();
			} else if (state.getValue(LEFT) == BeltState.OPEN) {
				return side == facing.getClockWise() || side == facing.getOpposite();
			}
		} else if (state.getBlock() instanceof BlockConveyorSwitch || state.getBlock() instanceof BlockConveyorBelt) {
			return state.getValue(FACING) == side.getOpposite();
		}
		return false;
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockState state1 = updateState(worldIn, pos, state);
		if (!state1.equals(state)) {
			worldIn.setBlockAndUpdate(pos, state1);
		}
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return updateState(context.getLevel(), context.getClickedPos(), this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(ENABLED, false));
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return true;
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityConveyorBelt();
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof TileEntityConveyorBelt) {
			if (((TileEntityConveyorBelt) tileEntity).isEmpty() && !player.getMainHandItem().isEmpty()) {
				((TileEntityConveyorBelt) tileEntity).setItem(0, player.getMainHandItem());
				player.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
			} else {
				InventoryHelper.dropContents(worldIn, pos, (IInventory) tileEntity);
				((TileEntityConveyorBelt) tileEntity).clearContent();
			}
			return ActionResultType.CONSUME;
		}
		return ActionResultType.PASS;
	}
	
	
	
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.maxItems", "~1.3"));
			info.add(new TranslationTextComponent("industria.block.info.conveyorSwitch"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING)));
	}
	
}
