package de.m_marvin.industria.core.client.kinetics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.physics.ClientPhysicsUtility;
import de.m_marvin.industria.core.contraptions.ContraptionUtility;
import de.m_marvin.industria.core.kinetics.engine.KineticNetworkSpaceCapability;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE,modid=IndustriaCore.MODID, value=Dist.CLIENT)
public class KineticComponentRenderer {

	protected static float animationTicks;

	@SubscribeEvent
	public static void onWorldRender(RenderLevelStageEvent event) {

		if (event.getStage() == Stage.AFTER_PARTICLES) {
			
			animationTicks = event.getRenderTick() + event.getPartialTick();
			
			BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
			PoseStack matrixStack = event.getPoseStack();
			ClientLevel level = Minecraft.getInstance().level;
			
			RenderSystem.enableDepthTest();
			
			Vec3 offset = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
			matrixStack.pushPose();
			matrixStack.translate(-offset.x, -offset.y, -offset.z);
			
			if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
				
				drawDebugFrames(matrixStack, source, level, event.getPartialTick());
				
			}
			
			source.endBatch();
			matrixStack.popPose();
			
			RenderSystem.disableDepthTest();
			
		}
		
	}
	
	/* private render methods, called by the render event */
	
	@SuppressWarnings({ "deprecation" })
	private static void drawDebugFrames(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, float partialTicks) {
		
		LazyOptional<KineticNetworkSpaceCapability> optionalKineticHolder = clientLevel.getCapability(Capabilities.KINETIC_NETWORK_SPACE_CAPABILITY);
		if (optionalKineticHolder.isPresent()) {
			KineticNetworkSpaceCapability kineticHolder = optionalKineticHolder.resolve().get();
			
			Vec3d playerPosition = Vec3d.fromVec(Minecraft.getInstance().player.position());
			int renderDistance = Minecraft.getInstance().options.renderDistance().get() * 16;
			
			for (KineticNetworkSpaceCapability.KineticComponent component : kineticHolder.listComponents()) {
				
				BlockPos pos = component.reference().pos();
				
				BlockState state = component.instance(clientLevel);
				VoxelShape shape = state.getBlock().getShape(state, clientLevel, pos, null);
				
				AABB bounds = new AABB(0, 0, 0, 1, 1, 1);
							
				if (shape != null && !shape.isEmpty()) bounds = shape.bounds();
				
				double distance = playerPosition.dist(ContraptionUtility.ensureWorldCoordinates(clientLevel, pos, Vec3d.fromVec(pos)));
				if (distance < renderDistance * renderDistance) drawKineticFrame(clientLevel, bufferSource, matrixStack, pos, bounds, partialTicks);
				
			}
			
		}
		
	}
	
	private static void drawKineticFrame(ClientLevel clientLevel, MultiBufferSource bufferSource, PoseStack matrixStack, BlockPos pos, AABB bounds, float partialTicks) {
		
		matrixStack.pushPose();
		
		ClientPhysicsUtility.ensureWorldTransformTo(clientLevel, matrixStack, pos);
		
		float f = 0.0625F + (float) -Math.sin(animationTicks * 0.1F) * 0.03125F;
		
		float fxl = (float) (bounds.minX + -f);
		float fyl = (float) (bounds.minY + -f);
		float fzl = (float) (bounds.minZ + -f);
		float fxh = (float) (bounds.maxX + f);
		float fyh = (float) (bounds.maxY + f);
		float fzh = (float) (bounds.maxZ + f);
		
		float r = 1F;
		float g = 0.5F;
		float b = 0F;
		
		VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lines());
		LevelRenderer.renderLineBox(
        		matrixStack, vertexconsumer, 
        		fxl, fyl, fzl, 
        		fxh, fyh, fzh, 
        		r, g, b, 1F,
        		r, g, b);
		
		matrixStack.popPose();
		
	}
	
}
