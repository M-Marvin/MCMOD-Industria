package de.m_marvin.industria.content.client.screens;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.types.containers.IFluidSlotContainer.FluidSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FluidSlotRenderable extends AbstractWidget {
	
	protected ResourceLocation texture = IndustriaCore.UTILITY_WIDGETS_TEXTURE;
	protected final FluidSlot fluidSlot;
	
	public FluidSlotRenderable(int pX, int pY, FluidSlot fluidSlot) {
		super(pX, pY, 18, 18 * 3, Component.empty());
		this.fluidSlot = fluidSlot;
	}
	
	public void setTexture(ResourceLocation texture) {
		this.texture = texture;
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		
		
		
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}

}
