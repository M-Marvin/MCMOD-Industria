package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.blocks.BlockConveyorBelt;
import de.industria.tileentity.TileEntityConveyorBelt;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntityConveyorBeltRenderer extends TileEntityRenderer<TileEntityConveyorBelt> {
	
	protected ItemRenderer itemRenderer;
	
	public TileEntityConveyorBeltRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.itemRenderer = Minecraft.getInstance().getItemRenderer();
	}
	
	@Override
	public void render(TileEntityConveyorBelt tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		if (!tileEntityIn.isEmpty()) {
			
			ItemStack itemIn = tileEntityIn.getItem(0);
			ItemStack itemOut = tileEntityIn.getItem(1);
			ItemStack itemOutSec = tileEntityIn.getItem(2);
			BlockState state = tileEntityIn.getBlockState();
			Direction facing = state.getValue(BlockConveyorBelt.FACING);
			
			matrixStackIn.pushPose();
			
			matrixStackIn.translate(0.5F, 0.35F, 0.5F);
			
			matrixStackIn.pushPose();
				
				matrixStackIn.mulPose(facing.getRotation());
				matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
				
				float itemOffsetIn = (tileEntityIn.beltMoveStateIn) / 16F;
				float itemOffsetOut = (tileEntityIn.beltMoveStateOut) / 16F;
				
				int insertSide = tileEntityIn.beltInsertSide;
				
				if (!itemIn.isEmpty()) {
					matrixStackIn.pushPose();
					matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(insertSide * 90));
					matrixStackIn.translate(0, 0.2F, 0.3F - itemOffsetIn);
					matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
					matrixStackIn.scale(1.4F, 1.4F, 1.4F);
					this.itemRenderer.renderStatic(itemIn, TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
					matrixStackIn.popPose();
				}
				
				if (!itemOut.isEmpty()) {
					matrixStackIn.pushPose();
					matrixStackIn.translate(0, 0.201, -0.2F - itemOffsetOut);
					matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
					matrixStackIn.scale(1.4F, 1.4F, 1.4F);
					this.itemRenderer.renderStatic(itemOut, TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
					matrixStackIn.popPose();
				}
				
			matrixStackIn.popPose();
			
			matrixStackIn.pushPose();
				
				facing = tileEntityIn.getSecondary(state);
				matrixStackIn.mulPose(facing.getRotation());	
				matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
				
				float itemOffsetOutSec = (tileEntityIn.beltMoveStateOutSecondary) / 16F;
				
				if (!itemOutSec.isEmpty()) {
					matrixStackIn.pushPose();
					matrixStackIn.translate(0, 0.201, -0.2F - itemOffsetOutSec);
					matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
					matrixStackIn.scale(1.4F, 1.4F, 1.4F);
					this.itemRenderer.renderStatic(itemOutSec, TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
					matrixStackIn.popPose();
				}
				
			matrixStackIn.popPose();
			
			matrixStackIn.popPose();
			
		}
		
	}

}
