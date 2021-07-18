package de.industria.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockStackedRedstoneTorch extends BlockBase {
	
	public static final BooleanProperty LIT = BooleanProperty.create("lit");
	
	public BlockStackedRedstoneTorch() {
		super("stacked_redstone_torch", Material.METAL, 1.5F, 0.5F, SoundType.LANTERN, true);
		this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return Block.box(5, 0, 5, 11, 16, 11);
	}
	
	@Override
	public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return (blockState.getValue(LIT) && side == Direction.DOWN) ? 15 : 0;
	}
	
	@Override
	public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return (blockState.getValue(LIT) && side == Direction.DOWN) ? 15 : 0;
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		if (canSurvive(state, worldIn, pos)) {
			
			boolean powered = state.getValue(LIT);
			boolean power = worldIn.getSignal(pos.below(), Direction.DOWN) > 0;
			
			if (powered != power) {
				
				worldIn.getBlockTicks().scheduleTick(pos, this, 1);
				
			}
			
		} else {
			
			dropResources(state, worldIn, pos);
			worldIn.removeBlock(pos, false);
			
		}
		
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		
		boolean powered = worldIn.getSignal(pos.below(), Direction.DOWN) > 0;
		worldIn.setBlockAndUpdate(pos, state.setValue(LIT, powered));
		worldIn.updateNeighborsAt(pos.above(), this);
		
	}
	
	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return 8;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
	     if (stateIn.getValue(LIT)) {
	        double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d1 = (double)pos.getY() + 0.9D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        worldIn.addParticle(RedstoneParticleData.REDSTONE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
	     } else {
	        double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d1 = (double)pos.getY() + 0.4D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        worldIn.addParticle(RedstoneParticleData.REDSTONE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
	     }
	 }
	
	@Override
	public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return !isAir(worldIn.getBlockState(pos.below()), worldIn, pos);
	}
	
	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
		return true;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		boolean powered = context.getLevel().getSignal(context.getClickedPos().below(), Direction.DOWN) > 0;
		return this.defaultBlockState().setValue(LIT, powered);
	}
	
}
