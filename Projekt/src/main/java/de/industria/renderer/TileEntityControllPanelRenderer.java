package de.industria.renderer;

import java.util.HashMap;
import java.util.Map.Entry;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.blocks.BlockRControllPanel;
import de.industria.items.panelitems.ItemPanelElement;
import de.industria.tileentity.TileEntityControllPanel;
import de.industria.tileentity.TileEntityControllPanel.Pos;
import de.industria.typeregistys.ModItems;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntityControllPanelRenderer extends TileEntityRenderer<TileEntityControllPanel> {

	public TileEntityControllPanelRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(TileEntityControllPanel tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		HashMap<Pos, ItemStack> elements = tileEntityIn.getPanelElements();
		Direction facing = tileEntityIn.getBlockState().getBlock() == ModItems.controll_panel ? tileEntityIn.getBlockState().getValue(BlockRControllPanel.FACING) : Direction.NORTH;
		
		matrixStackIn.pushPose();
		
		switch (facing) {
		case NORTH: 
			matrixStackIn.translate(0.5F, 0.5F, 5 * 0.0625F);
			matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));
			break;
		case SOUTH: 
			matrixStackIn.translate(0.5F, 0.5F, 11 * 0.0625F);
			matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(-90));
			matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(-180));
			break;
		case EAST: 
			matrixStackIn.translate(11 * 0.0625F, 0.5F, 0.5F);
			matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(90));
			matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));
			break;
		case WEST: 
			matrixStackIn.translate(5 * 0.0625F, 0.5F, 0.5F);
			matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(-90));
			matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(90));
			break;
		case UP: 
			matrixStackIn.translate(0.5F, 11 * 0.0625F, 0.5F);
			break;
		case DOWN: 
			matrixStackIn.translate(0.5F, 5 * 0.0625F, 0.5F);
			matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(180));
			break;
		}
		
		for (Entry<Pos, ItemStack> element : elements.entrySet()) {
			
			Pos position = element.getKey();

			matrixStackIn.pushPose();
			
			((ItemPanelElement) element.getValue().getItem()).draw(position.getX(), position.getY(), element.getValue(), combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);

			matrixStackIn.popPose();
			
		}
		
		matrixStackIn.popPose();
		
	}

}
