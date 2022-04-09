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
import de.m_marvin.industria.util.conduit.ConduitWorldStorageCapability;
import de.m_marvin.industria.util.conduit.IFlexibleConnection;
import de.m_marvin.industria.util.conduit.IFlexibleConnection.ConnectionPoint;
import de.m_marvin.industria.util.conduit.IFlexibleConnection.PlacedConduit;
import de.m_marvin.industria.util.unifiedvectors.Vec3f;
import de.m_marvin.industria.util.UtilityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
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

					Matrix4f matrix4f = matrixStack.last().pose();
					Matrix3f matrix3f = matrixStack.last().normal();
					VertexConsumer vertexBuilder = bufferSource.getBuffer(RenderType.lineStrip());

					int size = type.getThickness();
					
					for (int i = 0; i < shape.nodes.length; i++) {
						
						Vec3f node = shape.nodes[i];
						int light = LevelRenderer.getLightColor(clientLevel, cornerMin.offset(node.x, node.y, node.z));
						vertex(vertexBuilder, matrix4f, matrix3f, node.x, node.y, node.z, 0, 0, light, 255, 255, 255, 255);
						
					}
										
				}
				
//				float[] conduitNodes = conduit.getConduit().calculateShape(clientLevel, conduit);	
//				
//				if (conduitNodes.length > 1 && conduitNodes.length % 6 == 0) {
//					
//					for (int n = 0; n < conduitNodes.length; n += 6) {
//						
//						float x = conduitNodes[n + 0];
//						float y = conduitNodes[n + 1];
//						float z = conduitNodes[n + 2];
//						float rx = conduitNodes[n + 3];
//						float ry = conduitNodes[n + 4];
//						float rz = conduitNodes[n + 5];
//						
//						Vector3f nodeAtl = new Vector3f(x - size / 2, y + size / 2, z - size / 2);
//						Vector3f nodeAbl = new Vector3f(x - size / 2, y - size / 2, z - size / 2);
//						Vector3f nodeAtr = new Vector3f(x + size / 2, y + size / 2, z - size / 2);
//						Vector3f nodeAbr = new Vector3f(x + size / 2, y - size / 2, z - size / 2);
//						
////						Quaternion quaternion = Quaternion.fromXYZ(rx, ry, rz);
////						quaternion.;
////						FishingHookRenderer
//						
//						//System.out.println("TSET" + nodeApos + " " + nodeBpos);
//						
//						vertex(vertexBuilder, matrix4f, matrix3f, x, y, z, 0, 0, 0, 0, 0, 0, 255);
//						
////						int lightA = LevelRenderer.getLightColor(clientLevel, nodeApos);
////						int lightB = LevelRenderer.getLightColor(clientLevel, nodeBpos);
////						vertex(vertexBuilder, matrix4f, matrix3f, conduitEndA.x + 0, conduitEndA.y + 0.2F, conduitEndA.z + 0, 0, 0, lightA, 255, 255, 255, 255);
////						vertex(vertexBuilder, matrix4f, matrix3f, conduitEndA.x + 0, conduitEndA.y + 0, conduitEndA.z + 0, 0, 0, lightA, 255, 255, 255, 255);
////						vertex(vertexBuilder, matrix4f, matrix3f, conduitEndB.x + 0, conduitEndB.y + 0, conduitEndB.z + 0, 0, 0, lightB, 255, 255, 255, 255);
////						vertex(vertexBuilder, matrix4f, matrix3f, conduitEndB.x + 0, conduitEndB.y + 0.2F, conduitEndB.z + 0, 0, 0, lightB, 255, 255, 255, 255);
//						
//					}
//					
//				}
				
				// TODO Debug markers
//				vertex(vertexBuilder, matrix4f, matrix3f, conduitEndA.x + -0.5F, conduitEndA.y + 1, conduitEndA.z + -0.5F, 0, 0, lightB, 255, 0, 0, 255);
//				vertex(vertexBuilder, matrix4f, matrix3f, conduitEndA.x + 0.5F, conduitEndA.y + 1, conduitEndA.z + 0.5F, 0, 0, lightB, 255, 0, 0, 255);
//				vertex(vertexBuilder, matrix4f, matrix3f, conduitEndA.x + 0, conduitEndA.y + 0, conduitEndA.z + 0, 0, 0, lightB, 255, 0, 0, 255);
//				vertex(vertexBuilder, matrix4f, matrix3f, conduitEndA.x + 0, conduitEndA.y + 0, conduitEndA.z + 0, 0, 0, lightB, 255, 0, 0, 255);
//				vertex(vertexBuilder, matrix4f, matrix3f, conduitEndB.x + -0.5F, conduitEndB.y + 1, conduitEndB.z + -0.5F, 0, 0, lightB, 0, 255, 0, 255);
//				vertex(vertexBuilder, matrix4f, matrix3f, conduitEndB.x + 0.5F, conduitEndB.y + 1, conduitEndB.z + 0.5F, 0, 0, lightB, 0, 255, 0, 255);
//				vertex(vertexBuilder, matrix4f, matrix3f, conduitEndB.x + 0, conduitEndB.y + 0, conduitEndB.z + 0, 0, 0, lightB, 0, 255, 0, 255);
//				vertex(vertexBuilder, matrix4f, matrix3f, conduitEndB.x + 0, conduitEndB.y + 0, conduitEndB.z + 0, 0, 0, lightB, 0, 255, 0, 255);
								
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
