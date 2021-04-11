package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.Industria;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class BlockEnderCoreItemRenderer extends ItemStackTileEntityRenderer {

	private TileEntityEnderCoreModel coreModel;
	private float rotationProgress;
	
	public static final ResourceLocation ENDER_CORE_TEXTURES = new ResourceLocation(Industria.MODID, "textures/block/ender_core.png");
	
	public BlockEnderCoreItemRenderer() {
		this.coreModel = new TileEntityEnderCoreModel();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void func_239207_a_(ItemStack stack, TransformType type, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

		Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(Industria.ender_core.getDefaultState(), matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
		
		IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.getEntityTranslucent(ENDER_CORE_TEXTURES));
		
		matrixStackIn.push();

		rotationProgress += 0.1F;
		if (rotationProgress > 360) rotationProgress -= 360;
		float rotation = rotationProgress;
		
		matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
		matrixStackIn.translate(0.5F, -1.5F, -0.5F);
		
		coreModel.setRotation(rotation);
		coreModel.render(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
		
		matrixStackIn.pop();
		
	}
	
}
