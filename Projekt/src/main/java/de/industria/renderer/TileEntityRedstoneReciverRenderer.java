package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.tileentity.TileEntityRedstoneReciver;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class TileEntityRedstoneReciverRenderer extends TileEntityRenderer<TileEntityRedstoneReciver> {

	public TileEntityRedstoneReciverRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(TileEntityRedstoneReciver tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		
		
	}

}
