package de.m_marvin.industria.core.util;

import java.util.List;

import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;


public class Formatter {
	
	protected MutableComponent textComponent;
	
	public static Formatter build() {
		return new Formatter();
	}
	
	public Formatter appand(Component comp) {
		textComponent = (textComponent == null) ? (MutableComponent) comp : textComponent.append(comp);
		return this;
	}
	
	public Formatter text(String s) {
		return appand(Component.literal(s));
	}
	
	public Formatter translate(String key) {
		return appand(Component.translatable(key));
	}

	public Formatter translate(String key, Object... args) {
		return appand(Component.translatable(key, args));
	}
	
	public Formatter space() {
		return text(" ");
	}
	
	public Formatter space(int spaces) {
		return text(Strings.repeat(' ', spaces));
	}
	
	public Formatter newLine() {
		return text("\n");
	}
	
	public Formatter withStyle(Style style) {
		if (textComponent != null) textComponent.withStyle(style);
		return this;
	}
	
	public Formatter withStyle(ChatFormatting format) {
		if (textComponent != null) textComponent.withStyle(format);
		return this;
	}
	
	public Component component() {
		return this.textComponent;
	}
	
	public void addTooltip(List<Component> tooltip) {
		tooltip.add(this.component());
	}
	
}
