package de.industria.multipartbuilds;

import de.industria.blocks.BlockMultipartBuilded;
import de.industria.typeregistys.ModItems;

public class MultipartBuildSchredder extends MultipartBuild {
	
	public MultipartBuildSchredder() {
		super((BlockMultipartBuilded<?>) ModItems.schredder);
		this.addPatternLayer("FF", "FF");
		this.addPatternLayer("MS", "SM");
		this.addKey("F", ModItems.steel_machine_casing);
		this.addKey("S", ModItems.steel_plates);
		this.addKey("M", ModItems.motor);
	}
	
}
