package de.m_marvin.industria.core.client.electrics.screens;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.AbstractJunctionEditContainer;
import de.m_marvin.industria.core.util.Direction2d;
import de.m_marvin.univec.impl.Vec2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PowerSourceJunctionScreen extends AbstractJunctionEditScreen {

	public static final ResourceLocation POWER_SOURCE_LOCATION = new ResourceLocation(IndustriaCore.MODID, "textures/gui/junction_box.png");
	
	public PowerSourceJunctionScreen(AbstractJunctionEditContainer<? extends IJunctionEdit> pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	@Override
	protected void init() {
		super.init();
		
		this.titleLabelY = -10;

		this.conduitNodes = this.menu.getCableNodes();
		this.cableNodes = new CableNode[2];
		this.cableNodes[0] = new CableNode(new Vec2i(70, 8), Direction2d.UP, this.conduitNodes[0]);
		this.cableNodes[1] = new InternalNode(new Vec2i(70, 112), Direction2d.DOWN, 0);
		
	}

	@Override
	protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
		super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
		int i = this.leftPos;
		int j = this.topPos;
		pGuiGraphics.blit(getJunctionBoxTexture(), i, j - 14, 0, 138, this.imageWidth, 14);
	}
	
	@Override
	protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
		pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
	}
	
	@Override
	public ResourceLocation getJunctionBoxTexture() {
		return POWER_SOURCE_LOCATION;
	}
	
}
