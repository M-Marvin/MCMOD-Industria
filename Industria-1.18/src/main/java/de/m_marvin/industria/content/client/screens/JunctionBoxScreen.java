package de.m_marvin.industria.content.client.screens;

import java.lang.invoke.TypeDescriptor.OfField;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.content.container.JunctionBoxContainer;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.univec.impl.Vec2i;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class JunctionBoxScreen extends AbstractContainerScreen<JunctionBoxContainer> {
	
	public static final ResourceLocation JUNCTION_BOX_LOCATION = new ResourceLocation(Industria.MODID, "textures/gui/junction_box.png");
	
	protected NodePos[] cableNodesUDLR;
	protected String[] laneWiresUp;
	protected String[] laneWiresDown;
	protected String[] laneWiresLeft;
	protected String[] laneWiresRight;
	
	protected WireNode[] wireNodesUp;
	protected WireNode[] wireNodesDown;
	protected WireNode[] wireNodesLeft;
	protected WireNode[] wireNodesRight;
	
	protected class WireNode  {
		
		protected int labelId;
		protected int nodeId;
		protected Vec2i uiPos;
		
		public WireNode(int labelId, int nodeId, Vec2i uiPos) {
			this.labelId = labelId;
			this.nodeId = nodeId;
			this.uiPos = uiPos;
		}
		
	}
	
	public JunctionBoxScreen(JunctionBoxContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	@Override
	protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
		// TODO Auto-generated method stub
		//super.renderLabels(pPoseStack, pMouseX, pMouseY);
	}
	
	@Override
	protected void init() {
		super.init();
		
		this.cableNodesUDLR = this.menu.getUDLRCableNodes();
		this.laneWiresUp = this.menu.getWireLabels(this.cableNodesUDLR[0]);
		this.laneWiresDown = this.menu.getWireLabels(this.cableNodesUDLR[1]);
		this.laneWiresLeft = this.menu.getWireLabels(this.cableNodesUDLR[2]);
		this.laneWiresRight = this.menu.getWireLabels(this.cableNodesUDLR[3]);
		
		System.out.println(this.laneWiresUp.length);
		System.out.println(this.laneWiresDown.length);
		System.out.println(this.laneWiresLeft.length);
		System.out.println(this.laneWiresRight.length);
		
		this.wireNodesUp = new WireNode[this.laneWiresUp.length];
		int offsetu = 0;
		for (int i = 0; i < this.laneWiresUp.length; i++) this.wireNodesUp[i] = new WireNode(i, 0, new Vec2i(i * 20 + offsetu, 10));
		this.wireNodesDown = new WireNode[this.laneWiresDown.length];
		int offsetd = 0;
		for (int i = 0; i < this.laneWiresDown.length; i++) this.wireNodesDown[i] = new WireNode(i, 1, new Vec2i(i * 20 + offsetd, 130));
		this.wireNodesLeft = new WireNode[this.laneWiresLeft.length];
		int offsetl = 0;
		for (int i = 0; i < this.laneWiresLeft.length; i++) this.wireNodesLeft[i] = new WireNode(i, 2, new Vec2i(130, i * 20 + offsetl));
		this.wireNodesRight = new WireNode[this.laneWiresRight.length];
		int offsetr = 0;
		for (int i = 0; i < this.laneWiresRight.length; i++) this.wireNodesRight[i] = new WireNode(i, 3, new Vec2i(10, i * 20 + offsetr));
		
	}
	
	@Override
	protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
		
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, JUNCTION_BOX_LOCATION);
		
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(pPoseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		for (WireNode node : this.wireNodesRight) {
			
			this.blit(pPoseStack, i + node.uiPos.x(), j + node.uiPos.y(), 238, 0, 18, 9);
			this.blit(pPoseStack, i + node.uiPos.x(), j + node.uiPos.y(), 238, 0 + 9, 18, 9);
			
		}
		
//		this.itemRenderer.blitOffset = 100.0F;
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.NETHERITE_INGOT), i + 20, j + 109);
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.EMERALD), i + 41, j + 109);
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.DIAMOND), i + 41 + 22, j + 109);
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
//		this.itemRenderer.blitOffset = 0.0F;
		
	}
	
}
