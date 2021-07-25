package de.industria.typeregistys;

import de.industria.Industria;
import de.industria.worldgen.JigsawFeature;
import de.industria.worldgen.JigsawFeatureConfig;
import de.industria.worldgen.LakeFeature;
import de.industria.worldgen.LakeFeatureConfig;
import de.industria.worldgen.StoneOreFeature;
import de.industria.worldgen.StoneOreFeatureConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModFeature {
	
	public static final Feature<LakeFeatureConfig> LAKE_FEATUR = register("lake_feature", new LakeFeature(LakeFeatureConfig.CODEC));
	public static final Feature<StoneOreFeatureConfig> STONE_ORE = register("stone_ore", new StoneOreFeature(StoneOreFeatureConfig.CODEC));
	public static final Feature<JigsawFeatureConfig> JIGSAW_FEATURE = register("jigsaw_feature", new JigsawFeature(JigsawFeatureConfig.CODEC));
	
	private static <C extends IFeatureConfig, F extends Feature<C>> F register(String key, F value) {
		value.setRegistryName(new ResourceLocation(Industria.MODID, key));
		ForgeRegistries.FEATURES.register(value);
		return value;
	}
	
}
