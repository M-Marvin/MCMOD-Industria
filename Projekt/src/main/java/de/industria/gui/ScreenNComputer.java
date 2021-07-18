package de.industria.gui;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.packet.CEditComputerCode;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.client.gui.widget.button.Button;

public class ScreenNComputer extends ContainerScreen<ContainerNComputer> {
	
	public static final ResourceLocation COMPUTER_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/computer.png");
	
	public TextFieldWidget[] codeFields;
	public Button saveChanges;
	public Button abbort;
	public Button startComputer;
	public TextFieldWidget consoleLine;
	
	public ScreenNComputer(ContainerNComputer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.imageHeight = 216;
	}
	
	@Override
	protected void init() {
		super.init();
		
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
    	int i = this.width / 2 - 100;
    	int j = this.height / 2 - 128;
    	
    	String[] code = this.menu.getTileEntity().getCachedBootCode().split("\n");
    	String[] codeLines = new String[10];
    	for (int i2 = 0; i2 < Math.min(10, code.length); i2++) codeLines[i2] = code[i2];
    	
    	this.codeFields = new TextFieldWidget[codeLines.length];
    	for (int i2 = 0; i2 < codeLines.length; i2++) {
    		this.codeFields[i2] = new TextFieldWidget(this.font, i + 21, j + 35 + (i2 * 10), 129, 10, new TranslationTextComponent("line" + i2));
    		this.codeFields[i2].setBordered(false);
    		this.codeFields[i2].setMaxLength(128);
    		this.codeFields[i2].setValue(codeLines[i2]);
    		this.codeFields[i2].setResponder(this::onCodeChanged);
    		this.children.add(this.codeFields[i2]);
    	}
		
    	this.saveChanges = this.addButton(new Button(i + 158, j + 122, 22, 20, new StringTextComponent(""), this::onSaveChanges));
    	this.abbort = this.addButton(new Button(i + 158, j + 103, 22, 20, new StringTextComponent(""), this::onAbbort));
    	this.startComputer = this.addButton(new Button(i + 158, j + 84, 22, 20, new StringTextComponent(""), this::onRun));
    	
    	this.consoleLine = new TextFieldWidget(this.font, i + 21, j + 133, 129, 10, new TranslationTextComponent("console"));
    	this.consoleLine.setBordered(false);
    	this.consoleLine.setMaxLength(256);
    	this.children.add(this.consoleLine);
    	
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean flag1 = super.mouseClicked(mouseX, mouseY, button);
		boolean flag = false;
		for (TextFieldWidget field : this.codeFields) {
			if (flag) {
				field.setFocus(false);
				field.moveCursorToStart();
				field.setHighlightPos(0);
			} else {
				if (field.isFocused()) flag = true;
			}
		}
		return flag1;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (handleMultiLine(scanCode)) {
			for (TextFieldWidget field : this.codeFields) {
				if (field.canConsumeInput()) {
					return field.keyPressed(keyCode, scanCode, modifiers);
				}
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public void tick() {
		int cp = this.consoleLine.getCursorPosition();
		this.consoleLine.setValue(this.menu.getTileEntity().getConsoleLine());
		this.consoleLine.moveCursorTo(Math.min(cp, this.consoleLine.getValue().length()));
		for (TextFieldWidget field : this.codeFields) {
			field.tick();
		}
		super.tick();
	}
	
	protected boolean handleMultiLine(int scanCode) {
		TextFieldWidget currentLine = getCurrentLine();
		if (currentLine != null) {
			if (scanCode == 28) {
				for (int i = 0; i < this.codeFields.length; i++) {
					if (this.codeFields[i].isFocused() &&  i + 1 < this.codeFields.length) {
						this.codeFields[i].changeFocus(false);
						int coursorPos = Math.min(this.codeFields[i].getCursorPosition() , this.codeFields[ + 1].getValue().length());
						this.codeFields[i + 1].setEditable(true);
						this.codeFields[i + 1].changeFocus(true);
						this.codeFields[i + 1].moveCursorTo(coursorPos);
						return false;
					}
				}
			} else if (scanCode == 14 && getCurrentLine().getCursorPosition() == 0) {
				for (int i = 0; i < this.codeFields.length; i++) {
					if (this.codeFields[i].isFocused() &&  i - 1 >= 0) {
						this.codeFields[i].changeFocus(false);
						this.codeFields[i - 1].changeFocus(true);
						this.codeFields[i - 1].moveCursorToEnd();
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public TextFieldWidget getCurrentLine() {
		for (int i = 0; i < this.codeFields.length; i++) {
			if (this.codeFields[i].isFocused() &&  i + 1 < this.codeFields.length) {
				return this.codeFields[i];
			}
		}
		return null;
	}
	
	public String[] getCode() {
		String[] code = new String[10];
		for (int i = 0; i < 10; i++) {
			code[i] = this.codeFields[i].getValue();
		}
		return code;
	}
	
	protected void onCodeChanged(String line) {
		// Not in Use
	}
	
	protected void onSaveChanges(Button button) {
		Industria.NETWORK.sendToServer(new CEditComputerCode(this.menu.getTileEntity().getBlockPos(), false, true, this.getCode()));
	}
	
	protected void onAbbort(Button button) {
		Industria.NETWORK.sendToServer(new CEditComputerCode(this.menu.getTileEntity().getBlockPos(), false, false, this.getCode()));
		this.onClose();
	}
	
	protected void onRun(Button button) {
		Industria.NETWORK.sendToServer(new CEditComputerCode(this.menu.getTileEntity().getBlockPos(), true, false, this.getCode()));
	}
	
	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
		this.renderBackground(p_230430_1_);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(COMPUTER_GUI_TEXTURES);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		for (TextFieldWidget field : this.codeFields) {
			field.render(matrixStack, x, y, partialTicks);
		}
		this.consoleLine.render(matrixStack, x, y, partialTicks);
		
	}
	
	@Override
	protected void renderLabels(MatrixStack matrixStack, int x, int y) {
		this.font.draw(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
		this.font.draw(matrixStack, this.inventory.getDisplayName(), (float)this.inventoryLabelX, (float)this.inventoryLabelY + 52, 4210752);
		
		this.minecraft.getTextureManager().bind(COMPUTER_GUI_TEXTURES);
		this.blit(matrixStack, 147, 65, !this.menu.getTileEntity().isComputerRunning() ? 147 : 176, 65, 20, 18);
		this.blit(matrixStack, 147, 84, 147, 84, 20, 18);
		this.blit(matrixStack, 147, 103, 147, 103, 20, 18);
	}
	
}
