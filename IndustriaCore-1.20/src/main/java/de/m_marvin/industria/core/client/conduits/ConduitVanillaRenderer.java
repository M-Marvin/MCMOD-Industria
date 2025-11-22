package de.m_marvin.industria.core.client.conduits;

import java.util.function.Supplier;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.util.FlywheelUtility;
import de.m_marvin.industria.core.client.util.GraphicsUtility;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.engine.ConduitHolderCapability;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitShape;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.registries.Tags;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec2f;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Like with the block entity renderer for blocks that have flywheel visuals, this is an fallback renderer in case flywheel is not available.
 */
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE, modid=IndustriaCore.MODID, value=Dist.CLIENT)
public class ConduitVanillaRenderer {
	
	private static final Supplier<ModelBlockRenderer> MODEL_RENDERER = () -> Minecraft.getInstance().getBlockRenderer().getModelRenderer();
	private static final Supplier<ProfilerFiller> PROFILER = Minecraft.getInstance()::getProfiler;
	
	@SubscribeEvent
	public static void onWorldRender(RenderLevelStageEvent event) {
		
		if (event.getStage() == Stage.AFTER_SOLID_BLOCKS) {
			
			PROFILER.get().push(IndustriaCore.MODID + ":conduits");
			
			MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
			PoseStack matrixStack = event.getPoseStack();
			ClientLevel level = Minecraft.getInstance().level;
			
			RenderSystem.enableDepthTest();
			
			Vec3 offset = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
			matrixStack.pushPose();
			matrixStack.translate(-offset.x, -offset.y, -offset.z);
			
			if (!Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
				
				if (!FlywheelUtility.isFlywheelEnabled())
					drawConduits(matrixStack, source, level, event.getPartialTick());
				
			} else {
				
				drawDebugConduits(matrixStack, source, level, event.getPartialTick());
				drawPlayerFocusedDebugNodes(matrixStack, source, level, Minecraft.getInstance().player, event.getPartialTick());
				
			}

			if (Minecraft.getInstance().player.getMainHandItem().is(Tags.Items.CONDUITS)) {

				drawConduitSymbols(matrixStack, source, level, event.getLevelRenderer().getTicks(), event.getPartialTick());
				drawPlayerFocusedNodeSymbols(matrixStack, source, level, Minecraft.getInstance().player, event.getLevelRenderer().getTicks(), event.getPartialTick());
				
			}
			
			source.endBatch();
			matrixStack.popPose();
			
			RenderSystem.disableDepthTest();
			
			PROFILER.get().pop();
			
		}
		
	}
	
