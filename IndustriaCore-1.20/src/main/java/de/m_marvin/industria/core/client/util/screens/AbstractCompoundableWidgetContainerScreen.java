package de.m_marvin.industria.core.client.util.screens;

import de.m_marvin.industria.core.client.util.widgets.AbstractCompoundWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class AbstractCompoundableWidgetContainerScreen<C extends AbstractContainerMenu> extends AbstractContainerScreen<C> {

	public AbstractCompoundableWidgetContainerScreen(C pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	@Override
	protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T pWidget) {
		if (pWidget instanceof AbstractCompoundWidget compound) {
			compound.getChildren().forEach(super::addWidget);
			compound.getRenderables().forEach(super::addRenderableOnly);
		}
		return super.addRenderableWidget(pWidget);
	}
	
	@Override
	protected <T extends Renderable> T addRenderableOnly(T pRenderable) {
		if (pRenderable instanceof AbstractCompoundWidget compound) {
			compound.getRenderables().forEach(super::addRenderableOnly);
		}
		return super.addRenderableOnly(pRenderable);
	}
	
	@Override
	protected <T extends GuiEventListener & NarratableEntry> T addWidget(T pListener) {
		if (pListener instanceof AbstractCompoundWidget compound) {
			compound.getChildren().forEach(super::addWidget);
		}
		return super.addWidget(pListener);
	}
	
}
