package de.m_marvin.industria.core.client.util.screens;

import java.awt.Color;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import de.m_marvin.industria.core.client.util.GraphicsUtility;
import de.m_marvin.industria.core.util.container.IFluidSlotContainer;
import de.m_marvin.industria.core.util.container.IFluidSlotContainer.FluidSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

public abstract class AbstractFluidContainerScreen<T extends AbstractContainerMenu & IFluidSlotContainer> extends AbstractContainerScreen<T> {

	protected ResourceLocation fluidSlotTexture = GraphicsUtility.UTILITY_WIDGETS_TEXTURE;
	
	public AbstractFluidContainerScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	public void setFluidSlotTexture(ResourceLocation fluidSlotTexture) {
		this.fluidSlotTexture = fluidSlotTexture;
	}
	
	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		
		for (FluidSlot fluidSlot : this.menu.getFluidSlots()) {
			renderFluidSlot(pGuiGraphics, fluidSlot.getX(), fluidSlot.getY(), fluidSlot.getFluid().getFluid(), fluidSlot.getFluid().getAmount(), fluidSlot.getCapacity());
		}
	}
	
	public void renderFluidSlot(GuiGraphics guiGraphics, int x, int y, Fluid fluid, int amount, int maxAmount) {
		
		if (fluid != Fluids.EMPTY) {

			@SuppressWarnings("deprecation")
			ResourceLocation atlasTexture = TextureAtlas.LOCATION_BLOCKS;
			IClientFluidTypeExtensions fluidExtension = IClientFluidTypeExtensions.of(fluid);
			ResourceLocation fluidTextureName = fluidExtension.getStillTexture();
			Color fluidColor = new Color(fluidExtension.getTintColor());
			TextureAtlasSprite fluidTextureSprite = Minecraft.getInstance().getTextureAtlas(atlasTexture).apply(fluidTextureName);
			
			int fluidBarHeight = (int) (Math.min(50, Math.max(0, amount / (float) maxAmount)) * 50);
			
			if (fluid.getFluidType().getDensity() <= 0) {
				renderGasBar(guiGraphics, fluidTextureSprite, fluidColor, atlasTexture, x, y + 2, fluidBarHeight);
			} else {
				renderFluidBar(guiGraphics, fluidTextureSprite, fluidColor, atlasTexture, x, y + 36, fluidBarHeight);
			}
			
		}

		guiGraphics.blit(this.fluidSlotTexture, this.leftPos + x - 1, this.topPos + y - 1, 164, 1, 18, 54);
		
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(this.leftPos + x, this.topPos + y, 1);
		guiGraphics.pose().scale(0.3F, 0.3F, 0.3F);
		guiGraphics.drawString(font, Integer.toString(amount) + " mb", 0, 0, 0xFFFFFF, false);
		guiGraphics.pose().popPose();
		
	}
	
	public void renderFluidBar(GuiGraphics guiGraphics, TextureAtlasSprite fluidTextureSprite, Color fluidColor, ResourceLocation atlasTexture, int x, int y, int barHeight) {
		
		float u0 = fluidTextureSprite.getU0();
		float v0 = fluidTextureSprite.getV0();
		float u1 = fluidTextureSprite.getU1();
		float v1 = fluidTextureSprite.getV1();
		
		int i;
		for (i = 0; i < barHeight / 16; i++) {
			innerBlit(guiGraphics, atlasTexture, this.leftPos + x, this.topPos + y + -i * 16, 16, 16, 0, u0, u1, v0, v1, fluidColor.getRed() / 255F, fluidColor.getGreen() / 255F, fluidColor.getBlue() / 255F, 1);
		}
		
		int height = barHeight - i * 16;
		float v02 = v1 - (v1 - v0) * (height / 16F);
		innerBlit(guiGraphics, atlasTexture, this.leftPos + x, this.topPos + y + -i * 16 + 16 - height, 16, height, 0, u0, u1, v02, v1, fluidColor.getRed() / 255F, fluidColor.getGreen() / 255F, fluidColor.getBlue() / 255F, 1);
		
	}

	public void renderGasBar(GuiGraphics guiGraphics, TextureAtlasSprite fluidTextureSprite, Color fluidColor, ResourceLocation atlasTexture, int x, int y, int barHeight) {
		
		float u0 = fluidTextureSprite.getU0();
		float v0 = fluidTextureSprite.getV0();
		float u1 = fluidTextureSprite.getU1();
		float v1 = fluidTextureSprite.getV1();
		
		int i;
		for (i = 0; i < barHeight / 16; i++) {
			innerBlit(guiGraphics, atlasTexture, this.leftPos + x, this.topPos + y + i * 16, 16, 16, 0, u0, u1, v0, v1, fluidColor.getRed() / 255F, fluidColor.getGreen() / 255F, fluidColor.getBlue() / 255F, 1);
		}
		
		int height = barHeight - i * 16;
		float v12 = v0 + (v1 - v0) * (height / 16F);
		innerBlit(guiGraphics, atlasTexture, this.leftPos + x, this.topPos + y + i * 16, 16, height, 0, u0, u1, v0, v12, fluidColor.getRed() / 255F, fluidColor.getGreen() / 255F, fluidColor.getBlue() / 255F, 1);
		
	}
	
	public void innerBlit(GuiGraphics guiGraphics, ResourceLocation pAtlasLocation, int pX1, int pY1, int pWidth, int pHeight, int pBlitOffset, float pMinU, float pMaxU, float pMinV, float pMaxV, float pRed, float pGreen, float pBlue, float pAlpha) {
	      RenderSystem.setShaderTexture(0, pAtlasLocation);
	      RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
	      RenderSystem.enableBlend();
	      Matrix4f matrix4f = guiGraphics.pose().last().pose();
	      BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
	      bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
	      bufferbuilder.vertex(matrix4f, (float)pX1, (float)pY1, (float)pBlitOffset).color(pRed, pGreen, pBlue, pAlpha).uv(pMinU, pMinV).endVertex();
	      bufferbuilder.vertex(matrix4f, (float)pX1, (float)pY1 + pHeight, (float)pBlitOffset).color(pRed, pGreen, pBlue, pAlpha).uv(pMinU, pMaxV).endVertex();
	      bufferbuilder.vertex(matrix4f, (float)pX1 + pWidth, (float)pY1 + pHeight, (float)pBlitOffset).color(pRed, pGreen, pBlue, pAlpha).uv(pMaxU, pMaxV).endVertex();
	      bufferbuilder.vertex(matrix4f, (float)pX1 + pWidth, (float)pY1, (float)pBlitOffset).color(pRed, pGreen, pBlue, pAlpha).uv(pMaxU, pMinV).endVertex();
	      BufferUploader.drawWithShader(bufferbuilder.end());
	      RenderSystem.disableBlend();
	}
	
}
