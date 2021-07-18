package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.items.ItemStructureCladdingPane;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class ItemStructureCladdingRenderer extends ItemStackTileEntityRenderer {
	
	@SuppressWarnings("deprecation")
	@Override
	public void renderByItem(ItemStack stack, TransformType transformer, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		
		matrixStack.pushPose();
				
		matrixStack.translate(0.5F, 0, 0);
		matrixStack.scale(0.0625F, 1, 1);
		
		BlockState claddingState = ItemStructureCladdingPane.getBlockState(stack);
		BlockRendererDispatcher renderDispatcher = Minecraft.getInstance().getBlockRenderer();
		renderDispatcher.renderSingleBlock(claddingState, matrixStack, buffer, combinedLight, combinedOverlay);
		
		matrixStack.popPose();
		
	}
	
}
