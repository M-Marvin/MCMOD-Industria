package de.m_marvin.industria.core.client.kinetics.screens;

import de.m_marvin.industria.core.client.util.screens.AbstractTickableWidgedContainerScreen;
import de.m_marvin.industria.core.kinetics.types.containers.MotorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MotorScreen extends AbstractTickableWidgedContainerScreen<MotorMenu> {
	
	protected EditBox rpmField;
	protected EditBox torqueField;
	
	public MotorScreen(MotorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}

	@Override
	protected void init() {
		super.init();
		
		this.rpmField = new EditBox(font, this.leftPos + 95, this.topPos + 30, 80, 20, Component.translatable("industriacore.power_source.voltage"));
		this.rpmField.setMaxLength(5);
		this.rpmField.setValue(Double.toString(this.menu.getRPM()));
		this.addRenderableWidget(this.rpmField);
		
		this.torqueField = new EditBox(font, this.leftPos + 5, this.topPos + 30, 80, 20, Component.translatable("industriacore.power_source.power"));
		this.torqueField.setMaxLength(5);
		this.torqueField.setValue(Double.toString(this.menu.getTorque()));
		this.addRenderableWidget(this.torqueField);
		
		this.titleLabelY = 0;
		this.titleLabelX = this.imageWidth / 2 - this.font.width(this.title) / 2;
		
	}
	
	@Override
	public void onClose() {
		try {
			double rpm = Double.parseDouble(this.rpmField.getValue());
			this.menu.setRPM(rpm);
		} catch (NumberFormatException e) {}
		try {
			double torque = Double.parseDouble(this.torqueField.getValue());
			this.menu.setTorque(torque);
		} catch (NumberFormatException e) {}
		super.onClose();
	}
	
	@Override
	protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
		pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFF, true);
		pGuiGraphics.drawString(this.font, Component.translatable("industriacore.ui.motor.torque"), 5, 20, 0xFFFFFF, true);
		pGuiGraphics.drawString(this.font, Component.translatable("industriacore.ui.motor.rpm"), 95, 20, 0xFFFFFF, true);
	}
	
	@Override
	protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
		renderBackground(pGuiGraphics);
	}
	
	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		if (pKeyCode == 256) {
			this.rpmField.setFocused(false);
			this.torqueField.setFocused(false);
		}
		if (this.rpmField.keyPressed(pKeyCode, pScanCode, pModifiers) || this.rpmField.canConsumeInput()) return true;
		if (this.torqueField.keyPressed(pKeyCode, pScanCode, pModifiers) || this.torqueField.canConsumeInput()) return true;
		return super.keyPressed(pKeyCode, pScanCode, pModifiers);
	}
	
}
