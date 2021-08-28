package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.Industria;
import de.industria.blocks.BlockMultiPart;
import de.industria.tileentity.TileEntityMGasHeater;
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

public class TileEntityMGasHeaterRenderer extends TileEntityRenderer<TileEntityMGasHeater> {
	
	public static final ResourceLocation GAS_HEATER_TEXTURES = new ResourceLocation(Industria.MODID, "textures/block/gas_heater.png");
	public static final ResourceLocation GAS_HEATER_TEXTURES_LIT = new ResourceLocation(Industria.MODID, "textures/block/gas_heater_lit.png");
	
	private TileEntityMGasHeaterModel gasHeaterModel;
	
	public TileEntityMGasHeaterRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.gasHeaterModel = new TileEntityMGasHeaterModel();
	}
	
	@Override
	public void render(TileEntityMGasHeater tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

		BlockState blockState = tileEntityIn.getBlockState();
		Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
		BlockPos partPos = BlockMultiPart.getInternPartPos(blockState);
		
		if (partPos.equals(BlockPos.ZERO)) {
			
			boolean lit = tileEntityIn.isWorking;
			IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.entityTranslucent(lit ? GAS_HEATER_TEXTURES_LIT : GAS_HEATER_TEXTURES));
			
			matrixStackIn.pushPose();
			
			matrixStackIn.translate(0F, -1F, 0F);
			
			matrixStackIn.translate(0.5F, 1.5F, 0.5F);
			matrixStackIn.mulPose(facing.getRotation());
			matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
			matrixStackIn.translate(0F, -1F, 0F);
			
			gasHeaterModel.renderToBuffer(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
			
			matrixStackIn.popPose();
			
		}
		
	}
	
	@Override
	public boolean shouldRenderOffScreen(TileEntityMGasHeater p_188185_1_) {
		return true;
	}
	
}
