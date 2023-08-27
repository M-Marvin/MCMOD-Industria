package de.m_marvin.industria.content.client.screens;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.container.MobileFuelGeneratorContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MobileFuelGeneratorScreen extends AbstractContainerScreen<MobileFuelGeneratorContainer> {
	
	public static final ResourceLocation SCREEN_TEXTURE = new ResourceLocation(Industria.MODID, "textures/gui/mobile_fuel_generator");
	
	public MobileFuelGeneratorScreen(MobileFuelGeneratorContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}

	@Override
	protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
		
		pGuiGraphics.blit(SCREEN_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		
	}
	
}
