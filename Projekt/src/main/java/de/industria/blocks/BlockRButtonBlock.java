package de.industria.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockRButtonBlock extends BlockBase {
	
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	
	public BlockRButtonBlock() {
		super("button_block", Material.WOOD, 0.5F, SoundType.WOOD, true);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		if (!state.getValue(POWERED)) {
			
			worldIn.setBlockAndUpdate(pos, state.setValue(POWERED, true));
			worldIn.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1, 0.5F);
			worldIn.getBlockTicks().scheduleTick(pos, this, 20);
			this.updateNeighbors(worldIn, pos);
			
		}
		
		return ActionResultType.SUCCESS;
		
	}
	
	@Override
	public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		
		if (isMoving) {
			
			worldIn.setBlockAndUpdate(pos, state.setValue(POWERED, false));
			
		}
		
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		
		worldIn.setBlockAndUpdate(pos, state.setValue(POWERED, false));
		worldIn.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 1, 0.5F);
		this.updateNeighbors(worldIn, pos);
		
	}
	
	@Override
	public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return blockState.getValue(POWERED) ? 15 : 0;
	}
	
	@Override
	public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return blockState.getValue(POWERED) ? 15 : 0;
	}
	
	public void updateNeighbors(World world, BlockPos pos) {
		
		for (Direction d : Direction.values()) {
			
			world.updateNeighborsAtExceptFromFacing(pos.relative(d), this, d.getOpposite());
			
		}
		
	}
	
}
