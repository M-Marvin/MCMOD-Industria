package de.m_marvin.industria.core.client.magnetism;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.physics.ClientPhysicsUtility;
import de.m_marvin.industria.core.client.util.GraphicsUtility;
import de.m_marvin.industria.core.magnetism.engine.MagnetismHandlerCapability;
import de.m_marvin.industria.core.magnetism.types.MagneticField;
import de.m_marvin.industria.core.magnetism.types.MagneticFieldInfluence;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE,modid=IndustriaCore.MODID, value=Dist.CLIENT)
public class MagneticFieldRenderer {

	protected static float animationTicks;

	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void onWorldRender(RenderLevelStageEvent event) {
		
		if (event.getStage() == Stage.AFTER_PARTICLES) { // AFTER_SOLID_BLOCKS // TODO
			
			animationTicks += event.getPartialTick();

			MultiBufferSource.BufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();
			PoseStack matrixStack = event.getPoseStack();
			ClientLevel level = Minecraft.getInstance().level;
			
			RenderSystem.enableDepthTest();
			
			Vec3 offset = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
			matrixStack.pushPose();
			matrixStack.translate(-offset.x, -offset.y, -offset.z);
			
			if (!Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
				
			}
			
			if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
				
				drawDebugFields(matrixStack, source, level, event.getPartialTick());
				
			}
			
			source.endBatch();
			matrixStack.popPose();
			
			RenderSystem.disableDepthTest();
			
		}
		
	}
	
	/* protected render methods, called by the render event */
	
	@SuppressWarnings("resource")
	protected static void drawDebugFields(PoseStack matrixStack, MultiBufferSource bufferSource, ClientLevel clientLevel, float partialTicks) {
		
		LazyOptional<MagnetismHandlerCapability> optionalMagneticHolder = clientLevel.getCapability(Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		if (optionalMagneticHolder.isPresent()) {
			MagnetismHandlerCapability magneticHolder = optionalMagneticHolder.resolve().get();
			
			Vec3d playerPosition = Vec3d.fromVec(Minecraft.getInstance().player.position());
			int renderDistance = Minecraft.getInstance().options.renderDistance().get() * 16;
			
			for (MagneticFieldInfluence influence : magneticHolder.getMagneticInfluences()) {
				
				BlockPos pos = influence.getPos();
				
				double distance = playerPosition.dist(PhysicUtility.ensureWorldCoordinates(clientLevel, pos, Vec3d.fromVec(pos)));
				if (distance < renderDistance * renderDistance) drawMangeticInfluence(clientLevel, bufferSource, matrixStack, influence, partialTicks);
				
			}
			
			for (MagneticField field : magneticHolder.getMagneticFields()) {
				
				BlockPos pos = MathUtility.getMiddleBlock(field.getMinPos(), field.getMaxPos());
				
				double distance = playerPosition.dist(Vec3i.fromVec(PhysicUtility.ensureWorldBlockCoordinates(clientLevel, pos, pos)));
				
				try {
					if (distance < renderDistance * renderDistance) drawMagneticField(clientLevel, bufferSource, matrixStack, field, partialTicks);
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
	
	protected static void drawMangeticInfluence(ClientLevel clientLevel, MultiBufferSource bufferSource, PoseStack matrixStack, MagneticFieldInfluence influence, float partialTicks) {
		
		matrixStack.pushPose();
		
		BlockPos pos = influence.getPos();
		Vec3d fieldVector = influence.getVector();
		Vec3d inducedVector = influence.getInducedVector();
		boolean alternating = influence.isAlternating();
		
		ClientPhysicsUtility.ensureWorldTransformTo(clientLevel, matrixStack, pos);
		
		float f = 0.0625F + (float) Math.sin(animationTicks * 0.1F) * 0.03125F;
		
		float fxl = -f;
		float fyl = -f;
		float fzl = -f;
		float fxh = 1 + f;
		float fyh = 1 + f;
		float fzh = 1 + f;
		
		float r = 0F;
		float g = 0F;
		float b = 1F;
		
		VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lines());
		LevelRenderer.renderLineBox(
        		matrixStack, vertexconsumer, 
        		fxl, fyl, fzl, 
        		fxh, fyh, fzh, 
        		r, g, b, 1F,
        		r, g, b);
		
		GraphicsUtility.renderVector(
				vertexconsumer, matrixStack, 
				new Vec3f(0.5F, 0.5F, 0.5F), 
				new Vec3f(fieldVector),
				alternating ? 0 : 255, 0, alternating ? 255 : 0, 255);

		GraphicsUtility.renderVector(
				vertexconsumer, matrixStack, 
				new Vec3f(0.5F, 0.5F, 0.5F), 
				new Vec3f(inducedVector),
				0, 255, 0, 255);
		
		matrixStack.popPose();
		
	}
	
	protected static void drawMagneticField(ClientLevel clientLevel, MultiBufferSource bufferSource, PoseStack matrixStack, MagneticField field, float partialTicks) {
		
		matrixStack.pushPose();
		
		BlockPos pos = field.getInfluences().isEmpty() ? BlockPos.ZERO : field.getInfluences().iterator().next().getPos();
		Vec3d fieldVectorLinear = field.getFieldVectorLinear();
		Vec3d fieldVectorAlternating = field.getFieldVectorAlternating();
		Vec3f magneticCenter = new Vec3f(field.getMagneticCenter().add(field.getGeometricCenter().sub(Vec3d.fromVec(pos))));
		
		ClientPhysicsUtility.ensureWorldTransformTo(clientLevel, matrixStack, pos);
		
		float f = 2 * 0.0625F + (float) Math.sin(animationTicks * 0.1F) * 0.03125F;
		
		float fxl = (float) (field.getMinPos().getX() - pos.getX() - f);
		float fyl = (float) (field.getMinPos().getY() - pos.getY() - f);
		float fzl = (float) (field.getMinPos().getZ() - pos.getZ() - f);
		float fxh = (float) (field.getMaxPos().getX() - pos.getX() + 1 + f);
		float fyh = (float) (field.getMaxPos().getY() - pos.getY() + 1 + f);
		float fzh = (float) (field.getMaxPos().getZ() - pos.getZ() + 1 + f);
		
		float r = 0F;
		float g = 1F;
		float b = 1F;
		float a = 0.1F;

		VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lines());
		LevelRenderer.renderLineBox(
        		matrixStack, vertexconsumer, 
        		fxl, fyl, fzl, 
        		fxh, fyh, fzh, 
        		r, g, b, 1F,
        		r, g, b);
		
		GraphicsUtility.renderVector(
				vertexconsumer, matrixStack, 
				magneticCenter, 
				new Vec3f(fieldVectorLinear),
				255, 0, 0, 255);
		
		GraphicsUtility.renderVector(
				vertexconsumer, matrixStack, 
				magneticCenter, 
				new Vec3f(fieldVectorAlternating),
				0, 0, 255, 255);
		
		Matrix4f pose = matrixStack.last().pose();

		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.debugFilledBox());

		vertexConsumer.vertex(pose, fxl, fyh, fzl).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxl, fyh, fzh).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxh, fyh, fzl).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxh, fyh, fzh).color(r, g, b, a).endVertex();
		
		vertexConsumer = bufferSource.getBuffer(RenderType.debugFilledBox());
		
		vertexConsumer.vertex(pose, fxl, fyl, fzl).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxl, fyh, fzl).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxh, fyl, fzl).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxh, fyh, fzl).color(r, g, b, a).endVertex();

		vertexConsumer = bufferSource.getBuffer(RenderType.debugFilledBox());
		
		vertexConsumer.vertex(pose, fxl, fyl, fzl).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxl, fyl, fzh).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxl, fyh, fzl).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxl, fyh, fzh).color(r, g, b, a).endVertex();
		
		vertexConsumer = bufferSource.getBuffer(RenderType.debugFilledBox());
		
		vertexConsumer.vertex(pose, fxh, fyl, fzl).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxh, fyl, fzh).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxl, fyl, fzl).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxl, fyl, fzh).color(r, g, b, a).endVertex();
		
		vertexConsumer = bufferSource.getBuffer(RenderType.debugFilledBox());
		
		vertexConsumer.vertex(pose, fxh, fyl, fzh).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxh, fyh, fzh).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxl, fyl, fzh).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxl, fyh, fzh).color(r, g, b, a).endVertex();
		
		vertexConsumer = bufferSource.getBuffer(RenderType.debugFilledBox());
		
		vertexConsumer.vertex(pose, fxh, fyh, fzl).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxh, fyh, fzh).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxh, fyl, fzl).color(r, g, b, a).endVertex();
		vertexConsumer.vertex(pose, fxh, fyl, fzh).color(r, g, b, a).endVertex();
		
		matrixStack.popPose();
		
	}
		
}
