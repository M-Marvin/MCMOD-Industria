package de.redtec.worldgen;

import de.redtec.RedTec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModFeature {
	
	public static final Feature<StoneOreFeatureConfig> STONE_ORE = register(new ResourceLocation(RedTec.MODID, "stone_ore"), new StoneOreFeature(StoneOreFeatureConfig.CODEC));
	
	@SuppressWarnings("deprecation")
	private static <C extends IFeatureConfig, F extends Feature<C>> F register(ResourceLocation key, F value) {
		return Registry.register(Registry.FEATURE, key, value);
	}
	
}
