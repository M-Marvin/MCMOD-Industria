package de.m_marvin.industria.core.client.electrics.screens;

import com.mojang.blaze3d.systems.RenderSystem;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.AbstractJunctionEditContainer;
import de.m_marvin.industria.core.util.Direction2d;
import de.m_marvin.univec.impl.Vec2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractJunctionEditScreen<B extends BlockEntity & IJunctionEdit, C extends AbstractJunctionEditContainer<B>> extends AbstractContainerScreen<C> {
	
	public static final int[] WIRE_COLORS = {
			0x9E511A,
			0x0094FF,
			0x19FF00,
			0xFFEE00,
			0xFF0000,
			0x000000,
			0xFF00FA,
			0x545453
	};
	
	protected static class CableNode implements GuiEventListener, NarratableEntry {

		protected static class WireNode implements GuiEventListener, NarratableEntry {

			public static final int WIRE_NODE_WIDTH = 9;
			public static final int WIRE_NODE_LENGTH = 18;
			
			protected final CableNode cableNode;
			protected String label;
			protected int id;
			protected final int offset;
			
			public WireNode(CableNode cableNode, int id, int offset, String lane) {
				this.offset = offset;
				this.label = lane;
				this.id = id;
				this.cableNode = cableNode;
			}
			
			@Override
			public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}
			
			@Override
			public NarrationPriority narrationPriority() {
				return NarrationPriority.NONE;
			}
			
			public Vec2i getPosition() {
				Vec2i pos = cableNode.position;
				switch (cableNode.orientation) {
				case LEFT:
				case RIGHT:
					return pos.add(0, this.offset);
				case UP:
				case DOWN:
					return pos.add(this.offset, 0);
				}
				return pos;
			}
			
			public CableNode getNode() {
				return cableNode;
			}
			
			@Override
			public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
				if (isMouseOver(pMouseX, pMouseY)) {
					if (pButton == 0) {
						this.cableNode.getScreen().showNamingField(this);
						return false;
					} else if (pButton == 1) {
						return true;
					}
				}
				return false;
			}
			
			@Override
			public boolean isMouseOver(double x, double y) {
				double i = x - getNode().getScreen().leftPos;
				double j = y - this.getNode().getScreen().topPos;
				int i1 = this.cableNode.isHorizontal() ? WIRE_NODE_LENGTH : WIRE_NODE_WIDTH;
				int i2 = !this.cableNode.isHorizontal() ? WIRE_NODE_LENGTH : WIRE_NODE_WIDTH;
				Vec2i pos = getPosition();
				return pos.x < i && pos.x + i1 > i && pos.y < j && pos.y + i2 > j;
			}

			public void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {

				int i = this.cableNode.getScreen().leftPos;
				int j = this.cableNode.getScreen().topPos;
				int w = WIRE_NODE_WIDTH;
				int h = WIRE_NODE_LENGTH;
				Vec2i uiPos = getPosition();
				
				int color = WIRE_COLORS[this.id % WIRE_COLORS.length];
				
				switch (this.cableNode.orientation) {
				case UP:
					pGuiGraphics.blit(this.cableNode.getScreen().getJunctionBoxTexture(), i + uiPos.x(), j + uiPos.y(), 238, h, w, h);
					RenderSystem.setShaderColor(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, ((color >> 0) & 0xFF) / 255F, 1F);
					pGuiGraphics.blit(this.cableNode.getScreen().getJunctionBoxTexture(), i + uiPos.x(), j + uiPos.y(), 238 + w, h, w, h);
					break;
				case DOWN:
					pGuiGraphics.blit(this.cableNode.getScreen().getJunctionBoxTexture(), i + uiPos.x(), j + uiPos.y(), 238 + w, 54, w, h);
					RenderSystem.setShaderColor(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, ((color >> 0) & 0xFF) / 255F, 1F);
					pGuiGraphics.blit(this.cableNode.getScreen().getJunctionBoxTexture(), i + uiPos.x(), j + uiPos.y(), 238, 54, w, h);
					break;
				case LEFT:
					pGuiGraphics.blit(this.cableNode.getScreen().getJunctionBoxTexture(), i + uiPos.x(), j + uiPos.y(), 238, 36 + w, h, w);
					RenderSystem.setShaderColor(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, ((color >> 0) & 0xFF) / 255F, 1F);
					pGuiGraphics.blit(this.cableNode.getScreen().getJunctionBoxTexture(), i + uiPos.x(), j + uiPos.y(), 238, 36, h, w);
					break;
				case RIGHT:
					pGuiGraphics.blit(this.cableNode.getScreen().getJunctionBoxTexture(), i + uiPos.x(), j + uiPos.y(), 238, 0, h, w);
					RenderSystem.setShaderColor(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, ((color >> 0) & 0xFF) / 255F, 1F);
					pGuiGraphics.blit(this.cableNode.getScreen().getJunctionBoxTexture(), i + uiPos.x(), j + uiPos.y(), 238, 0 + w, h, w);
					break;
				}
				
				RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
				
			}

			@Override
			public void setFocused(boolean p_265728_) {}

			@Override
			public boolean isFocused() {
				return false;
			}
			
		}
		
		protected final AbstractJunctionEditScreen<?, ?> screen;
		protected Vec2i position;
		protected Direction2d orientation;
		protected NodePos cableNode;
		protected WireNode[] wireNodes;
		
		public CableNode(AbstractJunctionEditScreen<?, ?> screen, Vec2i position, Direction2d orientation, NodePos cableNode) {
			this.screen = screen;
			this.position = position;
			this.orientation = orientation;
			this.cableNode = cableNode;
			
			if (this.cableNode != null) {
				String[] lanes = screen.menu.getWireLabels(this.cableNode);
				this.wireNodes = new WireNode[lanes.length];
				int offset1 = -this.wireNodes.length * 10 / 2;
				for (int i = 0; i < lanes.length; i++) {
					this.wireNodes[i] = new WireNode(this, i, i * 10 + offset1, lanes[i]);
					this.screen.addWidget(this.wireNodes[i]);
				}
				this.screen.addWidget(this);
			} else {
				this.wireNodes = new WireNode[0];
			}
		}
		
		@Override
		public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

		@Override
		public NarrationPriority narrationPriority() {
			return NarrationPriority.NONE;
		}

		public boolean isHorizontal() {
			return this.orientation != Direction2d.DOWN && CableNode.this.orientation != Direction2d.UP;
		}
		
		public int getNodeId() {
			return this.cableNode.getNode();
		}
		
		public NodePos getNode() {
			return cableNode;
		}
		
		public AbstractJunctionEditScreen<?, ?> getScreen() {
			return screen;
		}
		
		public String[] getLanes() {
			String[] lanes = new String[this.wireNodes.length];
			for (int i = 0; i < lanes.length; i++) lanes[i] = this.wireNodes[i].label;
			return lanes;
		}

		public void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
			for (WireNode node : this.wireNodes) {
				node.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
			}
		}

		@Override
		public void setFocused(boolean p_265728_) {}

		@Override
		public boolean isFocused() {
			return false;
		}
		
	}
	
	protected static class InternalNode extends CableNode {

		protected int id;
		
		public InternalNode(AbstractJunctionEditScreen<?, ?> screen, Vec2i position, Direction2d orientation, int id) {
			super(screen, position, orientation, null);
			this.id = id;
			
			String[] lanes = this.screen.menu.getInternalLabels(this.id);
			this.wireNodes = new WireNode[lanes.length];
			int offset1 = -this.wireNodes.length * 10 / 2;
			for (int i = 0; i < lanes.length; i++) {
				this.wireNodes[i] = new WireNode(this, i, i * 10 + offset1, lanes[i]);
				this.screen.addWidget(this.wireNodes[i]);
			}
			this.screen.addWidget(this);
			
		}

		public int getNodeId() {
			return this.id;
		}
		
	}
	
	protected NodePos[] conduitNodes;
	protected CableNode[] cableNodes;
	
	protected EditBox namingField;
	protected CableNode.WireNode selectedNode = null;
	
	public AbstractJunctionEditScreen(C pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	@Override
	protected void init() {
		super.init();
		this.imageHeight = 138;
		this.imageWidth = 138;
		
		this.namingField = new EditBox(this.font, this.leftPos, this.topPos, 103, 12, Component.translatable("naming field "));
		this.namingField.setTextColor(-1);
		this.namingField.setTextColorUneditable(-1);
		this.namingField.setBordered(false);
		this.namingField.setMaxLength(50);
		this.namingField.setResponder(this::renameNode);
		this.namingField.setVisible(false);
		this.addRenderableWidget(this.namingField);
		
	}
	
	protected void showNamingField(CableNode.WireNode node) {
		this.selectedNode = node;
		this.namingField.setX(this.leftPos + node.getPosition().x);
		this.namingField.setY(this.topPos + node.getPosition().y);
		this.namingField.setVisible(true);
		this.namingField.setValue(node.label);
	}
	
	protected void renameNode(String name) {
		if (this.selectedNode == null) return;
		this.selectedNode.label = name;
	}
	
	public void sendNewNames() {
		for (CableNode node : this.cableNodes) {
			if (node instanceof InternalNode) {
				this.menu.setInternalWireLabels(node.getNodeId(), node.getLanes());
			} else {
				this.menu.setWireLabels(node.getNode(), node.getLanes());
			}
		}
	}
	
	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		if (pKeyCode == 256) {
			if (this.namingField.isVisible()) sendNewNames();
			onClose();
		}
		return !this.namingField.keyPressed(pKeyCode, pScanCode, pModifiers) && !this.namingField.canConsumeInput() ? super.keyPressed(pKeyCode, pScanCode, pModifiers) : true;
	}
	
	public abstract ResourceLocation getJunctionBoxTexture();
	
	@Override
	protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
		
		pGuiGraphics.blit(getJunctionBoxTexture(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		
		for (CableNode node : this.cableNodes) {
			if (node != null) node.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
		}
		
	}
	
}
