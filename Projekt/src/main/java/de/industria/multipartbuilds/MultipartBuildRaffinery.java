package de.industria.multipartbuilds;

import de.industria.blocks.BlockMultipartBuilded;
import de.industria.typeregistys.ModItems;
import de.industria.util.types.MultipartBuild;
import net.minecraft.block.Blocks;

public class MultipartBuildRaffinery extends MultipartBuild {
	
	public MultipartBuildRaffinery() {
		super((BlockMultipartBuilded<?>) ModItems.raffinery);
		this.addPatternLayer("FFF", "FFF");
		this.addPatternLayer("MCC", "ICC");
		this.addPatternLayer("KCC", "PCC");
		this.addPatternLayer("KCC", "PCC");
		this.addKey("F", ModItems.steel_machine_casing);
		this.addKey("K", ModItems.copper_machine_casing);
		this.addKey("M", ModItems.motor);
		this.addKey("C", Blocks.CAULDRON);
		this.addKey("P", ModItems.steel_pipe);
		this.addKey("I", ModItems.fluid_input);
	}
	
}
