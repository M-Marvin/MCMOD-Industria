package de.industria.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.industria.Industria;
import de.industria.blocks.BlockMultiPart;
import de.industria.tileentity.TileEntityMCoalHeater;
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

public class TileEntityMCoalHeaterRenderer extends TileEntityRenderer<TileEntityMCoalHeater> {
	
	public static final ResourceLocation COAL_HEATER_TEXTURES = new ResourceLocation(Industria.MODID, "textures/block/coal_heater.png");
	public static final ResourceLocation COAL_HEATER_TEXTURES_LIT = new ResourceLocation(Industria.MODID, "textures/block/coal_heater_lit.png");
	
	private TileEntityMCoalHeaterModel coalHeaterModel;
	
	public TileEntityMCoalHeaterRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.coalHeaterModel = new TileEntityMCoalHeaterModel();
	}

	@Override
	public void render(TileEntityMCoalHeater tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

		BlockState blockState = tileEntityIn.getBlockState();
		Direction facing = blockState.get(BlockStateProperties.HORIZONTAL_FACING);
		BlockPos partPos = BlockMultiPart.getInternPartPos(blockState);
		
		if (partPos.equals(BlockPos.ZERO)) {
			
			boolean lit = tileEntityIn.isWorking;
			IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.getEntityTranslucent(lit ? COAL_HEATER_TEXTURES_LIT : COAL_HEATER_TEXTURES));
			
			matrixStackIn.push();
			
			matrixStackIn.translate(0F, -1F, 0F);
			
			matrixStackIn.translate(0.5F, 1.5F, 0.5F);
			matrixStackIn.rotate(facing.getRotation());
			matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
			matrixStackIn.translate(0F, -1F, 0F);
			
			coalHeaterModel.render(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
			
			matrixStackIn.pop();
			
		}
		
	}

}
