package de.redtec.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.redtec.RedTec;
import de.redtec.items.ItemProcessor;
import de.redtec.packet.CEditProcessorCodePacket;
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
public class ScreenProcessor extends ContainerScreen<ContainerProcessor> {
	
	public static final ResourceLocation PROCESSOR_GUI_TEXTURES = new ResourceLocation(RedTec.MODID, "textures/gui/processor.png");
	private TextFieldWidget[] codeFields;
	private ItemStack processorItem;
	private BlockPos contactPos;
	
	public ScreenProcessor(ContainerProcessor screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    	super(screenContainer, inv, titleIn);
    	this.processorItem = screenContainer.getTileEntity().getProcessorStack();
    	this.contactPos = screenContainer.getTileEntity().getPos();
	}
	
	@Override
	protected void init() {

    	this.minecraft.keyboardListener.enableRepeatEvents(true);
    	int i = this.width / 2 - 100;
    	int j = this.height / 2 - 100;
    	
    	String[] codeLines = ((ItemProcessor) this.processorItem.getItem()).getCodeLinesFromProcessor(this.processorItem);
    	this.codeFields = new TextFieldWidget[codeLines.length];
    	for (int i2 = 0; i2 < codeLines.length; i2++) {
    		this.codeFields[i2] = new TextFieldWidget(this.font, i + 21, j + 35 + (i2 * 10), 160, 10, new TranslationTextComponent("line" + i2));
    		this.codeFields[i2].setEnableBackgroundDrawing(false);
    		this.codeFields[i2].setMaxStringLength(128);
    		this.codeFields[i2].setText(codeLines[i2]);
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
		return super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		for (TextFieldWidget field : this.codeFields) {
			if (field.canWrite()) {
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
	
	private void onCodeChanged(String line) {
		
		String[] codeLines = new String[this.codeFields.length];
		for (int i = 0; i < this.codeFields.length; i++) {
			codeLines[i] = this.codeFields[i].getText();
		}
		
		ItemProcessor processor = (ItemProcessor) this.processorItem.getItem();
		processor.storeCodeLinesInProcessor(this.processorItem, codeLines);
		
		RedTec.NETWORK.sendToServer(new CEditProcessorCodePacket(this.contactPos, this.processorItem));
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(PROCESSOR_GUI_TEXTURES);
	    int i = this.guiLeft;
	    int j = (this.height - this.ySize) / 2;
	    this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
		for (TextFieldWidget field : this.codeFields) {
			field.render(matrixStack, x, y, partialTicks);;
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
		this.font.func_243248_b(matrixStack, this.title, (float)this.titleX, (float)this.titleY, 4210752);
	}
	
}
