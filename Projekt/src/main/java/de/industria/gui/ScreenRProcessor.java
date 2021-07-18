package de.industria.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.items.ItemProcessor;
import de.industria.packet.CEditProcessorCodePacket;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenRProcessor extends ContainerScreen<ContainerRProcessor> {
	
	public static final ResourceLocation PROCESSOR_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/processor.png");
	private TextFieldWidget[] codeFields;
	private ItemStack processorItem;
	private BlockPos contactPos;
	
	public ScreenRProcessor(ContainerRProcessor screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    	super(screenContainer, inv, titleIn);
    	this.processorItem = screenContainer.getTileEntity().getProcessorStack();
    	this.contactPos = screenContainer.getTileEntity().getBlockPos();
	}
	
	@Override
	protected void init() {

    	this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
    	int i = this.width / 2 - 100;
    	int j = this.height / 2 - 100;
    	
    	String[] codeLines = ((ItemProcessor) this.processorItem.getItem()).getCodeLinesFromProcessor(this.processorItem);
    	this.codeFields = new TextFieldWidget[codeLines.length];
    	for (int i2 = 0; i2 < codeLines.length; i2++) {
    		this.codeFields[i2] = new TextFieldWidget(this.font, i + 21, j + 35 + (i2 * 10), 160, 10, new TranslationTextComponent("line" + i2));
    		this.codeFields[i2].setBordered(false);
    		this.codeFields[i2].setMaxLength(128);
    		this.codeFields[i2].setValue(codeLines[i2]);
    		this.codeFields[i2].setResponder(this::onCodeChanged);
    		this.children.add(this.codeFields[i2]);
    	}
    	
		super.init();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (TextFieldWidget field : this.codeFields) {
			field.mouseClicked(mouseX, mouseY, button);
		}
		boolean flag = false;
		for (TextFieldWidget field : this.codeFields) {
			if (flag) {
				field.setFocus(false);
			} else {
				if (field.isFocused()) flag = true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		for (TextFieldWidget field : this.codeFields) {
			if (field.canConsumeInput()) {
				return field.keyPressed(keyCode, scanCode, modifiers);
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	public void tick() {
		for (TextFieldWidget field : this.codeFields) {
			field.tick();
		}
		super.tick();
	}
	
	protected void onCodeChanged(String line) {
		
		String[] codeLines = new String[this.codeFields.length];
		for (int i = 0; i < this.codeFields.length; i++) {
			codeLines[i] = this.codeFields[i].getValue();
		}
		
		ItemProcessor processor = (ItemProcessor) this.processorItem.getItem();
		processor.storeCodeLinesInProcessor(this.processorItem, codeLines);
		
		Industria.NETWORK.sendToServer(new CEditProcessorCodePacket(this.contactPos, this.processorItem));
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(PROCESSOR_GUI_TEXTURES);
	    int i = this.leftPos;
	    int j = (this.height - this.imageHeight) / 2;
	    this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		for (TextFieldWidget field : this.codeFields) {
			field.render(matrixStack, x, y, partialTicks);;
		}
	}
	
	@Override
	protected void renderLabels(MatrixStack matrixStack, int x, int y) {
		this.font.draw(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
	}
	
}
