package de.industria.blocks;

import de.industria.Industria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;

public class BlockRailBase extends RailBlock {
	
	public BlockRailBase(String name) {
		super(AbstractBlock.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE));
		this.setRegistryName(new ResourceLocation(Industria.MODID, name));
	}
	
}
