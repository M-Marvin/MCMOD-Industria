package de.redtec.fluids.util;

import de.redtec.RedTec;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.util.ResourceLocation;

public class BlockModFlowingFluid extends FlowingFluidBlock {

	@SuppressWarnings("deprecation")
	public BlockModFlowingFluid(String name, FlowingFluid fluidIn, Properties builder) {
		super(fluidIn, builder);
		this.setRegistryName(new ResourceLocation(RedTec.MODID, name));
	}

}
