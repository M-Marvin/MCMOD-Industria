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

public class BlockMElectricHeaterItemRenderer extends ItemStackTileEntityRenderer {
	
	private TileEntityMElectricHeaterModel electricHeaterModel;
	
	public static final ResourceLocation ELECTRIC_HEATER_TEXTURES = new ResourceLocation(Industria.MODID, "textures/block/electric_heater.png");
	
	public BlockMElectricHeaterItemRenderer() {
		this.electricHeaterModel = new TileEntityMElectricHeaterModel();
	}
	
	@Override
	public void func_239207_a_(ItemStack stack, TransformType type, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.getEntityTranslucent(ELECTRIC_HEATER_TEXTURES));
		
		matrixStackIn.push();
		
		matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(180));
		matrixStackIn.translate(-0.5F, -1.5F, 0.5F);
		
		electricHeaterModel.render(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
		
		matrixStackIn.pop();
		
	}
	
}
