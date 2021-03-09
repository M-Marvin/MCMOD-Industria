package de.redtec.typeregistys;

import de.redtec.RedTec;
import de.redtec.worldgen.placements.HorizontalSpreadPlacement;
import de.redtec.worldgen.placements.HorizontalSpreadPlacementConfig;
import de.redtec.worldgen.placements.SimpleOrePlacement;
import de.redtec.worldgen.placements.SimpleOrePlacementConfig;
import de.redtec.worldgen.placements.VerticalOffsetPlacement;
import de.redtec.worldgen.placements.VerticalOffsetPlacementConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModPlacement {
	
	public static final Placement<SimpleOrePlacementConfig> SIMPLE_ORE = register(new ResourceLocation(RedTec.MODID, "simple_ore"), new SimpleOrePlacement(SimpleOrePlacementConfig.CODEC));
	public static final Placement<HorizontalSpreadPlacementConfig> HORIZONTAL_SPREAD = register(new ResourceLocation(RedTec.MODID, "horizontal_spread"), new HorizontalSpreadPlacement(HorizontalSpreadPlacementConfig.CODEC));
	public static final Placement<VerticalOffsetPlacementConfig> VERTICAL_OFFSET = register(new ResourceLocation(RedTec.MODID, "vertical_offset"), new VerticalOffsetPlacement(VerticalOffsetPlacementConfig.CODEC));
	
	private static <T extends IPlacementConfig, G extends Placement<T>> G register(ResourceLocation key, G p_214999_1_) {
		p_214999_1_.setRegistryName(key);
		ForgeRegistries.DECORATORS.register(p_214999_1_);
		return p_214999_1_;
	}
	
}
