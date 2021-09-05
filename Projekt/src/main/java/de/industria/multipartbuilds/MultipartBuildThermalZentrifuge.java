package de.industria.multipartbuilds;

import de.industria.blocks.BlockMultipartBuilded;
import de.industria.typeregistys.ModItems;
import de.industria.util.types.MultipartBuild;
import net.minecraft.block.Blocks;

public class MultipartBuildThermalZentrifuge extends MultipartBuild {
	
	public MultipartBuildThermalZentrifuge() {
		super((BlockMultipartBuilded<?>) ModItems.thermal_zentrifuge);
		this.addPatternLayer("MM", "FF");
		this.addPatternLayer("CC", "CC");
		this.addPatternLayer("SS", "SS");
		this.addKey("F", ModItems.steel_machine_casing);
		this.addKey("C", Blocks.CAULDRON);
		this.addKey("M", ModItems.motor);
		this.addKey("S", ModItems.copper_planks_slab);
	}
	
}
