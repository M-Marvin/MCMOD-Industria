package de.m_marvin.industria.core.client.electrics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.physics.ClientPhysicsUtility;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE,modid=IndustriaCore.MODID, value=Dist.CLIENT)
public class ElectricComponentRenderer {

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
			
			if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
				
				drawDebugFrames(matrixStack, source, level, event.getPartialTick());
				
			}
			
			source.endBatch();
			matrixStack.popPose();
			
			RenderSystem.disableDepthTest();
			
		}
		
	}
	
	/* private render methods, called by the render event */
	
	@SuppressWarnings("resource")
	private static void drawDebugFrames(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, float partialTicks) {
		
		LazyOptional<ElectricNetworkHandlerCapability> optionalElectricHolder = clientLevel.getCapability(Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		if (optionalElectricHolder.isPresent()) {
			ElectricNetworkHandlerCapability electricHolder = optionalElectricHolder.resolve().get();
			
			Vec3d playerPosition = Vec3d.fromVec(Minecraft.getInstance().player.position());
			int renderDistance = Minecraft.getInstance().options.renderDistance().get() * 16;
			
			for (ElectricNetworkHandlerCapability.Component<?, ?, ?> component : electricHolder.getComponents()) {
				
				if (component.type() instanceof Block block) {
					
					BlockPos pos = (BlockPos) component.pos();
					
					double distance = playerPosition.dist(PhysicUtility.ensureWorldCoordinates(clientLevel, pos, Vec3d.fromVec(pos)));
					if (distance < renderDistance * renderDistance) drawElectricFrame(clientLevel, bufferSource, matrixStack, pos, partialTicks);
					
				}
				
			}
			
		}
		
	}
	
	private static void drawElectricFrame(ClientLevel clientLevel, MultiBufferSource bufferSource, PoseStack matrixStack, BlockPos pos, float partialTicks) {
		
		matrixStack.pushPose();
		
		ClientPhysicsUtility.ensureWorldTransformTo(clientLevel, matrixStack, pos);
		
		float f = 0.0625F + (float) -Math.sin(animationTicks * 0.1F) * 0.03125F;
		
		float fxl = -f;
		float fyl = -f;
		float fzl = -f;
		float fxh = 1 + f;
		float fyh = 1 + f;
		float fzh = 1 + f;
		
		float r = 1F;
		float g = 1F;
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
