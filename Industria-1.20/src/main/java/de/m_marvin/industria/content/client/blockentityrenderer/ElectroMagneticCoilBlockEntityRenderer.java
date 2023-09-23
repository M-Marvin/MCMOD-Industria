package de.m_marvin.industria.content.client.blockentityrenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import de.m_marvin.industria.content.blockentities.machines.ElectroMagneticCoilBlockEntity;
import de.m_marvin.industria.core.client.conduits.ConduitRenderer;
import de.m_marvin.industria.core.client.conduits.ConduitTextureManager;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;

public class ElectroMagneticCoilBlockEntityRenderer implements BlockEntityRenderer<ElectroMagneticCoilBlockEntity> {

	public ElectroMagneticCoilBlockEntityRenderer(Context context) {
		
	}
	
	@Override
	public void render(ElectroMagneticCoilBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
		
		if (pBlockEntity.isMaster()) {
			
			BlockPos minPos = pBlockEntity.getMinPos();
			BlockPos maxPos = pBlockEntity.getMaxPos();
			BlockPos size = maxPos.subtract(minPos).offset(1, 1, 1);
			int wires = pBlockEntity.getWires().getCount();
			Conduit type = pBlockEntity.getWireConduit();
			
			if (wires == 0) return;

			pPoseStack.pushPose();
			
			float height = size.getY() - 4F * 0.0625F;
			float width = size.getX() - 4F * 0.0625F;
			float depth = size.getZ() - 4F * 0.0625F;
			switch (pBlockEntity.getAxis()) {
			case X:				
				height = size.getX() - 4F * 0.0625F;
				width = size.getY() - 4F * 0.0625F;
				depth = size.getZ() - 4F * 0.0625F;
				pPoseStack.rotateAround(Axis.ZP.rotationDegrees(90), 0F, 0F, 0F);
				pPoseStack.translate(0, -size.getX(), 0);
				break;
			case Z:				
				height = size.getZ() - 4F * 0.0625F;
				width = size.getX() - 4F * 0.0625F;
				depth = size.getY() - 4F * 0.0625F;
				pPoseStack.rotateAround(Axis.XP.rotationDegrees(-90), 0F, 0F, 0F);
				pPoseStack.translate(0, -size.getZ(), 0);
				break;
			default:
			}
			
			int windings = pBlockEntity.getWindings();
			float windingHeight = height / windings;
			
			VertexConsumer vertexBuffer = pBuffer.getBuffer(RenderType.entitySolid(ConduitTextureManager.LOCATION_CONDUITS));
			TextureAtlasSprite texture = ConduitTextureManager.getInstance().get(type);
			int thickness = type.getConduitType().getThickness();
			
			for (int i = 0; i < windings; i++) {

				Vec3d p1 = new Vec3d(2F * 0.0625F, 			2 * 0.0625F + (i + 0) * windingHeight, 		2F * 0.0625F);
				Vec3d p2 = new Vec3d(2F * 0.0625F + width, 	2 * 0.0625F + (i + 0.25F) * windingHeight, 	2F * 0.0625F);
				Vec3d p3 = new Vec3d(2F * 0.0625F + width, 	2 * 0.0625F + (i + 0.5F) * windingHeight, 	2F * 0.0625F + depth);
				Vec3d p4 = new Vec3d(2F * 0.0625F, 			2 * 0.0625F + (i + 0.75F) * windingHeight,	2F * 0.0625F + depth);
				Vec3d p5 = new Vec3d(2F * 0.0625F, 			2 * 0.0625F + (i + 1F) * windingHeight, 	2F * 0.0625F);

				ConduitRenderer.drawConduitSegment(vertexBuffer, pPoseStack, 0xFFFFFF, pPackedLight, p1, p2, thickness, 0, texture);
				ConduitRenderer.drawConduitSegment(vertexBuffer, pPoseStack, 0xFFFFFF, pPackedLight, p2, p3, thickness, 0, texture);
				ConduitRenderer.drawConduitSegment(vertexBuffer, pPoseStack, 0xFFFFFF, pPackedLight, p3, p4, thickness, 0, texture);
				ConduitRenderer.drawConduitSegment(vertexBuffer, pPoseStack, 0xFFFFFF, pPackedLight, p4, p5, thickness, 0, texture);
				
			}
			
			pPoseStack.popPose();
			
		}
		
	}

}