	protected static void drawDebugConduits(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, float partialTicks) {
		
		LazyOptional<ConduitHolderCapability> optionalConduitHolder = clientLevel.getCapability(Capabilities.CONDUIT_HOLDER_CAPABILITY);
		if (optionalConduitHolder.isPresent()) {
			ConduitHolderCapability conduitHolder = optionalConduitHolder.resolve().get();
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
				
				BlockPos masterPos = connector.getConnectorMasterPos(clientLevel, targetBlock, blockState);
				BlockState masterState = clientLevel.getBlockState(masterPos);
				if (!masterPos.equals(targetBlock) && masterState.getBlock() instanceof IConduitConnector masterConnector) {
					blockState = masterState;
					targetBlock = masterPos;
					connector = masterConnector;
				}
				
				int nodeId = 0;
				for (ConduitNode node : connector.getConduitNodes(clientLevel, targetBlock, blockState)) {
					drawNodeDebug(matrixStack, bufferSource, clientLevel, partialTicks, targetBlock, node, nodeId++);
				}
			}
		}
		
	}
	
	protected static void drawConduitSymbols(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, int ticks, float partialTicks) {
		
		LazyOptional<ConduitHolderCapability> optionalConduitHolder = clientLevel.getCapability(Capabilities.CONDUIT_HOLDER_CAPABILITY);
		if (optionalConduitHolder.isPresent()) {
			ConduitHolderCapability conduitHolder = optionalConduitHolder.resolve().get();
			for (ConduitEntity conduit : conduitHolder.getConduits()) {
				drawConduitNodesSymbols(matrixStack, bufferSource, clientLevel, ticks, partialTicks, conduit);
			}
		}
		
	}
	
	protected static void drawPlayerFocusedNodeSymbols(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, Player player, int ticks, float partialTicks) {
		
		HitResult result = GameUtility.raycast(clientLevel, Vec3d.fromVec(player.getEyePosition()), Vec3d.fromVec(player.getViewVector(partialTicks)), player.getBlockReach());
		if (result.getType() == Type.BLOCK) {
			BlockPos targetBlock = ((BlockHitResult) result).getBlockPos();
			BlockState blockState = clientLevel.getBlockState(targetBlock);
			if (blockState.getBlock() instanceof IConduitConnector connector) {
				
				BlockPos masterPos = connector.getConnectorMasterPos(clientLevel, targetBlock, blockState);
				BlockState masterState = clientLevel.getBlockState(masterPos);
				if (!masterPos.equals(targetBlock) && masterState.getBlock() instanceof IConduitConnector masterConnector) {
					blockState = masterState;
					targetBlock = masterPos;
					connector = masterConnector;
				}
				
				int nodeId = 0;
				for (ConduitNode node : connector.getConduitNodes(clientLevel, targetBlock, blockState)) {
					drawNodeSymbol(matrixStack, bufferSource, clientLevel, ticks, partialTicks, targetBlock, node, nodeId++);
				}
			}
		}
		
	}

	protected static void drawConduits(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, float partialTicks) {
		
		LazyOptional<ConduitHolderCapability> optionalConduitHolder = clientLevel.getCapability(Capabilities.CONDUIT_HOLDER_CAPABILITY);
		if (optionalConduitHolder.isPresent()) {
			
			ConduitHolderCapability conduitHolder = optionalConduitHolder.resolve().get();
			
			for (ConduitEntity conduit : conduitHolder.getConduits()) {
				
				Vec3d playerPosition = Vec3d.fromVec(Minecraft.getInstance().player.position());
				
				BlockState nodeAstate = clientLevel.getBlockState(conduit.getPosition().getNodeApos());
				BlockState nodeBstate = clientLevel.getBlockState(conduit.getPosition().getNodeBpos());
				if (nodeAstate.getBlock() instanceof IConduitConnector nodeAconnector && nodeBstate.getBlock() instanceof IConduitConnector nodeBconnector) {
					ConduitNode nodeA = nodeAconnector.getConduitNode(clientLevel, conduit.getPosition().getNodeApos(), nodeAstate, conduit.getPosition().getNodeAid());
					ConduitNode nodeB = nodeBconnector.getConduitNode(clientLevel, conduit.getPosition().getNodeBpos(), nodeBstate, conduit.getPosition().getNodeBid());
					if (nodeA != null && nodeB != null) {
						Vec3d nodeAworldPosition = nodeA.getWorldRenderPosition(clientLevel, conduit.getPosition().getNodeApos());
						Vec3d nodeBworldPosition = nodeB.getWorldRenderPosition(clientLevel, conduit.getPosition().getNodeBpos());
						double distancaA = playerPosition.dist(nodeAworldPosition);
						double distancaB = playerPosition.dist(nodeBworldPosition);
						double distance = (distancaA + distancaB) / 2;
						int renderDistance = Minecraft.getInstance().options.renderDistance().get() * 16;
						
						if (distance < renderDistance * renderDistance) drawConduit(clientLevel, bufferSource, matrixStack, conduit, partialTicks);
						
					}
					
				}
				
			}
			
		}
		
	}
	
	public static void drawConduitDebug(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel level, float partialTick, ConduitEntity conduit) {
		
		BlockState nodeAstate = level.getBlockState(conduit.getPosition().getNodeApos());
		BlockState nodeBstate = level.getBlockState(conduit.getPosition().getNodeBpos());
		if (nodeAstate.getBlock() instanceof IConduitConnector nodeAconnector && nodeBstate.getBlock() instanceof IConduitConnector nodeBconnector) {
			ConduitNode nodeA = nodeAconnector.getConduitNode(level, conduit.getPosition().getNodeApos(), nodeAstate, conduit.getPosition().getNodeAid());
			ConduitNode nodeB = nodeBconnector.getConduitNode(level, conduit.getPosition().getNodeBpos(), nodeBstate, conduit.getPosition().getNodeBid());
			if (nodeA != null && nodeB != null) {
				Vec3d nodeAworldPosition = nodeA.getWorldRenderPosition(level, conduit.getPosition().getNodeApos());
				Vec3d nodeBworldPosition = nodeB.getWorldRenderPosition(level, conduit.getPosition().getNodeBpos());
				Vec3f normal = new Vec3f(nodeAworldPosition.sub(nodeBworldPosition)).tryNormalize();
				Vec3d nodeOrigin = MathUtility.getMinCorner(nodeAworldPosition, nodeBworldPosition).sub(0.5, 0.5, 0.5);
				Vec4f color = new Vec4f(0.5F, 1.0F, 0.5F, 1F);
				
				VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lines());
				
				for (int i = 0; i < conduit.getShape().nodes.length - 1; i++) {
					
					Vec3d node1 = conduit.getShape().nodes[i].add(nodeOrigin);
					Vec3d node2 = conduit.getShape().nodes[i + 1].add(nodeOrigin);
					Vec4f colorS = color.mul(i % 2 == 0 ? 1.0F : 2F);
					
					vertexconsumer.vertex(matrixStack.last().pose(), (float) node1.x, (float) node1.y, (float) node1.z).color(colorS.x * 0.5F, colorS.y * 0.5F, colorS.z * 0.5F, colorS.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
					vertexconsumer.vertex(matrixStack.last().pose(), (float) node2.x, (float) node2.y, (float) node2.z).color(colorS.x * 0.5F, colorS.y * 0.5F, colorS.z * 0.5F, colorS.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
					
				}
				vertexconsumer.vertex(matrixStack.last().pose(), (float) nodeAworldPosition.x, (float) nodeAworldPosition.y, (float) nodeAworldPosition.z).color(color.x, color.y, color.z, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
				vertexconsumer.vertex(matrixStack.last().pose(), (float) nodeBworldPosition.x, (float) nodeBworldPosition.y, (float) nodeBworldPosition.z).color(color.x, color.y, color.z, color.w).normal(matrixStack.last().normal(), normal.x, normal.y, normal.z).endVertex();
				
				drawNodeDebug(matrixStack, bufferSource, level, partialTick, conduit.getPosition().getNodeApos(), nodeA, conduit.getPosition().getNodeAid());
				drawNodeDebug(matrixStack, bufferSource, level, partialTick, conduit.getPosition().getNodeBpos(), nodeB, conduit.getPosition().getNodeBid());
				
			}
		}
		
	}
	
	public static void drawNodeDebug(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel level, float partialTicks, BlockPos pos, ConduitNode node, int nodeId) {
		
		Vec3d position = node.getWorldRenderPosition(level, pos);
		int colori = node.getType().getColor().getColor();
		Vec4f color = MathUtility.toVecColor(colori);
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

	public static void drawConduitNodesSymbols(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel level, int ticks, float partialTicks, ConduitEntity conduit) {
		BlockState nodeAstate = level.getBlockState(conduit.getPosition().getNodeApos());
		BlockState nodeBstate = level.getBlockState(conduit.getPosition().getNodeBpos());
		if (nodeAstate.getBlock() instanceof IConduitConnector nodeAconnector && nodeBstate.getBlock() instanceof IConduitConnector nodeBconnector) {
			ConduitNode nodeA = nodeAconnector.getConduitNode(level, conduit.getPosition().getNodeApos(), nodeAstate, conduit.getPosition().getNodeAid());
			ConduitNode nodeB = nodeBconnector.getConduitNode(level, conduit.getPosition().getNodeBpos(), nodeBstate, conduit.getPosition().getNodeBid());
			if (nodeA != null && nodeB != null) {
				drawNodeSymbol(matrixStack, bufferSource, level, ticks, partialTicks, conduit.getPosition().getNodeApos(), nodeA, conduit.getPosition().getNodeAid());
				drawNodeSymbol(matrixStack, bufferSource, level, ticks, partialTicks, conduit.getPosition().getNodeBpos(), nodeB, conduit.getPosition().getNodeBid());
			}
		}
	}

	public static void drawNodeSymbol(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel level, int ticks, float partialTicks, BlockPos pos, ConduitNode node, int nodeId) {
		
		Vec3d position = node.getWorldRenderPosition(level, pos);
		int colori = node.getType().getColor().getColor();
		Vec4f color = MathUtility.toVecColor(colori);
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
		
		float animationTicks = ticks + partialTicks;
		float o = (float) Math.sin(animationTicks / 20F) * 0.1F + 0.2F;
		
		matrixStack.pushPose();
		matrixStack.translate(position.x, position.y + 0.1 + o, position.z);
		matrixStack.mulPose(Axis.YN.rotationDegrees(Minecraft.getInstance().player.yHeadRot + 180));
		matrixStack.translate(0, 0, 4 * 0.0635F);
		
		Matrix4f pose = matrixStack.last().pose();
		Matrix3f normal = matrixStack.last().normal();
		float w = 6 / 16F;
		
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.outline(node.getType().getSymbolTexture()));
		vertex(vertexConsumer, pose, normal, w/2, 0, 0, 	0, 0, 1, 	1, 0, 15728880, colori);
		vertex(vertexConsumer, pose, normal, w/2, w , 0, 	0, 0, 1, 	1, 1, 15728880, colori);
		vertex(vertexConsumer, pose, normal, -w/2, w, 0, 	0, 0, 1, 	0, 1, 15728880, colori);
		vertex(vertexConsumer, pose, normal, -w/2, 0, 0, 	0, 0, 1, 	0, 0, 15728880, colori);
		
		matrixStack.scale(0.01F, -0.01F, 0.01F);
		
		int conduitCount = ConduitUtility.getConduitsAtNode(level, pos, nodeId).size();
		String info = conduitCount + "/" + node.getMaxConnections();
		GraphicsUtility.drawStringCentered(matrixStack, bufferSource, info, 0, 0, color.x, color.y, color.z, color.w);
		matrixStack.popPose();
		
	}
	
	public static void drawConduit(ClientLevel clientLevel, MultiBufferSource bufferSource, PoseStack poseStack, ConduitEntity conduit, float partialTicks) {
		
		ConduitShape shape = conduit.getShape();
		
		if (shape != null) {
			
			BakedModel[] models = ConduitModelManager.getModels(conduit.getConduitState());

			int segments = shape.nodes.length - 1;
			Vec3f origin = new Vec3f(shape.shapeNodeA.min(shape.shapeNodeB));
			for (int segment = 0; segment < segments; segment++) {

				Vec3d node1 = shape.lastPos[segment + 0].lerp(shape.nodes[segment + 0], (double) partialTicks);
				Vec3d node2 = shape.lastPos[segment + 1].lerp(shape.nodes[segment + 1], (double) partialTicks);

				Vec3f position = new Vec3f(node1);
				Vec3f direction = new Vec3f(node2.sub(node1));
				position.addI(origin).subI(0.5F, 0.5F, 0.5F);
				
				float angleHorizontal = -(float) new Vec2f(direction.x, direction.z).angle(new Vec2f(0F, -1F));
				Vec2f directionProjection = new Vec2f((float) Math.sqrt(direction.x * direction.x + direction.z * direction.z), direction.y).tryNormalize();
				float angleVertical = (float) directionProjection.angle(new Vec2f(1F, 0F));
				float scale = (float) (direction.length() / shape.segmentLength);
				
				int light1 = LevelRenderer.getLightColor(conduit.getLevel(), MathUtility.toBlockPos(node1.add(origin).sub(0.5, 0.5, 0.5)));
				int light2 = LevelRenderer.getLightColor(conduit.getLevel(), MathUtility.toBlockPos(node2.add(origin).sub(0.5, 0.5, 0.5)));
				int light = (light1 + light2) / 2;
				
				poseStack.pushPose();
				poseStack.translate(direction.x / 2, direction.y / 2, direction.z / 2);
				poseStack.translate(position.x, position.y, position.z);
				poseStack.mulPose(Axis.YP.rotation(angleHorizontal));
				poseStack.mulPose(Axis.XP.rotation(angleVertical));
				poseStack.scale(1F, 1F, scale);
				
				ModelBlockRenderer renderer = MODEL_RENDERER.get();
				
				BakedModel model = models[segment % models.length];
				VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(ConduitModelManager.LOCATION_CONDUITS));
				for (RenderType renderType : model.getRenderTypes(Blocks.AIR.defaultBlockState(), clientLevel.getRandom(), ModelData.EMPTY))
					renderer.renderModel(poseStack.last(), vertexConsumer, Blocks.AIR.defaultBlockState(), model, 1F, 1F, 1F, light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
				
				poseStack.popPose();
				
			}
			
		}
		
	}
	
	protected static void vertex(VertexConsumer vertexBuilder, Matrix4f pose, Matrix3f normal, float x, float y, float z, float nx, float ny, float nz, float u, float v, int light, int color) {
		vertexBuilder.vertex(pose, x, y, z).color(color).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, nx, ny, nz).endVertex();
	}
	
}
