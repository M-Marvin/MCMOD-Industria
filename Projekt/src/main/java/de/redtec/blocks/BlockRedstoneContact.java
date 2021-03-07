package de.redtec.blocks;

import java.util.Random;

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
import net.minecraft.world.server.ServerWorld;

public class BlockRedstoneContact extends BlockBase {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	
	public BlockRedstoneContact() {
		super("redstone_contact", Material.WOOD, 1.5F, SoundType.WOOD, true);
		this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, false));
	}
		
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(POWERED, FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		BlockState newState = updateState(state, worldIn, pos);
		worldIn.setBlockState(pos, newState);
		if (state.get(POWERED) != newState.get(POWERED)) {
			worldIn.notifyNeighborsOfStateExcept(pos.offset(state.get(FACING).getOpposite()), this, state.get(FACING));
		}
		
	}
	
	public BlockState updateState(BlockState state, World world, BlockPos pos) {
		
		Direction facing = state.get(FACING);
		BlockState otherBlock1 = world.getBlockState(pos.offset(state.get(FACING), 1));
		BlockState otherBlock2 = world.getBlockState(pos.offset(state.get(FACING), 2));
		boolean power =	(otherBlock1.getBlock() == this ? otherBlock1.get(FACING).getOpposite() == facing : false) ||
						(otherBlock2.getBlock() == this ? otherBlock2.get(FACING).getOpposite() == facing : false);
		boolean powered = state.get(POWERED);
		
		if (power != powered) {
			
			state = state.with(POWERED, power);
			world.getPendingBlockTicks().scheduleTick(pos.offset(state.get(FACING), 2), this, 1);
			
		}
		
		return state;
		
	}
	
	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		
		BlockState newState = updateState(state, worldIn, pos);
		worldIn.setBlockState(pos, newState);
		
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {

		BlockState newState = updateState(state, worldIn, pos);
		worldIn.setBlockState(pos, newState);
		if (state.get(POWERED) != newState.get(POWERED)) {
			worldIn.notifyNeighborsOfStateExcept(pos.offset(state.get(FACING).getOpposite()), this, state.get(FACING));
		}
		
	}
	
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		
		if (newState.getBlock() != state.getBlock()) {
			
			worldIn.notifyNeighborsOfStateExcept(pos.offset(state.get(FACING).getOpposite()), this, state.get(FACING));
			worldIn.getPendingBlockTicks().scheduleTick(pos.offset(state.get(FACING), 2), this, 1);
			
		}
		
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
