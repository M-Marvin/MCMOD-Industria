package de.industria.renderer;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.tileentity.TileEntityFluidCannister;
import de.industria.util.gui.AnimatedTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector4f;

public class TileEntityFluidCannisterRenderer extends TileEntityRenderer<TileEntityFluidCannister> {

	public TileEntityFluidCannisterRenderer(TileEntityRendererDispatcher renderDispatcher) {
		super(renderDispatcher);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(TileEntityFluidCannister tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		if (!tileEntityIn.getContent().isEmpty()) {
			
			float amount = tileEntityIn.getContent().getAmount() / (float) TileEntityFluidCannister.MAX_CONTENT;
			Fluid type = tileEntityIn.getContent().getFluid();
			
			ResourceLocation fluidTexture = type.getAttributes().getStillTexture();
			AnimatedTexture animatedTexture = AnimatedTexture.prepareTexture(fluidTexture);
			IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.entityTranslucent(fluidTexture));
			Color color = new Color(type.getAttributes().getColor());
			
			
			matrixStackIn.pushPose();
			
			GlStateManager._color4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
			
			Vector4f uv = animatedTexture.getFrameUV(16, 16);
			
			System.out.println("TETST");
			
			vertexBuilder.vertex(0, 0, 0).uv(uv.x(), uv.y());
			vertexBuilder.vertex(16, 0, 0).uv(uv.z(), uv.y());
			vertexBuilder.vertex(16, 16, 0).uv(uv.z(), uv.w());
			vertexBuilder.vertex(0, 16, 0).uv(uv.x(), uv.w());
			
			matrixStackIn.popPose();
			
		}
		
	}
		
}
