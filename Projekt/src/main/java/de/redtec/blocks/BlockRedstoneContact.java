package de.redtec.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockRedstoneContact extends BlockBase {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	
	public BlockRedstoneContact() {
		super("redstone_contact", Material.WOOD, 0.7F, SoundType.WOOD);
		this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, false));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(POWERED, FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
		return updateState(state, context.getWorld(), context.getPos());
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		BlockState newState = updateState(state, worldIn, pos);
		worldIn.setBlockState(pos, newState);
		if (state.get(POWERED) != newState.get(POWERED)) worldIn.notifyNeighborsOfStateExcept(pos.offset(state.get(FACING).getOpposite()), this, state.get(FACING));
		
	}
	
	public BlockState updateState(BlockState state, World world, BlockPos pos) {
		
		BlockState otherBlock = world.getBlockState(pos.offset(state.get(FACING)));
		if (otherBlock.getBlock() == this) {
			boolean powered = otherBlock.get(FACING).getOpposite() == state.get(FACING);
			state = state.with(POWERED, powered);
		} else {
			state = state.with(POWERED, false);
		}
		
		return state;
		
	}
	
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return side == blockState.get(FACING) && blockState.get(POWERED) ? 15 : 0;
	}
	
	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return side == blockState.get(FACING) && blockState.get(POWERED) ? 15 : 0;
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return side == world.getBlockState(pos).get(FACING);
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
