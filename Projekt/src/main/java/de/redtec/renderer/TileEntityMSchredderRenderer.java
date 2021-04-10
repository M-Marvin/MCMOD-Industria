package de.redtec.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import de.redtec.RedTec;
import de.redtec.blocks.BlockMultiPart;
import de.redtec.items.ItemSchredderTool;
import de.redtec.tileentity.TileEntityMSchredder;
import de.redtec.typeregistys.ModClientBindings;
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

public class TileEntityMSchredderRenderer extends TileEntityRenderer<TileEntityMSchredder> {
	
	private TileEntityMSchredderModel schredderModel;
	
	public static final ResourceLocation SCHREDDER_TEXTURES = new ResourceLocation(RedTec.MODID, "textures/block/schredder.png");
	
	public TileEntityMSchredderRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.schredderModel = new TileEntityMSchredderModel();
	}
	
	@Override
	public void render(TileEntityMSchredder tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

		BlockState blockState = tileEntityIn.getBlockState();
		Direction facing = blockState.get(BlockStateProperties.HORIZONTAL_FACING);
		BlockPos partPos = BlockMultiPart.getInternPartPos(blockState);
		
		if (partPos.equals(BlockPos.ZERO)) {
			
			IVertexBuilder vertexBuffer = bufferIn.getBuffer(RenderType.getEntityTranslucent(SCHREDDER_TEXTURES));
			
			matrixStackIn.push();
			
			matrixStackIn.translate(0F, -1F, 0F);
			
			matrixStackIn.translate(0.5F, 1.5F, 0.5F);
			matrixStackIn.rotate(facing.getRotation());
			matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90));
			matrixStackIn.translate(0F, -1F, 0F);
			
			schredderModel.render(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
			
			ItemSchredderTool tool = tileEntityIn.getToolItem();

			if (tool != null) {
				
				float motion = tileEntityIn.isWorking ? 10.0F : 0F;
				float partialToAdd = partialTicks < tileEntityIn.lastPartial ? partialTicks : partialTicks - tileEntityIn.lastPartial;
				tileEntityIn.rotation += motion * partialToAdd;
				
				ItemSchredderToolModel toolModel = (ItemSchredderToolModel) ModClientBindings.getBindedModel(tool).getModel();
				toolModel.setRotationState(tileEntityIn.rotation);
				
				vertexBuffer = bufferIn.getBuffer(RenderType.getEntityTranslucent(ModClientBindings.getBindedModel(tool).getTextureLoc()));
				toolModel.render(matrixStackIn, vertexBuffer, combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
				
			}
			
			matrixStackIn.pop();
			
		}
		
	}
	
}
