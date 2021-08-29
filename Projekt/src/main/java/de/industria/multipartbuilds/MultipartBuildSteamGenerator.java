package de.industria.multipartbuilds;

import de.industria.blocks.BlockMultipartBuilded;
import de.industria.typeregistys.ModItems;
import net.minecraft.block.Blocks;

public class MultipartBuildSteamGenerator extends MultipartBuild {
	
	public MultipartBuildSteamGenerator() {
		super((BlockMultipartBuilded<?>) ModItems.steam_generator);
		this.addPatternLayer("RRR", "RRR");
		this.addPatternLayer("RER", "RMR");
		this.addPatternLayer("RRR", "RRR");
		this.addKey("R", ModItems.crude_steel_block);
		this.addKey("E", Blocks.IRON_BARS);
		this.addKey("M", ModItems.motor);
	}
	
}
