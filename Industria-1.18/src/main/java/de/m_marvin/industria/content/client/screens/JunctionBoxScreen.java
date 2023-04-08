package de.m_marvin.industria.content.client.screens;

import java.lang.invoke.TypeDescriptor.OfField;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.ibm.icu.impl.RuleCharacterIterator.Position;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.content.container.JunctionBoxContainer;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.univec.impl.Vec2d;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec3d;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class JunctionBoxScreen extends AbstractContainerScreen<JunctionBoxContainer> {
	
	public static final ResourceLocation JUNCTION_BOX_LOCATION = new ResourceLocation(Industria.MODID, "textures/gui/junction_box.png");

	public static final int WIRE_NODE_WIDTH = 9;
	public static final int WIRE_NODE_LENGTH = 18;
	
	protected NodePos[] cableNodesUDLR;
	protected String[] laneWiresUp;
	protected String[] laneWiresDown;
	protected String[] laneWiresLeft;
	protected String[] laneWiresRight;
	
	protected WireNode[] wireNodesUp;
	protected WireNode[] wireNodesDown;
	protected WireNode[] wireNodesLeft;
	protected WireNode[] wireNodesRight;
	
	protected WireNode firstSelection = null;
	
	protected Set<WireNode[]> connections = new HashSet<>();
	
	protected class WireNode implements GuiEventListener, NarratableEntry {
		
		protected final int labelId;
		protected final int nodeId;
		protected final Vec2i uiPos;
		protected final boolean horizontal;
		
		public WireNode(int labelId, int nodeId, Vec2i uiPos, boolean horizontal) {
			this.labelId = labelId;
			this.nodeId = nodeId;
			this.uiPos = uiPos;
			this.horizontal = horizontal;
			JunctionBoxScreen.this.addWidget(this);
		}

		@Override
		public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

		@Override
		public NarrationPriority narrationPriority() {
			return NarrationPriority.NONE;
		}
		
		@Override
		public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
			if (isMouseOver(pMouseX, pMouseY)) {
				JunctionBoxScreen.this.firstSelection = this;
				return true;
			}
			return false;
		}
		
		@Override
		public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
			if (isMouseOver(pMouseX, pMouseY)) {
				connectNodes(JunctionBoxScreen.this.firstSelection, this);
				return true;
			}
			return true;
		}
		
		@Override
		public boolean isMouseOver(double x, double y) {
			double i = x - (JunctionBoxScreen.this.width - JunctionBoxScreen.this.imageWidth) / 2;
			double j = y - (JunctionBoxScreen.this.height - JunctionBoxScreen.this.imageHeight) / 2;
			int i1 = horizontal ? WIRE_NODE_LENGTH : WIRE_NODE_WIDTH;
			int i2 = !horizontal ? WIRE_NODE_LENGTH : WIRE_NODE_WIDTH;
			return uiPos.x < i && uiPos.x + i1 > i && uiPos.y < j && uiPos.y + i2 > j;
		}
		
		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			return super.equals(obj);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash()
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
		
		int wireDistance = 10;
		
		this.wireNodesUp = new WireNode[this.laneWiresUp.length];
		int offsetu = 89 - this.wireNodesUp.length * wireDistance / 2;
		for (int i = 0; i < this.laneWiresUp.length; i++) this.wireNodesUp[i] = new WireNode(i, 0, new Vec2i(i * wireDistance + offsetu, 27), false);
		this.wireNodesDown = new WireNode[this.laneWiresDown.length];
		int offsetd = 89 - this.wireNodesDown.length * wireDistance / 2;
		for (int i = 0; i < this.laneWiresDown.length; i++) this.wireNodesDown[i] = new WireNode(i, 1, new Vec2i(i * wireDistance + offsetd, 131), false);
		this.wireNodesLeft = new WireNode[this.laneWiresLeft.length];
		int offsetl = 89 - this.wireNodesLeft.length * wireDistance / 2;
		for (int i = 0; i < this.laneWiresLeft.length; i++) this.wireNodesLeft[i] = new WireNode(i, 2, new Vec2i(27, i * wireDistance + offsetl), true);
		this.wireNodesRight = new WireNode[this.laneWiresRight.length];
		int offsetr = 89 - this.laneWiresRight.length * wireDistance / 2;
		for (int i = 0; i < this.laneWiresRight.length; i++) this.wireNodesRight[i] = new WireNode(i, 3, new Vec2i(131, i * wireDistance + offsetr), true);
		
	}
	
	protected void connectNodes(WireNode nodeA, WireNode nodeB) {
		
		WireNode[] connection = new WireNode[] {nodeA, nodeB};
		if (this.connections.contains(connection)) {
			this.connections.remove(connection);
		} else {
			this.connections.add(connection);
		}
		
	}
	
	protected void drawConnection(Vec2d origin, Vec2d nodeA, Vec2d nodeB) {
		
		
		
	}
	
	@Override
	protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
		
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, JUNCTION_BOX_LOCATION);
		
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(pPoseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		int w = WIRE_NODE_WIDTH;
		int h = WIRE_NODE_LENGTH;
		for (WireNode node : this.wireNodesUp) {
			this.blit(pPoseStack, i + node.uiPos.x(), j + node.uiPos.y(), 238, h, w, h);
			this.blit(pPoseStack, i + node.uiPos.x(), j + node.uiPos.y(), 238 + w, h, w, h);
		}
		for (WireNode node : this.wireNodesDown) {
			this.blit(pPoseStack, i + node.uiPos.x(), j + node.uiPos.y(), 238, 54, w, h);
			this.blit(pPoseStack, i + node.uiPos.x(), j + node.uiPos.y(), 238 + w, 54, w, h);
		}
		for (WireNode node : this.wireNodesLeft) {
			this.blit(pPoseStack, i + node.uiPos.x(), j + node.uiPos.y(), 238, 36, h, w);
			this.blit(pPoseStack, i + node.uiPos.x(), j + node.uiPos.y(), 238, 36 + w, h, w);
		}
		for (WireNode node : this.wireNodesRight) {
			this.blit(pPoseStack, i + node.uiPos.x(), j + node.uiPos.y(), 238, 0, h, w);
			this.blit(pPoseStack, i + node.uiPos.x(), j + node.uiPos.y(), 238, 0 + w, h, w);
		}
		
//		this.itemRenderer.blitOffset = 100.0F;
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.NETHERITE_INGOT), i + 20, j + 109);
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.EMERALD), i + 41, j + 109);
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.DIAMOND), i + 41 + 22, j + 109);
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
////		this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
//		this.itemRenderer.blitOffset = 0.0F;
		
	}
	
	protected WireNode getNodeAt(double x, double y) {
		for (WireNode node : this.wireNodesUp) {
			if (node.uiPos.x < x && node.uiPos.y < y && node.uiPos.x + WIRE_NODE_WIDTH > x && node.uiPos.y + WIRE_NODE_LENGTH > y) return node;
		}
		for (WireNode node : this.wireNodesDown) {
			if (node.uiPos.x < x && node.uiPos.y < y && node.uiPos.x + WIRE_NODE_WIDTH > x && node.uiPos.y + WIRE_NODE_LENGTH > y) return node;
		}
		for (WireNode node : this.wireNodesLeft) {
			if (node.uiPos.x < x && node.uiPos.y < y && node.uiPos.x + WIRE_NODE_LENGTH > x && node.uiPos.y + WIRE_NODE_WIDTH > y) return node;
		}
		for (WireNode node : this.wireNodesRight) {
			if (node.uiPos.x < x && node.uiPos.y < y && node.uiPos.x + WIRE_NODE_LENGTH > x && node.uiPos.y + WIRE_NODE_WIDTH > y) return node;
		}
		return null;
	}
	
	@Override
	public List<? extends GuiEventListener> children() {
		// TODO Auto-generated method stub
		return super.children();
	}
	
	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
		if (super.mouseClicked(pMouseX, pMouseY, pButton)) return true;
		
//		WireNode node = getNodeAt(pMouseX, pMouseY);
//		if (node != null) {
//			
//			System.out.println("SELCTED " + node.nodeId);
//			return true;
//			
//		}
		
		return false;
	}
	
}
