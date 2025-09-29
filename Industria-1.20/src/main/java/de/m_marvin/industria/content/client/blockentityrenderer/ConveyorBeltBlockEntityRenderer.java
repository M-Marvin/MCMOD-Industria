package de.m_marvin.industria.content.client.blockentityrenderer;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.industria.content.blockentities.machines.ConveyorBeltBlockEntity;
import de.m_marvin.industria.content.blockentities.machines.ConveyorBeltBlockEntity.ItemOnBelt;
import de.m_marvin.industria.content.blocks.machines.ConveyorBeltBlock;
import de.m_marvin.industria.content.client.AnimatedTransform;
import de.m_marvin.industria.core.client.kinetics.blockentityrenderers.BeltBlockEntityRenderer;
import de.m_marvin.industria.core.kinetics.types.blockentities.BeltBlockEntity;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.types.DiagonalPlanarDirection;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.debug.DebugRenderer;
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
			
			

//			if (axis == Axis.X)
//				pPoseStack.rotateAround(com.mojang.math.Axis.YN.rotation((float) (Math.PI / 2)), 0.0F, 0.0F, 0.0F);
//			else
//				pPoseStack.rotateAround(com.mojang.math.Axis.YN.rotation((float) (-Math.PI)), 0.0F, 0.0F, 0.0F);
//			pPoseStack.translate(-0.5F, 0.0F, 0.0F);
			
//			AnimatedTransform itemTransform = null;
//			if (isHorizontal) {
//				itemTransform = AnimatedTransform
//						.firstLinear(new Vec3f(1.0F, 0.0F, 0.0F))
//						.fromStart();
//			} else {
//				if (isUpwards) {
//					if (isEnd) {
//
//						itemTransform = AnimatedTransform
//								.firstAngular(new Vec3f(0.0F, 0.5F, 0.0F), new Vec3f(0, 0, 1.0F).mul((float) Math.PI / 4))
//								.thenLinear(new Vec3f(1.0F, 0.0F, 0.0F))
//								.fromStart();
//					} else {
//						itemTransform = AnimatedTransform
//								.firstLinear(new Vec3f(1.0F, 1.0F, 0.0F))
//								.fromStart();
//					}
//				} else {
//					if (isEnd) {
//
//						itemTransform = AnimatedTransform
//								.firstAngular(new Vec3f(0.0F, 0.5F, 0.0F), new Vec3f(0, 0, 1.0F).mul((float) -Math.PI / 4))
//								.thenLinear(new Vec3f(1.0F, 0.0F, 0.0F))
//								.fromStart();
//					} else {
//						itemTransform = AnimatedTransform
//								.firstLinear(new Vec3f(1.0F, -1.0F, 0.0F))
//								.fromStart();
//					}
//				}
//			}
			
//			if (itemTransform == null) {
//
//				pPoseStack.popPose();
//				
//				return;
//				
//			}
			
			Vec3f beltDir = new Vec3f(conveyor.getVisualBeltDirection());
			
			for (ItemOnBelt item : conveyor.getItems()) {
				
				Vec3f insertionDirection = item.insertedFrom;

				AnimatedTransform animation;
				
				if (insertionDirection.y == 0 && beltDir.y == 0) {
					
					// input horizontal to horizontal
					animation = AnimatedTransform
							.firstLinear(insertionDirection.mul(0.5F))
							.thenLinear(beltDir.mul(0.5F));
					
				} else if (insertionDirection.y == 0) {
					
					// input horizontal to diagonal
					animation = AnimatedTransform
							.firstLinear(insertionDirection.mul(0.5F))
							.thenLinear(beltDir.mul(0.5F));
					
				} else if (beltDir.y == 0) {

					// TODO beltDir never y == 0
					System.out.println("TEST");
					
					// input diagonal to horizontal
					animation = AnimatedTransform
							.firstLinear(insertionDirection.mul(0.5F))
							.thenLinear(beltDir.mul(0.5F));
					
				} else {

					// input diagonal to diagonal
					animation = AnimatedTransform
							.firstLinear(insertionDirection.mul(0.5F))
							.thenLinear(beltDir.mul(0.5F));
					
				}
				
				pPoseStack.pushPose();

				pPoseStack.translate(insertionDirection.x * -0.5F, 0, insertionDirection.z * -0.5F);
				
//				if (item.insertedFrom != null)
//					dir = new Vec3f(0, 0, 0);
				
				if (animation != null)
					animation.fromStart().transform(pPoseStack, ((item.position + 1) / 2F) );
				
//				System.out.println(" " + (item.position + 1) / 2F);
				
//				AnimatedTransform itemTransform = AnimatedTransform
//						.firstLinear(dir)
//						.fromStart();
//				
//				
//				itemTransform.transform(pPoseStack, item.position + motionSpeed * pPartialTick);
				
				itemDispatcher.get().renderStatic(item.stack, ItemDisplayContext.GROUND, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, conveyor.getLevel(), 0);
				
				pPoseStack.popPose();
				
			}
			
			pPoseStack.popPose();
			
		}
		
	}
	
}
