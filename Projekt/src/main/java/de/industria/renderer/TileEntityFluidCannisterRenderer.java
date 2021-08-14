package de.industria.renderer;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.tileentity.TileEntityFluidCannister;
import de.industria.typeregistys.ModItems;
import de.industria.util.gui.AnimatedTexture;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntityFluidCannisterRenderer extends TileEntityRenderer<TileEntityFluidCannister> {
	
	public TileEntityFluidCannisterRenderer(TileEntityRendererDispatcher renderDispatcher) {
		super(renderDispatcher);
	}
	
	@Override
	public void render(TileEntityFluidCannister tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		if (!tileEntityIn.getContent().isEmpty()) {
			
			float amount = tileEntityIn.getContent().getAmount() / (float) TileEntityFluidCannister.MAX_CONTENT;
			Fluid type = tileEntityIn.getContent().getFluid();
			BlockState state = tileEntityIn.getBlockState();
			Direction rotation = state.getBlock() == ModItems.fluid_cannister.getBlock() ? state.getValue(BlockStateProperties.HORIZONTAL_FACING) : Direction.NORTH;
			
			renderFluid(matrixStackIn, bufferIn, type, amount, rotation, combinedOverlayIn, combinedLightIn);
			
		}
		
	}
	
	public static void renderFluid(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, Fluid type, float amount, Direction rotation, int combinedOverlayIn, int combinedLightIn) {
		
		ResourceLocation fluidTextureResLoc = type.getAttributes().getStillTexture();
		ResourceLocation fluidTexturePath = new ResourceLocation(fluidTextureResLoc.getNamespace(), "textures/" + fluidTextureResLoc.getPath() + ".png");
		AnimatedTexture animatedTexture = AnimatedTexture.prepareTexture(fluidTexturePath);
		IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.entityTranslucent(fluidTexturePath));
		
		Color color = new Color(type.getAttributes().getColor());
		float cr = color.getRed() / 255F;
		float cg = color.getGreen() / 255F;
		float cb = color.getBlue() / 255F;
		float ca = color.getAlpha() / 255F;
		
		Vector3f fc1 = new Vector3f(2.1F / 16F, 0F / 16F, 1.1F / 16F);
		Vector3f fc2 = new Vector3f(13.9F / 16F, (12.9F / 16F) * amount, 14.9F / 16F);
		
		Vector2f frameSideUV0 = animatedTexture.getFrameUV(fc1.y(), fc1.z());
		Vector2f frameSideUV1 = animatedTexture.getFrameUV(fc2.y(), fc2.z());
		Vector2f frameTopUV0 = animatedTexture.getFrameUV(fc1.z(), fc1.x());
		Vector2f frameTopUV1 = animatedTexture.getFrameUV(fc2.z(), fc2.x());
		
		matrixStackIn.pushPose();
		
		matrixStackIn.translate(0.5F, 0.5F, 0.5F);
		matrixStackIn.mulPose(Vector3f.YN.rotationDegrees(rotation.toYRot()));
		matrixStackIn.translate(-0.5F, -0.5F, -0.5F);
		
		Matrix4f posMatrix = matrixStackIn.last().pose();
		Matrix3f normalMatrix = matrixStackIn.last().normal();
		
		// West face
		vertexBuilder.vertex(posMatrix, fc1.x(), fc1.y(), fc1.z()).color(cr, cg, cb, ca).uv(frameSideUV0.x, frameSideUV0.y).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(normalMatrix, 1, 1, 1).endVertex();
		vertexBuilder.vertex(posMatrix, fc1.x(), fc1.y(), fc2.z()).color(cr, cg, cb, ca).uv(frameSideUV0.x, frameSideUV1.y).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(normalMatrix, 1, 1, 1).endVertex();
		vertexBuilder.vertex(posMatrix, fc1.x(), fc2.y(), fc2.z()).color(cr, cg, cb, ca).uv(frameSideUV1.x, frameSideUV1.y).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(normalMatrix, 1, 1, 1).endVertex();
		vertexBuilder.vertex(posMatrix, fc1.x(), fc2.y(), fc1.z()).color(cr, cg, cb, ca).uv(frameSideUV1.x, frameSideUV0.y).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(normalMatrix, 1, 1, 1).endVertex();
		// East face
		vertexBuilder.vertex(posMatrix, fc2.x(), fc1.y(), fc1.z()).color(cr, cg, cb, ca).uv(frameSideUV0.x, frameSideUV0.y).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(normalMatrix, 1, 1, 1).endVertex();
		vertexBuilder.vertex(posMatrix, fc2.x(), fc1.y(), fc2.z()).color(cr, cg, cb, ca).uv(frameSideUV0.x, frameSideUV1.y).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(normalMatrix, 1, 1, 1).endVertex();
		vertexBuilder.vertex(posMatrix, fc2.x(), fc2.y(), fc2.z()).color(cr, cg, cb, ca).uv(frameSideUV1.x, frameSideUV1.y).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(normalMatrix, 1, 1, 1).endVertex();
		vertexBuilder.vertex(posMatrix, fc2.x(), fc2.y(), fc1.z()).color(cr, cg, cb, ca).uv(frameSideUV1.x, frameSideUV0.y).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(normalMatrix, 1, 1, 1).endVertex();
		// Top face
		vertexBuilder.vertex(posMatrix, fc1.x(), fc2.y(), fc1.z()).color(cr, cg, cb, ca).uv(frameTopUV0.x, frameTopUV0.y).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(normalMatrix, 1, 1, 1).endVertex();
		vertexBuilder.vertex(posMatrix, fc1.x(), fc2.y(), fc2.z()).color(cr, cg, cb, ca).uv(frameTopUV0.x, frameTopUV1.y).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(normalMatrix, 1, 1, 1).endVertex();
		vertexBuilder.vertex(posMatrix, fc2.x(), fc2.y(), fc2.z()).color(cr, cg, cb, ca).uv(frameTopUV1.x, frameTopUV1.y).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(normalMatrix, 1, 1, 1).endVertex();
		vertexBuilder.vertex(posMatrix, fc2.x(), fc2.y(), fc1.z()).color(cr, cg, cb, ca).uv(frameTopUV1.x, frameTopUV0.y).overlayCoords(combinedOverlayIn).uv2(combinedLightIn).normal(normalMatrix, 1, 1, 1).endVertex();
		
		matrixStackIn.popPose();
		
	}
	
}
