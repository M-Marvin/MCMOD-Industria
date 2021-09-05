package de.industria.multipartbuilds;

import de.industria.blocks.BlockMultipartBuilded;
import de.industria.typeregistys.ModItems;
import de.industria.util.types.MultipartBuild;
import net.minecraft.block.Blocks;

public class MultipartBuildBlastFurnace extends MultipartBuild {
	
	public MultipartBuildBlastFurnace() {
		super((BlockMultipartBuilded<?>) ModItems.blast_furnace);
		this.addPatternLayer("IKK", "KKK", "KKK");
		this.addPatternLayer("PFM", "F F", "MFM");
		this.addPatternLayer("PFM", "F F", "MFM");
		this.addPatternLayer("PPD", "DAD", "DDD");
		this.addPatternLayer("   ", " K ", "   ");
		this.addKey("K", ModItems.copper_machine_casing);
		this.addKey("P", ModItems.copper_pipe, ModItems.steel_pipe);
		this.addKey("I", ModItems.fluid_input);
		this.addKey("A", ModItems.fluid_output);
		this.addKey("D", Blocks.SMOOTH_STONE_SLAB);
		this.addKey("F", Blocks.BLAST_FURNACE);
		this.addKey("M", Blocks.BRICK_WALL);
	}
	
}
