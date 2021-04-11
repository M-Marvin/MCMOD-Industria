package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.blocks.BlockRSignalProcessorContact;
import de.industria.tileentity.TileEntityRSignalProcessorContact;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntitySignalProcessorContactRenderer extends TileEntityRenderer<TileEntityRSignalProcessorContact> {
	
	private ItemRenderer itemRenderer;
	
	public TileEntitySignalProcessorContactRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.itemRenderer = Minecraft.getInstance().getItemRenderer();
	}

	@Override
	public void render(TileEntityRSignalProcessorContact tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		if (tileEntityIn.hasProcessor()) {
			
			ItemStack processorStack = tileEntityIn.getProcessorStack();
			BlockState state = tileEntityIn.getBlockState();
			
			
			matrixStackIn.push();
			
				Direction direction = state.get(BlockRSignalProcessorContact.FACING);
				matrixStackIn.translate(0.5F, 0.5F, 0.5F);
				matrixStackIn.rotate(direction.getRotation());
				matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
				matrixStackIn.scale(0.625F, 0.625F, 0.625F);
				matrixStackIn.translate(0, 0, 9 * 0.0625F);
				
				if (!processorStack.isEmpty()) {
					
					this.itemRenderer.renderItem(processorStack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
					
				}
				
			matrixStackIn.pop();
			
		}
		
	}

}
