package de.redtec.fluids;

import de.redtec.fluids.util.BlockModFlowingFluid;
import de.redtec.registys.ModFluids;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.material.Material;

public class BlockRawOil extends BlockModFlowingFluid {
	
	public BlockRawOil() {
		super("raw_oil", ModFluids.RAW_OIL, AbstractBlock.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
	}
	
}