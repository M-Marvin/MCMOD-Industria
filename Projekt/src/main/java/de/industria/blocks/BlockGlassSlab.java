package de.industria.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockGlassSlab extends BlockSlabBase {

	public BlockGlassSlab(String name) {
		super(name, Properties.create(Material.GLASS).hardnessAndResistance(0.3F).sound(SoundType.GLASS).harvestTool(getDefaultToolType(Material.GLASS)).setRequiresTool().notSolid());
	}

}
