package de.m_marvin.industria.core.client.util.screens;

import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class AbstractTickableWidgedContainerScreen<T extends AbstractContainerMenu> extends AbstractCompoundableWidgetContainerScreen<T> {
	
	public AbstractTickableWidgedContainerScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	@Override
	public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
		if (this.getFocused() != null && this.isDragging() && pButton == 0 ? this.getFocused().mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY) : false) return true;
		return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
	}
	
	@Override
	protected void containerTick() {
		this.children().forEach(s -> { if (s instanceof Tickable t) t.tick(); });
		super.containerTick();
	}
	
}
