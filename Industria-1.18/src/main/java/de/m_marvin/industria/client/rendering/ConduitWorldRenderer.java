package de.m_marvin.industria.client.rendering;

import java.awt.Color;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.jozufozu.flywheel.repack.joml.Vector3f;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.registries.ModCapabilities;
import de.m_marvin.industria.util.IConduitHolder;
import de.m_marvin.industria.util.IFlexibleConnection;
import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.IFlexibleConnection.ConnectionPoint;
import de.m_marvin.industria.util.IFlexibleConnection.PlacedConduit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
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
		LazyOptional<IConduitHolder> optionalConduitHolder = clientLevel.getCapability(ModCapabilities.CONDUIT_HOLDER_CAPABILITY);
		if (optionalConduitHolder.isPresent()) {
			
			IConduitHolder conduitHolder = optionalConduitHolder.resolve().get();
			
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
				
				if (nodeAstate.getBlock() instanceof IFlexibleConnection && nodeBstate.getBlock() instanceof IFlexibleConnection) {
					
					ConnectionPoint pointA = ((IFlexibleConnection) nodeAstate.getBlock()).getConnectionPoints(clientLevel, nodeApos, nodeAstate)[conduit.getConnectionPointA()];
					ConnectionPoint pointB = ((IFlexibleConnection) nodeBstate.getBlock()).getConnectionPoints(clientLevel, nodeBpos, nodeBstate)[conduit.getConnectionPointB()];
					
					BlockPos bp1 = nodeApos.subtract(cornerMin);
					BlockPos bp2 = nodeBpos.subtract(cornerMin);
					Vector3f conduitEndA = new Vector3f(bp1.getX() + pointA.offset().x / 16F, bp1.getY() + pointA.offset().y / 16F, bp1.getZ() + pointA.offset().z / 16F);
					Vector3f conduitEndB = new Vector3f(bp2.getX() + pointB.offset().x / 16F, bp2.getY() + pointB.offset().y / 16F, bp2.getZ() + pointB.offset().z / 16F);
					
					Matrix4f matrix4f = matrixStack.last().pose();
					Matrix3f matrix3f = matrixStack.last().normal();
					VertexConsumer vertexBuilder = bufferSource.getBuffer(RenderType.solid());
					
					//System.out.println("TSET" + nodeApos + " " + nodeBpos);
					
					int lightA = LevelRenderer.getLightColor(clientLevel, nodeApos);
					int lightB = LevelRenderer.getLightColor(clientLevel, nodeBpos);
					vertex(vertexBuilder, matrix4f, matrix3f, conduitEndA.x + 0, conduitEndA.y + 0.2F, conduitEndA.z + 0, 0, 0, lightA, 255, 255, 255, 255);
					vertex(vertexBuilder, matrix4f, matrix3f, conduitEndA.x + 0, conduitEndA.y + 0, conduitEndA.z + 0, 0, 0, lightA, 255, 255, 255, 255);
					vertex(vertexBuilder, matrix4f, matrix3f, conduitEndB.x + 0, conduitEndB.y + 0, conduitEndB.z + 0, 0, 0, lightB, 255, 255, 255, 255);
					vertex(vertexBuilder, matrix4f, matrix3f, conduitEndB.x + 0, conduitEndB.y + 0.2F, conduitEndB.z + 0, 0, 0, lightB, 255, 255, 255, 255);
					
					// TODO Debug markers
					vertex(vertexBuilder, matrix4f, matrix3f, conduitEndA.x + -0.5F, conduitEndA.y + 1, conduitEndA.z + -0.5F, 0, 0, lightB, 255, 0, 0, 255);
					vertex(vertexBuilder, matrix4f, matrix3f, conduitEndA.x + 0.5F, conduitEndA.y + 1, conduitEndA.z + 0.5F, 0, 0, lightB, 255, 0, 0, 255);
					vertex(vertexBuilder, matrix4f, matrix3f, conduitEndA.x + 0, conduitEndA.y + 0, conduitEndA.z + 0, 0, 0, lightB, 255, 0, 0, 255);
					vertex(vertexBuilder, matrix4f, matrix3f, conduitEndA.x + 0, conduitEndA.y + 0, conduitEndA.z + 0, 0, 0, lightB, 255, 0, 0, 255);
					vertex(vertexBuilder, matrix4f, matrix3f, conduitEndB.x + -0.5F, conduitEndB.y + 1, conduitEndB.z + -0.5F, 0, 0, lightB, 0, 255, 0, 255);
					vertex(vertexBuilder, matrix4f, matrix3f, conduitEndB.x + 0.5F, conduitEndB.y + 1, conduitEndB.z + 0.5F, 0, 0, lightB, 0, 255, 0, 255);
					vertex(vertexBuilder, matrix4f, matrix3f, conduitEndB.x + 0, conduitEndB.y + 0, conduitEndB.z + 0, 0, 0, lightB, 0, 255, 0, 255);
					vertex(vertexBuilder, matrix4f, matrix3f, conduitEndB.x + 0, conduitEndB.y + 0, conduitEndB.z + 0, 0, 0, lightB, 0, 255, 0, 255);
					
				}
				
				
//				matrixStack.pushPose();
//				matrixStack.translate(lowCorner.getX(), lowCorner.getY(), lowCorner.getZ());
//				Matrix4f matrix4f = matrixStack.last().pose();
//				Matrix3f matrix3f = matrixStack.last().normal();
//				VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.solid());
//				int packedLight = LevelRenderer.getLightColor(clientLevel, middlePos);
//				// Render marker
//				int color = new Color(255, 255, 255).getRGB();
//				vertexConsumer.vertex(matrix4f, -0.5F, 1, 0).color(color).uv(0, 0).uv2(packedLight).normal(matrix3f, 1, 1, 1).endVertex();
//				vertexConsumer.vertex(matrix4f, 0.5F, 1, 0).color(color).uv(0, 0).uv2(packedLight).normal(matrix3f, 1, 1, 1).endVertex();
//				vertexConsumer.vertex(matrix4f, 0, 0, 0).color(color).uv(0, 0).uv2(packedLight).normal(matrix3f, 1, 1, 1).endVertex();
//				vertexConsumer.vertex(matrix4f, 0, 0, 0).color(color).uv(0, 0).uv2(packedLight).normal(matrix3f, 1, 1, 1).endVertex();
//				matrixStack.popPose();
				
				
				matrixStack.popPose();
				
			}
			
		}
		
		BlockPos pos = new BlockPos(-190,-60,-554);
		
		matrixStack.pushPose();
		
		matrixStack.popPose();
		
	}
	
	public static void vertex(VertexConsumer vertexBuilder, Matrix4f pose, Matrix3f normal, float x, float y, float z, float u, float v, int light, int r, int g, int b, int a) {
		int value = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
		vertexBuilder.vertex(pose, x, y, z).color(value).uv(u, v).uv2(light).normal(normal, 1, 1, 1).endVertex();
	}
	
}
