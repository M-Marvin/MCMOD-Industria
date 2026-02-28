package de.m_marvin.industria.core.client.util.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractTextureWidget extends AbstractWidget {
	
	protected ResourceLocation texture;
	
	public AbstractTextureWidget(int x, int y, int width, int height, Component message, ResourceLocation texture) {
		super(x, y, width, height, message);
		this.texture = texture;
	}
	
	public void setTexture(ResourceLocation texture) {
		this.texture = texture;
	}
	
	public ResourceLocation getTexture() {
		return texture;
	}
	
	public Font getFont() {
		return Minecraft.getInstance().font;
	}
	
}
