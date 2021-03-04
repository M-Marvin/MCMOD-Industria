package de.redtec.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.redtec.RedTec;
import de.redtec.blocks.BlockMultiPart;
import de.redtec.tileentity.TileEntityMFluidBath;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntityMFluidBathRenderer extends TileEntityRenderer<TileEntityMFluidBath> {
	
	public static final ResourceLocation FLUID_BATH_TEXTURES = new ResourceLocation(RedTec.MODID, "textures/block/fluid_bath.png");
	
	private TileEntityMFluidBathModel fluidBathModel;
	private ItemRenderer itemRenderDispatcher;
	
	public TileEntityMFluidBathRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.fluidBathModel = new TileEntityMFluidBathModel();
		this.itemRenderDispatcher = Minecraft.getInstance().getItemRenderer();
	}

	@Override
	public void render(TileEntityMFluidBath tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		BlockState blockState = tileEntityIn.getBlockState();
		Direction facing = blockState.get(BlockStateProperties.HORIZONTAL_FACING);
		BlockPos partPos = BlockMultiPart.getInternPartPos(blockState);
		
		if (partPos.equals(BlockPos.ZERO)) {
			
			IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.getEntityTranslucent(FLUID_BATH_TEXTURES));
			
			matrixStackIn.push();
			
			matrixStackIn.translate(0F, -1F, 0F);
			
			matrixStackIn.translate(0.5F, 1.5F, 0.5F);
			matrixStackIn.rotate(facing.getRotation());
			matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
			matrixStackIn.translate(0F, -1F, 0F);
			
			fluidBathModel.render(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
			
			if (tileEntityIn.fluidBufferState > 0) {
				
				matrixStackIn.push();
				matrixStackIn.translate(-11 * 0.0625F, -(12 + tileEntityIn.fluidBufferState * 4) * 0.0625F, 1);
				
				fluidBathModel.renderFluidPlate(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
				
				matrixStackIn.pop();
				
			}

			if (!tileEntityIn.getStackInSlot(0).isEmpty()) {

				float progress = tileEntityIn.progress / (float) tileEntityIn.progressTotal;
				
				matrixStackIn.push();
				
				matrixStackIn.translate(-11 * 0.0625F, -7.8F * 0.0625F, (16 + progress * 30) * 0.0625F);
				fluidBathModel.renderItemHolder(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
				
				matrixStackIn.translate(4 * 0.0625F, 8 * 0.0625F, -15 * 0.0625F);
				itemRenderDispatcher.renderItem(tileEntityIn.getStackInSlot(0), TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
				
				matrixStackIn.pop();
				
			}
			
			matrixStackIn.pop();
			
		}
		
	}
	
}
