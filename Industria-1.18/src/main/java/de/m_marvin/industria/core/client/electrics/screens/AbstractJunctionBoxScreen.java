package de.m_marvin.industria.core.client.electrics.screens;

import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.AbstractJunctionEditContainer;
import de.m_marvin.industria.core.util.Direction2d;
import de.m_marvin.univec.impl.Vec2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class AbstractJunctionBoxScreen extends AbstractJunctionEditScreen {

	public AbstractJunctionBoxScreen(AbstractJunctionEditContainer<? extends IJunctionEdit> pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	@Override
	protected void init() {
		super.init();

		this.conduitNodes = this.menu.getCableNodes();
		this.cableNodes = new CableNode[4];
		this.cableNodes[0] = new CableNode(new Vec2i(70, 8), Direction2d.UP, this.conduitNodes[0]);
		this.cableNodes[1] = new CableNode(new Vec2i(70, 112), Direction2d.DOWN, this.conduitNodes[1]);
		this.cableNodes[2] = new CableNode(new Vec2i(8, 70), Direction2d.LEFT, this.conduitNodes[2]);
		this.cableNodes[3] = new CableNode(new Vec2i(112, 70), Direction2d.RIGHT, this.conduitNodes[3]);
		
	}

	@Override
	protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
		// Disabling screen titles
		//super.renderLabels(pPoseStack, pMouseX, pMouseY);
	}
	
}
