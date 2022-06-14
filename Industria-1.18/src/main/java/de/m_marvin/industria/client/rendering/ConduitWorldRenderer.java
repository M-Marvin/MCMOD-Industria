package de.m_marvin.industria.client.rendering;

import javax.swing.plaf.basic.BasicDesktopIconUI;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.conduits.Conduit.ConduitType;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.registries.ModRenderTypes;
import de.m_marvin.industria.util.conduit.ConduitWorldStorageCapability;
import de.m_marvin.industria.util.conduit.IWireConnector;
import de.m_marvin.industria.util.conduit.IWireConnector.ConnectionPoint;
import de.m_marvin.industria.util.conduit.PlacedConduit;
import de.m_marvin.industria.util.unifiedvectors.Vec3f;
import de.m_marvin.industria.util.UtilityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
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
		
		RenderSystem.enableDepthTest();

		Vec3 offset = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		matrixStack.translate(-offset.x, -offset.y, -offset.z);
		
		drawConduits(matrixStack, source);
		
		source.endBatch();
		
		RenderSystem.disableDepthTest();
		
	}
	
	public static void drawConduits(PoseStack matrixStack, MultiBufferSource bufferSource) {
		
		// TODO only for testing
		RenderSystem.disableCull();
		
		ClientLevel clientLevel = Minecraft.getInstance().level;
		LazyOptional<ConduitWorldStorageCapability> optionalConduitHolder = clientLevel.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
		if (optionalConduitHolder.isPresent()) {
			
			ConduitWorldStorageCapability conduitHolder = optionalConduitHolder.resolve().get();
			
			for (PlacedConduit conduit : conduitHolder.getConduits()) {
				
				BlockPos middlePos = UtilityHelper.getMiddle(conduit.getNodeA(), conduit.getNodeB());
				
				// TODO Render distance check
				
				matrixStack.pushPose();
				
				BlockPos nodeApos = conduit.getNodeA();
				BlockState nodeAstate = clientLevel.getBlockState(nodeApos);
				BlockPos nodeBpos = conduit.getNodeB();
				BlockState nodeBstate = clientLevel.getBlockState(nodeBpos);
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
						
						drawModelLine(Minecraft.getInstance().level, cornerMin, vertexBuilder, matrixStack, nodeA, nodeB); // TODO Debugging only
						
					}
															
				}
								
				matrixStack.popPose();
				
			}
			
		}
		
	}

	
	public static void drawModelLine(Level level, BlockPos worldPos, VertexConsumer vertexConsumer, PoseStack poseStack, Vec3f start, Vec3f end) {
		
		Vec3f lineVec = end.copy().sub(start);
		Vec3f lineNormal = lineVec.copy();
		lineNormal.normalize();
		
		float length = (float) lineVec.length();
		Quaternion rotation = lineNormal.rotationQuatFromDirection(new Vec3f(0, 0, -1));
		
		poseStack.pushPose();
		poseStack.translate(start.x, start.y, start.z);
		poseStack.mulPose(rotation);
				
		wirePart(level, worldPos, vertexConsumer, poseStack, start, length, 1 / 16F);
		
		poseStack.popPose();
		
	}
	
	public static void wirePart(Level level, BlockPos worldPos, VertexConsumer vertexConsumer, PoseStack poseStack, Vec3f pos, float length, float width) {
		
		int color = ((255 & 0xFF) << 24) | ((0 & 0xFF) << 16) | ((128 & 0xFF) << 8) | ((255 & 0xFF) << 0);
		
		BlockPos blockpos = new BlockPos(pos.x, pos.y, pos.z).offset(worldPos);
		int brightness = level.getBrightness(LightLayer.SKY, blockpos);
		int lightlevel = level.getLightEmission(blockpos);
		int packegLight = LightTexture.pack(lightlevel, brightness);
		
		float fw = width / 2;
		
		Matrix3f normal = poseStack.last().normal();
		Matrix4f pose = poseStack.last().pose();
		
		// Botom
		vertex(vertexConsumer, pose, normal, -fw, -fw, 0, 0, 0, packegLight, color);
		vertex(vertexConsumer, pose, normal, fw, -fw, 0, 0, 0, packegLight, color);
		vertex(vertexConsumer, pose, normal, fw, -fw, -length, 0, 0, packegLight, color);
		vertex(vertexConsumer, pose, normal, -fw, -fw, -length, 0, 0, packegLight, color);
		// Top
		vertex(vertexConsumer, pose, normal, -fw, fw, 0, 0, 0, packegLight, color);
		vertex(vertexConsumer, pose, normal, fw, fw, 0, 0, 0, packegLight, color);
		vertex(vertexConsumer, pose, normal, fw, fw, -length, 0, 0, packegLight, color);
		vertex(vertexConsumer, pose, normal, -fw, fw, -length, 0, 0, packegLight, color);
		// East
		vertex(vertexConsumer, pose, normal, fw, -fw, 0, 0, 0, packegLight, color);
		vertex(vertexConsumer, pose, normal, fw, fw, 0, 0, 0, packegLight, color);
		vertex(vertexConsumer, pose, normal, fw, fw, -length, 0, 0, packegLight, color);
		vertex(vertexConsumer, pose, normal, fw, -fw, -length, 0, 0, packegLight, color);
		// West
		vertex(vertexConsumer, pose, normal, -fw, -fw, 0, 0, 0, packegLight, color);
		vertex(vertexConsumer, pose, normal, -fw, fw, 0, 0, 0, packegLight, color);
		vertex(vertexConsumer, pose, normal, -fw, fw, -length, 0, 0, packegLight, color);
		vertex(vertexConsumer, pose, normal, -fw, -fw, -length, 0, 0, packegLight, color);
		
	}
	
	public static void vertex(VertexConsumer vertexBuilder, Matrix4f pose, Matrix3f normal, float x, float y, float z, float u, float v, int light, int value) {
		vertexBuilder.vertex(pose, x, y, z).color(value).uv(u, v).uv2(light).normal(normal, 1, 1, 1).endVertex();
	}

	public static void vertex(VertexConsumer vertexBuilder, Matrix4f pose, Matrix3f normal, float x, float y, float z, float u, float v, int light, int r, int g, int b, int a) {
		int value = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
		vertexBuilder.vertex(pose, x, y, z).color(value).uv(u, v).uv2(light).normal(normal, 1, 1, 1).endVertex();
	}
	
}
