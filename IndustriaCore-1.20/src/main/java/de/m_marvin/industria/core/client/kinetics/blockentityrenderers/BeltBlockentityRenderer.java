package de.m_marvin.industria.core.client.kinetics.blockentityrenderers;

import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.industria.core.client.util.AdvancedBakedAnimation;
import de.m_marvin.industria.core.client.util.ClientTimer;
import de.m_marvin.industria.core.kinetics.types.blockentities.BeltBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class BeltBlockentityRenderer implements BlockEntityRenderer<BeltBlockEntity> {

	protected final BlockRenderDispatcher dispatcher;
	
	public BeltBlockentityRenderer(BlockEntityRendererProvider.Context context) {
		this.dispatcher = context.getBlockRenderDispatcher();
	}
	
	@Override
	public void render(BeltBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {

		pPartialTick = Minecraft.getInstance().getFrameTime();

		pPoseStack.pushPose();
	
		BlockState state = pBlockEntity.getBlockState();
		BakedModel model = dispatcher.getBlockModel(state);
		ModelData data = ModelData.EMPTY;
		
		double rpm = pBlockEntity.getRPM(0);
		float animation = (float) (rpm * -0.333F * ClientTimer.getRenderTicks() / 1000) % 1F;
		if (animation < 0F) animation += 1F;
		
		if (model instanceof SimpleBakedModel simpleModel) {
			// TODO texture selective animation
			AdvancedBakedAnimation.shiftTextureUV(simpleModel, 0F, animation * 0.5F, tex -> true);
		}
		
		for (net.minecraft.client.renderer.RenderType rt : model.getRenderTypes(state, pBlockEntity.getLevel().getRandom(), data))
			this.dispatcher.getModelRenderer().renderModel(pPoseStack.last(), pBuffer.getBuffer(net.minecraftforge.client.RenderTypeHelper.getEntityRenderType(rt, false)), state, model, 1F, 1F, 1F, pPackedLight, pPackedOverlay, data, rt);

		pPoseStack.popPose();
		
	}

}
