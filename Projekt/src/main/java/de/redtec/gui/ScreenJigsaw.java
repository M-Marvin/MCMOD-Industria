package de.redtec.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import de.redtec.RedTec;
import de.redtec.blocks.BlockJigsaw;
import de.redtec.blocks.BlockJigsaw.JigsawType;
import de.redtec.packet.CEditJigsawTileEntityPacket;
import de.redtec.packet.CGenerateJigsaw;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ScreenJigsaw extends ContainerScreen<ContainerJigsaw>{
	
	private TextFieldWidget poolName;
	private TextFieldWidget name;
	private TextFieldWidget targetName;
	private TextFieldWidget replaceState;
	@SuppressWarnings("unused")
	private Button buttonLockRotation;
	private boolean lock;
	
	@SuppressWarnings("unused")
	private AbstractSlider generationLevels;
	private int levels;
	@SuppressWarnings("unused")
	private Button buttonKeepJigsaws;
	private boolean keepJigsaws;
	@SuppressWarnings("unused")
	private Button buttonGenerate;
	
	private Button buttonDone;
	@SuppressWarnings("unused")
	private Button buttonAbbort;
	
	private boolean showLockOrientation;
	
	public ScreenJigsaw(ContainerJigsaw screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}
	
	private boolean isInputValid() {
		return	ResourceLocation.isResouceNameValid(this.poolName.getText()) &&
				ResourceLocation.isResouceNameValid(this.name.getText()) &&
				ResourceLocation.isResouceNameValid(this.targetName.getText()) &&
				ResourceLocation.isResouceNameValid(this.replaceState.getText());
	}
	
	private void updateTileEntity() {
		
		ResourceLocation poolName = ResourceLocation.tryCreate(this.poolName.getText());
		ResourceLocation name = ResourceLocation.tryCreate(this.name.getText());
		ResourceLocation targetName = ResourceLocation.tryCreate(this.targetName.getText());
		ResourceLocation replaceState = ResourceLocation.tryCreate(this.replaceState.getText());
		boolean lock = this.lock;
		
		RedTec.NETWORK.sendToServer(new CEditJigsawTileEntityPacket(this.container.getTileEntity().getPos(), poolName, name, targetName, replaceState, lock));
		
	}
	
	private void sendGenerate() {
		
		if (isInputValid()) this.updateTileEntity();
		
		int levels = this.levels;
		boolean keepJigsaws = this.keepJigsaws;
		
		RedTec.NETWORK.sendToServer(new CGenerateJigsaw(this.container.getTileEntity().getPos(), levels, keepJigsaws));
		
	}
	
	private void onChangeField() {
		
		this.buttonDone.field_230693_o_ = isInputValid();
		
	}
	
	//Tick
	@Override
	public void func_231023_e_() {
		this.poolName.tick();
		this.name.tick();
		this.targetName.tick();
		this.replaceState.tick();
		super.func_231023_e_();
	}
	
	// Handle TextField Key-Typed
	@Override
	public boolean func_231046_a_(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
		
		if (poolName.canWrite()) {
			return poolName.func_231046_a_(p_231046_1_, p_231046_2_, p_231046_3_);
		} else if (name.canWrite()) {
			return name.func_231046_a_(p_231046_1_, p_231046_2_, p_231046_3_);
		} else if (targetName.canWrite()) {
			return targetName.func_231046_a_(p_231046_1_, p_231046_2_, p_231046_3_);
		} else if (replaceState.canWrite()) {
			return replaceState.func_231046_a_(p_231046_1_, p_231046_2_, p_231046_3_);
		}
		
		return super.func_231046_a_(p_231046_1_, p_231046_2_, p_231046_3_);
		
	}
	
	//Init
	@Override
	protected void func_231160_c_() {
		
		BlockState jigsawState = this.container.getTileEntity().getBlockState();
		this.showLockOrientation = jigsawState.getBlock() == RedTec.jigsaw ? jigsawState.get(BlockJigsaw.TYPE) != JigsawType.HORIZONTAL : false;
		
		this.field_230706_i_.keyboardListener.enableRepeatEvents(true);
		
		this.poolName = new TextFieldWidget(this.field_230712_o_, this.field_230708_k_ / 2 - 152, 20, 300, 20, new TranslationTextComponent("jigsaw.pool"));
		this.poolName.setMaxStringLength(128);
		this.poolName.setText(this.container.getTileEntity().poolFile.toString());
		this.poolName.setResponder((string) -> {
			this.onChangeField();
		});
		this.field_230705_e_.add(poolName);
		this.field_230706_i_.keyboardListener.enableRepeatEvents(true);
		this.name = new TextFieldWidget(this.field_230712_o_, this.field_230708_k_ / 2 - 152, 55, 300, 20, new TranslationTextComponent("jigsaw.name"));
		this.name.setMaxStringLength(128);
		this.name.setText(this.container.getTileEntity().name.toString());
		this.name.setResponder((string) -> {
			this.onChangeField();
		});
		this.field_230705_e_.add(name);
		this.field_230706_i_.keyboardListener.enableRepeatEvents(true);
		this.targetName = new TextFieldWidget(this.field_230712_o_, this.field_230708_k_ / 2 - 152, 90, 300, 20, new TranslationTextComponent("jigsaw.name"));
		this.targetName.setMaxStringLength(128);
		this.targetName.setText(this.container.getTileEntity().targetName.toString());
		this.targetName.setResponder((string) -> {
			this.onChangeField();
		});
		this.field_230705_e_.add(targetName);
		this.field_230706_i_.keyboardListener.enableRepeatEvents(true);
		this.replaceState = new TextFieldWidget(this.field_230712_o_, this.field_230708_k_ / 2 - 152, 125, 300, 20, new TranslationTextComponent("jigsaw.replaceState"));
		this.replaceState.setMaxStringLength(128);
		this.replaceState.setText(this.container.getTileEntity().replaceState.toString());
		this.replaceState.setResponder((string) -> {
			this.onChangeField();
		});
		this.field_230705_e_.add(replaceState);
		
		this.lock = this.container.getTileEntity().lockOrientation;
		if (this.showLockOrientation) {
			ITextComponent infoName = new TranslationTextComponent("redtec.jigsaw_block.connection");
			int i = this.field_230712_o_.func_238414_a_(infoName) + 10;
			this.buttonLockRotation = this.func_230480_a_(new Button(this.field_230708_k_ / 2 - 152 + i, 150, 300 - i, 20, new TranslationTextComponent("redtec.jigsaw_block.orientation." + (this.container.getTileEntity().lockOrientation ? "locked" : "rotateable")), (button) -> {
				this.lock = !this.lock;
				button.func_238482_a_(new TranslationTextComponent("redtec.jigsaw_block.orientation." + (this.lock ? "locked" : "rotateable")));
			}));
		}
		
		this.keepJigsaws = true;
		this.buttonKeepJigsaws = this.func_230480_a_(new Button(this.field_230708_k_ / 2 - 50, 180, 100, 20, new TranslationTextComponent("redtec.jigsaw_block.keepJigsaws." + (this.keepJigsaws ? "on" : "off")), (button) -> {
			this.keepJigsaws = !this.keepJigsaws;
			button.func_238482_a_(new TranslationTextComponent("redtec.jigsaw_block.keepJigsaws." + (this.keepJigsaws ? "on" : "off")));
		}));
		this.buttonGenerate = this.func_230480_a_(new Button(this.field_230708_k_ / 2 + 54, 180, 100, 20, new TranslationTextComponent("redtec.jigsaw_block.generate"), (button) -> {
			this.sendGenerate();
			this.field_230706_i_.displayGuiScreen((Screen)null);
		}));
		
		this.generationLevels = this.func_230480_a_(new AbstractSlider(this.field_230708_k_ / 2 - 154, 180, 100, 20, StringTextComponent.field_240750_d_, 1.0D) {
			
			{
				this.func_230979_b_();
			}
			
			@Override
			protected void func_230979_b_() {
				this.func_238482_a_(new TranslationTextComponent("redtec.jigsaw_block.levels", ScreenJigsaw.this.levels));
			}
			
			@Override
			protected void func_230972_a_() {
				ScreenJigsaw.this.levels = (int) (this.field_230683_b_ * 20);
			}
			
		});
		
		this.buttonDone = this.func_230480_a_(new Button(this.field_230708_k_ / 2 - 4 - 150, 210, 150, 20, DialogTexts.field_240632_c_, (button) -> {
			this.updateTileEntity();
			this.field_230706_i_.displayGuiScreen((Screen)null);
		}));
		this.buttonAbbort = this.func_230480_a_(new Button(this.field_230708_k_ / 2 + 4, 210, 150, 20, DialogTexts.field_240633_d_, (button) -> {
			this.field_230706_i_.displayGuiScreen((Screen)null);
		}));
		
		super.func_231160_c_();
	}
	
	//On Screen Close
	@Override
	public void func_231164_f_() {
		this.field_230706_i_.keyboardListener.enableRepeatEvents(false);
	}
	
	//Background
	@Override
	protected void func_230450_a_(MatrixStack matrix, float f, int combinedLight, int combinedOverlay) {

		this.func_230446_a_(matrix);
		
		func_238475_b_(matrix, field_230712_o_, new TranslationTextComponent("redtec.jigsaw_block.pool"), this.field_230708_k_  / 2 - 153, 10, 10526880);
		this.poolName.func_230430_a_(matrix, combinedLight, combinedOverlay, f);
		func_238475_b_(matrix, field_230712_o_, new TranslationTextComponent("redtec.jigsaw_block.name"), this.field_230708_k_  / 2 - 153, 45, 10526880);
		this.name.func_230430_a_(matrix, combinedLight, combinedOverlay, f);
		func_238475_b_(matrix, field_230712_o_, new TranslationTextComponent("redtec.jigsaw_block.targetName"), this.field_230708_k_  / 2 - 153, 80, 10526880);
		this.targetName.func_230430_a_(matrix, combinedLight, combinedOverlay, f);
		func_238475_b_(matrix, field_230712_o_, new TranslationTextComponent("redtec.jigsaw_block.state"), this.field_230708_k_  / 2 - 153, 115, 10526880);
		this.replaceState.func_230430_a_(matrix, combinedLight, combinedOverlay, f);
		if (this.showLockOrientation) func_238475_b_(matrix, this.field_230712_o_, new TranslationTextComponent("redtec.jigsaw_block.connection"), this.field_230708_k_ / 2 - 153, 156, 16777215);
		
	}

}
