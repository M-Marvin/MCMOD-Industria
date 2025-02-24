package de.m_marvin.industria.core.client.util;

import net.minecraft.client.Minecraft;

public class ClientTimer {
	
	private ClientTimer() {}
	
	@SuppressWarnings("resource")
	public static int getTicks() {
		return Minecraft.getInstance().levelRenderer.getTicks();
	}
	
	public static float getPartialTick() {
		return Minecraft.getInstance().getPartialTick();
	}
	
	public static float getRenderTicks() {
		return getTicks() + getPartialTick();
	}
	
}
