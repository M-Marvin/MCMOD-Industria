package de.m_marvin.industria.core.client.util.widgets;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import de.m_marvin.industria.core.client.util.GraphicsUtility;
import de.m_marvin.industria.core.client.util.widgets.StatusBar.BarSegment;
import de.m_marvin.industria.core.util.MathUtility;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class AnalogGauge extends AbstractScaleWidget {
	
	public AnalogGauge(int x, int y, Component message) {
		super(x, y, 38, 33, message, GraphicsUtility.UTILITY_WIDGETS_TEXTURE_CREATIVE);
		this.scale1 = 5;
		this.scale2 = 5;
		this.segments = new BarSegment[] { new BarSegment(0F, 1F, 1F, 1F, 1F) };
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		
		pGuiGraphics.blit(this.texture, getX(), getY(), 137, 1, 38, 33);
		
		renderColorBar(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		
		renderScale(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		
		pGuiGraphics.pose().pushPose();
		translateScala(pGuiGraphics, this.displayState);
		pGuiGraphics.pose().translate(-2.5F, -21, 0);
		pGuiGraphics.blit(this.texture, 0, 0, 137, 36, 5, 23);
		pGuiGraphics.pose().popPose();
		
		pGuiGraphics.pose().pushPose();
		pGuiGraphics.pose().translate(getX() + 19, getY() + 26.5, 0);
		pGuiGraphics.pose().scale(0.5F, 0.5F, 0.5F);
		pGuiGraphics.drawCenteredString(getFont(), getInfo(), 0, 0, 0xFFFFFFFF);
		pGuiGraphics.pose().popPose();
		
		pGuiGraphics.drawString(getFont(), getMessage(), getX() + 1, getY() - 9, 0xFFFFFFFF);
		
	}
	
	protected void renderColorBar(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

		for (var segment : this.segments) {
			for (float r = segment.start(); r <= segment.end(); r += 0.025F) {
				pGuiGraphics.pose().pushPose();
				translateScala(pGuiGraphics, r);
				pGuiGraphics.pose().translate(-0.5F, -15, 0);
				pGuiGraphics.fill(0, 0, 1, 1, MathUtility.toIntegerColor(segment.red(), segment.green(), segment.blue(), 1F));
				pGuiGraphics.pose().popPose();
			}
		}
		
	}
	
	protected void renderScale(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		
		if (this.scale1 < 1F)
			return;
		
		float tlength = 1F;
		float sl1 = tlength / this.scale1;
		float sl2 = this.scale2 < 1F ? sl1 : sl1 / this.scale2;
		float so1 = (tlength * this.zero) % sl1;
		float so2 = this.scale2 < 1F ? so1 : (tlength * this.zero) % sl2;
		for (float p1 = so2; p1 < tlength; p1 += sl2) {
			
			pGuiGraphics.pose().pushPose();
			translateScala(pGuiGraphics, p1);
			pGuiGraphics.pose().translate(-2.5F, -21, 0);
			
			if (Math.abs((p1 - so1 + 0.01F) % sl1) < 0.02F)
				pGuiGraphics.blit(this.texture, 0, 0, 144, 36, 5, 23);
			else
				pGuiGraphics.blit(this.texture, 0, 0, 151, 36, 5, 23);
			
			pGuiGraphics.pose().popPose();
			
		}
		
	}
	
	protected void translateScala(GuiGraphics pGuiGraphics, float position) {

		float r = (float) ((-0.3 + position / 1.666F) * Math.PI);
		
		pGuiGraphics.pose().translate(getX() + 19, getY() + 22, 0);
		pGuiGraphics.pose().mulPose(new Quaternionf(new AxisAngle4f(r, 0, 0, 1)));
		
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
