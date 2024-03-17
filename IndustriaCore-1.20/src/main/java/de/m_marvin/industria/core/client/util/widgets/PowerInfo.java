package de.m_marvin.industria.core.client.util.widgets;

import de.m_marvin.industria.core.client.util.GraphicsUtility;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricInfoProvider.ElectricInfo;
import de.m_marvin.industria.core.parametrics.BlockParametrics;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PowerInfo extends AbstractWidget {
	
	protected ResourceLocation texture = GraphicsUtility.UTILITY_WIDGETS_TEXTURE;
	protected final float voltageBarMax;
	protected final StatusBar voltageBar;
	protected final float powerBarMax;
	protected final StatusBar powerBar;
	
	public PowerInfo(Font font, int pX, int pY, ElectricInfo electricInfo) {
		this(font, pX, pY, 60, electricInfo);
	}

	public PowerInfo(Font font, int pX, int pY, int pWidth, ElectricInfo electricInfo) {
		this(font, pX, pY, pWidth, electricInfo.parametrics().get());
	}
	
	public PowerInfo(Font font, int pX, int pY, int pWidth, BlockParametrics parametrics) {
		super(pX, pY, pWidth, 44, Component.empty());
		this.voltageBarMax = parametrics.getVoltageMax() * 1.2F;
		this.voltageBar = new StatusBar(font, pX, pY + 8, pWidth, Component.empty(), parametrics.getVoltageMin() / voltageBarMax, parametrics.getVoltageMax() / voltageBarMax, parametrics.getNominalVoltage() / voltageBarMax);
		this.powerBarMax = parametrics.getPowerMax() * 1.2F;
		this.powerBar = new StatusBar(font, pX, pY + 31, pWidth, Component.empty(), parametrics.getPowerMin() / powerBarMax, parametrics.getPowerMax() / powerBarMax, parametrics.getNominalPower() / powerBarMax);
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
		this.voltageBar.setMessage(Component.literal(String.format("%.0fV", Math.abs(voltage))));
		this.powerBar.setMessage(Component.literal(String.format("%.0fW", Math.abs(power))));
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		
		this.voltageBar.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		this.powerBar.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}
	
}
