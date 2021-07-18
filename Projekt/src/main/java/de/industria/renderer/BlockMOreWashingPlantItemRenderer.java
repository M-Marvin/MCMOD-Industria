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

public class BlockMOreWashingPlantItemRenderer extends ItemStackTileEntityRenderer {

	private TileEntityMOreWashingPlantModel oreWashingPlantModel;
	
	public static final ResourceLocation ORE_WASHING_PLANT_TEXTURES = new ResourceLocation(Industria.MODID, "textures/block/ore_washing_plant.png");
	
	public BlockMOreWashingPlantItemRenderer() {
		this.oreWashingPlantModel = new TileEntityMOreWashingPlantModel();
	}
	
	@Override
	public void renderByItem(ItemStack stack, TransformType type, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.entityTranslucent(ORE_WASHING_PLANT_TEXTURES));
		
		matrixStackIn.pushPose();

		matrixStackIn.mulPose(Vector3f.ZN.rotationDegrees(180));
		matrixStackIn.translate(-0.5F, -1.5F, 0.5F);
		
		oreWashingPlantModel.renderToBuffer(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
		
		matrixStackIn.popPose();
		
	}
	
}
