package de.redtec.util;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;

public class FluidBarTexture {
	
	@SuppressWarnings("deprecation")
	public static void drawFluidTexture(MatrixStack matrixStack, AbstractGui screen, Fluid fluid, int x, int y, int width, int height, int alpha) {

		if (fluid != Fluids.EMPTY) {
			
			ResourceLocation textureKey = fluid.getAttributes().getStillTexture();
			ResourceLocation texture = new ResourceLocation(textureKey.getNamespace(), "textures/" + textureKey.getPath() + ".png");
			
			AnimatedTexture animatedTexture = AnimatedTexture.prepareTexture(texture);
			
			Color color = new Color(fluid.getAttributes().getColor());
			
			GlStateManager.color4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, alpha / 255F);
			
			y -= height;
			for (int ox = 0; ox < width;) {
				int difX = Math.min(8, width - ox);
				for (int oy = 0; oy < height;) {
					
					int difY = Math.min(8, height - oy);
					animatedTexture.draw(matrixStack, screen, x + ox, y + oy, difX, difY);
					oy += difY;
					
				}
				ox += difX;
			}
			
		}
		
	}
	
	public static void drawFluidTexture(MatrixStack matrixStack, AbstractGui screen, Fluid fluid, int x, int y, int width, int height) {
		drawFluidTexture(matrixStack, screen, fluid, x, y, width, height, 255);
	}
	
}
