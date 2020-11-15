package de.redtec.registys;

import de.redtec.RedTec;
import de.redtec.worldgen.StoneOreFeature;
import de.redtec.worldgen.StoneOreFeatureConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModFeature {
	
	public static final Feature<StoneOreFeatureConfig> STONE_ORE = register(new ResourceLocation(RedTec.MODID, "stone_ore"), new StoneOreFeature(StoneOreFeatureConfig.CODEC));
	
	private static <C extends IFeatureConfig, F extends Feature<C>> F register(ResourceLocation key, F value) {
		value.setRegistryName(key);
		ForgeRegistries.FEATURES.register(value);
		return value;
	}
	
}
