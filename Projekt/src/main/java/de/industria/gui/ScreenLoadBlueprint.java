package de.industria.gui;

import java.util.HashMap;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.items.ItemWritableBlueprint;
import de.industria.packet.CRequestSavedBlueprints;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenLoadBlueprint extends ContainerScreen<ContainerLoadBlueprint> {
	
	@SuppressWarnings("unused")
	private Button buttonLoad;
	@SuppressWarnings("unused")
	private Button buttonAbbort;
	
	private HashMap<String, BlockPos> sizeMap;
	private String[] structures;
	private int selected;
	private int listOffset;
	private int listLength = 10;
	
	public ScreenLoadBlueprint(ContainerLoadBlueprint container, PlayerInventory palyerInv, ITextComponent title) {
		super(container, palyerInv, title);
	}
	
	@Override
	protected void init() {
		
		int i = this.width / 2;
		
		this.buttonLoad = this.addButton(new Button(i + -61, 170, 60, 20, DialogTexts.GUI_DONE, this::onConfirm));
		this.buttonAbbort = this.addButton(new Button(i + 1, 170, 60, 20, DialogTexts.GUI_CANCEL, this::onCancel));
		
		Industria.NETWORK.sendToServer(new CRequestSavedBlueprints());
		this.structures = new String[] {};
		
		super.init();
	}
	
	public void onConfirm(Button button) {
		if (this.structures.length > 0) {
			String selectedBlueprint = this.structures[this.selected];
			BlockPos size = this.sizeMap.get(selectedBlueprint);
			ItemStack blueprint = ItemWritableBlueprint.writeBlueprint(this.menu.blueprintItem, selectedBlueprint, size);
			this.menu.playerInv.player.setItemInHand(Hand.MAIN_HAND, blueprint);
			this.onClose();
		}
	}
	
	public void onCancel(Button button) {
		this.onClose();
	}
	
	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
		this.renderBackground(p_230430_1_);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
		this.selected -= scroll;
		this.selected = Math.min(this.structures.length - 1, Math.max(0, this.selected));
		return super.mouseScrolled(mouseX, mouseY, scroll);
	}
	
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		if (this.selected > this.listOffset + (this.listLength - 1)) this.listOffset++;
		if (this.selected < this.listOffset) this.listOffset--;
		this.listOffset = Math.min(listOffset, Math.max(structures.length - listLength, 0));
		
		drawString(matrixStack, this.font, new TranslationTextComponent("industria.blueprint.blueprintList"), this.width / 2 - 61, 80, 10526880);
		for (int i = listOffset; i < Math.min(this.structures.length, this.listOffset + this.listLength); i++) {
			String pointer = (i == this.selected) ? "-> " : "- ";
			int posIndex = listOffset - i;
			try {
				drawString(matrixStack, this.font, new StringTextComponent(pointer + this.structures[i]), this.width / 2 - 61, 90 - posIndex * 8, 10526880);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void renderLabels(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_) {
	}

	public void setAviableBlueprints(String[] aviable, HashMap<String, BlockPos> sizeMap) {
		this.structures = aviable;
		this.sizeMap = sizeMap;
	}
	
}
