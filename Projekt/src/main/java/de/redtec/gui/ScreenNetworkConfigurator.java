package de.redtec.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.redtec.RedTec;
import de.redtec.packet.CConfigureNetworkDevice;
import de.redtec.util.INetworkDevice.NetworkDeviceIP;
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
		this.ipField.setMaxStringLength(15);
		this.ipField.setText(this.container.getIP().getString());
		this.ipField.setResponder(this::onChangeIP);
		this.ipValid = true;
		this.children.add(ipField);
		
		this.buttonConfirm = this.addButton(new Button(i + -61, 130, 60, 20, DialogTexts.GUI_DONE, this::onConfirm));
		this.buttonAbbort = this.addButton(new Button(i + 1, 130, 60, 20, DialogTexts.GUI_CANCEL, this::onCancel));
		
		super.init();
	}
	
	public void onConfirm(Button button) {
		NetworkDeviceIP ip = NetworkDeviceIP.ipFromString(this.ipField.getText());
		if (ip != null) {
			RedTec.NETWORK.sendToServer(new CConfigureNetworkDevice(this.container.getPos(), ip));
			this.closeScreen();
		}
	}
	
	public void onCancel(Button button) {
		this.closeScreen();
	}
	
	public void onChangeIP(String ip) {
		this.ipValid = NetworkDeviceIP.ipFromString(ip) != null;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
		
		drawString(matrixStack, this.font, new TranslationTextComponent("redtec.network_configurator.ipField." + (ipValid ? "valid" : "fail"), ipValid), this.width / 2 - 61, 90, this.ipValid ? 10526880 : new Color(255, 0, 0).getRGB());
		try {
			this.ipField.render(matrixStack, x, y, partialTicks);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
	}
	
}
