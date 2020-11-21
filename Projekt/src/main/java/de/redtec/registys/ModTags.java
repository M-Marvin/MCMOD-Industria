package de.redtec.registys;

import de.redtec.RedTec;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModTags {
	
	public static final ITag<Fluid> HOT_WATER = tagFluid("hot_water");
	public static final ITag<Fluid> STEAM = tagFluid("steam");
	
	private static IOptionalNamedTag<Fluid> tagFluid(String name) {
		return FluidTags.createOptional(new ResourceLocation(RedTec.MODID, name));
	}
	
	@SuppressWarnings("unused")
	private static IOptionalNamedTag<Block> tagBlock(String name) {
		return BlockTags.createOptional(new ResourceLocation(RedTec.MODID, name));
	}
	
}
