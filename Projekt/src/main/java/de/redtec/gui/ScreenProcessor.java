package de.redtec.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.redtec.RedTec;
import de.redtec.items.ItemProcessor;
import de.redtec.packet.CEditProcessorCodePacket;
import net.minecraft.client.Minecraft;
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
	
	// Init GUI when opened or resized
	@SuppressWarnings("resource")
	@Override
	protected void func_231160_c_() {
		
    	Minecraft.getInstance().keyboardListener.enableRepeatEvents(true);
    	int i = this.field_230708_k_ / 2 - 100;
    	int j = this.field_230709_l_ / 2 - 100;
    	
    	String[] codeLines = ((ItemProcessor) this.processorItem.getItem()).getCodeLinesFromProcessor(this.processorItem);
    	this.codeFields = new TextFieldWidget[codeLines.length];
    	for (int i2 = 0; i2 < codeLines.length; i2++) {
    		this.codeFields[i2] = new TextFieldWidget(Minecraft.getInstance().fontRenderer, i + 21, j + 35 + (i2 * 10), 160, 10, new TranslationTextComponent("line" + i2));
    		this.codeFields[i2].setEnableBackgroundDrawing(false);
    		this.codeFields[i2].setMaxStringLength(128);
    		this.codeFields[i2].setText(codeLines[i2]);
    		this.codeFields[i2].setResponder(this::onCodeChanged);
    		this.field_230705_e_.add(this.codeFields[i2]);
    	}
    	
		super.func_231160_c_();
	}
	
	// Handle TextField Mouse-Click
	@Override
	public boolean func_231044_a_(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
		for (TextFieldWidget field : this.codeFields) {
			field.func_231044_a_(p_231044_1_, p_231044_3_, p_231044_5_);
		}
		return super.func_231044_a_(p_231044_1_, p_231044_3_, p_231044_5_);
	}
	
	// Handle TextField Key-Typed
	@Override
	public boolean func_231046_a_(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
		
		for (TextFieldWidget field : this.codeFields) {
			
			if (field.canWrite()) {
				
				return field.func_231046_a_(p_231046_1_, p_231046_2_, p_231046_3_);
				
			}
			
		}
		
		return super.func_231046_a_(p_231046_1_, p_231046_2_, p_231046_3_);
		
	}
	
	// Handle TextField Ticking
	@Override
	public void func_231023_e_() {
		for (TextFieldWidget field : this.codeFields) {
			field.tick();
		}
		super.func_231023_e_();
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
	
	// Render Foreground (Strings)
	@Override
	protected void func_230451_b_(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_) {
		this.field_230712_o_.func_243248_b(p_230451_1_, this.field_230704_d_, (float)this.field_238742_p_, (float)this.field_238743_q_, 4210752);
	}
	
	// Render Background (Textures/TextFields)
	@SuppressWarnings("deprecation")
	protected void func_230450_a_(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_230706_i_.getTextureManager().bindTexture(PROCESSOR_GUI_TEXTURES);
	    int i = this.guiLeft;
	    int j = (this.field_230709_l_ - this.ySize) / 2;
	    this.func_238474_b_(p_230450_1_, i, j, 0, 0, this.xSize, this.ySize);
		for (TextFieldWidget field : this.codeFields) {
			field.func_230430_a_(p_230450_1_, p_230450_3_, p_230450_4_, 0);
		}
	}
	
}
