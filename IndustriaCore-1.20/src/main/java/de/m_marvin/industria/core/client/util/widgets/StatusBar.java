package de.m_marvin.industria.core.client.util.widgets;

import com.mojang.blaze3d.systems.RenderSystem;

import de.m_marvin.industria.core.client.util.GraphicsUtility;
import de.m_marvin.industria.core.util.MathUtility;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class StatusBar extends AbstractScaleWidget {
	
	public static record BarSegment(float start, float end, float red, float green, float blue) {}
	
	public StatusBar(int x, int y, int length, Component message) {
		super(x, y, Math.max(length, 16), 12, message, GraphicsUtility.UTILITY_WIDGETS_TEXTURE_CREATIVE);
		this.scale1 = 0;
		this.scale2 = 0;
		this.segments = new BarSegment[] { new BarSegment(0F, 1F, 1F, 1F, 1F) };
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		
		pGuiGraphics.blit(this.texture, getX(), getY(), 1, 1, 1, this.height);
		pGuiGraphics.blitRepeating(this.texture, getX() + 1, getY(), this.width - 2, this.height, 1, 1, 128, 12);
		pGuiGraphics.blit(this.texture, getX() + this.width - 1, getY(), 128, 1, 1, this.height);
		
		renderBar(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		
		renderScale(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		
		pGuiGraphics.pose().pushPose();
		pGuiGraphics.pose().translate(getX() + 4, getY() + 4, 0);
		pGuiGraphics.pose().scale(0.5F, 0.5F, 0.5F);
		pGuiGraphics.drawString(getFont(), getInfo(), 0, 0, 0xFFFFFFFF);
		pGuiGraphics.pose().popPose();

		pGuiGraphics.drawString(getFont(), getMessage(), getX() + 1, getY() - 9, 0xFFFFFFFF);
		
	}
	
	protected void renderBar(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

		int tlength = this.width - 2;
		int progress = Math.round(this.displayState * tlength);
		
		if (progress > 0) {
			for (var segment : this.segments) {
				if (segment.start() >= this.displayState) break;
				
				int x = Math.round(segment.start() * tlength);
				int l = Math.min(progress, Math.round(segment.end() * tlength)) - x;
				if (l == 0) break;
				
				RenderSystem.setShaderColor(segment.red(), segment.green(), segment.blue(), 1.0F);
				GraphicsUtility.UI.blitTilable(pGuiGraphics, this.texture, getX() + 1 + x, getY(), 2 + x, 15, l, this.height, 2, 15, 126, 12);
				
			}
			RenderSystem.setShaderColor(1, 1, 1, 1);
		}
		
	}
	
	protected void renderScale(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		
		if (this.scale1 < 1F)
			return;

		int tlength = this.width - 2;
		float sl1 = tlength / this.scale1;
		float sl2 = this.scale2 < 1F ? sl1 : sl1 / this.scale2;
		float so1 = (tlength * this.zero) % sl1;
		float so2 = this.scale2 < 1F ? so1 : (tlength * this.zero) % sl2;
		for (float p1 = so2; p1 < tlength; p1 += sl2) {
			
			pGuiGraphics.pose().pushPose();
			pGuiGraphics.pose().translate(p1 - 0.5F, 0, 0);

			if (Math.abs((p1 - so1 + 0.01F) % sl1) < 0.02F)
				pGuiGraphics.blit(this.texture, getX() - 1, getY(), 158, 36, 5, 10);
			else
				pGuiGraphics.blit(this.texture, getX() - 1, getY(), 165, 36, 5, 10);
			
			pGuiGraphics.pose().popPose();
			
		}
		
	}
	
	protected void renderColorBar(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

		int tlength = this.width - 2;
		
		for (int i = 0; i < this.segments.length; i++) {
			var segment = this.segments[i];
			int x = Math.round(segment.start() * tlength);
			int l = Math.round((segment.end() - segment.start()) * tlength);
			
			pGuiGraphics.fill(getX() + 4 + x, getY() + 13, getX() - 1 + x + l, getY() + 14, MathUtility.toIntegerColor(segment.red(), segment.green(), segment.blue(), 1F));
		}

		for (int i = 1; i < this.segments.length; i++) {
			var segment = this.segments[i];
			int x = Math.round(segment.start() * tlength);
			
			pGuiGraphics.blit(this.texture, getX() + x, getY() - 1, 131, 1, 3, 15);
		}

	}

	@Override
	protected boolean clicked(double pMouseX, double pMouseY) {
		return false;
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
		pNarrationElementOutput.add(NarratedElementType.TITLE, getMessage());
		pNarrationElementOutput.add(NarratedElementType.TITLE, getInfo());
	}
	
}
