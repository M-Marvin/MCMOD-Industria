package de.industria.blocks;

import de.industria.util.handler.MinecartHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class BlockTSteelRail extends BlockRailBase {
	
	public BlockTSteelRail() {
		super("steel_rail");
	}

	@Override
	public void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
		
		if (MinecartHandler.getHandlerForWorld(world).isBoosted(cart)) {
			Vector3d motion = cart.getDeltaMovement();
			double speed = Math.max(Math.abs(motion.x), Math.abs(motion.z));
			RailShape shape = state.getValue(SHAPE);
			boolean flag = shape == RailShape.NORTH_EAST || shape == RailShape.NORTH_WEST || shape == RailShape.SOUTH_EAST || shape == RailShape.SOUTH_WEST;
			if (speed > 0.1F && !flag) {
				double boostMul = 0.9F / speed;
				motion = motion.multiply(boostMul, 1, boostMul);
				cart.setDeltaMovement(motion);
			}
		}
		
	}
	
	@Override
	public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
		return MinecartHandler.getHandlerForWorld(world).isBoosted(cart) ? 1F : 0.4F;
	}
	
}
