package de.industria.typeregistys;

import de.industria.Industria;
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
	public static final ITag<Fluid> RAW_OIL = tagFluid("raw_oil");
	public static final ITag<Fluid> CHEMICAL_WATER = tagFluid("chemical_water");
	public static final ITag<Fluid> SULFURIC_ACID = tagFluid("sulfuric_acid");
	public static final ITag<Fluid> NATRON_LYE = tagFluid("natron_lye");
	public static final ITag<Fluid> TAR = tagFluid("tar");
	
	public static final ITag<Block> CONVEYOR_BELT = tagBlock("conveyor_belt");
	public static final ITag<Block> MACHINERY = tagBlock("machinery");
	public static final ITag<Block> WIRING = tagBlock("wiring");
	public static final ITag<Block> PREASSURE_PIPE = tagBlock("preassure_pipe");
	public static final ITag<Block> DIRT = tagBlockForge("dirt");
	public static final ITag<Block> RUBBER_LOGS = tagBlock("rubber_logs");
	public static final ITag<Block> BEECH_LOGS = tagBlock("beech_logs");
	public static final ITag<Block> MARPLE_LOGS = tagBlock("marple_logs");
	public static final ITag<Block> MANGROVE_LOGS = tagBlock("mangrove_logs");
	public static final ITag<Block> BURNED_WOOD = tagBlock("burned_wood");
	public static final ITag<Block> SCAFFOLDING = tagBlock("scaffolding");
	public static final ITag<Block> ACID_RESISTANT = tagBlock("acid_resistant");
	public static final ITag<Block> ANTENNA_BLOCK = tagBlock("antenna_block");
	public static final ITag<Block> UNDERGROUND_BLOCKS = tagBlock("underground_blocks");
	
	private static IOptionalNamedTag<Fluid> tagFluid(String name) {
		return FluidTags.createOptional(new ResourceLocation(Industria.MODID, name));
	}
	
	private static IOptionalNamedTag<Block> tagBlock(String name) {
		return BlockTags.createOptional(new ResourceLocation(Industria.MODID, name));
	}

	private static IOptionalNamedTag<Block> tagBlockForge(String name) {
		return BlockTags.createOptional(new ResourceLocation("forge", name));
	}
	
}
