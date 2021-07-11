package de.industria.fluids;

import de.industria.Industria;
import de.industria.ModItems;
import de.industria.fluids.util.GasFluid;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.fluids.FluidAttributes;

public class FluidFuelGas extends GasFluid implements IBucketPickupHandler {
	
	@Override
	public Item getFilledBucket() {
		return ModItems.fuel_gas_bucket;
	}
	
	@Override
	protected BlockState getBlockState(FluidState state) {
		return ModItems.fuel_gas.getDefaultState();
	}
	
	@Override
	public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
		worldIn.removeBlock(pos, false);
		return this;
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
			new ResourceLocation(Industria.MODID, "block/fuel_gas_still"), 
			new ResourceLocation(Industria.MODID, "block/fuel_gas_flow"))
				.gaseous()
				.build(this);
	}
	
}