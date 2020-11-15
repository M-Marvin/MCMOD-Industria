package de.redtec.fluids;

import de.redtec.RedTec;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;

public class FluidSteam extends MaterialFluid implements IBucketPickupHandler {
	
	@Override
	public Item getFilledBucket() {
		return RedTec.steam_bucket;
	}
	
	@Override
	protected BlockState getBlockState(FluidState state) {
		return RedTec.steam.getDefaultState();
	}
	
	@Override
	public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
		worldIn.removeBlock(pos, false);
		return this;
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
			new ResourceLocation(RedTec.MODID, "block/steam_still"), 
			new ResourceLocation(RedTec.MODID, "block/steam_flow"))
				.gaseous()
				.build(this);
	}
	
	@Override
	public void tick(World worldIn, BlockPos pos, FluidState state) {
		System.out.println("TEST");
	}
	
}