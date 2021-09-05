package de.industria.multipartbuilds;

import de.industria.blocks.BlockMultipartBuilded;
import de.industria.typeregistys.ModItems;
import de.industria.util.types.MultipartBuild;
import net.minecraft.block.Blocks;

public class MultipartBuildCoalHeater extends MultipartBuild {
	
	public MultipartBuildCoalHeater() {
		super((BlockMultipartBuilded<?>) ModItems.coal_heater);
		this.addPatternLayer("FF", "KK");
		this.addKey("K", ModItems.copper_machine_casing);
		this.addKey("F", Blocks.BLAST_FURNACE);
	}
	
}
