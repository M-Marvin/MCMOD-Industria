package de.m_marvin.industria.core.client.conduits;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.util.GraphicsUtility;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.engine.ConduitHandlerCapability;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitType;
import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.registries.IndustriaTags;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.unimat.impl.Quaterniond;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
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

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE, modid=IndustriaCore.MODID, value=Dist.CLIENT)
public class ConduitRenderer {

	protected static float animationTicks;

	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onWorldRender(RenderLevelStageEvent event) {
		
		if (event.getStage() == Stage.AFTER_PARTICLES) {
			
			animationTicks += event.getPartialTick();

			MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
			PoseStack matrixStack = event.getPoseStack();
			ClientLevel level = Minecraft.getInstance().level;
			
			RenderSystem.enableDepthTest();
			
			Vec3 offset = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
			matrixStack.pushPose();
			matrixStack.translate(-offset.x, -offset.y, -offset.z);
			
			if (!Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
				
				drawConduits(matrixStack, source, level, event.getPartialTick());
				
			} else {
				
				drawDebugConduits(matrixStack, source, level, event.getPartialTick());
				drawPlayerFocusedDebugNodes(matrixStack, source, level, Minecraft.getInstance().player, event.getPartialTick());
				
			}

			if (Minecraft.getInstance().player.getMainHandItem().is(IndustriaTags.Items.CONDUITS)) {

				drawConduitSymbols(matrixStack, source, level, event.getPartialTick());
				drawPlayerFocusedNodeSymbols(matrixStack, source, level, Minecraft.getInstance().player, event.getPartialTick());
				
			}
			
			source.endBatch();
			matrixStack.popPose();
			
			RenderSystem.disableDepthTest();
			
		}
		
	}
	
	/* protected render methods, called by the render event */
	
	protected static void drawDebugConduits(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, float partialTicks) {
		
		LazyOptional<ConduitHandlerCapability> optionalConduitHolder = clientLevel.getCapability(Capabilities.CONDUIT_HANDLER_CAPABILITY);
		if (optionalConduitHolder.isPresent()) {
			ConduitHandlerCapability conduitHolder = optionalConduitHolder.resolve().get();
			for (ConduitEntity conduit : conduitHolder.getConduits()) {
				drawConduitDebug(matrixStack, bufferSource, clientLevel, partialTicks, conduit);
			}
		}
		
	}

	protected static void drawPlayerFocusedDebugNodes(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, Player player, float partialTicks) {
		
		HitResult result = GameUtility.raycast(clientLevel, Vec3d.fromVec(player.getEyePosition()), Vec3d.fromVec(player.getViewVector(partialTicks)), player.getBlockReach());
		if (result.getType() == Type.BLOCK) {
			BlockPos targetBlock = ((BlockHitResult) result).getBlockPos();
			BlockState blockState = clientLevel.getBlockState(targetBlock);
			if (blockState.getBlock() instanceof IConduitConnector connector) {
				int nodeId = 0;
				for (ConduitNode node : connector.getConduitNodes(clientLevel, targetBlock, blockState)) {
					drawNodeDebug(matrixStack, bufferSource, clientLevel, partialTicks, targetBlock, node, nodeId++);
				}
			}
		}
		
	}
	
	protected static void drawConduitSymbols(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, float partialTicks) {
		
		LazyOptional<ConduitHandlerCapability> optionalConduitHolder = clientLevel.getCapability(Capabilities.CONDUIT_HANDLER_CAPABILITY);
		if (optionalConduitHolder.isPresent()) {
			ConduitHandlerCapability conduitHolder = optionalConduitHolder.resolve().get();
			for (ConduitEntity conduit : conduitHolder.getConduits()) {
				drawConduitNodesSymbols(matrixStack, bufferSource, clientLevel, partialTicks, conduit);
			}
		}
		
	}
	
