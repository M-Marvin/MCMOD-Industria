package de.m_marvin.industria.core.client.electrics.screens;

import java.util.List;
import java.util.stream.Stream;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;

import de.m_marvin.industria.core.client.electrics.screens.AbstractJunctionEditScreen.CableNode.WireNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.containers.AbstractJunctionEditContainer;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.types.Direction2d;
import de.m_marvin.univec.impl.Vec2f;
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
				int offset1 = -this.wireNodes.length * WireNode.WIRE_NODE_WIDTH / 2;
				for (int i = 0; i < lanes.length; i++) {
					this.wireNodes[i] = new WireNode(this, i, i * WireNode.WIRE_NODE_WIDTH + offset1, lanes[i]);
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
			int offset1 = -this.wireNodes.length * WireNode.WIRE_NODE_WIDTH / 2;
			for (int i = 0; i < lanes.length; i++) {
				this.wireNodes[i] = new WireNode(this, i, i * WireNode.WIRE_NODE_WIDTH + offset1, lanes[i]);
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
	protected boolean connectsOnlyToInternal;
	
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
				if (node.cableNode == null) continue;
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
	
	public void renderConnection(GuiGraphics graphics, WireNode nodeA, WireNode nodeB, int color) {
		
		Vec2f p1 = new Vec2f(nodeA.getPosition()).add(WireNode.WIRE_NODE_WIDTH / 2F, WireNode.WIRE_NODE_WIDTH / 2F);
		Vec2f p2 = new Vec2f(nodeB.getPosition()).add(WireNode.WIRE_NODE_WIDTH / 2F, WireNode.WIRE_NODE_WIDTH / 2F);
		Vec2f v1 = new Vec2f(MathUtility.getDirectionVec2D(nodeA.getNode().orientation));
		Vec2f v2 = new Vec2f(MathUtility.getDirectionVec2D(nodeB.getNode().orientation));
		Vec2f[] va = MathUtility.makeBezierVectors2D(p1, v1, p2, v2, 10F);

		Vec2f p = p1.add((float) this.leftPos, (float) this.topPos);
		for (Vec2f v : va) {
			float a = (float) v.angle(new Vec2f(0, 1));
			
			graphics.pose().pushPose();
			graphics.pose().translate(p.x, p.y, 0);
			graphics.pose().mulPose(Axis.ZP.rotation(a));
			RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 1F);
			graphics.fill(-2, 0, 0, (int) Math.ceil(v.length()), color | 0xFF000000);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			graphics.fill(-1, 0, 0, (int) Math.ceil(v.length()), color | 0xFF000000);
			graphics.pose().popPose();

			p.addI(v);
		}

	}
	
	@Override
	protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
		
		renderBackground(pGuiGraphics);
		pGuiGraphics.blit(getJunctionBoxTexture(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		
	}
	
	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		
		List<String> labels = Stream.of(this.cableNodes).flatMap(cn -> Stream.of(cn.wireNodes)).map(wn -> wn.label).distinct().filter(s -> !s.isBlank()).toList();
		
		if (!this.connectsOnlyToInternal) {
			for (int i = 0; i < this.cableNodes.length; i++) {
				for (WireNode wire1 : this.cableNodes[i].wireNodes) {
					if (wire1.label.isBlank()) continue;
					for (int i2 = i + 1; i2 < this.cableNodes.length; i2++) {
						for (WireNode wire2 : this.cableNodes[i2].wireNodes) {
							if (wire1.label.equals(wire2.label)) {
								int index = labels.indexOf(wire1.label);
								int color = WIRE_COLORS[index % WIRE_COLORS.length];
								renderConnection(pGuiGraphics, wire1, wire2, color);
							}
						}
					}
				}
			}
		} else {
			for (int i = 0; i < this.cableNodes.length; i++) {
				if (!(this.cableNodes[i] instanceof InternalNode)) continue;
				for (WireNode wire1 : this.cableNodes[i].wireNodes) {
					if (wire1.label.isBlank()) continue;
					for (int i2 = 0; i2 < this.cableNodes.length; i2++) {
						for (WireNode wire2 : this.cableNodes[i2].wireNodes) {
							if (wire1.label.equals(wire2.label)) {
								int index = labels.indexOf(wire1.label);
								int color = WIRE_COLORS[index % WIRE_COLORS.length];
								renderConnection(pGuiGraphics, wire1, wire2, color);
							}
						}
					}
				}
			}
		}
		
		for (CableNode node : this.cableNodes) {
			if (node != null) node.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
		}
		
	}
	
}
