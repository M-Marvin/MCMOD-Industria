package de.m_marvin.industria.core.client.kinetics.blockentityrenderers;

import java.util.Objects;

import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.industria.core.client.util.AdvancedBakedAnimation;
import de.m_marvin.industria.core.client.util.ClientTimer;
import de.m_marvin.industria.core.client.util.SingleBlockBatchedRenderer;
import de.m_marvin.industria.core.kinetics.types.blockentities.BeltBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.world.level.block.state.BlockState;

public class BeltBlockEntityRenderer implements BlockEntityRenderer<BeltBlockEntity> {

	protected final BlockRenderDispatcher dispatcher;
	
	public BeltBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.dispatcher = context.getBlockRenderDispatcher();
	}
	
	@Override
	public void render(BeltBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {

		pPartialTick = Minecraft.getInstance().getFrameTime();

		pPoseStack.pushPose();
	
		BlockState state = pBlockEntity.getBlockState();
		
		double rpm = pBlockEntity.getRPM(0);
		float animation = (float) (rpm * -0.333F * ClientTimer.getRenderTicks() / 1000) % 1F;
		if (animation < 0F) animation += 1F;

		BakedModel model = dispatcher.getBlockModel(state);
		if (model instanceof SimpleBakedModel simpleModel) {
			AdvancedBakedAnimation.shiftTextureUV(simpleModel, 0F, animation * 0.5F, "belt");
		}

		SingleBlockBatchedRenderer.renderBlock(state, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, Objects.hash(animation));
		
		pPoseStack.popPose();
		
	}

}
