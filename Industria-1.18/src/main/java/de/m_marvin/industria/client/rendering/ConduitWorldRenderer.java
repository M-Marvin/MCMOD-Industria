package de.m_marvin.industria.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.conduits.Conduit.ConduitType;
import de.m_marvin.industria.registries.Conduits;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.conduit.ConduitHandlerCapability;
import de.m_marvin.industria.util.conduit.PlacedConduit;
import de.m_marvin.industria.util.unifiedvectors.Vec3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE,modid=Industria.MODID, value=Dist.CLIENT)
public class ConduitWorldRenderer {
	
	public static final int TEXTURE_MAP_SIZE = 64;
	
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onWorldRenderLast(RenderLevelLastEvent event) {
		
		
		MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
		PoseStack matrixStack = event.getPoseStack();
		ClientLevel level = Minecraft.getInstance().level;
		
		RenderSystem.enableDepthTest();
		
		Vec3 offset = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		matrixStack.translate(-offset.x, -offset.y, -offset.z);
		
		drawConduits(matrixStack, source, level, event.getPartialTick());
		
		source.endBatch();
		
		RenderSystem.disableDepthTest();
		
	}
	
	@SuppressWarnings("resource")
	public static void drawConduits(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, float partialTicks) {
		
		LazyOptional<ConduitHandlerCapability> optionalConduitHolder = clientLevel.getCapability(ModCapabilities.CONDUIT_HANDLER_CAPABILITY);
		if (optionalConduitHolder.isPresent()) {
			
			ConduitHandlerCapability conduitHolder = optionalConduitHolder.resolve().get();
			
			// Sort for type
			for (Conduit conduitType : Conduits.CONDUITS_REGISTRY.get()) {
				
				ResourceLocation texturePath = conduitType.getTextureLoc();
				VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(texturePath));
				
				for (PlacedConduit conduit : conduitHolder.getConduits()) {
					
					if (conduit.getConduit() == conduitType) {
						
						BlockPos nodeApos = conduit.getNodeA();
						BlockPos nodeBpos = conduit.getNodeB();
						BlockPos cornerMin = UtilityHelper.getMinCorner(nodeApos, nodeBpos);
						
						double distancaA = Minecraft.getInstance().player.blockPosition().distSqr(nodeApos);
						double distancaB = Minecraft.getInstance().player.blockPosition().distSqr(nodeBpos);
						double distance = Math.sqrt((distancaA + distancaB) / 2);
						int renderDistance = Minecraft.getInstance().options.renderDistance * 16;
						
						if (distance < renderDistance * renderDistance) conduitModel(clientLevel, vertexConsumer, matrixStack, conduit, cornerMin, partialTicks);
						
					}
					
				}
				
			}
			
		}
		
	}
	
	public static void conduitModel(ClientLevel clientLevel, VertexConsumer vertexConsumer, PoseStack poseStack, PlacedConduit conduit, BlockPos cornerMin, float partialTicks) {
		
		poseStack.pushPose();
		
		poseStack.translate(cornerMin.getX(), cornerMin.getY(), cornerMin.getZ());
		
		ConduitShape shape = conduit.getShape();
		ConduitType type = conduit.getConduit().getConduitType();
		
		if (shape != null) {
			
			int size = type.getThickness();
			int blockLight = 0;
			int skyLight = 0;
			
			// Pre-iteration for lightning
			for (int i = shape.nodes.length - 1; i > 0; i--) {
				
				Vec3f nodeB = shape.nodes[i - 1];
				Vec3f nodeA = shape.nodes[i - 0];
				
				if (i == shape.nodes.length - 1) {
					BlockPos nodeBlockPos = new BlockPos(nodeA.x, nodeA.y + 0.1F, nodeA.z).offset(cornerMin);
					blockLight = clientLevel.getLightEmission(nodeBlockPos);
					skyLight = clientLevel.getBrightness(LightLayer.SKY, nodeBlockPos);
				}
				BlockPos nodeBlockPos = new BlockPos(nodeB.x, nodeB.y + 0.1F, nodeB.z).offset(cornerMin);
				blockLight = (clientLevel.getLightEmission(nodeBlockPos) * 1 + blockLight * 1) / 2;
				skyLight = (clientLevel.getBrightness(LightLayer.SKY, nodeBlockPos) * 1 + skyLight * 1) / 2;
				
			}
			
			float lengthOffset = 0;
			
			for (int i = 1; i < shape.nodes.length; i++) {
				
				Vec3f nodeA = shape.nodes[i - 1];
				Vec3f nodeB = shape.nodes[i - 0];
				
				if (i == 1) {
					BlockPos nodeBlockPos = new BlockPos(nodeA.x, nodeA.y + 0.1F, nodeA.z).offset(cornerMin);
					blockLight = (clientLevel.getLightEmission(nodeBlockPos) * 1 + blockLight * 2) / 3;
					skyLight = (clientLevel.getBrightness(LightLayer.SKY, nodeBlockPos) * 1 + skyLight * 2) / 3;
				}
				BlockPos nodeBlockPos = new BlockPos(nodeB.x, nodeB.y + 0.1F, nodeB.z).offset(cornerMin);
				blockLight = (clientLevel.getLightEmission(nodeBlockPos) * 1 + blockLight * 2) / 3;
				skyLight = (clientLevel.getBrightness(LightLayer.SKY, nodeBlockPos) * 1 + skyLight * 2) / 3;
				
				int packedLight = LightTexture.pack(blockLight, skyLight);
				int segmentColor = conduit.getConduit().getColorAt(clientLevel, nodeA, conduit);
				
				Vec3f nodeAinterpolated = shape.lastPos[i - 1].getInterpolated(nodeA, partialTicks);
				Vec3f nodeBinterpolated = shape.lastPos[i - 0].getInterpolated(nodeB, partialTicks);
				
				lengthOffset += wireModel(vertexConsumer, poseStack, segmentColor, packedLight, nodeAinterpolated, nodeBinterpolated, size, lengthOffset);
				
			}
			
		}
		
		poseStack.popPose();
		
	}
	
	public static float wireModel(VertexConsumer vertexConsumer, PoseStack poseStack, int color, int packedLight, Vec3f start, Vec3f end, int width, float lengthOffset) {
		
		Vec3f lineVec = end.copy().sub(start);
		Vec3f lineNormal = lineVec.copy();
		lineNormal.safeNormalize();
		
		float length = (float) lineVec.length();
		Quaternion rotation = lineNormal.rotationQuatFromDirection(new Vec3f(0, 0, 1)); // Default model orientation positive Z
		
		poseStack.pushPose();
		poseStack.translate(start.x, start.y, start.z);
		poseStack.mulPose(rotation);
		
		wirePart(vertexConsumer, poseStack, color, packedLight, length, width / 16F, lengthOffset);
		
		poseStack.popPose();
		
		return length;
		
	}
	
	public static void wirePart(VertexConsumer vertexConsumer, PoseStack poseStack, int color, int packedLight, float length, float width, float lengthOffest) {
		
		float fwh = width / 2;
		
		Matrix3f normal = poseStack.last().normal();
		Matrix4f pose = poseStack.last().pose();
		
		float fw = width * 16;
		float fl = length * 16;
		
		float uvX0 = lengthOffest * 16 / TEXTURE_MAP_SIZE;
		float uvYb0 = 0;
		float uvYt0 = fw * 2 / TEXTURE_MAP_SIZE;
		float uvYe0 = fw * 1 / TEXTURE_MAP_SIZE;
		float uvYw0 = fw * 3 / TEXTURE_MAP_SIZE;
		float uvX1 = uvX0 + fl / TEXTURE_MAP_SIZE;
		float uvYb1 = fw * 1 / TEXTURE_MAP_SIZE;
		float uvYt1 = fw * 3 / TEXTURE_MAP_SIZE;
		float uvYe1 = fw * 2 / TEXTURE_MAP_SIZE;
		float uvYw1 = fw * 4 / TEXTURE_MAP_SIZE;
		float uvEXn0 = 0;
		float uvEXs0 = fw * 1 / TEXTURE_MAP_SIZE;
		float uvEXn1 = fw * 1 / TEXTURE_MAP_SIZE;
		float uvEXs1 = fw * 2 / TEXTURE_MAP_SIZE;
		float uvEY0 = fw * 4 / TEXTURE_MAP_SIZE;
		float uvEY1 = fw * 5 / TEXTURE_MAP_SIZE;
		
		// Bottom
		vertex(vertexConsumer, pose, normal, -fwh, -fwh, 0, 		0, -1, 0, 	uvX0, uvYb0, packedLight, color);
		vertex(vertexConsumer, pose, normal, fwh, -fwh, 0, 			0, -1, 0, 	uvX0, uvYb1, packedLight, color);
		vertex(vertexConsumer, pose, normal, fwh, -fwh, length, 	0, -1, 0, 	uvX1, uvYb1, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fwh, -fwh, length, 	0, -1, 0, 	uvX1, uvYb0, packedLight, color);
		// Top
		vertex(vertexConsumer, pose, normal, fwh, fwh, 0, 			0, 1, 0, 	uvX0, uvYt0, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fwh, fwh, 0, 			0, 1, 0, 	uvX0, uvYt1, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fwh, fwh, length,		0, 1, 0, 	uvX1, uvYt1, packedLight, color);
		vertex(vertexConsumer, pose, normal, fwh, fwh, length, 		0, 1, 0, 	uvX1, uvYt0, packedLight, color);
		// East
		vertex(vertexConsumer, pose, normal, fwh, -fwh, 0, 			1, 0, 0, 	uvX0, uvYe0, packedLight, color);
		vertex(vertexConsumer, pose, normal, fwh, fwh, 0, 			1, 0, 0, 	uvX0, uvYe1, packedLight, color);
		vertex(vertexConsumer, pose, normal, fwh, fwh, length, 		1, 0, 0, 	uvX1, uvYe1, packedLight, color);
		vertex(vertexConsumer, pose, normal, fwh, -fwh, length, 	1, 0, 0, 	uvX1, uvYe0, packedLight, color);
		// West
		vertex(vertexConsumer, pose, normal, -fwh, fwh, 0, 			-1, 0, 0, 	uvX0, uvYw0, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fwh, -fwh, 0, 		-1, 0, 0, 	uvX0, uvYw1, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fwh, -fwh, length, 	-1, 0, 0, 	uvX1, uvYw1, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fwh, fwh, length, 	-1, 0, 0, 	uvX1, uvYw0, packedLight, color);
		// Front
		vertex(vertexConsumer, pose, normal, fwh, -fwh, length, 	0, 0, 1, 	uvEXn1, uvEY0, packedLight, color);
		vertex(vertexConsumer, pose, normal, fwh, fwh, length, 		0, 0, 1, 	uvEXn1, uvEY1, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fwh, fwh, length, 	0, 0, 1, 	uvEXn0, uvEY1, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fwh, -fwh, length, 	0, 0, 1, 	uvEXn0, uvEY0, packedLight, color);
		// Back
		vertex(vertexConsumer, pose, normal, fwh, fwh, 0, 			0, 0, -1, 	uvEXs1, uvEY1, packedLight, color);
		vertex(vertexConsumer, pose, normal, fwh, -fwh, 0, 			0, 0, -1, 	uvEXs1, uvEY0, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fwh, -fwh, 0, 		0, 0, -1, 	uvEXs0, uvEY0, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fwh, fwh, 0, 			0, 0, -1, 	uvEXs0, uvEY1, packedLight, color);
		
	}
	
	public static void vertex(VertexConsumer vertexBuilder, Matrix4f pose, Matrix3f normal, float x, float y, float z, float nx, float ny, float nz, float u, float v, int light, int value) {
		vertexBuilder.vertex(pose, x, y, z).color(value).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, nx, ny, nz).endVertex();
	}
	
}
