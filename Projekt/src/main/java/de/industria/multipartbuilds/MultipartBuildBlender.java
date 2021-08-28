package de.industria.multipartbuilds;

import de.industria.blocks.BlockMultipartBuilded;
import de.industria.typeregistys.ModItems;
import net.minecraft.block.Blocks;

public class MultipartBuildBlender extends MultipartBuild {
	
	public MultipartBuildBlender() {
		super((BlockMultipartBuilded<?>) ModItems.blender);
		this.addPatternLayer("FFF", "FFF", "FFF");
		this.addPatternLayer("KML", "CCP", "CCP");
		this.addPatternLayer("S  ", "CCP", "CCP");
		this.addKey("F", ModItems.steel_machine_casing);
		this.addKey("K", ModItems.copper_plates);
		this.addKey("S", ModItems.copper_planks_slab);
		this.addKey("M", ModItems.motor);
		this.addKey("L", ModItems.alloy_furnace);
		this.addKey("C", Blocks.CAULDRON);
		this.addKey("P", ModItems.steel_pipe);
	}
	
}
