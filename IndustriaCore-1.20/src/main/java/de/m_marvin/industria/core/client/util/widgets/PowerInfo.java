package de.m_marvin.industria.core.client.util.widgets;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricInfoProvider.ElectricInfo;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PowerInfo extends AbstractWidget {
	
	protected ResourceLocation texture = IndustriaCore.UTILITY_WIDGETS_TEXTURE;
	protected final float voltageBarMax;
	protected final StatusBar voltageBar;
	protected final float powerBarMax;
	protected final StatusBar powerBar;
	
	public PowerInfo(Font font, int pX, int pY, ElectricInfo electricInfo) {
		this(font, pX, pY, 60, electricInfo);
	}
	
	public PowerInfo(Font font, int pX, int pY, int pWidth, ElectricInfo electricInfo) {
		this(font, pX, pY, pWidth, electricInfo.targetVoltage().get(), electricInfo.voltageTolerance().get(), electricInfo.targetPower().get(), electricInfo.powerTolerance().get());
	}
	
	public PowerInfo(Font font, int pX, int pY, int pWidth, int targetVoltage, float voltageTolerance, int targetPower, float powerTolerance) {
		super(pX, pY, pWidth, 44, Component.empty());
		float voltageMax = targetVoltage + (targetVoltage * voltageTolerance * 0.5F);
		float voltageMin = targetVoltage - (targetVoltage * voltageTolerance * 0.5F);
		this.voltageBarMax = voltageMax * 1.2F;
		this.voltageBar = new StatusBar(font, pX, pY + 8, pWidth, Component.empty(), voltageMin / voltageBarMax, voltageMax / voltageBarMax, voltageMax / voltageBarMax);
		float powerMax = targetPower + (targetPower * powerTolerance * 0.5F);
		float powerMin = targetPower - (targetPower * powerTolerance * 0.5F);
		this.powerBarMax = powerMax * 1.2F;
		this.powerBar = new StatusBar(font, pX, pY + 31, pWidth, Component.empty(), powerMin / powerBarMax, powerMax / powerBarMax, powerMax / powerBarMax);
	}
	
	public void setTexture(ResourceLocation texture) {
		this.texture = texture;
		this.powerBar.setTexture(texture);
		this.voltageBar.setTexture(texture);
	}
	
	public void setStatus(ElectricInfo electricInfo) {
		setStatus(electricInfo.voltage().get().floatValue(), electricInfo.power().get().floatValue());
	}
	
	public void setStatus(float voltage, float power) {
		this.voltageBar.setStatus(voltage / this.voltageBarMax);
		this.powerBar.setStatus(power / this.powerBarMax);
		this.voltageBar.setMessage(Component.literal(String.format("%.0fV", voltage)));
		this.powerBar.setMessage(Component.literal(String.format("%.0fW", power)));
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		
		this.voltageBar.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		this.powerBar.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}
	
}
