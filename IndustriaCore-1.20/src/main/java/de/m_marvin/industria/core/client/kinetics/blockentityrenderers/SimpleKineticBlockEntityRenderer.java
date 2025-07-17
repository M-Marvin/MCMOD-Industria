package de.m_marvin.industria.core.client.kinetics.blockentityrenderers;

import java.util.Objects;

import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.industria.core.client.util.ClientTimer;
import de.m_marvin.industria.core.client.util.SingleBlockBatchedRenderer;
import de.m_marvin.industria.core.kinetics.types.blockentities.IKineticBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleKineticBlockEntityRenderer<T extends BlockEntity & IKineticBlockEntity> implements BlockEntityRenderer<T> {
	
	protected final BlockRenderDispatcher dispatcher;
	
	public SimpleKineticBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.dispatcher = context.getBlockRenderDispatcher();
	}
	
	@Override
	public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		
		pPartialTick = Minecraft.getInstance().getFrameTime();
		
		for (IKineticBlockEntity.CompoundPart part : pBlockEntity.getVisualParts()) {

			pPoseStack.pushPose();
		
			BlockState state = part.state();
			Axis axis = part.rotationAxis();
			float rotationalOffset = (float) part.axialOffset();
			float rotationalSpeed = (float) part.rotationRatio();
			
			double rpm = pBlockEntity.getRPM(0);
			float rotation = (float) ((float) (ClientTimer.getRenderTicks() / 3000 * rpm * rotationalSpeed) * 2 * Math.PI);
			
			pPoseStack.translate(0.5, 0.5, 0.5);
			switch (axis) {
			case X: pPoseStack.rotateAround(com.mojang.math.Axis.XP.rotation(rotation - rotationalOffset), 0, 0, 0); break;
			case Y: pPoseStack.rotateAround(com.mojang.math.Axis.YP.rotation(rotation - rotationalOffset), 0, 0, 0); break;
			case Z: pPoseStack.rotateAround(com.mojang.math.Axis.ZP.rotation(rotation - rotationalOffset), 0, 0, 0); break;
			}
			pPoseStack.translate(-0.5, -0.5, -0.5);
			
			SingleBlockBatchedRenderer.renderBlock(state, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, Objects.hash(rotation - rotationalOffset));
			
			pPoseStack.popPose();

		}
		
	}
	
}
