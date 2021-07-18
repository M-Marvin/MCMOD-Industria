package de.industria.blocks;

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

public class BlockRRedstoneContact extends BlockBase {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	
	public BlockRRedstoneContact() {
		super("redstone_contact", Material.WOOD, 1.5F, SoundType.WOOD, true);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
	}
		
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(POWERED, FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		BlockState newState = updateState(state, worldIn, pos);
		worldIn.setBlockAndUpdate(pos, newState);
		if (state.getValue(POWERED) != newState.getValue(POWERED)) {
			worldIn.updateNeighborsAtExceptFromFacing(pos.relative(state.getValue(FACING).getOpposite()), this, state.getValue(FACING));
		}
		
	}
	
	public BlockState updateState(BlockState state, World world, BlockPos pos) {
		
		Direction facing = state.getValue(FACING);
		BlockState otherBlock1 = world.getBlockState(pos.relative(state.getValue(FACING), 1));
		BlockState otherBlock2 = world.getBlockState(pos.relative(state.getValue(FACING), 2));
		boolean power =	(otherBlock1.getBlock() == this ? otherBlock1.getValue(FACING).getOpposite() == facing : false) ||
						(otherBlock2.getBlock() == this ? otherBlock2.getValue(FACING).getOpposite() == facing : false);
		boolean powered = state.getValue(POWERED);
		
		if (power != powered) {
			
			state = state.setValue(POWERED, power);
			world.getBlockTicks().scheduleTick(pos.relative(state.getValue(FACING), 2), this, 1);
			
		}
		
		return state;
		
	}
	
	@Override
	public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		
		BlockState newState = updateState(state, worldIn, pos);
		worldIn.setBlockAndUpdate(pos, newState);
		
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {

		BlockState newState = updateState(state, worldIn, pos);
		worldIn.setBlockAndUpdate(pos, newState);
		if (state.getValue(POWERED) != newState.getValue(POWERED)) {
			worldIn.updateNeighborsAtExceptFromFacing(pos.relative(state.getValue(FACING).getOpposite()), this, state.getValue(FACING));
		}
		
	}
	
	@Override
	public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		
		if (newState.getBlock() != state.getBlock()) {
			
			worldIn.updateNeighborsAtExceptFromFacing(pos.relative(state.getValue(FACING).getOpposite()), this, state.getValue(FACING));
			worldIn.getBlockTicks().scheduleTick(pos.relative(state.getValue(FACING), 2), this, 1);
			
		}
		
	}
	
	@Override
	public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return side == blockState.getValue(FACING) && blockState.getValue(POWERED) ? 15 : 0;
	}
	
	@Override
	public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return side == blockState.getValue(FACING) && blockState.getValue(POWERED) ? 15 : 0;
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return side == world.getBlockState(pos).getValue(FACING);
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
