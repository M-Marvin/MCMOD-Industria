package de.redtec.blocks;

import de.redtec.util.MinecartBoostHandler;
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
		if (state.get(POWERED)) {
			float mul = 1.1F;
			Vector3d motion = cart.getMotion();
			motion = motion.mul(mul, mul, mul);
			cart.setMotion(motion);
			MinecartBoostHandler.getHandlerForWorld(world).setBoosted(cart);
		} else {
			float mul = 0.9F;
			Vector3d motion = cart.getMotion();
			motion = motion.mul(mul, 1, mul);
			cart.setMotion(motion);
			MinecartBoostHandler.getHandlerForWorld(world).stopBoosted(cart);
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
		boolean flag = state.get(POWERED);
		boolean flag1 = isRailPowered(worldIn, pos, state) || this.findPoweredRailSignal(worldIn, pos, state, true, 0) || this.findPoweredRailSignal(worldIn, pos, state, false, 0);
		if (flag1 != flag) {
				worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(flag1)), 3);
				worldIn.notifyNeighborsOfStateChange(pos.down(), this);
			if (state.get(SHAPE).isAscending()) {
				worldIn.notifyNeighborsOfStateChange(pos.up(), this);
			}
		}
	}
	
	protected boolean isRailPowered(World world, BlockPos pos, BlockState state) {
		
		for (Direction d : Direction.values()) {
			BlockPos checkPos = pos.offset(d);
			BlockState checkState = world.getBlockState(checkPos);
			
			if (checkState.getBlock() instanceof BlockTRailAdapter) {
				
				return checkState.get(BlockTRailAdapter.POWERED);
				
			}
			
		}
		
		return false;
		
	}
	
}
