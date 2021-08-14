package de.industria.fluids;

import de.industria.Industria;
import de.industria.fluids.util.GasFluid;
import de.industria.typeregistys.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.fluids.FluidAttributes;

public class FluidBiogas extends GasFluid implements IBucketPickupHandler {
	
	@Override
	public Item getBucket() {
		return ModItems.biogas_bucket;
	}
	
	@Override
	protected BlockState createLegacyBlock(FluidState state) {
		return ModItems.biogas.defaultBlockState();
	}
	
	@Override
	public Fluid takeLiquid(IWorld worldIn, BlockPos pos, BlockState state) {
		worldIn.removeBlock(pos, false);
		return this;
	}
	
	@Override
	protected FluidAttributes createAttributes() {
		return FluidAttributes.builder(
			new ResourceLocation(Industria.MODID, "block/biogas_still"), 
			new ResourceLocation(Industria.MODID, "block/biogas_flow"))
				.gaseous()
				.build(this);
	}
	
}