package de.m_marvin.industria.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import de.m_marvin.industria.Industria;
import net.minecraft.core.BlockPos;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE,modid=Industria.MODID)
public class ConduitWorldRenderer {
	
	@SubscribeEvent
	public static void onWorldRenderLast(RenderLevelLastEvent event) {
		
		BlockPos pos = new BlockPos(0,0,0);
		
		
		
		//MultiBufferSource source;
		
		PoseStack matrixStack = event.getPoseStack();
		VertexConsumer vertexConsumer = VertexMultiConsumer.create();
		
		Matrix4f matrix4f = matrixStack.last().pose();
		Matrix3f matrix3f = matrixStack.last().normal();
		
		matrixStack.pushPose();
		
		vertexConsumer.vertex(matrix4f, pos.getX() + 0, pos.getY() + 0, pos.getZ() + 0).uv(0, 0).color(2555).normal(1, 1, 1).overlayCoords(0).endVertex();
		vertexConsumer.vertex(matrix4f, pos.getX() + 16, pos.getY() + 0, pos.getZ() + 0).uv(0, 0).color(2555).normal(1, 1, 1).overlayCoords(0).endVertex();
		vertexConsumer.vertex(matrix4f, pos.getX() + 16, pos.getY() + 16, pos.getZ() + 0).uv(0, 0).color(2555).normal(1, 1, 1).overlayCoords(0).endVertex();
		
		matrixStack.popPose();
		
	}
	
}
