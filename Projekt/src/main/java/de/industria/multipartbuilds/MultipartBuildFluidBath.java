package de.industria.multipartbuilds;

import de.industria.blocks.BlockMultipartBuilded;
import de.industria.typeregistys.ModItems;
import net.minecraft.block.Blocks;

public class MultipartBuildFluidBath extends MultipartBuild {
	
	public MultipartBuildFluidBath() {
		super((BlockMultipartBuilded<?>) ModItems.fluid_bath);
		this.addPatternLayer("FF", "FF", "FF");
		this.addPatternLayer("CC", "PP", "IM");
		this.addKey("F", ModItems.steel_machine_casing);
		this.addKey("C", Blocks.CAULDRON);
		this.addKey("M", ModItems.motor);
		this.addKey("P", ModItems.steel_pipe);
		this.addKey("I", ModItems.fluid_input);
	}
	
}
