
package de.redtec.fluids;

import de.redtec.RedTec;
import de.redtec.fluids.util.BlockGasFluid;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModFluids {
	
	public static final FluidDestilledWater FLOWING_DESTILLED_WATER = register("flowing_destilled_water", new FluidDestilledWater.Flow());
	public static final FluidDestilledWater DESTILLED_WATER = register("destilled_water", new FluidDestilledWater.Still());
	public static final FluidSteam STEAM = register("steam", new FluidSteam());
	
	private static <T extends Fluid> T register(String key, T p_215710_1_) {
		p_215710_1_.setRegistryName(new ResourceLocation(RedTec.MODID, key));
		ForgeRegistries.FLUIDS.register(p_215710_1_);
		return p_215710_1_;
	}
	
	public static boolean isFluidBlock(Block block) {
		return block instanceof FlowingFluidBlock || block instanceof BlockGasFluid;
	}
	
}
