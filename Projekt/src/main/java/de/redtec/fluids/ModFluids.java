
package de.redtec.fluids;

import de.redtec.RedTec;
import de.redtec.fluids.util.GasFluid;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModFluids {
	
	public static final FlowingFluid FLOWING_HOT_WATER = register("flowing_hot_water", new FluidHotWater.Flow());
	public static final FlowingFluid HOT_WATER = register("hot_water", new FluidHotWater.Still());
	public static final GasFluid STEAM = register("steam", new FluidSteam());
	public static final GasFluid PREASURIZED_STEAM = register("preasurized_steam", new FluidPreasurizedSteam());
	
	private static <T extends Fluid> T register(String key, T p_215710_1_) {
		p_215710_1_.setRegistryName(new ResourceLocation(RedTec.MODID, key));
		ForgeRegistries.FLUIDS.register(p_215710_1_);
		return p_215710_1_;
	}
	
}
