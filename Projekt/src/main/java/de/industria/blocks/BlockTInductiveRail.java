package de.industria.blocks;

import de.industria.util.handler.MinecartHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class BlockTInductiveRail extends BlockRailStraightBase {
	
	public BlockTInductiveRail() {
		super("inductive_rail");
	}
	
	@Override
	public void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
		if (state.getValue(POWERED)) {
			float mul = 1.1F;
			Vector3d motion = cart.getDeltaMovement();
			motion = motion.multiply(mul, mul, mul);
			cart.setDeltaMovement(motion);
			MinecartHandler.getHandlerForWorld(world).setBoosted(cart);
		} else {
			float mul = 0.9F;
			Vector3d motion = cart.getDeltaMovement();
			motion = motion.multiply(mul, 1, mul);
			cart.setDeltaMovement(motion);
			MinecartHandler.getHandlerForWorld(world).stopBoosted(cart);
		}
		
	}
	
	@Override
	public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
		return 1F;
	}
	
	protected boolean isSamePoweredRail(World world, BlockPos state, boolean searchForward, int recursionCount, RailShape shape) {
		BlockState blockstate = world.getBlockState(state);
		if (!(blockstate.getBlock() instanceof BlockTInductiveRail)) {
			return false;
		} else {
			RailShape railshape = getRailDirection(blockstate, world, state, null);
			if (shape != RailShape.EAST_WEST || railshape != RailShape.NORTH_SOUTH && railshape != RailShape.ASCENDING_NORTH && railshape != RailShape.ASCENDING_SOUTH) {
				if (shape != RailShape.NORTH_SOUTH || railshape != RailShape.EAST_WEST && railshape != RailShape.ASCENDING_EAST && railshape != RailShape.ASCENDING_WEST) {
					
					return isRailPowered(world, state, blockstate) ? true : this.findPoweredRailSignal(world, state, blockstate, searchForward, recursionCount + 1);
					
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	@Override
	protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn) {
		boolean flag = state.getValue(POWERED);
		boolean flag1 = isRailPowered(worldIn, pos, state) || this.findPoweredRailSignal(worldIn, pos, state, true, 0) || this.findPoweredRailSignal(worldIn, pos, state, false, 0);
		if (flag1 != flag) {
				worldIn.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(flag1)), 3);
				worldIn.updateNeighborsAt(pos.below(), this);
			if (state.getValue(SHAPE).isAscending()) {
				worldIn.updateNeighborsAt(pos.above(), this);
			}
		}
	}
	
	protected boolean isRailPowered(World world, BlockPos pos, BlockState state) {
		
		for (Direction d : Direction.values()) {
			BlockPos checkPos = pos.relative(d);
			BlockState checkState = world.getBlockState(checkPos);
			
			if (checkState.getBlock() instanceof BlockTRailAdapter) {
				
				return checkState.getValue(BlockTRailAdapter.POWERED);
				
			}
			
		}
		
		return false;
		
	}
	
}
