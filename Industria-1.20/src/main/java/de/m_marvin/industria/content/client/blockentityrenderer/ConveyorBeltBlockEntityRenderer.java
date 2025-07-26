package de.m_marvin.industria.content.client.blockentityrenderer;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.industria.content.blockentities.machines.ConveyorBeltBlockEntity;
import de.m_marvin.industria.content.blocks.machines.ConveyorBeltBlock;
import de.m_marvin.industria.core.client.kinetics.blockentityrenderers.BeltBlockEntityRenderer;
import de.m_marvin.industria.core.kinetics.types.blockentities.BeltBlockEntity;
import de.m_marvin.industria.core.util.types.DiagonalPlanarDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.ItemDisplayContext;

public class ConveyorBeltBlockEntityRenderer extends BeltBlockEntityRenderer {
	
	protected final Supplier<ItemRenderer> itemDispatcher = () -> Minecraft.getInstance().getItemRenderer();
	
	public ConveyorBeltBlockEntityRenderer(Context context) {
		super(context);
	}

	@Override
	public void render(BeltBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		super.render(pBlockEntity, pPartialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
		
		if (pBlockEntity instanceof ConveyorBeltBlockEntity conveyor) {
			
			pPoseStack.pushPose();
			
			pPoseStack.translate(0.5, 1, 0.5);
			
			Axis axis = pBlockEntity.getBlockState().getValue(ConveyorBeltBlock.AXIS);
			DiagonalPlanarDirection orientation = pBlockEntity.getBlockState().getValue(ConveyorBeltBlock.ORIENTATION);
			
			if (axis == Axis.X)
				pPoseStack.rotateAround(com.mojang.math.Axis.YN.rotation((float) (Math.PI / 2)), 0.0F, 0.0F, 0.0F);
			if (orientation.getNormal().x < 0)
				pPoseStack.rotateAround(com.mojang.math.Axis.YN.rotation((float) (Math.PI)), 0.0F, 0.0F, 0.0F);
			

			float motionSpeed = (float) pBlockEntity.getRPM(0) * 0.0006F;
			
			for (var item : conveyor.getItems()) {
				
				float pos = item.position + motionSpeed * pPartialTick;
				
				pPoseStack.pushPose();
				pPoseStack.translate(pos, 0, 0);
				
				itemDispatcher.get().renderStatic(item.stack, ItemDisplayContext.GROUND, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, conveyor.getLevel(), 0);
				
				pPoseStack.popPose();
				
			}
			
			pPoseStack.popPose();
			
		}
		
	}
	
}
