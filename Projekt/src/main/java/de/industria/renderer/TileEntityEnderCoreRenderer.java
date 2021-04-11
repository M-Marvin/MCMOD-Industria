package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.Industria;
import de.industria.tileentity.TileEntityEnderCore;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnderCoreRenderer extends TileEntityRenderer<TileEntityEnderCore> {
	
	public static final ResourceLocation ENDER_CORE_TEXTURES = new ResourceLocation(Industria.MODID, "textures/block/ender_core.png");
	
	private TileEntityEnderCoreModel coreModel;
	
	public TileEntityEnderCoreRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.coreModel = new TileEntityEnderCoreModel();
	}

	@Override
	public void render(TileEntityEnderCore tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		matrixStackIn.push();
		
		matrixStackIn.translate(0.5F, -0.5F, 0.5F);
		
		tileEntityIn.rotationProgress += 0.1F;
		if (tileEntityIn.rotationProgress > 360) tileEntityIn.rotationProgress -= 360;
		float rotation = tileEntityIn.rotationProgress;
		
		IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityTranslucent(ENDER_CORE_TEXTURES));
		this.coreModel.setRotation(rotation);
		this.coreModel.render(matrixStackIn, vertexBuilder, combinedLightIn, combinedOverlayIn, 1, 1, 1, 1);
		
		matrixStackIn.pop();
		
	}
	
}
