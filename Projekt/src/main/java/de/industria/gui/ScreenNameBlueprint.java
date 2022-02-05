package de.industria.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.items.ItemEmptyBlueprint;
import de.industria.packet.CSaveBlueprint;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenNameBlueprint extends ContainerScreen<ContainerNameBlueprint> {
	
	private TextFieldWidget nameField;
	@SuppressWarnings("unused")
	private Button buttonConfirm;
	@SuppressWarnings("unused")
	private Button buttonAbbort;
	
	private boolean nameValid;
	private String structureSize;
	
	public ScreenNameBlueprint(ContainerNameBlueprint container, PlayerInventory palyerInv, ITextComponent title) {
		super(container, palyerInv, title);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (nameField.canConsumeInput()) {
			return nameField.keyPressed(keyCode, scanCode, modifiers);
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	protected void init() {
		
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
    	int i = this.width / 2;
		this.nameField = new TextFieldWidget(this.font, i + -60, 100, 120, 20, new TranslationTextComponent("industria.blueprint.structureName"));
		this.nameField.setMaxLength(25);
		this.nameField.setResponder(this::onChangeIP);
		this.nameValid = true;
		this.children.add(nameField);
		
		this.buttonConfirm = this.addButton(new Button(i + -61, 130, 60, 20, DialogTexts.GUI_DONE, this::onConfirm));
		this.buttonAbbort = this.addButton(new Button(i + 1, 130, 60, 20, DialogTexts.GUI_CANCEL, this::onCancel));
		
		BlockPos cornerA = ItemEmptyBlueprint.getPositionA(this.menu.blueprintItem);
		BlockPos cornerB = ItemEmptyBlueprint.getPositionB(this.menu.blueprintItem);
		int sizeX = Math.max(cornerA.getX(), cornerB.getX()) - Math.min(cornerA.getX(), cornerB.getX());
		int sizeY = Math.max(cornerA.getY(), cornerB.getY()) - Math.min(cornerA.getY(), cornerB.getY());
		int sizeZ = Math.max(cornerA.getZ(), cornerB.getZ()) - Math.min(cornerA.getZ(), cornerB.getZ());
		this.structureSize = (sizeX + 1) + "x" + (sizeY + 1) + "x" + (sizeZ + 1);
		
		super.init();
	}
	
	public void onConfirm(Button button) {
		if (this.nameValid && this.nameField.getValue().length() > 0) {
			BlockPos cornerA = ItemEmptyBlueprint.getPositionA(this.menu.blueprintItem);
			BlockPos cornerB = ItemEmptyBlueprint.getPositionB(this.menu.blueprintItem);
			Industria.NETWORK.sendToServer(new CSaveBlueprint(this.nameField.getValue(), cornerA, cornerB));
			this.onClose();
		}
	}
	
	public void onCancel(Button button) {
		this.onClose();
	}
	
	public void onChangeIP(String ip) {
		this.nameValid = true; // TODO
	}
	
	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
		this.renderBackground(p_230430_1_);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
	}
	
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		drawString(matrixStack, this.font, new TranslationTextComponent("industria.blueprint.structureName." + (nameValid ? "valid" : "fail"), nameValid), this.width / 2 - 61, 90, this.nameValid ? 10526880 : new Color(255, 0, 0).getRGB());
		drawString(matrixStack, this.font, new TranslationTextComponent("industria.blueprint.structureSize", this.structureSize), this.width / 2 - 61, 80, 10526880);
		this.nameField.render(matrixStack, x, y, partialTicks);
	}
	
	@Override
	protected void renderLabels(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_) {
	}
	
}
