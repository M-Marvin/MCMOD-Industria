package de.industria.multipartbuilds;

import de.industria.blocks.BlockMultipartBuilded;
import de.industria.typeregistys.ModItems;
import de.industria.util.types.MultipartBuild;

public class MultipartBuildElectricHeater extends MultipartBuild {
	
	public MultipartBuildElectricHeater() {
		super((BlockMultipartBuilded<?>) ModItems.electric_heater);
		this.addPatternLayer("CK", "KS");
		this.addKey("K", ModItems.copper_machine_casing);
		this.addKey("C", ModItems.transformator_contact);
		this.addKey("S", ModItems.transformator_coil);
	}
	
}
