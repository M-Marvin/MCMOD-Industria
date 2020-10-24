package de.redtec.worldgen;

import de.redtec.RedTec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModPlacement {
	
	public static final Placement<SimpleOrePlacementConfig> SIMPLE_ORE = register(new ResourceLocation(RedTec.MODID, "simple_ore"), new SimpleOrePlacement(SimpleOrePlacementConfig.CODEC));
	
	@SuppressWarnings("deprecation")
	private static <T extends IPlacementConfig, G extends Placement<T>> G register(ResourceLocation key, G p_214999_1_) {
		return Registry.register(Registry.DECORATOR, key, p_214999_1_);
	}
	
}
