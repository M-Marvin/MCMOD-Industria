package de.m_marvin.industria.content.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.content.container.JunctionBoxContainer;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class JunctionBoxScreen extends AbstractContainerScreen<JunctionBoxContainer> {
	
	public static final ResourceLocation JUNCTION_BOX_LOCATION = new ResourceLocation(Industria.MODID, "textures/gui/junction_box.png");
	
	public JunctionBoxScreen(JunctionBoxContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
		
		
	}
	
	protected void updateConnections() {
		
		NodePos[] nodes = this.menu.getUDLRCableNodes();
		
		
		
	}
	
	@Override
	protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
		
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, JUNCTION_BOX_LOCATION);
		
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(pPoseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
//		this.itemRenderer.blitOffset = 100.0F;
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.NETHERITE_INGOT), i + 20, j + 109);
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.EMERALD), i + 41, j + 109);
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.DIAMOND), i + 41 + 22, j + 109);
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
//		this.itemRenderer.blitOffset = 0.0F;
		
	}
		
}
