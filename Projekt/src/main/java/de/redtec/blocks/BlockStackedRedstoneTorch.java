package de.redtec.blocks;

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
		super("stacked_redstone_torch", Material.IRON, 1.5F, 0.5F, SoundType.LANTERN, true);
		this.setDefaultState(this.stateContainer.getBaseState().with(LIT, false));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(LIT);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return Block.makeCuboidShape(5, 0, 5, 11, 16, 11);
	}
	
	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return (blockState.get(LIT) && side == Direction.DOWN) ? 15 : 0;
	}
	
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return (blockState.get(LIT) && side == Direction.DOWN) ? 15 : 0;
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		if (isValidPosition(state, worldIn, pos)) {
			
			boolean powered = state.get(LIT);
			boolean power = worldIn.getRedstonePower(pos.down(), Direction.DOWN) > 0;
			
			if (powered != power) {
				
				worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
				
			}
			
		} else {
			
			spawnDrops(state, worldIn, pos);
			worldIn.removeBlock(pos, false);
			
		}
		
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		
		boolean powered = worldIn.getRedstonePower(pos.down(), Direction.DOWN) > 0;
		worldIn.setBlockState(pos, state.with(LIT, powered));
		worldIn.notifyNeighborsOfStateChange(pos.up(), this);
		
	}
	
	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return 8;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
	     if (stateIn.get(LIT)) {
	        double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d1 = (double)pos.getY() + 0.9D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D);
	     } else {
	        double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d1 = (double)pos.getY() + 0.4D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D);
	     }
	 }
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return !isAir(worldIn.getBlockState(pos.down()), worldIn, pos);
	}
	
	@Override
	public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
		return true;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		boolean powered = context.getWorld().getRedstonePower(context.getPos().down(), Direction.DOWN) > 0;
		return this.getDefaultState().with(LIT, powered);
	}
	
}
