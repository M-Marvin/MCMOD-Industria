package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.redtec.tileentity.TileEntityConveyorBelt;
import de.redtec.util.IAdvancedBlockInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockConveyorBelt extends BlockContainerBase implements IAdvancedBlockInfo {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final EnumProperty<BeltState> RIGHT = EnumProperty.create("right", BeltState.class);
	public static final EnumProperty<BeltState> LEFT = EnumProperty.create("left", BeltState.class);
	
	public BlockConveyorBelt(String name) {
		super(name, Material.IRON, 1.5F, SoundType.LADDER);
	}
	
	public BlockConveyorBelt() {
		super("conveyor_belt", Material.IRON, 1.5F, SoundType.LADDER, true);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(RIGHT, BeltState.CLOSE).with(LEFT, BeltState.CLOSE));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Direction facing = state.get(FACING); 
		if (facing.getAxis() == Axis.Z) {
			boolean connectedWest = facing == Direction.NORTH ? (state.get(LEFT) == BeltState.OPEN) : (state.get(RIGHT) == BeltState.OPEN);
			boolean connectedEast = facing == Direction.NORTH ? (state.get(RIGHT) == BeltState.OPEN) : (state.get(LEFT) == BeltState.OPEN);
			VoxelShape shapeWest = connectedWest ? VoxelShapes.or(Block.makeCuboidShape(1, 0, 0, 2, 2, 16), Block.makeCuboidShape(1, 2, 0, 2, 3, 1), Block.makeCuboidShape(1, 2, 15, 2, 3, 16)) : Block.makeCuboidShape(1, 0, 0, 2, 3, 16);
			VoxelShape shapeEast = connectedEast ? VoxelShapes.or(Block.makeCuboidShape(14, 0, 0, 15, 2, 16), Block.makeCuboidShape(14, 2, 0, 15, 3, 1), Block.makeCuboidShape(14, 2, 15, 15, 3, 16)) : Block.makeCuboidShape(14, 0, 0, 15, 3, 16);
			return VoxelShapes.or(Block.makeCuboidShape(2, 1, 0, 14, 2, 16), shapeWest, shapeEast);
		} else {
			boolean connectedNorth = facing == Direction.EAST ? (state.get(LEFT) == BeltState.OPEN) : (state.get(RIGHT) == BeltState.OPEN);
			boolean connectedSouth = facing == Direction.EAST ? (state.get(RIGHT) == BeltState.OPEN) : (state.get(LEFT) == BeltState.OPEN);
			VoxelShape shapeNorth = connectedNorth ? VoxelShapes.or(Block.makeCuboidShape(0, 0, 1, 16, 2, 2), Block.makeCuboidShape(0, 2, 1, 1, 3, 2), Block.makeCuboidShape(15, 2, 1, 16, 3, 2)) : Block.makeCuboidShape(0, 0, 1, 16, 3, 2);
			VoxelShape shapeSouth = connectedSouth ? VoxelShapes.or(Block.makeCuboidShape(0, 0, 14, 16, 2, 15), Block.makeCuboidShape(0, 2, 14, 1, 3, 15), Block.makeCuboidShape(15, 2, 14, 16, 3, 15)) : Block.makeCuboidShape(0, 0, 14, 16, 3, 15);
			return VoxelShapes.or(Block.makeCuboidShape(0, 1, 2, 16, 2, 14), shapeNorth, shapeSouth);
		}
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, RIGHT, LEFT);
	}
	
	public BlockState updateState(World world, BlockPos pos, BlockState state) {
		Direction facing = state.get(FACING);
		BlockState stateRight = world.getBlockState(pos.offset(facing.rotateY()));
		BlockState stateLeft = world.getBlockState(pos.offset(facing.rotateYCCW()));
		state = state.with(RIGHT, canConnectWithState(stateRight, facing.rotateY()) ? BeltState.OPEN : BeltState.CLOSE);
		state = state.with(LEFT, canConnectWithState(stateLeft, facing.rotateYCCW()) ? BeltState.OPEN : BeltState.CLOSE);
		return state;
	}
	
	public boolean canConnectWithState(BlockState state, Direction side) {
		if (state.getBlock() instanceof BlockConveyorSpliter) {
			Direction facing = state.get(FACING);
			if (state.get(RIGHT) == BeltState.OPEN) {
				return side == facing.rotateYCCW() || side == facing.getOpposite();
			} else if (state.get(LEFT) == BeltState.OPEN) {
				return side == facing.rotateY() || side == facing.getOpposite();
			}
		} else if (state.getBlock() instanceof BlockConveyorBelt) {
			return state.get(FACING) == side.getOpposite();
		}
		return false;
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockState state1 = updateState(worldIn, pos, state);
		if (!state1.equals(state)) {
			worldIn.setBlockState(pos, state1);
		}
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return updateState(context.getWorld(), context.getPos(), this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing()));
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityConveyorBelt();
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof TileEntityConveyorBelt) {
			if (((TileEntityConveyorBelt) tileEntity).isEmpty() && !player.getHeldItemMainhand().isEmpty()) {
				((TileEntityConveyorBelt) tileEntity).setInventorySlotContents(0, player.getHeldItemMainhand());
				player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
			} else {
				InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileEntity);
				((TileEntityConveyorBelt) tileEntity).clear();
			}
			return ActionResultType.CONSUME;
		}
		return ActionResultType.PASS;
	}
	
	
	
	public static enum BeltState implements IStringSerializable {
		
		OPEN("open"),CLOSE("close");
		
		BeltState(String name) {
			this.name = name;
		}
		
		private String name;
		
		public String getName() {
			return name;
		}
		
		@Override
		public String getString() {
			return name;
		}
		
	}
	
	
	
	@Override
	public List<ITextComponent> getBlockInfo() {
		List<ITextComponent> info = new ArrayList<ITextComponent>();
		info.add(new TranslationTextComponent("redtec.block.info.maxItems", "~1.3"));
		return info;
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
	}
	
}
