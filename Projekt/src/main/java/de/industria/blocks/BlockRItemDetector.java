package de.industria.blocks;

import de.industria.tileentity.TileEntityRItemDetector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockRItemDetector extends BlockContainerBase {
	
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public BlockRItemDetector() {
		super("item_detector", Material.WOOD, 1.5F, SoundType.WOOD, true);
		this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, false));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
		super.fillStateContainer(builder);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityRItemDetector();
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof TileEntityRItemDetector) {
			ItemStack heldStack = player.getHeldItemMainhand();
			if (heldStack.isEmpty() && player.isSneaking()) {
				((TileEntityRItemDetector) tileEntity).setItemFilter(ItemStack.EMPTY);
			} else {
				ItemStack filterStack = heldStack.copy();
				filterStack.setCount(1);
				((TileEntityRItemDetector) tileEntity).setItemFilter(filterStack);
			}
			return ActionResultType.SUCCESS;
		}		
		return ActionResultType.PASS;
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return side == state.get(FACING).getOpposite();
	}
	
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return blockState.get(POWERED) && side == blockState.get(FACING) ? 15 : 0;
	}
	
	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return this.getWeakPower(blockState, blockAccess, pos, side);
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
