package de.m_marvin.industria.core.client.util;

import java.awt.Color;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;

public class GraphicsUtility {
	
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
	
}
