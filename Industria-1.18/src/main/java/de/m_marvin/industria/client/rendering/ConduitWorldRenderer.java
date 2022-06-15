package de.m_marvin.industria.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.conduits.Conduit.ConduitType;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.conduit.ConduitWorldStorageCapability;
import de.m_marvin.industria.util.conduit.PlacedConduit;
import de.m_marvin.industria.util.unifiedvectors.Vec3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE,modid=Industria.MODID)
public class ConduitWorldRenderer {
	
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onWorldRenderLast(RenderLevelLastEvent event) {
		
		MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
		PoseStack matrixStack = event.getPoseStack();
		ClientLevel level = Minecraft.getInstance().level;
		
		RenderSystem.enableDepthTest();

		Vec3 offset = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		matrixStack.translate(-offset.x, -offset.y, -offset.z);
		
		drawConduits(matrixStack, source, level);
		
		source.endBatch();
		
		RenderSystem.disableDepthTest();
		
	}
	
	public static void drawConduits(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel) {
		
		// TODO only for testing
		RenderSystem.disableCull();
		
		LazyOptional<ConduitWorldStorageCapability> optionalConduitHolder = clientLevel.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
		if (optionalConduitHolder.isPresent()) {
			
			ConduitWorldStorageCapability conduitHolder = optionalConduitHolder.resolve().get();
			
			for (PlacedConduit conduit : conduitHolder.getConduits()) {
				
				// TODO Render distance check
				
				matrixStack.pushPose();
				
				BlockPos nodeApos = conduit.getNodeA();
				BlockPos nodeBpos = conduit.getNodeB();
				BlockPos cornerMin = UtilityHelper.getMinCorner(nodeApos, nodeBpos);
				
				matrixStack.translate(cornerMin.getX(), cornerMin.getY(), cornerMin.getZ());
				
				ConduitShape shape = conduit.getShape();
				ConduitType type = conduit.getConduit().getConduitType();
				
				if (shape != null) {
					
					VertexConsumer vertexBuilder = bufferSource.getBuffer(RenderType.solid());
					
					int size = type.getThickness();
					
					for (int i = 1; i < shape.nodes.length; i++) {
						
						Vec3f nodeA = shape.nodes[i - 1];
						Vec3f nodeB = shape.nodes[i - 0];
						
						BlockPos nodeBlockPos = new BlockPos(nodeB.x, nodeB.y, nodeB.z).offset(cornerMin);
						int blockLight = clientLevel.getLightEmission(nodeBlockPos);
						int skyLight = clientLevel.getBrightness(LightLayer.SKY, nodeBlockPos);
						int packedLight = LightTexture.pack(blockLight, skyLight);
						
						wireModel(vertexBuilder, matrixStack, packedLight, nodeA, nodeB, size);
						
					}
					
				}
						
				matrixStack.popPose();
				
			}
			
		}
		
	}
		
	public static void wireModel(VertexConsumer vertexConsumer, PoseStack poseStack, int packedLight, Vec3f start, Vec3f end, int width) {
		
		Vec3f lineVec = end.copy().sub(start);
		Vec3f lineNormal = lineVec.copy();
		lineNormal.normalize();
		
		float length = (float) lineVec.length();
		Quaternion rotation = lineNormal.rotationQuatFromDirection(new Vec3f(0, 0, -1)); // Default model orientation negative Z
		
		poseStack.pushPose();
		poseStack.translate(start.x, start.y, start.z);
		poseStack.mulPose(rotation);
		
		wirePart(vertexConsumer, poseStack, packedLight, length, width / 16F);
		
		poseStack.popPose();
		
	}
	
	public static void wirePart(VertexConsumer vertexConsumer, PoseStack poseStack, int packedLight, float length, float width) {
		
		int color = ((255 & 0xFF) << 24) | ((0 & 0xFF) << 16) | ((128 & 0xFF) << 8) | ((255 & 0xFF) << 0);
		
		float fw = width / 2;
		
		Matrix3f normal = poseStack.last().normal();
		Matrix4f pose = poseStack.last().pose();
		
		// Botom
		vertex(vertexConsumer, pose, normal, -fw, -fw, 0, 0, 0, packedLight, color);
		vertex(vertexConsumer, pose, normal, fw, -fw, 0, 0, 0, packedLight, color);
		vertex(vertexConsumer, pose, normal, fw, -fw, -length, 0, 0, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fw, -fw, -length, 0, 0, packedLight, color);
		// Top
		vertex(vertexConsumer, pose, normal, -fw, fw, 0, 0, 0, packedLight, color);
		vertex(vertexConsumer, pose, normal, fw, fw, 0, 0, 0, packedLight, color);
		vertex(vertexConsumer, pose, normal, fw, fw, -length, 0, 0, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fw, fw, -length, 0, 0, packedLight, color);
		// East
		vertex(vertexConsumer, pose, normal, fw, -fw, 0, 0, 0, packedLight, color);
		vertex(vertexConsumer, pose, normal, fw, fw, 0, 0, 0, packedLight, color);
		vertex(vertexConsumer, pose, normal, fw, fw, -length, 0, 0, packedLight, color);
		vertex(vertexConsumer, pose, normal, fw, -fw, -length, 0, 0, packedLight, color);
		// West
		vertex(vertexConsumer, pose, normal, -fw, -fw, 0, 0, 0, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fw, fw, 0, 0, 0, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fw, fw, -length, 0, 0, packedLight, color);
		vertex(vertexConsumer, pose, normal, -fw, -fw, -length, 0, 0, packedLight, color);
		
	}
	
	public static void vertex(VertexConsumer vertexBuilder, Matrix4f pose, Matrix3f normal, float x, float y, float z, float u, float v, int light, int value) {
		vertexBuilder.vertex(pose, x, y, z).color(value).uv(u, v).uv2(light).normal(normal, 1, 1, 1).endVertex();
	}
	
}
