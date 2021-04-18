package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.Industria;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class BlockMSchredderItemRenderer extends ItemStackTileEntityRenderer {

	private TileEntityMSchredderModel schredderModel;
	
	public static final ResourceLocation SCHREDDER_TEXTURES = new ResourceLocation(Industria.MODID, "textures/block/schredder.png");
	
	public BlockMSchredderItemRenderer() {
		this.schredderModel = new TileEntityMSchredderModel();
	}
	
	@Override
	public void func_239207_a_(ItemStack stack, TransformType type, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.getEntityTranslucent(SCHREDDER_TEXTURES));
		
		matrixStackIn.push();

		matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
		matrixStackIn.translate(1.5F, -1.5F, -1.5F);
		
		schredderModel.render(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
		
		matrixStackIn.pop();
		
	}
	
}
