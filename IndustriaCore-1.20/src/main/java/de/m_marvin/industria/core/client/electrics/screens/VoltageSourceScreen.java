package de.m_marvin.industria.core.client.electrics.screens;

import de.m_marvin.industria.core.client.util.screens.AbstractTickableWidgedContainerScreen;
import de.m_marvin.industria.core.client.util.widgets.AnalogGauge;
import de.m_marvin.industria.core.client.util.widgets.ElectricInterface;
import de.m_marvin.industria.core.electrics.types.IElectric.ElectricReference;
import de.m_marvin.industria.core.electrics.types.containers.VoltageSourceMenu;
import de.m_marvin.industria.core.parametrics.BlockParametrics;
import de.m_marvin.industria.core.parametrics.engine.BlockParametricsManager;
import de.m_marvin.industria.core.util.types.AutoScaler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;

public class VoltageSourceScreen extends AbstractTickableWidgedContainerScreen<VoltageSourceMenu> {
	
	protected BlockParametrics parametrics;
	protected ElectricInterface electric;
	protected AnalogGauge voltMeter;
	protected AnalogGauge powerMeter;
	protected AutoScaler voltMeterScaler;
	protected AutoScaler powerMeterScaler;
	
	protected EditBox voltageField;
	protected EditBox powerField;

	public VoltageSourceScreen(VoltageSourceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}

	@Override
	protected void init() {
		super.init();
		
		BlockState blockState = this.menu.getBlockState();
		this.parametrics = BlockParametricsManager.getInstance().getParametrics(blockState.getBlock());
		
		this.electric = new ElectricInterface(this.leftPos, this.topPos + 30, this.menu, Component.translatable("industriacore.ui.electrical.mainfuse"));
		this.electric.setReference(ElectricReference.block(this.menu.getBlockPos()));
		this.addRenderableWidget(this.electric);
		
		this.voltMeter = new AnalogGauge(this.leftPos + 60, this.topPos + 30, Component.translatable("industriacore.ui.electrical.voltage"));
		this.voltMeterScaler = AutoScaler.autoRangeVoltMeter(this.voltMeter, this.parametrics);
		this.addRenderableWidget(this.voltMeter);
		
		this.powerMeter = new AnalogGauge(this.leftPos + 120, this.topPos + 30, Component.translatable("industriacore.ui.electrical.power"));
		this.powerMeterScaler = AutoScaler.autoRangePowerMeter(this.powerMeter, this.parametrics);
		this.addRenderableWidget(this.powerMeter);
		
		this.voltageField = new EditBox(font, this.leftPos + 61, this.topPos + 70, 36, 20, Component.translatable("industriacore.ui.electrical.voltage"));
		this.voltageField.setMaxLength(5);
		this.voltageField.setValue(Integer.toString(this.menu.getVoltage()));
		this.addRenderableWidget(this.voltageField);

		this.powerField = new EditBox(font, this.leftPos + 121, this.topPos + 70, 36, 20, Component.translatable("industriacore.ui.electrical.power"));
		this.powerField.setMaxLength(5);
		this.powerField.setValue(Integer.toString(this.menu.getPower()));
		this.addRenderableWidget(this.powerField);
		
		this.titleLabelY = 0;
		this.titleLabelX = this.imageWidth / 2 - this.font.width(this.title) / 2;
		
		containerTick();
		
	}
	
	@Override
	protected void containerTick() {
		super.containerTick();
		
		double voltage = this.menu.getDeviceVoltage();
		double current = this.menu.getDeviceCurrent();
		double power = voltage * current;
		
		this.voltMeter.setState(this.voltMeterScaler.getScaleValue((float) voltage));
		this.voltMeter.setInfo(Component.literal(String.format("%.3f %sV", voltage * this.voltMeterScaler.getMagnitudeFactor(), this.voltMeterScaler.getMagnitudeSymbol())));
		this.powerMeter.setState(this.powerMeterScaler.getScaleValue((float) this.menu.getDeviceCurrent() * (float) this.menu.getDeviceVoltage()));
		this.powerMeter.setInfo(Component.literal(String.format("%.3f %sW", power * this.powerMeterScaler.getMagnitudeFactor(), this.powerMeterScaler.getMagnitudeSymbol())));
		
	}

	@Override
	public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
		String powerString = this.powerField.getValue();
		String voltageString = this.voltageField.getValue();
		this.init(pMinecraft, pWidth, pHeight);
		this.powerField.setValue(powerString);
		this.voltageField.setValue(voltageString);
	}

	@Override
	public void onClose() {
		try {
			int voltage = Integer.parseInt(this.voltageField.getValue());
			this.menu.setVoltage(voltage);
		} catch (NumberFormatException e) {}
		try {
			int power = Integer.parseInt(this.powerField.getValue());
			this.menu.setPower(power);
		} catch (NumberFormatException e) {}
		this.menu.sendDataToClient();
		super.onClose();
	}
	
	@Override
	protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
		pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFFFFF, true);
	}
	
	@Override
	protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
		renderBackground(pGuiGraphics);
	}
	
	@Override
	public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
		if (pKeyCode == 256) {
			this.voltageField.setFocused(false);
			this.powerField.setFocused(false);
		}
		if (this.voltageField.keyPressed(pKeyCode, pScanCode, pModifiers) || this.voltageField.canConsumeInput()) return true;
		if (this.powerField.keyPressed(pKeyCode, pScanCode, pModifiers) || this.powerField.canConsumeInput()) return true;
		return super.keyPressed(pKeyCode, pScanCode, pModifiers);
	}
	
}
