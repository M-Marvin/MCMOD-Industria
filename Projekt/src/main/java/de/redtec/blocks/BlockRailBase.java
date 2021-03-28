package de.redtec.blocks;

import de.redtec.RedTec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.RailBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public class BlockRailBase extends RailBlock {
	
	public BlockRailBase(String name) {
		super(AbstractBlock.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL));
		this.setRegistryName(new ResourceLocation(RedTec.MODID, name));
	}
	
}
