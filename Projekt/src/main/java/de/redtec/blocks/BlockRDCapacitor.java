package de.redtec.blocks;

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
		this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, false).with(INVERTED, false).with(FACING, Direction.NORTH).with(DELAY, 1));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(INVERTED, POWERED, DELAY);
		super.fillStateContainer(builder);
	}

	@Override
	public boolean switchStates(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		boolean inverted = state.get(INVERTED);
		int delay = state.get(DELAY);
		delay++;
		
		if (delay > 4) {
			delay = 1;
			inverted = !inverted;
		}
		
		worldIn.setBlockState(pos, state.with(INVERTED, inverted).with(DELAY, delay));
		
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
			boolean inverted = state.get(INVERTED);
			
			if (powered != power) {
				
				if (inverted != powered) {
					
					worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2);
					
				} else {
					
					worldIn.getPendingBlockTicks().scheduleTick(pos, this, state.get(DELAY) * 2 + 2);
					
				}
				
			}
			
		}
		
	}
	
	@Override
	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (side == blockState.get(FACING).getOpposite()) {
			return blockState.get(POWERED) ? 15 : 0;
		}
		return 0;
	}
	
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		if (side == blockState.get(FACING).getOpposite()) {
			return blockState.get(POWERED) ? 15 : 0;
		}
		return 0;
	}
	
	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		
		BlockPos inputPos = pos.offset(state.get(FACING).getOpposite());
		boolean powered = worldIn.getRedstonePower(inputPos, state.get(FACING).getOpposite()) > 0;
		
		worldIn.setBlockState(pos, state.with(POWERED, powered));
		worldIn.notifyNeighborsOfStateChange(pos.offset(state.get(FACING)), this);
		
	}
	
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (stateIn.get(POWERED) || (rand.nextBoolean() && stateIn.get(INVERTED))) {
		Direction direction = stateIn.get(FACING).getOpposite();
			double d0 = (double)pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
			double d1 = (double)pos.getY() + 0.4D + (rand.nextDouble() - 0.5D) * 0.2D;
	        double d2 = (double)pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
	        float f = -5.0F;
	        f = f / 16.0F;
	        double d3 = (double)(f * (float)direction.getXOffset());
	        double d4 = (double)(f * (float)direction.getZOffset());
	        worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
		}	
	}
	
}
