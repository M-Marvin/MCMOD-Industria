package de.m_marvin.industria.core.client.conduits;

import java.awt.Color;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.ConduitHandlerCapability;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitType;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.registries.Conduits;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE,modid=IndustriaCore.MODID, value=Dist.CLIENT)
public class ConduitWorldRenderer {
	
	public static final int TEXTURE_MAP_SIZE = 64;
	
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onWorldRender(RenderLevelStageEvent event) {
		
		if (event.getStage() == Stage.AFTER_PARTICLES) { // AFTER_SOLID_BLOCKS // TODO

			if (!Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
				
				MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
				PoseStack matrixStack = event.getPoseStack();
				ClientLevel level = Minecraft.getInstance().level;
				
				RenderSystem.enableDepthTest();
				
				Vec3 offset = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
				matrixStack.pushPose();
				matrixStack.translate(-offset.x, -offset.y, -offset.z);
				drawConduits(matrixStack, source, level, event.getPartialTick());
				source.endBatch();
				matrixStack.popPose();
				
				RenderSystem.disableDepthTest();
				
			}
			
		//} else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS) {
			
			if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
				
				MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
				PoseStack matrixStack = event.getPoseStack();
				ClientLevel level = Minecraft.getInstance().level;
				
				RenderSystem.enableDepthTest();
				
				Vec3 offset = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
				matrixStack.pushPose();
				matrixStack.translate(-offset.x, -offset.y, -offset.z);
				drawDebugConduits(matrixStack, source, level, event.getPartialTick());
				drawDebugNodes(matrixStack, source, level, Minecraft.getInstance().player, event.getPartialTick());
				source.endBatch();
				matrixStack.popPose();
				
				RenderSystem.disableDepthTest();
				
			}
			
		}
		
	}
	
	public static void drawDebugConduits(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, float partialTicks) {
		
		LazyOptional<ConduitHandlerCapability> optionalConduitHolder = clientLevel.getCapability(Capabilities.CONDUIT_HANDLER_CAPABILITY);
		if (optionalConduitHolder.isPresent()) {
			
			ConduitHandlerCapability conduitHolder = optionalConduitHolder.resolve().get();
			
			for (ConduitEntity conduit : conduitHolder.getConduits()) {
				
				drawConduitInfo(matrixStack, bufferSource, clientLevel, partialTicks, conduit);
				
			}
			
		}
		
	}
	
	public static void drawDebugNodes(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, LocalPlayer player, float partialTicks) {
		
		HitResult result = GameUtility.raycast(clientLevel, Vec3d.fromVec(player.getEyePosition()), Vec3d.fromVec(player.getViewVector(partialTicks)), player.getReachDistance());
		
		if (result.getType() == Type.BLOCK) {
			
			BlockPos targetBlock = ((BlockHitResult) result).getBlockPos();
			BlockState blockState = clientLevel.getBlockState(targetBlock);
			
			if (blockState.getBlock() instanceof IConduitConnector connector) {
				
				int nodeId = 0;
				for (ConduitNode node : connector.getConduitNodes(clientLevel, targetBlock, blockState)) {
					drawNodeInfo(matrixStack, bufferSource, clientLevel, partialTicks, targetBlock, node, nodeId++);
				}
				
			}
			
		}
		
	}
	
	public static void drawConduitInfo(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel level, float partialTick, ConduitEntity conduit) {
		BlockState nodeAstate = level.getBlockState(conduit.getPosition().getNodeApos());
		BlockState nodeBstate = level.getBlockState(conduit.getPosition().getNodeBpos());
		if (nodeAstate.getBlock() instanceof IConduitConnector nodeAconnector && nodeBstate.getBlock() instanceof IConduitConnector nodeBconnector) {
			ConduitNode nodeA = nodeAconnector.getConduitNode(level, conduit.getPosition().getNodeApos(), nodeAstate, conduit.getPosition().getNodeAid());
			ConduitNode nodeB = nodeBconnector.getConduitNode(level, conduit.getPosition().getNodeBpos(), nodeBstate, conduit.getPosition().getNodeBid());
			if (nodeA != null && nodeB != null) {
				Vec3d nodeAworldPosition = nodeA.getWorldPosition(level, conduit.getPosition().getNodeApos());
				Vec3d nodeBworldPosition = nodeB.getWorldPosition(level, conduit.getPosition().getNodeBpos());
				
				Vec4f color = new Vec4f(0.5F, 1.0F, 0.5F, 1F); // TODO Color representing physic-mode
				
				Vec3f normal = new Vec3f(nodeAworldPosition.sub(nodeBworldPosition)).normalize();
				Vec3d nodeOrigin = MathUtility.getMinCorner(nodeAworldPosition, nodeBworldPosition).sub(0.5, 0.5, 0.5);
				
				VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lines());
				
				for (int i = 0; i < conduit.getShape().nodes.length - 1; i++) {
					
					Vec3d node1 = conduit.getShape().nodes[i].add(nodeOrigin);
					Vec3d node2 = conduit.getShape().nodes[i + 1].add(nodeOrigin);
					
					vertexconsumer.vertex(matrixStack.last().pose(), (float) node1.x, (float) node1.y, (float) node1.z).color(color.x * 0.5F, color.y * 0.5F, color.z * 0.5F, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
					vertexconsumer.vertex(matrixStack.last().pose(), (float) node2.x, (float) node2.y, (float) node2.z).color(color.x * 0.5F, color.y * 0.5F, color.z * 0.5F, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
					
					
				}
				vertexconsumer.vertex(matrixStack.last().pose(), (float) nodeAworldPosition.x, (float) nodeAworldPosition.y, (float) nodeAworldPosition.z).color(color.x, color.y, color.z, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
				vertexconsumer.vertex(matrixStack.last().pose(), (float) nodeBworldPosition.x, (float) nodeBworldPosition.y, (float) nodeBworldPosition.z).color(color.x, color.y, color.z, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
				
				drawNodeInfo(matrixStack, bufferSource, level, partialTick, conduit.getPosition().getNodeApos(), nodeA, conduit.getPosition().getNodeAid());
				
				color = new Vec4f(1.0F, 0.0F, 0.0F, 1F);
				
				// TODO
//				Vec3d nodeAForceVecEnd = nodeAworldPosition.add(conduit.getShape().forceNodeA);
//				vertexconsumer.vertex(matrixStack.last().pose(), (float) nodeAworldPosition.x, (float) nodeAworldPosition.y + 0.2F, (float) nodeAworldPosition.z).color(color.x, color.y, color.z, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
//				vertexconsumer.vertex(matrixStack.last().pose(), (float) nodeAForceVecEnd.x, (float) nodeAForceVecEnd.y + 0.2F, (float) nodeAForceVecEnd.z).color(color.x, color.y, color.z, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
				
				drawNodeInfo(matrixStack, bufferSource, level, partialTick, conduit.getPosition().getNodeBpos(), nodeB, conduit.getPosition().getNodeBid());

//				Vec3d nodeBForceVecEnd = nodeBworldPosition.add(conduit.getShape().forceNodeB);
//				vertexconsumer.vertex(matrixStack.last().pose(), (float) nodeBworldPosition.x, (float) nodeBworldPosition.y + 0.2F, (float) nodeBworldPosition.z).color(color.x, color.y, color.z, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
//				vertexconsumer.vertex(matrixStack.last().pose(), (float) nodeBForceVecEnd.x, (float) nodeBForceVecEnd.y + 0.2F, (float) nodeBForceVecEnd.z).color(color.x, color.y, color.z, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
				
			}
		}
		
	}
	
	@SuppressWarnings("resource")
	public static void drawNodeInfo(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel level, float partialTicks, BlockPos pos, ConduitNode node, int nodeId) {
		
		
		Vec3d position = node.getWorldPosition(level, pos);
		Vec4f color = node.getType().getColor();
		double halfSize = 1.5 / 16.0;
		Vec3d boxMin = position.sub(halfSize, halfSize, halfSize);
		Vec3d boxMax = position.add(halfSize, halfSize, halfSize);
		VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lines());
		LevelRenderer.renderLineBox(
        		matrixStack, vertexconsumer, 
        		boxMin.x, boxMin.y, boxMin.z, 
        		boxMax.x, boxMax.y, boxMax.z, 
        		color.x, color.y, color.z, color.w,
        		color.x, color.y, color.z);
		
		matrixStack.pushPose();
		matrixStack.translate(position.x, position.y + 0.2, position.z);
		matrixStack.mulPose(Vector3f.YN.rotationDegrees(Minecraft.getInstance().player.yHeadRot + 180));
		matrixStack.translate(0, 0, 4 * 0.0635F);
		matrixStack.scale(0.01F, -0.01F, 0.01F);
		
		String info = "Id:" + nodeId;
		float width = Minecraft.getInstance().font.width(info);
		Minecraft.getInstance().font.draw(matrixStack, info, -width / 2, 0, new Color(color.x, color.y, color.z, color.w).getRGB());
		
		matrixStack.popPose();
		
	}
	
	@SuppressWarnings("resource")
	public static void drawConduits(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, float partialTicks) {
		
		LazyOptional<ConduitHandlerCapability> optionalConduitHolder = clientLevel.getCapability(Capabilities.CONDUIT_HANDLER_CAPABILITY);
		if (optionalConduitHolder.isPresent()) {
			
			ConduitHandlerCapability conduitHolder = optionalConduitHolder.resolve().get();
			
			// Sort for type
			for (Conduit conduitType : Conduits.CONDUITS_REGISTRY.get()) {
				
				ResourceLocation texturePath = conduitType.getTextureLoc();
				VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(texturePath));
				Vec3d playerPosition = Vec3d.fromVec(Minecraft.getInstance().player.position());
				
				for (ConduitEntity conduit : conduitHolder.getConduits()) {
					
					if (conduit.getConduit() == conduitType) {
						
						BlockState nodeAstate = clientLevel.getBlockState(conduit.getPosition().getNodeApos());
						BlockState nodeBstate = clientLevel.getBlockState(conduit.getPosition().getNodeBpos());
						if (nodeAstate.getBlock() instanceof IConduitConnector nodeAconnector && nodeBstate.getBlock() instanceof IConduitConnector nodeBconnector) {
							ConduitNode nodeA = nodeAconnector.getConduitNode(clientLevel, conduit.getPosition().getNodeApos(), nodeAstate, conduit.getPosition().getNodeAid());
							ConduitNode nodeB = nodeBconnector.getConduitNode(clientLevel, conduit.getPosition().getNodeBpos(), nodeBstate, conduit.getPosition().getNodeBid());
							if (nodeA != null && nodeB != null) {
								Vec3d nodeAworldPosition = nodeA.getWorldPosition(clientLevel, conduit.getPosition().getNodeApos());
								Vec3d nodeBworldPosition = nodeB.getWorldPosition(clientLevel, conduit.getPosition().getNodeBpos());
								
								Vec3d nodeOrigin = MathUtility.getMinCorner(nodeAworldPosition, nodeBworldPosition).sub(0.5, 0.5, 0.5);
								
								double distancaA = playerPosition.dist(nodeAworldPosition);
								double distancaB = playerPosition.dist(nodeBworldPosition);
								double distance = (distancaA + distancaB) / 2;
								int renderDistance = Minecraft.getInstance().options.renderDistance * 16;
								
								if (distance < renderDistance * renderDistance) conduitModel(clientLevel, vertexConsumer, matrixStack, conduit, nodeOrigin, partialTicks);
								
							}
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
	public static void conduitModel(ClientLevel clientLevel, VertexConsumer vertexConsumer, PoseStack poseStack, ConduitEntity conduit, Vec3d nodeOrigin, float partialTicks) {
		
		poseStack.pushPose();
		
		poseStack.translate(nodeOrigin.getX(), nodeOrigin.getY(), nodeOrigin.getZ());
		
		ConduitShape shape = conduit.getShape();
		ConduitType type = conduit.getConduit().getConduitType();
		
		if (shape != null) {
			
			int size = type.getThickness();
			int blockLight = 0;
			int skyLight = 0;
			
			// Pre-iteration for lightning
			for (int i = shape.nodes.length - 1; i > 0; i--) {
				
				Vec3d nodeB = shape.nodes[i - 1];
				Vec3d nodeA = shape.nodes[i - 0];
				
				if (i == shape.nodes.length - 1) {
					BlockPos nodeBlockPos =  nodeA.add(nodeOrigin).writeTo(new BlockPos(0, 0, 0)); // new BlockPos(nodeA.x, nodeA.y + 0.1F, nodeA.z).offset(nodeOrigin);
					blockLight = clientLevel.getLightEmission(nodeBlockPos);
					skyLight = clientLevel.getBrightness(LightLayer.SKY, nodeBlockPos);
				}
				BlockPos nodeBlockPos =  nodeB.add(nodeOrigin).writeTo(new BlockPos(0, 0, 0));
				blockLight = (clientLevel.getLightEmission(nodeBlockPos) * 1 + blockLight * 1) / 2;
				skyLight = (clientLevel.getBrightness(LightLayer.SKY, nodeBlockPos) * 1 + skyLight * 1) / 2;
				
			}
			
			float lengthOffset = 0;
			
			for (int i = 1; i < shape.nodes.length; i++) {
				
				Vec3d nodeA = shape.nodes[i - 1];
				Vec3d nodeB = shape.nodes[i - 0];
				
				if (i == 1) {
					BlockPos nodeBlockPos =  nodeA.add(nodeOrigin).writeTo(new BlockPos(0, 0, 0)); // new BlockPos(nodeA.x, nodeA.y + 0.1F, nodeA.z).offset(nodeOrigin);
					blockLight = (clientLevel.getLightEmission(nodeBlockPos) * 1 + blockLight * 2) / 3;
					skyLight = (clientLevel.getBrightness(LightLayer.SKY, nodeBlockPos) * 1 + skyLight * 2) / 3;
				}
				BlockPos nodeBlockPos =  nodeB.add(nodeOrigin).writeTo(new BlockPos(0, 0, 0));
				blockLight = (clientLevel.getLightEmission(nodeBlockPos) * 1 + blockLight * 2) / 3;
				skyLight = (clientLevel.getBrightness(LightLayer.SKY, nodeBlockPos) * 1 + skyLight * 2) / 3;
				
				int packedLight = LightTexture.pack(blockLight, skyLight);
				int segmentColor = conduit.getConduit().getColorAt(clientLevel, nodeA, conduit);
				
				Vec3d nodeAinterpolated = shape.lastPos[i - 1].lerp(nodeA, (double) partialTicks);
				Vec3d nodeBinterpolated = shape.lastPos[i - 0].lerp(nodeB, (double) partialTicks);
				
				lengthOffset += wireModel(vertexConsumer, poseStack, segmentColor, packedLight, nodeAinterpolated, nodeBinterpolated, size, lengthOffset);
				
			}
			
		}
		
		poseStack.popPose();
		
	}
	
	public static double wireModel(VertexConsumer vertexConsumer, PoseStack poseStack, int color, int packedLight, Vec3d start, Vec3d end, int width, float lengthOffset) {
		
		Vec3d lineVec = end.copy().sub(start);
		Vec3d lineNormal = lineVec.normalize();
		
		double length = lineVec.length();
		de.m_marvin.unimat.impl.Quaternion rotation = lineNormal.relativeRotationQuat(new Vec3f(0, 0, 1)); // Default model orientation positive Z
		
		poseStack.pushPose();
		poseStack.translate(start.x, start.y, start.z);
		poseStack.mulPose(new Quaternion(rotation.i(), rotation.j(), rotation.k(), rotation.r()));
		
		wirePart(vertexConsumer, poseStack, color, packedLight, (float) length, width / 16F, lengthOffset);
		
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
