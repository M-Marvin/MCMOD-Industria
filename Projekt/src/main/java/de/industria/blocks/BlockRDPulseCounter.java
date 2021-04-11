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
		this.setDefaultState(this.stateContainer.getBaseState().with(ACTIVE, false).with(POWERED, false).with(FACING, Direction.NORTH).with(COUNT, 1));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(COUNT, POWERED, ACTIVE, PULSES);
		super.fillStateContainer(builder);
	}
	
	@Override
	public boolean switchStates(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		int count = state.get(COUNT);
		count++;
		if (count > 16) count = 1;
		worldIn.setBlockState(pos, state.with(COUNT, count));
		
		return true;
		
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		
		return side == state.get(FACING) || side == state.get(FACING).getOpposite();
		
	}
	
	@Override
	public void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		
		BlockPos inputPos = pos.offset(state.get(FACING).getOpposite());
		
		if (inputPos.equals(fromPos)) {
			
			boolean power = worldIn.getRedstonePower(inputPos, state.get(FACING).getOpposite()) > 0;
			boolean powered = state.get(POWERED);
			
			if (powered != power) {
				
				worldIn.setBlockState(pos, state.with(POWERED, power));
				worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
				
			}
			
		}
		
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		
		if (state.get(POWERED)) {
			
			if (state.get(ACTIVE)) {
				
				int pulses = state.get(PULSES);
				pulses++;

				worldIn.setBlockState(pos, state.with(PULSES, pulses).with(ACTIVE, false));
				worldIn.notifyNeighborsOfStateChange(pos.offset(state.get(FACING)), this);
				
				if (pulses < state.get(COUNT)) {
					
					BlockPos inputPos = pos.offset(state.get(FACING).getOpposite());
					int power = worldIn.getRedstonePower(inputPos, state.get(FACING).getOpposite());
					
					worldIn.getPendingBlockTicks().scheduleTick(pos, this, (15 - power) * 2);
					
				}
				
			} else {
				
				worldIn.setBlockState(pos, state.with(ACTIVE, true));
				worldIn.notifyNeighborsOfStateChange(pos.offset(state.get(FACING)), this);
				
				BlockPos inputPos = pos.offset(state.get(FACING).getOpposite());
				int power = worldIn.getRedstonePower(inputPos, state.get(FACING).getOpposite());
				
				worldIn.getPendingBlockTicks().scheduleTick(pos, this, (15 - power) * 2);
				
			}
			
		} else {
			
			worldIn.setBlockState(pos, state.with(PULSES, 0).with(ACTIVE, false));
			
		}
		
	}
	
	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (side == blockState.get(FACING).getOpposite()) {
			return blockState.get(ACTIVE) ? 15 : 0;
		}
		return 0;
	}
	
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (side == blockState.get(FACING).getOpposite()) {
			return blockState.get(ACTIVE) ? 15 : 0;
		}
		return 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		
		if (stateIn.get(POWERED)) {
			
			Direction direction = stateIn.get(FACING).getOpposite();
			double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
			double d1 = (double)pos.getY() + 0.4D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        float f = -5.0F;
	        f = f / 16.0F;
	        double d3 = (double)(f * (float)direction.getXOffset());
	        double d4 = (double)(f * (float)direction.getZOffset());
	        worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
	        
	        BlockPos inputPos = pos.offset(stateIn.get(FACING).getOpposite());
			int power = worldIn.getRedstonePower(inputPos, stateIn.get(FACING).getOpposite());
			
	        if (power == 15 && stateIn.get(PULSES) < stateIn.get(COUNT)) {
	        	
				d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
				d1 = (double)pos.getY() + 0.4D + (rand.nextDouble() - 0.5D) * 0.2D + 0.2D;
		        d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
		        worldIn.addParticle(ParticleTypes.FLAME, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
		        
	        }
	        
		}
		
	}
	
}
