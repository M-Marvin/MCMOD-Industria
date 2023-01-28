package de.m_marvin.industria.core.util;

import java.util.List;

import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class Formater {
	
	protected MutableComponent textComponent;
	
	public static Formater build() {
		return new Formater();
	}
	
	public Formater appand(Component comp) {
		textComponent = (textComponent == null) ? (MutableComponent) comp : textComponent.append(comp);
		return this;
	}
	
	public Formater text(String s) {
		return appand(new TextComponent(s));
	}
	
	public Formater translate(String key) {
		return appand(new TranslatableComponent(key));
	}

	public Formater translate(String key, Object... args) {
		return appand(new TranslatableComponent(key, args));
	}
	
	public Formater space() {
		return text(" ");
	}
	
	public Formater space(int spaces) {
		return text(Strings.repeat(' ', spaces));
	}
	
	public Formater newLine() {
		return text("\n");
	}
	
	public Formater withStyle(Style style) {
		if (textComponent != null) textComponent.withStyle(style);
		return this;
	}
	
	public Formater withStyle(ChatFormatting format) {
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
