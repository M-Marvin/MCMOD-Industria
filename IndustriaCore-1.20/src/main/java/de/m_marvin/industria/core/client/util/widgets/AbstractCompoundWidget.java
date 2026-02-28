package de.m_marvin.industria.core.client.util.widgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class AbstractCompoundWidget extends AbstractWidget {
	
	protected List<AbstractWidget> children = new ArrayList<AbstractWidget>();
	protected List<Renderable> renderables = new ArrayList<Renderable>();
	protected List<NarratableEntry> narratables = new ArrayList<NarratableEntry>();
	
	public AbstractCompoundWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
		super(pX, pY, pWidth, pHeight, pMessage);
	}

	protected AbstractWidget addRenderableWidget(AbstractWidget pWidget) {
		this.renderables.add(pWidget);
		return this.addWidget(pWidget);
	}

	protected <T extends Renderable> T addRenderableOnly(T pRenderable) {
		this.renderables.add(pRenderable);
		return pRenderable;
	}

	protected AbstractWidget addWidget(AbstractWidget pListener) {
		this.children.add(pListener);
		this.narratables.add(pListener);
		return pListener;
	}

	protected void removeWidget(GuiEventListener pListener) {
		if (pListener instanceof Renderable) {
			this.renderables.remove((Renderable)pListener);
		}

		if (pListener instanceof NarratableEntry) {
			this.narratables.remove((NarratableEntry)pListener);
		}

		this.children.remove(pListener);
	}

	protected void clearWidgets() {
		this.renderables.clear();
		this.children.clear();
		this.narratables.clear();
	}

	public List<Renderable> getRenderables() {
		return renderables;
	}
	
	public List<AbstractWidget> getChildren() {
		return children;
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		pGuiGraphics.drawString(Minecraft.getInstance().font, getMessage(), getX() + 1, getY() - 9, 0xFFFFFFFF);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {
		pNarrationElementOutput.add(NarratedElementType.TITLE, getMessage());
		for (var narratable : this.narratables)
			narratable.updateNarration(pNarrationElementOutput);
	}
	
}
