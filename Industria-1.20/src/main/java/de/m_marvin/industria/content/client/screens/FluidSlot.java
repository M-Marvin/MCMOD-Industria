package de.m_marvin.industria.content.client.screens;

import de.m_marvin.industria.IndustriaCore;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FluidSlot extends AbstractWidget {
	
	protected ResourceLocation texture = IndustriaCore.UTILITY_WIDGETS_TEXTURE;
	
	
	public FluidSlot(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
		super(pX, pY, pWidth, pHeight, pMessage);
		
	}

	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
		// TODO Auto-generated method stub
		
	}

}