	protected static void drawPlayerFocusedNodeSymbols(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, Player player, float partialTicks) {
		
		HitResult result = GameUtility.raycast(clientLevel, Vec3d.fromVec(player.getEyePosition()), Vec3d.fromVec(player.getViewVector(partialTicks)), player.getBlockReach());
		if (result.getType() == Type.BLOCK) {
			BlockPos targetBlock = ((BlockHitResult) result).getBlockPos();
			BlockState blockState = clientLevel.getBlockState(targetBlock);
			if (blockState.getBlock() instanceof IConduitConnector connector) {
				int nodeId = 0;
				for (ConduitNode node : connector.getConduitNodes(clientLevel, targetBlock, blockState)) {
					drawNodeSymbol(matrixStack, bufferSource, clientLevel, partialTicks, targetBlock, node, nodeId++);
				}
			}
		}
		
	}

	@SuppressWarnings("resource")
	protected static void drawConduits(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, float partialTicks) {
		
		LazyOptional<ConduitHandlerCapability> optionalConduitHolder = clientLevel.getCapability(Capabilities.CONDUIT_HANDLER_CAPABILITY);
		if (optionalConduitHolder.isPresent()) {
			
			ConduitHandlerCapability conduitHolder = optionalConduitHolder.resolve().get();
			
			VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(ConduitTextureManager.LOCATION_CONDUITS));
			
			for (ConduitEntity conduit : conduitHolder.getConduits()) {
				
				TextureAtlasSprite sprite = ConduitTextureManager.getInstance().get(conduit.getConduit());
				Vec3d playerPosition = Vec3d.fromVec(Minecraft.getInstance().player.position());
				
				BlockState nodeAstate = clientLevel.getBlockState(conduit.getPosition().getNodeApos());
				BlockState nodeBstate = clientLevel.getBlockState(conduit.getPosition().getNodeBpos());
				if (nodeAstate.getBlock() instanceof IConduitConnector nodeAconnector && nodeBstate.getBlock() instanceof IConduitConnector nodeBconnector) {
					ConduitNode nodeA = nodeAconnector.getConduitNode(clientLevel, conduit.getPosition().getNodeApos(), nodeAstate, conduit.getPosition().getNodeAid());
					ConduitNode nodeB = nodeBconnector.getConduitNode(clientLevel, conduit.getPosition().getNodeBpos(), nodeBstate, conduit.getPosition().getNodeBid());
					if (nodeA != null && nodeB != null) {
						Vec3d nodeAworldPosition = nodeA.getWorldRenderPosition(clientLevel, conduit.getPosition().getNodeApos());
						Vec3d nodeBworldPosition = nodeB.getWorldRenderPosition(clientLevel, conduit.getPosition().getNodeBpos());
						
						Vec3d nodeOrigin = MathUtility.getMinCorner(nodeAworldPosition, nodeBworldPosition).sub(0.5, 0.5, 0.5);
						
						double distancaA = playerPosition.dist(nodeAworldPosition);
						double distancaB = playerPosition.dist(nodeBworldPosition);
						double distance = (distancaA + distancaB) / 2;
						int renderDistance = Minecraft.getInstance().options.renderDistance().get() * 16;
						
						if (distance < renderDistance * renderDistance) drawConduitModel(clientLevel, vertexConsumer, matrixStack, conduit, nodeOrigin, partialTicks, sprite);
						
					}
					
				}
				
			}
			
		}
		
	}
	
	/* public render methods */
	
	public static void drawConduitDebug(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel level, float partialTick, ConduitEntity conduit) {
		
		BlockState nodeAstate = level.getBlockState(conduit.getPosition().getNodeApos());
		BlockState nodeBstate = level.getBlockState(conduit.getPosition().getNodeBpos());
		if (nodeAstate.getBlock() instanceof IConduitConnector nodeAconnector && nodeBstate.getBlock() instanceof IConduitConnector nodeBconnector) {
			ConduitNode nodeA = nodeAconnector.getConduitNode(level, conduit.getPosition().getNodeApos(), nodeAstate, conduit.getPosition().getNodeAid());
			ConduitNode nodeB = nodeBconnector.getConduitNode(level, conduit.getPosition().getNodeBpos(), nodeBstate, conduit.getPosition().getNodeBid());
			if (nodeA != null && nodeB != null) {
				Vec3d nodeAworldPosition = nodeA.getWorldRenderPosition(level, conduit.getPosition().getNodeApos());
				Vec3d nodeBworldPosition = nodeB.getWorldRenderPosition(level, conduit.getPosition().getNodeBpos());
				Vec3f normal = new Vec3f(nodeAworldPosition.sub(nodeBworldPosition)).normalize();
				Vec3d nodeOrigin = MathUtility.getMinCorner(nodeAworldPosition, nodeBworldPosition).sub(0.5, 0.5, 0.5);
				Vec4f color = new Vec4f(0.5F, 1.0F, 0.5F, 1F);
				
				VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lines());
				
				for (int i = 0; i < conduit.getShape().nodes.length - 1; i++) {
					
					Vec3d node1 = conduit.getShape().nodes[i].add(nodeOrigin);
					Vec3d node2 = conduit.getShape().nodes[i + 1].add(nodeOrigin);
					
					vertexconsumer.vertex(matrixStack.last().pose(), (float) node1.x, (float) node1.y, (float) node1.z).color(color.x * 0.5F, color.y * 0.5F, color.z * 0.5F, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
					vertexconsumer.vertex(matrixStack.last().pose(), (float) node2.x, (float) node2.y, (float) node2.z).color(color.x * 0.5F, color.y * 0.5F, color.z * 0.5F, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
					
					
				}
				vertexconsumer.vertex(matrixStack.last().pose(), (float) nodeAworldPosition.x, (float) nodeAworldPosition.y, (float) nodeAworldPosition.z).color(color.x, color.y, color.z, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
				vertexconsumer.vertex(matrixStack.last().pose(), (float) nodeBworldPosition.x, (float) nodeBworldPosition.y, (float) nodeBworldPosition.z).color(color.x, color.y, color.z, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
				
				drawNodeDebug(matrixStack, bufferSource, level, partialTick, conduit.getPosition().getNodeApos(), nodeA, conduit.getPosition().getNodeAid());
				drawNodeDebug(matrixStack, bufferSource, level, partialTick, conduit.getPosition().getNodeBpos(), nodeB, conduit.getPosition().getNodeBid());
				
			}
		}
		
	}
	
	@SuppressWarnings("resource")
	public static void drawNodeDebug(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel level, float partialTicks, BlockPos pos, ConduitNode node, int nodeId) {
		
		Vec3d position = node.getWorldRenderPosition(level, pos);
		int colori = node.getType().getColor().getColor();
		Vec4f color = GameUtility.toVecColor(colori);
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
		matrixStack.mulPose(Axis.YN.rotationDegrees(Minecraft.getInstance().player.yHeadRot + 180));
		matrixStack.translate(0, 0, 4 * 0.0635F);
		matrixStack.scale(0.01F, -0.01F, 0.01F);
		String info = "Id:" + nodeId;
		GraphicsUtility.drawStringCentered(matrixStack, bufferSource, info, 0, 0, color.x, color.y, color.z, color.w);
		matrixStack.popPose();
		
	}

	public static void drawConduitNodesSymbols(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel level, float partialTick, ConduitEntity conduit) {
		BlockState nodeAstate = level.getBlockState(conduit.getPosition().getNodeApos());
		BlockState nodeBstate = level.getBlockState(conduit.getPosition().getNodeBpos());
		if (nodeAstate.getBlock() instanceof IConduitConnector nodeAconnector && nodeBstate.getBlock() instanceof IConduitConnector nodeBconnector) {
			ConduitNode nodeA = nodeAconnector.getConduitNode(level, conduit.getPosition().getNodeApos(), nodeAstate, conduit.getPosition().getNodeAid());
			ConduitNode nodeB = nodeBconnector.getConduitNode(level, conduit.getPosition().getNodeBpos(), nodeBstate, conduit.getPosition().getNodeBid());
			if (nodeA != null && nodeB != null) {
				drawNodeSymbol(matrixStack, bufferSource, level, partialTick, conduit.getPosition().getNodeApos(), nodeA, conduit.getPosition().getNodeAid());
				drawNodeSymbol(matrixStack, bufferSource, level, partialTick, conduit.getPosition().getNodeBpos(), nodeB, conduit.getPosition().getNodeBid());
			}
		}
	}

	@SuppressWarnings("resource")
	public static void drawNodeSymbol(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel level, float partialTicks, BlockPos pos, ConduitNode node, int nodeId) {
		
		Vec3d position = node.getWorldRenderPosition(level, pos);
		int colori = node.getType().getColor().getColor();
		Vec4f color = GameUtility.toVecColor(colori);
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
		
		float o = (float) Math.sin(animationTicks / 20F) * 0.1F + 0.2F;
		
		matrixStack.pushPose();
		matrixStack.translate(position.x, position.y + 0.1 + o, position.z);
		matrixStack.mulPose(Axis.YN.rotationDegrees(Minecraft.getInstance().player.yHeadRot + 180));
		matrixStack.translate(0, 0, 4 * 0.0635F);
		
		Matrix4f pose = matrixStack.last().pose();
		Matrix3f normal = matrixStack.last().normal();
		float w = 6 / 16F;
		
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(node.getType().getSymbolTexture()));
		vertex(vertexConsumer, pose, normal, w/2, 0, 0, 	0, 0, 1, 	1, 0, 15728880, colori);
		vertex(vertexConsumer, pose, normal, w/2, w , 0, 	0, 0, 1, 	1, 1, 15728880, colori);
		vertex(vertexConsumer, pose, normal, -w/2, w, 0, 	0, 0, 1, 	0, 1, 15728880, colori);
		vertex(vertexConsumer, pose, normal, -w/2, 0, 0, 	0, 0, 1, 	0, 0, 15728880, colori);
		vertexConsumer.endVertex();
		
		matrixStack.scale(0.01F, -0.01F, 0.01F);
		
		int conduitCount = ConduitUtility.getConduitsAtNode(level, pos, nodeId).size();
		String info = conduitCount + "/" + node.getMaxConnections();
		GraphicsUtility.drawStringCentered(matrixStack, bufferSource, info, 0, 0, color.x, color.y, color.z, color.w);
		matrixStack.popPose();
		
	}
	
	public static void drawConduitModel(ClientLevel clientLevel, VertexConsumer vertexConsumer, PoseStack poseStack, ConduitEntity conduit, Vec3d nodeOrigin, float partialTicks, TextureAtlasSprite sprite) {
		
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
					BlockPos nodeBlockPos =  nodeA.add(nodeOrigin).writeTo(new BlockPos(0, 0, 0));
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
				
				lengthOffset += drawConduitSegment(vertexConsumer, poseStack, segmentColor, packedLight, nodeAinterpolated, nodeBinterpolated, size, lengthOffset, sprite);
				
			}
			
		}
		
		poseStack.popPose();
		
	}
	
	public static double drawConduitSegment(VertexConsumer vertexConsumer, PoseStack poseStack, int color, int packedLight, Vec3d start, Vec3d end, int width, float lengthOffset, TextureAtlasSprite sprite) {
		
		Vec3d lineVec = end.sub(start);
		Vec3d lineNormal = lineVec.normalize();
		
		double length = lineVec.length();
		Quaterniond rotation = lineNormal.relativeRotationQuat(new Vec3f(0, 0, 1)); // Default model orientation positive Z
		
		poseStack.pushPose();
		poseStack.translate(start.x, start.y, start.z);
		poseStack.mulPose(new org.joml.Quaternionf(rotation.i(), rotation.j(), rotation.k(), rotation.r()));
		
		float textureOvershot = (lengthOffset + (float) length) % (ConduitTextureManager.TEXTURE_MAP_WIDTH / 16F) - (float) length;
		
		if (textureOvershot < 0) {

			drawConduitSegmentPartial(vertexConsumer, poseStack, color, packedLight, (float) -textureOvershot, width / 16F, 4F + textureOvershot, sprite);
			poseStack.pushPose();
			poseStack.translate(0, 0, -textureOvershot);
			drawConduitSegmentPartial(vertexConsumer, poseStack, color, packedLight, (float) length + textureOvershot, width / 16F, 0, sprite);
			poseStack.popPose();
			
		} else {

			drawConduitSegmentPartial(vertexConsumer, poseStack, color, packedLight, (float) length, width / 16F, textureOvershot, sprite);
			
		}
		
		poseStack.popPose();
		
		return length;
		
	}
	
	public static void drawConduitSegmentPartial(VertexConsumer vertexConsumer, PoseStack poseStack, int color, int packedLight, float length, float width, float lengthOffest, TextureAtlasSprite sprite) {
		
		float fwh = width / 2;
		
		Matrix3f normal = poseStack.last().normal();
		Matrix4f pose = poseStack.last().pose();
		
		float fw = width * 16;
		float fl = length * 16;
		float flo = lengthOffest * 16;
		
		float uf = sprite.getU1() - sprite.getU0();
		float vf = sprite.getV1() - sprite.getV0();
		
		float uvX0 = sprite.getU0() + (flo / ConduitTextureManager.TEXTURE_MAP_WIDTH) * uf;
		float uvYb0 = sprite.getV0() + (0 * fw / ConduitTextureManager.TEXTURE_MAP_HEIGHT) * vf;
		float uvYt0 = sprite.getV0() + (1 * fw / ConduitTextureManager.TEXTURE_MAP_HEIGHT) * vf;
		float uvYe0 = sprite.getV0() + (2 * fw / ConduitTextureManager.TEXTURE_MAP_HEIGHT) * vf;
		float uvYw0 = sprite.getV0() + (3 * fw / ConduitTextureManager.TEXTURE_MAP_HEIGHT) * vf;
		float uvX1 = sprite.getU0() + ((flo + fl) / ConduitTextureManager.TEXTURE_MAP_WIDTH) * uf;
		float uvYb1 = sprite.getV0() + (1 * fw / ConduitTextureManager.TEXTURE_MAP_HEIGHT) * vf;
		float uvYt1 = sprite.getV0() + (2 * fw / ConduitTextureManager.TEXTURE_MAP_HEIGHT) * vf;
		float uvYe1 = sprite.getV0() + (3 * fw / ConduitTextureManager.TEXTURE_MAP_HEIGHT) * vf;
		float uvYw1 = sprite.getV0() + (4 * fw / ConduitTextureManager.TEXTURE_MAP_HEIGHT) * vf;
		
		float uvEXn0 = sprite.getU0() + (0) * uf;
		float uvEXs0 = sprite.getU0() + (fw * 1 / ConduitTextureManager.TEXTURE_MAP_WIDTH) * uf;
		float uvEXn1 = sprite.getU0() + (fw * 1 / ConduitTextureManager.TEXTURE_MAP_WIDTH) * uf;
		float uvEXs1 = sprite.getU0() + (fw * 2 / ConduitTextureManager.TEXTURE_MAP_WIDTH) * uf;
		float uvEY0 = sprite.getV0() + (fw * 4 / ConduitTextureManager.TEXTURE_MAP_HEIGHT) * vf;
		float uvEY1 = sprite.getV0() + (fw * 5 / ConduitTextureManager.TEXTURE_MAP_HEIGHT) * vf;
		
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

	protected static void vertex(VertexConsumer vertexBuilder, Matrix4f pose, Matrix3f normal, float x, float y, float z, float nx, float ny, float nz, float u, float v, int light, int color) {
		vertexBuilder.vertex(pose, x, y, z).color(color).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, nx, ny, nz).endVertex();
	}
	
}
