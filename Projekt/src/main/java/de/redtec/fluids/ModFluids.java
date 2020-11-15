
package de.redtec.fluids;

import de.redtec.RedTec;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModFluids {
	
	public static final Fluid STEAM = register("steam", new FluidSteam());
	
	private static <T extends Fluid> T register(String key, T p_215710_1_) {
		p_215710_1_.setRegistryName(new ResourceLocation(RedTec.MODID, key));
		ForgeRegistries.FLUIDS.register(p_215710_1_);
		return p_215710_1_;
		//return Registry.register(Registry.FLUID, new ResourceLocation(RedTec.MODID, key), p_215710_1_);
	}
	
}
