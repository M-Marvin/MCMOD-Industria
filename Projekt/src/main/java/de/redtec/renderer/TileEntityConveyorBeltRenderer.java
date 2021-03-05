package de.redtec.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.redtec.blocks.BlockConveyorBelt;
import de.redtec.tileentity.TileEntityConveyorBelt;
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
			
			ItemStack itemIn = tileEntityIn.getStackInSlot(0);
			ItemStack itemOut = tileEntityIn.getStackInSlot(1);
			ItemStack itemOutSec = tileEntityIn.getStackInSlot(2);
			BlockState state = tileEntityIn.getBlockState();
			Direction facing = state.get(BlockConveyorBelt.FACING);
			
			matrixStackIn.push();
			
			matrixStackIn.translate(0.5F, 0.35F, 0.5F);
			
			matrixStackIn.push();
				
				matrixStackIn.rotate(facing.getRotation());
				matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
				
				float itemOffsetIn = (tileEntityIn.beltMoveStateIn) / 16F;
				float itemOffsetOut = (tileEntityIn.beltMoveStateOut) / 16F;
				
				int insertSide = tileEntityIn.beltInsertSide;
				
				if (!itemIn.isEmpty()) {
					matrixStackIn.push();
					matrixStackIn.rotate(Vector3f.YP.rotationDegrees(insertSide * 90));
					matrixStackIn.translate(0, 0.2F, 0.3F - itemOffsetIn);
					matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
					matrixStackIn.scale(1.4F, 1.4F, 1.4F);
					this.itemRenderer.renderItem(itemIn, TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
					matrixStackIn.pop();
				}
				
				if (!itemOut.isEmpty()) {
					matrixStackIn.push();
					matrixStackIn.translate(0, 0.201, -0.2F - itemOffsetOut);
					matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
					matrixStackIn.scale(1.4F, 1.4F, 1.4F);
					this.itemRenderer.renderItem(itemOut, TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
					matrixStackIn.pop();
				}
				
			matrixStackIn.pop();
			
			matrixStackIn.push();
				
				facing = tileEntityIn.getSecondary(state);
				matrixStackIn.rotate(facing.getRotation());	
				matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
				
				float itemOffsetOutSec = (tileEntityIn.beltMoveStateOutSecondary) / 16F;
				
				if (!itemOutSec.isEmpty()) {
					matrixStackIn.push();
					matrixStackIn.translate(0, 0.201, -0.2F - itemOffsetOutSec);
					matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
					matrixStackIn.scale(1.4F, 1.4F, 1.4F);
					this.itemRenderer.renderItem(itemOutSec, TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
					matrixStackIn.pop();
				}
				
			matrixStackIn.pop();
			
			matrixStackIn.pop();
			
		}
		
	}

}
