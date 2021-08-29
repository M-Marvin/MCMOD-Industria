package de.industria.multipartbuilds;

import de.industria.blocks.BlockMultipartBuilded;
import de.industria.typeregistys.ModItems;

public class MultipartBuildGasHeater extends MultipartBuild {
	
	public MultipartBuildGasHeater() {
		super((BlockMultipartBuilded<?>) ModItems.gas_heater);
		this.addPatternLayer("AA", "KK");
		this.addKey("K", ModItems.copper_machine_casing);
		this.addKey("A", ModItems.fluid_output);
	}
	
}
