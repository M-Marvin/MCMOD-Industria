package de.redtec.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.redtec.RedTec;
import de.redtec.blocks.BlockMSteamGenerator;
import de.redtec.tileentity.TileEntityMSteamGenerator;
import de.redtec.tileentity.TileEntityMSteamGenerator.TEPart;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntityMSteamGeneratorRenderer extends TileEntityRenderer<TileEntityMSteamGenerator> {
	
	private TileEntityMSteamGeneratorModel steamGeneratorModel;
	
	public static final ResourceLocation STEAM_GENERATOR_TEXTURES = new ResourceLocation(RedTec.MODID, "textures/block/steam_generator.png");
	
	public TileEntityMSteamGeneratorRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.steamGeneratorModel = new TileEntityMSteamGeneratorModel();
	}

	@Override
	public void render(TileEntityMSteamGenerator tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		if (tileEntityIn.getPart() == TEPart.CENTER) {
			
			Direction facing = tileEntityIn.getBlockState().get(BlockMSteamGenerator.FACING);
			IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.getEntityTranslucent(STEAM_GENERATOR_TEXTURES));
			
			matrixStackIn.push();
			
			matrixStackIn.translate(0F, -1F, 0F);
			
			switch (facing) {
			case NORTH:
				matrixStackIn.translate(1.5F, 1.5F, -0.5F);
				break;
			case WEST:
				matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90));
				matrixStackIn.translate(0.5F, 1.5F, -0.5F);
				break;
			case EAST:
				matrixStackIn.rotate(Vector3f.YP.rotationDegrees(270));
				matrixStackIn.translate(1.5F, 1.5F, -1.5F);
				break;
			case SOUTH:
				matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180));
				matrixStackIn.translate(0.5F, 1.5F, -1.5F);
				break;
			default:
			}
			
			matrixStackIn.scale(0.999F, 0.999F, 0.999F);
			
			steamGeneratorModel.render(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
			
			matrixStackIn.pop();
			
		}
		
	}

}
