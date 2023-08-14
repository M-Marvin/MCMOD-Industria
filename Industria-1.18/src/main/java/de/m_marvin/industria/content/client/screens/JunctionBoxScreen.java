package de.m_marvin.industria.content.client.screens;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.electrics.AbstractJunctionBoxScreen;
import de.m_marvin.industria.core.electrics.types.blockentities.AbstractJunctionBoxBlockEntity;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class JunctionBoxScreen extends AbstractJunctionBoxScreen {
	
	public static final ResourceLocation JUNCTION_BOX_LOCATION = new ResourceLocation(IndustriaCore.MODID, "textures/gui/junction_box.png");
	
	public JunctionBoxScreen(JunctionBoxContainer<? extends AbstractJunctionBoxBlockEntity> pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}

	@Override
	public ResourceLocation getJunctionBoxTexture() {
		return JUNCTION_BOX_LOCATION;
	}
	
}
