package de.m_marvin.industria.core.client.compound.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.industria.core.compound.types.blockentities.CompoundBlockEntity;
import de.m_marvin.industria.core.registries.Blocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class CompoundBlockEntityRenderer<T extends CompoundBlockEntity> implements BlockEntityRenderer<T> {

	protected final BlockRenderDispatcher blockDispatcher;
	protected final BlockEntityRenderDispatcher blockEntityDispatcher;
	
	public CompoundBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.blockDispatcher = context.getBlockRenderDispatcher();
		this.blockEntityDispatcher = context.getBlockEntityRenderDispatcher();
	}
	
	@Override
	public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		
		if (pBlockEntity.getParts().isEmpty()) {
			
			renderCompoundBlock(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pBlockEntity.getLevel(), Blocks.COMPOUND_BLOCK.get().defaultBlockState());
			return;
			
		}
		
		for (var block : pBlockEntity.getParts().values()) {
			
			if (block.getState().getRenderShape() == RenderShape.MODEL) {
				
				renderCompoundBlock(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, block.getLevel(), block.getState());
				
			} else if (block.getState().getRenderShape() == RenderShape.ENTITYBLOCK_ANIMATED && block.getBlockEntity() != null) {
				
				renderCompoundBlockEntity(pPoseStack, pBuffer, pPartialTick, pPackedLight, pPackedOverlay, block.getBlockEntity());
				
			}
			
		}
		
	}
	
	protected void renderCompoundBlock(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, LevelAccessor level, BlockState state) {

		BakedModel model = this.blockDispatcher.getBlockModel(state);
		ModelData data = ModelData.EMPTY;

		for (net.minecraft.client.renderer.RenderType rt : model.getRenderTypes(state, level.getRandom(), data))
			this.blockDispatcher.getModelRenderer().renderModel(pPoseStack.last(), pBuffer.getBuffer(net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)), state, model, 1F, 1F, 1F, pPackedLight, pPackedOverlay, data, rt);
		
	}
	
	protected <B extends BlockEntity> void renderCompoundBlockEntity(PoseStack pPoseStack, MultiBufferSource pBuffer, float pPartialTick, int pPackedLight, int pPackedOverlay, B blockEntity) {
		
		BlockEntityRenderer<B> renderer = this.blockEntityDispatcher.getRenderer(blockEntity);
		if (renderer == null) return;
		
		renderer.render(blockEntity, pPartialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
		
	}
	
}
