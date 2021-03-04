package de.redtec.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.redtec.RedTec;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class BlockMFluidBathItemRenderer extends ItemStackTileEntityRenderer {

	private TileEntityMFluidBathModel fluidBathModel;
	
	public static final ResourceLocation FLUID_BATH_TEXTURES = new ResourceLocation(RedTec.MODID, "textures/block/fluid_bath.png");
	
	public BlockMFluidBathItemRenderer() {
		this.fluidBathModel = new TileEntityMFluidBathModel();
	}
	
	@Override
	public void func_239207_a_(ItemStack stack, TransformType type, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.getEntityTranslucent(FLUID_BATH_TEXTURES));
		
		matrixStackIn.push();
		
		matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
		matrixStackIn.translate(1.0F, -1.5F, -0.9F);
		
		fluidBathModel.render(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
		
		matrixStackIn.pop();
		
	}
	
}
