package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.Industria;
import de.industria.blocks.BlockMultipart;
import de.industria.tileentity.TileEntityMMetalFormer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntityMMetalFormerRenderer extends TileEntityRenderer<TileEntityMMetalFormer> {
	
	public static final ResourceLocation METAL_FORMER_TEXTURES = new ResourceLocation(Industria.MODID, "textures/block/metal_former.png");
	
	private TileEntityMMetalFormerModel metalFormerModel;
	
	public TileEntityMMetalFormerRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.metalFormerModel = new TileEntityMMetalFormerModel();
	}

	@Override
	public void render(TileEntityMMetalFormer tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		BlockState blockState = tileEntityIn.getBlockState();
		Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
		BlockPos partPos = BlockMultipart.getInternPartPos(blockState);
		
		if (partPos.equals(BlockPos.ZERO)) {
			
			IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.entityTranslucent(METAL_FORMER_TEXTURES));
			
			matrixStackIn.pushPose();
			
			matrixStackIn.translate(0F, -1F, 0F);
			
			matrixStackIn.translate(0.5F, 1.5F, 0.5F);
			matrixStackIn.mulPose(facing.getRotation());
			matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
			matrixStackIn.translate(0F, -1F, 0F);
			
			float motion = tileEntityIn.isWorking ? 10.0F : 0F;
			float partialToAdd = partialTicks < tileEntityIn.lastPartial ? partialTicks : partialTicks - tileEntityIn.lastPartial;
			tileEntityIn.rotation += motion * partialToAdd;
			
			metalFormerModel.setRotation(tileEntityIn.rotation);
			metalFormerModel.renderToBuffer(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
			
			matrixStackIn.popPose();
			
		}
		
	}
	
	@Override
	public boolean shouldRenderOffScreen(TileEntityMMetalFormer p_188185_1_) {
		return true;
	}
	
}
