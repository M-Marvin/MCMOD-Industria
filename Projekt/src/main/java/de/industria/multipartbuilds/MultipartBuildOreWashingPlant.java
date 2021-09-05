package de.industria.multipartbuilds;

import de.industria.blocks.BlockMultipartBuilded;
import de.industria.typeregistys.ModItems;
import de.industria.util.types.MultipartBuild;

public class MultipartBuildOreWashingPlant extends MultipartBuild {
	
	public MultipartBuildOreWashingPlant() {
		super((BlockMultipartBuilded<?>) ModItems.ore_washing_plant);
		this.addPatternLayer("FFFF", "FFFF", "FFFF");
		this.addPatternLayer("CSPP", "MSPP", "ISCC");
		this.addPatternLayer(" HPP", " HPP", " M  ");
		this.addKey("F", ModItems.steel_machine_casing);
		this.addKey("C", ModItems.conveyor_belt);
		this.addKey("M", ModItems.motor);
		this.addKey("P", ModItems.steel_pipe);
		this.addKey("I", ModItems.fluid_input);
		this.addKey("H", ModItems.steel_planks_slab);
		this.addKey("S", ModItems.steel_plates);
	}
	
}
