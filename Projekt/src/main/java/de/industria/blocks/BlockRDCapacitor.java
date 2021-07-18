package de.industria.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockRDCapacitor extends BlockRedstoneDiode {

	public static final BooleanProperty INVERTED = BooleanProperty.create("inverted");
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");
	public static final IntegerProperty DELAY = IntegerProperty.create("delay", 1, 4);
	
	public BlockRDCapacitor() {
		super("capacitor");
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(INVERTED, false).setValue(FACING, Direction.NORTH).setValue(DELAY, 1));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(INVERTED, POWERED, DELAY);
		super.createBlockStateDefinition(builder);
	}

	@Override
	public boolean switchStates(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		boolean inverted = state.getValue(INVERTED);
		int delay = state.getValue(DELAY);
		delay++;
		
		if (delay > 4) {
			delay = 1;
			inverted = !inverted;
		}
		
		worldIn.setBlockAndUpdate(pos, state.setValue(INVERTED, inverted).setValue(DELAY, delay));
		
		return true;
		
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		
		return side == state.getValue(FACING) || side == state.getValue(FACING).getOpposite();
		
	}
	
	@Override
	public void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		
		BlockPos inputPos = pos.relative(state.getValue(FACING).getOpposite());
		
		if (inputPos.equals(fromPos)) {
			
			boolean power = worldIn.getSignal(inputPos, state.getValue(FACING).getOpposite()) > 0;
			boolean powered = state.getValue(POWERED);
			boolean inverted = state.getValue(INVERTED);
			
			if (powered != power) {
				
				if (inverted != powered) {
					
					worldIn.getBlockTicks().scheduleTick(pos, this, 2);
					
				} else {
					
					worldIn.getBlockTicks().scheduleTick(pos, this, state.getValue(DELAY) * 2 + 2);
					
				}
				
			}
			
		}
		
	}
	
	@Override
	public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (side == blockState.getValue(FACING).getOpposite()) {
			return blockState.getValue(POWERED) ? 15 : 0;
		}
		return 0;
	}
	
	@Override
	public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (side == blockState.getValue(FACING).getOpposite()) {
			return blockState.getValue(POWERED) ? 15 : 0;
		}
		return 0;
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		
		BlockPos inputPos = pos.relative(state.getValue(FACING).getOpposite());
		boolean powered = worldIn.getSignal(inputPos, state.getValue(FACING).getOpposite()) > 0;
		
		worldIn.setBlockAndUpdate(pos, state.setValue(POWERED, powered));
		worldIn.updateNeighborsAt(pos.relative(state.getValue(FACING)), this);
		
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (stateIn.getValue(POWERED) || (rand.nextBoolean() && stateIn.getValue(INVERTED))) {
		Direction direction = stateIn.getValue(FACING).getOpposite();
			double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
			double d1 = (double)pos.getY() + 0.4D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        float f = -5.0F;
	        f = f / 16.0F;
	        double d3 = (double)(f * (float)direction.getStepX());
	        double d4 = (double)(f * (float)direction.getStepZ());
	        worldIn.addParticle(RedstoneParticleData.REDSTONE, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
		}	
	}
	
}
