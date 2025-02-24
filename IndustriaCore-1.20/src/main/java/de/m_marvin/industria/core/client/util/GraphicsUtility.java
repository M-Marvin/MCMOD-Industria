package de.m_marvin.industria.core.client.util;

import java.awt.Color;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

public class GraphicsUtility {

	private GraphicsUtility() {}
	
	public static final ResourceLocation UTILITY_WIDGETS_TEXTURE = new ResourceLocation(IndustriaCore.MODID, "textures/gui/utility_widgets.png");
	
	public static void drawStringCentered(PoseStack matrixStack, MultiBufferSource bufferSource, String string, int posX, int posY, float r, float g, float b, float a) {
		drawStringCentered(matrixStack, bufferSource, string, posX, posY, new Color(r, g, b, a).getRGB());
	}

	@SuppressWarnings("resource")
	public static void drawStringCentered(PoseStack matrixStack, MultiBufferSource bufferSource, String string, int x, int y, int color) {
		float width = Minecraft.getInstance().font.width(string);
		Minecraft.getInstance().font.drawInBatch(string, x + -width / 2, y, color, false, matrixStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880, false);
	}
	
	public static void drawString(PoseStack matrixStack, MultiBufferSource bufferSource, String string, int posX, int posY, float r, float g, float b, float a) {
		drawString(matrixStack, bufferSource, string, posX, posY, new Color(r, g, b, a).getRGB());
	}

	@SuppressWarnings("resource")
	public static void drawString(PoseStack matrixStack, MultiBufferSource bufferSource, String string, int x, int y, int color) {
		Minecraft.getInstance().font.drawInBatch(string, x, y, color, false, matrixStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880, false);
	}
	
	public static void renderVector(VertexConsumer vertexConsumer, PoseStack matrixStack, Vec3f origin, Vec3f vector, int r, int g, int b, int a) {
		
		Matrix4f pose = matrixStack.last().pose();
		Matrix3f normal = matrixStack.last().normal();
		
		Vec3f normalv = vector.normalize();
		Vec3f vector2 = origin.add(vector);
		
		vertexConsumer.vertex(pose, origin.x, origin.y, origin.z).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();
		vertexConsumer.vertex(pose, vector2.x, vector2.y, vector2.z).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();
		
		vertexConsumer.vertex(pose, origin.x - 0.1F, origin.y - 0.1F, origin.z - 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();
		vertexConsumer.vertex(pose, origin.x + 0.1F, origin.y + 0.1F, origin.z + 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();

		vertexConsumer.vertex(pose, origin.x + 0.1F, origin.y - 0.1F, origin.z - 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();
		vertexConsumer.vertex(pose, origin.x - 0.1F, origin.y + 0.1F, origin.z + 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();

		vertexConsumer.vertex(pose, origin.x - 0.1F, origin.y - 0.1F, origin.z + 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();
		vertexConsumer.vertex(pose, origin.x + 0.1F, origin.y + 0.1F, origin.z - 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();

		vertexConsumer.vertex(pose, origin.x + 0.1F, origin.y - 0.1F, origin.z + 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();
		vertexConsumer.vertex(pose, origin.x - 0.1F, origin.y + 0.1F, origin.z - 0.1F).color(r, g, b, a).normal(normal, normalv.x, normalv.y, normalv.z).endVertex();
		
	}
	
}
