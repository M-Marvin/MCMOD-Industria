package de.industria.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.industria.Industria;
import de.industria.packet.CConfigureNetworkDevice;
import de.industria.util.blockfeatures.ITENetworkDevice.NetworkDeviceIP;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenNetworkConfigurator extends ContainerScreen<ContainerNetworkConfigurator> {
	
	private TextFieldWidget ipField;
	@SuppressWarnings("unused")
	private Button buttonConfirm;
	@SuppressWarnings("unused")
	private Button buttonAbbort;
	
	private boolean ipValid;
	
	public ScreenNetworkConfigurator(ContainerNetworkConfigurator screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}
	
	@Override
	protected void init() {
		
		int i = this.width / 2;
		this.ipField = new TextFieldWidget(this.font, i + -60, 100, 120, 20, new TranslationTextComponent("networkConfigurator.ipField"));
		this.ipField.setMaxLength(15);
		this.ipField.setValue(this.menu.getIP().getString());
		this.ipField.setResponder(this::onChangeIP);
		this.ipValid = true;
		this.children.add(ipField);
		
		this.buttonConfirm = this.addButton(new Button(i + -61, 130, 60, 20, DialogTexts.GUI_DONE, this::onConfirm));
		this.buttonAbbort = this.addButton(new Button(i + 1, 130, 60, 20, DialogTexts.GUI_CANCEL, this::onCancel));
		
		super.init();
	}
	
	public void onConfirm(Button button) {
		NetworkDeviceIP ip = NetworkDeviceIP.ipFromString(this.ipField.getValue());
		if (ip != null) {
			Industria.NETWORK.sendToServer(new CConfigureNetworkDevice(this.menu.getPos(), ip));
			this.onClose();
		}
	}
	
	public void onCancel(Button button) {
		this.onClose();
	}
	
	public void onChangeIP(String ip) {
		this.ipValid = NetworkDeviceIP.ipFromString(ip) != null;
	}
	
	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
		this.renderBackground(p_230430_1_);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
	}
	
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		
		drawString(matrixStack, this.font, new TranslationTextComponent("industria.network_configurator.ipField." + (ipValid ? "valid" : "fail"), ipValid), this.width / 2 - 61, 90, this.ipValid ? 10526880 : new Color(255, 0, 0).getRGB());
		try {
			this.ipField.render(matrixStack, x, y, partialTicks);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
	
	@Override
	protected void renderLabels(MatrixStack matrixStack, int x, int y) {
	}
	
}
