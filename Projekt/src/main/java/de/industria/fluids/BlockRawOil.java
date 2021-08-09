package de.industria.fluids;

import de.industria.fluids.util.BlockModFlowingFluid;
import de.industria.typeregistys.ModFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.material.Material;

public class BlockRawOil extends BlockModFlowingFluid {
	
	public BlockRawOil() {
		super("raw_oil", ModFluids.RAW_OIL, AbstractBlock.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops());
	}
	
}