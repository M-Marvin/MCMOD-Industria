package de.m_marvin.industria.core.client.electrics.screens;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.JunctionBoxContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

public class JunctionBoxScreen<B extends BlockEntity & IJunctionEdit, C extends JunctionBoxContainer<B>> extends AbstractJunctionEditScreen<B, C> {

	public static final ResourceLocation JUNCTION_BOX_UI_LOCATION = new ResourceLocation(IndustriaCore.MODID, "textures/gui/junction_box.png");
	
	public JunctionBoxScreen(C pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	@Override
	protected void init() {
		super.init();
		
		this.titleLabelY = -10;
		
		try {
			this.conduitNodes = this.menu.getCableNodes();
			List<CableNode> nodes = new ArrayList<>();
			this.menu.setupScreenConduitNodes(this.conduitNodes, 
					(position, orientation, node) -> nodes.add(new CableNode(this, position, orientation, node)),
					(position, orientation, internalId) -> nodes.add(new InternalNode(this, position, orientation, internalId))
			);
			this.cableNodes = nodes.toArray(i -> new CableNode[i]);
			this.connectsOnlyToInternal = this.menu.connectsOnlyToInternal();
		} catch (Throwable e) {
			e.printStackTrace();
			this.cableNodes = new CableNode[0];
		}
		
	}
	
	@Override
	protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
		if (!this.title.getString().isEmpty()) pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
		super.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
		int i = this.leftPos;
		int j = this.topPos;
		if (!this.title.getString().isEmpty()) pGuiGraphics.blit(getJunctionBoxTexture(), i, j - 14, 0, 138, this.imageWidth, 14);
	}
	
	@Override
	public ResourceLocation getJunctionBoxTexture() {
		return JUNCTION_BOX_UI_LOCATION;
	}
	
}
