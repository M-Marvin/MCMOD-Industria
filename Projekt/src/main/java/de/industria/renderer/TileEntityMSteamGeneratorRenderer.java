package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.Industria;
import de.industria.blocks.BlockMSteamGenerator;
import de.industria.blocks.BlockMultipart;
import de.industria.tileentity.TileEntityMSteamGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntityMSteamGeneratorRenderer extends TileEntityRenderer<TileEntityMSteamGenerator> {
	
	private TileEntityMSteamGeneratorModel steamGeneratorModel;
	
	public static final ResourceLocation STEAM_GENERATOR_TEXTURES = new ResourceLocation(Industria.MODID, "textures/block/steam_generator.png");
	
	public TileEntityMSteamGeneratorRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.steamGeneratorModel = new TileEntityMSteamGeneratorModel();
	}

	@Override
	public void render(TileEntityMSteamGenerator tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		BlockState blockState = tileEntityIn.getBlockState();
		BlockPos partPos = BlockMultipart.getInternPartPos(blockState);
		
		if (partPos.equals(BlockPos.ZERO)) {
			
			Direction facing = tileEntityIn.getBlockState().getValue(BlockMSteamGenerator.FACING);
			IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.entityTranslucent(STEAM_GENERATOR_TEXTURES));
			
			matrixStackIn.pushPose();

			matrixStackIn.translate(0F, -1F, 0F);
			
			matrixStackIn.translate(0.5F, 1.5F, 0.5F);
			matrixStackIn.mulPose(facing.getRotation());
			matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
			matrixStackIn.translate(0F, -1F, 0F);
			
			matrixStackIn.scale(0.999F, 0.999F, 0.999F);
			
			float partialAccerlation = (partialTicks) * tileEntityIn.accerlation;
			float rotation = (tileEntityIn.turbinRotation + partialAccerlation) / 360F;
			
			steamGeneratorModel.setTurbinRotation((float) (rotation * Math.PI * 2));
			steamGeneratorModel.renderToBuffer(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
			
			matrixStackIn.popPose();
			
		}
		
	}
	
	@Override
	public boolean shouldRenderOffScreen(TileEntityMSteamGenerator p_188185_1_) {
		return true;
	}
	
}
