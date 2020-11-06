package de.redtec.fluids;

import de.redtec.RedTec;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.util.ResourceLocation;

public class ModFlowingFluidBlock extends FlowingFluidBlock {

	@SuppressWarnings("deprecation")
	public ModFlowingFluidBlock(String name, FlowingFluid fluidIn, Properties builder) {
		super(fluidIn, builder);
		this.setRegistryName(new ResourceLocation(RedTec.MODID, name));
	}

}
