package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.Industria;
import de.industria.blocks.BlockNComputer;
import de.industria.tileentity.TileEntityNComputer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntityNComputerRenderer extends TileEntityRenderer<TileEntityNComputer> {
	
	private TileEntityNComputerModel computerModel;
	
	public static final ResourceLocation COMPUTER_TEXTURES_ACTIVE = new ResourceLocation(Industria.MODID, "textures/block/computer_active.png");
	public static final ResourceLocation COMPUTER_TEXTURES_STANDBYE = new ResourceLocation(Industria.MODID, "textures/block/computer_standby.png");
	public static final ResourceLocation COMPUTER_TEXTURES_OFF = new ResourceLocation(Industria.MODID, "textures/block/computer_off.png");
	
	public TileEntityNComputerRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.computerModel = new TileEntityNComputerModel();
	}

	@Override
	public void render(TileEntityNComputer tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		
		Direction facing = tileEntityIn.getBlockState().getValue(BlockNComputer.FACING);
		IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.entityCutout(getTexture(tileEntityIn)));
		
		matrixStackIn.pushPose();
		
		matrixStackIn.translate(0F, -3F, 0F);
		
		switch (facing) {
		case NORTH:
			matrixStackIn.translate(1.5F, 1.5F, -0.5F);
			break;
		case WEST:
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90));
			matrixStackIn.translate(0.5F, 1.5F, -0.5F);
			break;
		case EAST:
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270));
			matrixStackIn.translate(1.5F, 1.5F, -1.5F);
			break;
		case SOUTH:
			matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180));
			matrixStackIn.translate(0.5F, 1.5F, -1.5F);
			break;
		default:
		}

		matrixStackIn.mulPose(Vector3f.ZN.rotationDegrees(180));
		matrixStackIn.translate(1F, -3F, 1F);
		
		computerModel.renderToBuffer(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
		
		matrixStackIn.popPose();
		
	}
	
	public ResourceLocation getTexture(TileEntityNComputer computer) {
		if (computer.hasPower()) {
			return computer.isComputerRunning() ? COMPUTER_TEXTURES_ACTIVE : COMPUTER_TEXTURES_STANDBYE;
		}
		return COMPUTER_TEXTURES_OFF;
	}
	
	@Override
	public boolean shouldRenderOffScreen(TileEntityNComputer p_188185_1_) {
		return true;
	}
	
}