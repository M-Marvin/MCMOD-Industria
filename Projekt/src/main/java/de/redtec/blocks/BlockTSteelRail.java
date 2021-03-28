package de.redtec.blocks;

import de.redtec.util.MinecartBoostHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class BlockTSteelRail extends BlockRailBase {
	
	public BlockTSteelRail() {
		super("steel_rail");
	}

	@Override
	public void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
		
		if (MinecartBoostHandler.getHandlerForWorld(world).isBoosted(cart)) {
			float mul = 1.01F;
			Vector3d motion = cart.getMotion();
			motion = motion.mul(mul, 1, mul);
			cart.setMotion(motion);

			if (state.get(SHAPE).isAscending()) {
				cart.setPosition(cart.getPosX(), cart.getPosY() + 1, cart.getPosZ());
			}
			
		}
		
	}
	
	@Override
	public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
		return MinecartBoostHandler.getHandlerForWorld(world).isBoosted(cart) ? 1F : 0.4F;
	}
	
}
