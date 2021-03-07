package de.redtec.blocks;

import de.redtec.RedTec;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

public class BlockLeavesBase extends LeavesBlock {
	
	public BlockLeavesBase(String name, Material material, float hardness, float resistance, SoundType sound) {
		super(Properties.create(material).hardnessAndResistance(hardness, resistance).sound(sound).tickRandomly().notSolid());
		this.setRegistryName(new ResourceLocation(RedTec.MODID, name));
	}
	
}
