package de.m_marvin.industria.content.client.blockentityrenderer;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.industria.content.blockentities.machines.ConveyorBeltBlockEntity;
import de.m_marvin.industria.content.blockentities.machines.ConveyorBeltBlockEntity.ItemOnBelt;
import de.m_marvin.industria.core.client.kinetics.blockentityrenderers.BeltBlockEntityRenderer;
import de.m_marvin.industria.core.client.util.PathTransfom;
import de.m_marvin.industria.core.kinetics.types.blockentities.BeltBlockEntity;
import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
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
			
			Vec3f beltDirection = new Vec3f(conveyor.getVisualBeltDirection());
			float patialMotion = (float) Math.abs(conveyor.getItemTransportSpeed() * pPartialTick);
			
			for (ItemOnBelt item : conveyor.getItems()) {
				
				Vec3f insertionDirection = item.insertedFrom;
				PathTransfom transform = new PathTransfom();
				
				// horizontal motion of items along the belt
				transform
					.at(-2.0F)
						.translate(insertionDirection.mul(-1.0F))
					.between(-2.0F, 0.0F)
						.translate(insertionDirection.mul(1.0F))
					.between(0.0F, 2.0F)
						.translate(beltDirection);
				
//				if (insertionDirection.y == 0 && beltDirection.y == 0) {
//					
//					// input horizontal to horizontal
//					
//				} else 
				if (insertionDirection.y == 0) {
					
					// input horizontal to diagonal
					transform
						.between(-0.5F, +0.5F).rotate(new Vec3f(0, 0, 0), new Vec3f(beltDirection.z, 0, beltDirection.x).mul((float) Math.PI / 4 * beltDirection.y));
					
				} else if (beltDirection.y == 0) {
					
					// input diagonal to horizontal
					transform
						.at(-2F).rotate(new Vec3f(0, 0, 0), new Vec3f(beltDirection.z, 0, beltDirection.x).mul((float) Math.PI / 4 * insertionDirection.y))
						.between(-0.5F, +0.5F).rotate(new Vec3f(0, 0, 0), new Vec3f(beltDirection.z, 0, beltDirection.x).mul((float) -Math.PI / 4 * insertionDirection.y));
					
				} else {

					// input diagonal to diagonal
					transform
						.at(-2F).rotate(new Vec3f(0, 0, 0), new Vec3f(beltDirection.z, 0, beltDirection.x).mul((float) Math.PI / 4 * beltDirection.y));
					
				}

				Vec3f va = insertionDirection.length() > 0 ? insertionDirection.mul(1F, 0F, 1F).normalize() : new Vec3f();
				Vec3f v1 = new Vec3f(va.mul(item.position).z, 0F, va.mul(item.position).x);
				Vec3f vb = beltDirection.length() > 0 ? beltDirection.mul(1F, 0F, 1F).normalize() : new Vec3f();
				Vec3f v2 = new Vec3f(vb.mul(item.position).z, 0F, vb.mul(item.position).x);
				
				// additional offsets for each item
				transform
					.at(-2.0F)
						.translate(v1)
						.between(-0.5F, 0.5F)
						.translate(v2.sub(v1))
					.at(-2.0F)
						.rotate(0F, 0F, 0F, 0F, (float) Math.toRadians(item.rotation), 0F);
				
				pPoseStack.pushPose();
				
				transform.transform(pPoseStack, item.offset + (item.isClogged ? 0F : patialMotion));
				
				itemDispatcher.get().renderStatic(item.stack, ItemDisplayContext.GROUND, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, conveyor.getLevel(), 0);
				
				pPoseStack.popPose();
				
			}
			
			pPoseStack.popPose();
			
		}
		
	}
	
}
