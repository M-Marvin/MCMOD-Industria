package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.items.ItemFluidCannister;
import de.industria.tileentity.TileEntityFluidCannister;
import de.industria.typeregistys.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.fluids.FluidStack;

public class BlockFluidCannisterItemRenderer extends ItemStackTileEntityRenderer {
	
	@SuppressWarnings("deprecation")
	@Override
	public void renderByItem(ItemStack cannisterStack, TransformType transform, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		BlockState defaultState = ModItems.fluid_cannister.getBlock().defaultBlockState();
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(defaultState, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		
		if (cannisterStack.getItem() == ModItems.fluid_cannister) {
			
			ItemFluidCannister cannisterItem = (ItemFluidCannister) cannisterStack.getItem();
			FluidStack fluidStack = cannisterItem.getContent(cannisterStack);
			
			if (!fluidStack.isEmpty()) {
				
				Fluid fluid = fluidStack.getFluid();
				float amount = fluidStack.getAmount() / (float) TileEntityFluidCannister.MAX_CONTENT;
				
				TileEntityFluidCannisterRenderer.renderFluid(matrixStackIn, bufferIn, fluid, amount, defaultState.getValue(BlockStateProperties.HORIZONTAL_FACING), combinedOverlayIn, combinedLightIn);
				
			}
			
		}
		
	}
	
}
