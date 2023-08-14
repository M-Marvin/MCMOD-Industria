package de.m_marvin.industria.core.client.electrics.screens;

import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.AbstractJunctionEditContainer;
import de.m_marvin.industria.core.util.Direction2d;
import de.m_marvin.univec.impl.Vec2i;
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
	protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
		this.font.draw(pPoseStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
	}
	
	@Override
	public ResourceLocation getJunctionBoxTexture() {
		return POWER_SOURCE_LOCATION;
	}
	
}
