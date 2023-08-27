package de.m_marvin.industria.core.client.electrics.screens.widgets;

import de.m_marvin.industria.IndustriaCore;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class StatusBar extends AbstractWidget {
	
	protected ResourceLocation texture = IndustriaCore.UTILITY_WIDGETS_TEXTURE;
	protected final Font font;
	protected float redRange;
	protected float greenRange;
	protected float percentageOffset;
	protected float state;
	protected Component percentageText;
	
	public StatusBar(Font font, int x, int y, int length, Component title, float greenRange, float percentageOffset, float redRange) {
		super(x, y, Math.min(length, 164), 16, title);
		this.greenRange = greenRange;
		this.redRange = redRange;
		this.percentageOffset = percentageOffset;
		this.font = font;
		updatePercentageString();
	}
	
	public void setTexture(ResourceLocation texture) {
		this.texture = texture;
	}
	
	public void setRedRange(float redRange) {
		this.redRange = redRange;
	}
	
	public float getGreenRange() {
		return greenRange;
	}
	
	public void setGreenRange(float greenRange) {
		this.greenRange = greenRange;
	}
	
	public void setStatus(float precentage) {
		this.state = Math.max(0, precentage);
		updatePercentageString();
	}
	
	public Font getFont() {
		return font;
	}
	
	protected void updatePercentageString() {
		percentageText = Component.literal(String.format("%.0f%%", (state / percentageOffset) * 100));
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		
		pGuiGraphics.blit(texture, this.getX(), this.getY() + 1, 1, 36, 1, 12);
		pGuiGraphics.blit(texture, this.getX() + 1, this.getY() + 1, 2, 36, this.getWidth() - 2, 12);
		pGuiGraphics.blit(texture, this.getX() + this.getWidth() - 1, this.getY() + 1, 162, 36, 10, 12);
		
		float length = this.getWidth() - 2;
		int l = Math.round(Math.min(1, state) * length);
		int lg = Math.round(this.greenRange * length);
		int lr = Math.round(this.redRange * length);
		int color = 0xFFFFFF;
		if (l > 0) {
			pGuiGraphics.blit(texture, this.getX() + 1, this.getY() + 2, 2, 13, Math.min(lg, l), 10);
		}
		if (l >= lg) {
			pGuiGraphics.blit(texture, this.getX() + 1 + lg, this.getY() + 2, 2, 25, Math.min(lr, l) - lg, 10);
			color = 0x009F00;
		}
		if (l >= lr) {
			pGuiGraphics.blit(texture, this.getX() + 1 + lr, this.getY() + 2, 2, 1, l - lr, 10);
			color = 0xFF0000;
		}
		
		if (lg > 0) pGuiGraphics.blit(texture, this.getX() + lg, this.getY(), 2, 50, 3, 15);
		if (lr > 0) pGuiGraphics.blit(texture, this.getX() + lr, this.getY(), 2, 50, 3, 15);
		
		pGuiGraphics.drawString(this.font, this.percentageText, this.getX() + 2, this.getY() + 3, color);
		pGuiGraphics.drawString(font, getMessage(), this.getX() + 2, this.getY() - 8, 0xFFFFFF);
		
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
		pNarrationElementOutput.add(NarratedElementType.TITLE, getMessage());
		pNarrationElementOutput.add(NarratedElementType.TITLE, this.percentageText);
	}
	
}
