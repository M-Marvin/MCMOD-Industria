package de.m_marvin.industria.content.client.screens;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.content.container.JunctionBoxContainer;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.univec.impl.Vec2d;
import de.m_marvin.univec.impl.Vec2i;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
	protected EditBox namingField;
	protected WireNode selectedNode = null;
	
	//protected Set<Set<WireNode>> connections = new HashSet<>();
	
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
				if (pButton == 0) {
					//JunctionBoxScreen.this.firstSelection = this;
					showNamingField(this);
					return false;
				} else if (pButton == 1) {
					//disconnectNode(this);
					return true;
				}
			}
			return false;
		}
		
//		@Override
//		public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
//			if (isMouseOver(pMouseX, pMouseY)) {
//				if (pButton == 0) {
//					if (JunctionBoxScreen.this.firstSelection != this && JunctionBoxScreen.this.firstSelection != null) connectNodes(JunctionBoxScreen.this.firstSelection, this);
//					JunctionBoxScreen.this.firstSelection = null;
//					return true;
//				}
//			}
//			return false;
//		}
		
		@Override
		public boolean isMouseOver(double x, double y) {
			double i = x - JunctionBoxScreen.this.leftPos;
			double j = y - JunctionBoxScreen.this.topPos;
			int i1 = horizontal ? WIRE_NODE_LENGTH : WIRE_NODE_WIDTH;
			int i2 = !horizontal ? WIRE_NODE_LENGTH : WIRE_NODE_WIDTH;
			return uiPos.x < i && uiPos.x + i1 > i && uiPos.y < j && uiPos.y + i2 > j;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof WireNode other) {
				return other.nodeId == nodeId && other.labelId == labelId;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(this.labelId, this.nodeId);
		}
		
	}
	
	public JunctionBoxScreen(JunctionBoxContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
		
	}
	
	@Override
	protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
		// Disabling screen titles
		//super.renderLabels(pPoseStack, pMouseX, pMouseY);
	}
	
	@Override
	protected void init() {
		super.init();
		this.imageHeight = 138;
		this.imageWidth = 138;
		
		this.cableNodesUDLR = this.menu.getUDLRCableNodes();
		this.laneWiresUp = this.menu.getWireLabels(this.cableNodesUDLR[0]);
		this.laneWiresDown = this.menu.getWireLabels(this.cableNodesUDLR[1]);
		this.laneWiresLeft = this.menu.getWireLabels(this.cableNodesUDLR[2]);
		this.laneWiresRight = this.menu.getWireLabels(this.cableNodesUDLR[3]);
		
		int wireDistance = 10;
		
		this.wireNodesUp = new WireNode[this.laneWiresUp.length];
		int offsetu = 70 - this.wireNodesUp.length * wireDistance / 2;
		for (int i = 0; i < this.laneWiresUp.length; i++) this.wireNodesUp[i] = new WireNode(i, 0, new Vec2i(i * wireDistance + offsetu, 8), false);
		this.wireNodesDown = new WireNode[this.laneWiresDown.length];
		int offsetd = 70 - this.wireNodesDown.length * wireDistance / 2;
		for (int i = 0; i < this.laneWiresDown.length; i++) this.wireNodesDown[i] = new WireNode(i, 1, new Vec2i(i * wireDistance + offsetd, 112), false);
		this.wireNodesLeft = new WireNode[this.laneWiresLeft.length];
		int offsetl = 70 - this.wireNodesLeft.length * wireDistance / 2;
		for (int i = 0; i < this.laneWiresLeft.length; i++) this.wireNodesLeft[i] = new WireNode(i, 2, new Vec2i(8, i * wireDistance + offsetl), true);
		this.wireNodesRight = new WireNode[this.laneWiresRight.length];
		int offsetr = 70 - this.laneWiresRight.length * wireDistance / 2;
		for (int i = 0; i < this.laneWiresRight.length; i++) this.wireNodesRight[i] = new WireNode(i, 3, new Vec2i(112, i * wireDistance + offsetr), true);

		this.namingField = new EditBox(this.font, this.leftPos, this.topPos, 103, 12, new TranslatableComponent("naming field "));
		this.namingField.setTextColor(-1);
		this.namingField.setTextColorUneditable(-1);
		this.namingField.setBordered(false);
		this.namingField.setMaxLength(50);
		this.namingField.setResponder(this::renameNode);
		this.namingField.setVisible(false);
		this.addWidget(this.namingField);
		
		//this.namingField.setVisible(false);
		
	}
	
	protected WireNode getNodeAt(Vec2d position) {
		for (WireNode node : this.wireNodesUp) {
			if (node.uiPos.x <= position.x && node.uiPos.y <= position.y && node.uiPos.x + WIRE_NODE_WIDTH >= position.x && node.uiPos.y + WIRE_NODE_LENGTH >= position.y) return node;
		}
		for (WireNode node : this.wireNodesDown) {
			if (node.uiPos.x <= position.x && node.uiPos.y <= position.y && node.uiPos.x + WIRE_NODE_WIDTH >= position.x && node.uiPos.y + WIRE_NODE_LENGTH >= position.y) return node;
		}
		for (WireNode node : this.wireNodesLeft) {
			if (node.uiPos.x <= position.x && node.uiPos.y <= position.y && node.uiPos.x + WIRE_NODE_LENGTH >= position.x && node.uiPos.y + WIRE_NODE_WIDTH >= position.y) return node;
		}
		for (WireNode node : this.wireNodesRight) {
			if (node.uiPos.x <= position.x && node.uiPos.y <= position.y && node.uiPos.x + WIRE_NODE_LENGTH >= position.x && node.uiPos.y + WIRE_NODE_WIDTH >= position.y) return node;
		}
		return null;
	}
	
	protected void showNamingField(WireNode node) {
		
		this.selectedNode = node;
		
		this.namingField.x = this.leftPos + node.uiPos.x;
		this.namingField.y = this.topPos + node.uiPos.y;
		this.namingField.setVisible(true);
		
		switch (node.nodeId) {
		case 0: this.namingField.setValue(this.laneWiresUp[node.labelId]); break;
		case 1: this.namingField.setValue(this.laneWiresDown[node.labelId]); break;
		case 2: this.namingField.setValue(this.laneWiresLeft[node.labelId]); break;
		case 3: this.namingField.setValue(this.laneWiresRight[node.labelId]); break;
		}
		

		this.namingField.setValue("TESTS");
		
	}
	
	protected void renameNode(String name) {
		
		if (this.selectedNode == null) return;
		
		switch (this.selectedNode.nodeId) {
		case 0: this.laneWiresUp[this.selectedNode.labelId] = this.namingField.getValue(); break;
		case 1: this.laneWiresDown[this.selectedNode.labelId] = this.namingField.getValue(); break;
		case 2: this.laneWiresLeft[this.selectedNode.labelId] = this.namingField.getValue(); break;
		case 3: this.laneWiresRight[this.selectedNode.labelId] = this.namingField.getValue(); break;
		}
		
		System.out.println("Lanes changed");
		// TODO Send to server
		
	}
	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	protected void connectNodes(WireNode nodeA, WireNode nodeB) {
//		
//		Optional<Set> combinedGroup = Stream.of(this.connections.toArray(i -> new Set[i]))
//			.filter(s -> s.contains(nodeA) || s.contains(nodeB))
//			.reduce((a, b) -> { 
//				a.addAll(b); 
//				this.connections.remove(b);
//				return a; 
//			});
//		
//		if (combinedGroup.isPresent()) {
//			if (!combinedGroup.get().contains(nodeA)) combinedGroup.get().add(nodeA);
//			if (!combinedGroup.get().contains(nodeB)) combinedGroup.get().add(nodeB);
//		} else {
//			Set<WireNode> newGroup = new HashSet<>();
//			newGroup.add(nodeA);
//			newGroup.add(nodeB);
//			this.connections.add(newGroup);
//		}
//
//		updateConnections();
//		
//	}
	
//	public void disconnectNode(WireNode node) {
//		
//		Set<WireNode> group = null;
//		for (Set<WireNode> g : this.connections) {
//			if (g.contains(node)) {
//				g.remove(node);
//				if (g.isEmpty()) group = g;
//			}
//		}
//		if (group != null) this.connections.remove(group);
//		
//		updateConnections();
//		
//	}
	
//	protected void updateConnections() {
//		
//		System.out.println("Connections changed");
//		
//		// TODO
//		
//	}
	
	protected void drawConnections(Set<WireNode> group) {
		
		// TODO
		
	}
	
	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		if (pKeyCode == 256) {
			this.minecraft.player.closeContainer();
		}

		return !this.namingField.keyPressed(pKeyCode, pScanCode, pModifiers) && !this.namingField.canConsumeInput() ? super.keyPressed(pKeyCode, pScanCode, pModifiers) : true;
	}
	
	@Override
	protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
		
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, JUNCTION_BOX_LOCATION);
		
		int i = this.leftPos;
		int j = this.topPos;
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
		
		if (this.namingField != null) this.namingField.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
		
		//for (Set<WireNode> group : this.connections) drawConnections(group);
		
	}
	
}
