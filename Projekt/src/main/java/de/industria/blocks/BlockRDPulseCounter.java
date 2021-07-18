package de.industria.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
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

public class BlockRDPulseCounter extends BlockRedstoneDiode {
	
	public static final IntegerProperty COUNT = IntegerProperty.create("count", 1, 16);
	public static final IntegerProperty PULSES = IntegerProperty.create("pulses", 0, 16);
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	
	public BlockRDPulseCounter() {
		super("pulse_counter");
		this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false).setValue(POWERED, false).setValue(FACING, Direction.NORTH).setValue(COUNT, 1));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(COUNT, POWERED, ACTIVE, PULSES);
		super.createBlockStateDefinition(builder);
	}
	
	@Override
	public boolean switchStates(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		int count = state.getValue(COUNT);
		count++;
		if (count > 16) count = 1;
		worldIn.setBlockAndUpdate(pos, state.setValue(COUNT, count));
		
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
			
			if (powered != power) {
				
				worldIn.setBlockAndUpdate(pos, state.setValue(POWERED, power));
				worldIn.getBlockTicks().scheduleTick(pos, this, 1);
				
			}
			
		}
		
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		
		if (state.getValue(POWERED)) {
			
			if (state.getValue(ACTIVE)) {
				
				int pulses = state.getValue(PULSES);
				pulses++;

				worldIn.setBlockAndUpdate(pos, state.setValue(PULSES, pulses).setValue(ACTIVE, false));
				worldIn.updateNeighborsAt(pos.relative(state.getValue(FACING)), this);
				
				if (pulses < state.getValue(COUNT)) {
					
					BlockPos inputPos = pos.relative(state.getValue(FACING).getOpposite());
					int power = worldIn.getSignal(inputPos, state.getValue(FACING).getOpposite());
					
					worldIn.getBlockTicks().scheduleTick(pos, this, (15 - power) * 2);
					
				}
				
			} else {
				
				worldIn.setBlockAndUpdate(pos, state.setValue(ACTIVE, true));
				worldIn.updateNeighborsAt(pos.relative(state.getValue(FACING)), this);
				
				BlockPos inputPos = pos.relative(state.getValue(FACING).getOpposite());
				int power = worldIn.getSignal(inputPos, state.getValue(FACING).getOpposite());
				
				worldIn.getBlockTicks().scheduleTick(pos, this, (15 - power) * 2);
				
			}
			
		} else {
			
			worldIn.setBlockAndUpdate(pos, state.setValue(PULSES, 0).setValue(ACTIVE, false));
			
		}
		
	}
	
	@Override
	public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (side == blockState.getValue(FACING).getOpposite()) {
			return blockState.getValue(ACTIVE) ? 15 : 0;
		}
		return 0;
	}
	
	@Override
	public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (side == blockState.getValue(FACING).getOpposite()) {
			return blockState.getValue(ACTIVE) ? 15 : 0;
		}
		return 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		
		if (stateIn.getValue(POWERED)) {
			
			Direction direction = stateIn.getValue(FACING).getOpposite();
			double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
			double d1 = (double)pos.getY() + 0.4D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        float f = -5.0F;
	        f = f / 16.0F;
	        double d3 = (double)(f * (float)direction.getStepX());
	        double d4 = (double)(f * (float)direction.getStepZ());
	        worldIn.addParticle(RedstoneParticleData.REDSTONE, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
	        
	        BlockPos inputPos = pos.relative(stateIn.getValue(FACING).getOpposite());
			int power = worldIn.getSignal(inputPos, stateIn.getValue(FACING).getOpposite());
			
	        if (power == 15 && stateIn.getValue(PULSES) < stateIn.getValue(COUNT)) {
	        	
				d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
				d1 = (double)pos.getY() + 0.4D + (rand.nextDouble() - 0.5D) * 0.2D + 0.2D;
		        d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
		        worldIn.addParticle(ParticleTypes.FLAME, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
		        
	        }
	        
		}
		
	}
	
}
