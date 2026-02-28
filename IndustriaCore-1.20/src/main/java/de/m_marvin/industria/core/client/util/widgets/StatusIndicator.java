package de.m_marvin.industria.core.client.util.widgets;

import de.m_marvin.industria.core.client.util.ClientTimer;
import de.m_marvin.industria.core.client.util.GraphicsUtility;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class StatusIndicator extends AbstractTextureWidget {
	
	public static final int STATUS_POWER_ACTIVE = 1;
	public static final int STATUS_POWER_PENDING = 2;
	public static final int STATUS_WARNING = 3;
	
	protected boolean blink = false;
	protected int status = 0;
	protected long statusChange = 0;
	
	public StatusIndicator(int x, int y, Component message) {
		super(x, y, 18, 15, message, GraphicsUtility.UTILITY_WIDGETS_TEXTURE_CREATIVE);
	}

	public void setStatus(int status, boolean blink) {
		this.blink = blink;
		if (this.status != status)
			this.statusChange = ClientTimer.getTicks();
		this.status = status;
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		
		pGuiGraphics.blit(this.texture, getX(), getY(), 237, 1, 18, 15);
		
		boolean blinkOn = !this.blink || ((ClientTimer.getTicks() - this.statusChange) % 20) < 10;
		if (blinkOn && this.status != 0)
			pGuiGraphics.blit(this.texture, getX(), getY(), 237, 18 + (this.status - 1) * 16, 18, 15);
		
		pGuiGraphics.drawString(getFont(), getMessage(), getX() + 1, getY() - 9, 0xFFFFFFFF);
		
	}

	@Override
	protected boolean clicked(double pMouseX, double pMouseY) {
		return false;
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
		pNarrationElementOutput.add(NarratedElementType.TITLE, getMessage());
	}
	
}
