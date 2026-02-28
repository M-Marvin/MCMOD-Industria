package de.m_marvin.industria.core.client.util.widgets;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;

import de.m_marvin.industria.core.client.util.ClientTimer;
import de.m_marvin.industria.core.client.util.GraphicsUtility;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class LargeLever extends AbstractTextureWidget {
	
	protected float time;
	protected float leverPosition = 0.0F;
	protected float leverReleasePosition;
	protected float leverReleaseTime;
	protected boolean leverGrabbed = false;
	protected double leverGrabPos;

	protected boolean leverIndicator = false;
	protected boolean leverState = false;
	protected Consumer<Boolean> action = null;
	
	public LargeLever(int pX, int pY, Component title) {
		super(pX, pY, 38, 54, title, GraphicsUtility.UTILITY_WIDGETS_TEXTURE_CREATIVE);
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		
		pGuiGraphics.blit(this.texture, this.getX(), this.getY(), 197, 1, 38, 54);
		
		this.time = ClientTimer.getRenderTicks();
		
		renderArrows(pGuiGraphics, (!this.leverState && this.leverIndicator) ? (int) (this.time % 40 / 10) : 0);
		renderLever(pGuiGraphics, this.leverPosition / 33F);
		
		if (!this.leverState && this.leverPosition > 0F && !this.leverGrabbed) {
			this.leverPosition = (1F - (this.time - this.leverReleaseTime) / 10F) * this.leverReleasePosition;
		}

		pGuiGraphics.drawString(getFont(), getMessage(), getX() + 1, getY() - 9, 0xFFFFFFFF);
		
	}
	
	public void resetLever() {
		this.leverState = false;
		this.leverIndicator = false;
		releaseLever();
	}
	
	public void releaseLever() {
		this.leverGrabbed = false;
		this.leverReleasePosition = this.leverPosition;
		this.leverReleaseTime = this.time;
	}
	
	public void setIndicator() {
		this.leverIndicator = true;
	}
	
	public void onLeverChanges(boolean state) {
		if (this.action != null)
			this.action.accept(state);
	}
	
	public void setLeverState(boolean leverState) {
		this.leverState = leverState;
		this.leverPosition = this.leverState ? 33F : 0F;
	}
	
	public boolean getLeverState() {
		return this.leverState;
	}
	
	public void setLeverIndicator(boolean leverIndicator) {
		this.leverIndicator = leverIndicator;
	}
	
	public void setAction(Consumer<Boolean> action) {
		this.action = action;
	}
	
	@Override
	public void onRelease(double pMouseX, double pMouseY) {
		super.onRelease(pMouseX, pMouseY);
		releaseLever();
	}
	
	@Override
	public void onClick(double pMouseX, double pMouseY) {
		super.onClick(pMouseX, pMouseY);
		this.leverGrabbed = true;
		this.leverGrabPos = pMouseY - this.leverPosition;
	}
	
	@Override
	protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
		super.onDrag(pMouseX, pMouseY, pDragX, pDragY);
		if (this.leverGrabbed) this.leverPosition = (float) Math.min(33F, Math.max(0F, pMouseY - this.leverGrabPos));
		
		if (this.leverPosition > 30F) {
			if (this.leverState == false) onLeverChanges(true);
			this.leverState = true;
			this.leverIndicator = false;
			this.leverPosition = 33.0F;
		} else {
			if (this.leverState == true) onLeverChanges(false);
			this.leverState = false;
		}
	}
	
	public void renderArrows(GuiGraphics pGuiGraphics, int lit) {

		RenderSystem.enableBlend();
		pGuiGraphics.blit(this.texture, this.getX() + 10, this.getY() + 14, 207, lit == 1 ? 90 : 81, 17, 7);
		pGuiGraphics.blit(this.texture, this.getX() + 10, this.getY() + 24, 207, lit == 2 ? 90 : 81, 17, 7);
		pGuiGraphics.blit(this.texture, this.getX() + 10, this.getY() + 34, 207, lit == 3 ? 90 : 81, 17, 7);
		RenderSystem.disableBlend();
		
	}
	
	public void renderLever(GuiGraphics pGuiGraphics, float position) {

		float a = (float) Math.cos((1 - position) * Math.PI);
		int sp = Math.round(a * 19);
		int bp =  Math.round(a * 1.5F + 0.5F);
		int bh = Math.round(a * 15);
		
		RenderSystem.disableCull();
		pGuiGraphics.blit(this.texture, this.getX() + 0, this.getY() + 26 + bp, 38, bh, 197, 64, 38, 15, 256, 256);
		RenderSystem.enableCull();
		pGuiGraphics.blit(this.texture, this.getX() + 0, this.getY() + 24 + sp, 197, 57, 38, 5);
		
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
		pNarrationElementOutput.add(NarratedElementType.TITLE, getMessage());
	}
	
}
