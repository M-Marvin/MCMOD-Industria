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

public class BlockMSteamGeneratorItemRenderer extends ItemStackTileEntityRenderer {

	private TileEntityMSteamGeneratorModel steamGeneratorModel;
	
	public static final ResourceLocation STEAM_GENERATOR_TEXTURES = new ResourceLocation(Industria.MODID, "textures/block/steam_generator.png");
	
	public BlockMSteamGeneratorItemRenderer() {
		this.steamGeneratorModel = new TileEntityMSteamGeneratorModel();
	}
	
	@Override
	public void func_239207_a_(ItemStack stack, TransformType type, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.getEntityTranslucent(STEAM_GENERATOR_TEXTURES));
		
		matrixStackIn.push();

		matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180));
		matrixStackIn.translate(1.5F, -1.5F, -1.5F);
		
		steamGeneratorModel.render(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
		
		matrixStackIn.pop();
		
	}
	
}
