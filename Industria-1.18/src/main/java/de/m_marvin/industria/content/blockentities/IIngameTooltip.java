package de.m_marvin.industria.content.blockentities;

import java.util.List;

import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;

import net.minecraft.network.chat.Component;

public interface IIngameTooltip extends IHaveGoggleInformation {
	
	@Override
	public boolean addToGoggleTooltip(final List<Component> tooltip, final boolean isPlayerSneaking);
	
}
