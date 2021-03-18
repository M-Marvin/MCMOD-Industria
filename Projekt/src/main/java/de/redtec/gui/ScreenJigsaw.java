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
		this.buttonDone.active = isInputValid(); // TODO
	}
	
	@Override
	public void tick() {
		this.poolName.tick();
		this.name.tick();
		this.targetName.tick();
		this.replaceState.tick();
		super.tick();
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

		if (poolName.canWrite()) {
			return poolName.keyPressed(keyCode, scanCode, modifiers);
		} else if (name.canWrite()) {
			return name.keyPressed(keyCode, scanCode, modifiers);
		} else if (targetName.canWrite()) {
			return targetName.keyPressed(keyCode, scanCode, modifiers);
		} else if (replaceState.canWrite()) {
			return replaceState.keyPressed(keyCode, scanCode, modifiers);
		}
		
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	protected void init() {

		BlockState jigsawState = this.container.getTileEntity().getBlockState();
		this.showLockOrientation = jigsawState.getBlock() == RedTec.jigsaw ? jigsawState.get(BlockJigsaw.TYPE) != JigsawType.HORIZONTAL : false;
		
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		
		this.poolName = new TextFieldWidget(this.font, this.width / 2 - 152, 20, 300, 20, new TranslationTextComponent("jigsaw.pool"));
		this.poolName.setMaxStringLength(128);
		this.poolName.setText(this.container.getTileEntity().poolFile.toString());
		this.poolName.setResponder((string) -> {
			this.onChangeField();
		});
		this.children.add(poolName);
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.name = new TextFieldWidget(this.font, this.width / 2 - 152, 55, 300, 20, new TranslationTextComponent("jigsaw.name"));
		this.name.setMaxStringLength(128);
		this.name.setText(this.container.getTileEntity().name.toString());
		this.name.setResponder((string) -> {
			this.onChangeField();
		});
		this.children.add(name);
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.targetName = new TextFieldWidget(this.font, this.width / 2 - 152, 90, 300, 20, new TranslationTextComponent("jigsaw.name"));
		this.targetName.setMaxStringLength(128);
		this.targetName.setText(this.container.getTileEntity().targetName.toString());
		this.targetName.setResponder((string) -> {
			this.onChangeField();
		});
		this.children.add(targetName);
		this.minecraft.keyboardListener.enableRepeatEvents(true);
		this.replaceState = new TextFieldWidget(this.font, this.width / 2 - 152, 125, 300, 20, new TranslationTextComponent("jigsaw.replaceState"));
		this.replaceState.setMaxStringLength(128);
		this.replaceState.setText(this.container.getTileEntity().replaceState.toString());
		this.replaceState.setResponder((string) -> {
			this.onChangeField();
		});
		this.children.add(replaceState);
		
		this.lock = this.container.getTileEntity().lockOrientation;
		if (this.showLockOrientation) {
			ITextComponent infoName = new TranslationTextComponent("redtec.jigsaw_block.connection");
			int i = this.font.getStringPropertyWidth(infoName) + 10;
			this.buttonLockRotation = this.addButton(new Button(this.width / 2 - 152 + i, 150, 300 - i, 20, new TranslationTextComponent("redtec.jigsaw_block.orientation." + (this.container.getTileEntity().lockOrientation ? "locked" : "rotateable")), (button) -> {
				this.lock = !this.lock;
				button.setMessage(new TranslationTextComponent("redtec.jigsaw_block.orientation." + (this.lock ? "locked" : "rotateable")));
			}));
		}
		
		this.keepJigsaws = true;
		this.buttonKeepJigsaws = this.addButton(new Button(this.width / 2 - 50, 180, 100, 20, new TranslationTextComponent("redtec.jigsaw_block.keepJigsaws." + (this.keepJigsaws ? "on" : "off")), (button) -> {
			this.keepJigsaws = !this.keepJigsaws;
			button.setMessage(new TranslationTextComponent("redtec.jigsaw_block.keepJigsaws." + (this.keepJigsaws ? "on" : "off")));
		}));
		this.buttonGenerate = this.addButton(new Button(this.width / 2 + 54, 180, 100, 20, new TranslationTextComponent("redtec.jigsaw_block.generate"), (button) -> {
			this.sendGenerate();
			this.minecraft.displayGuiScreen((Screen)null);
		}));
		
		this.generationLevels = this.addButton(new AbstractSlider(this.width / 2 - 154, 180, 100, 20, StringTextComponent.EMPTY, 1.0D) {
			
			{
				this.func_230979_b_();
			}
			
			@Override
			protected void func_230979_b_() {
				this.setMessage(new TranslationTextComponent("redtec.jigsaw_block.levels", ScreenJigsaw.this.levels));
			}
			
			@Override
			protected void func_230972_a_() {
				ScreenJigsaw.this.levels = (int) (this.sliderValue * 20);
			}
			
		});
		
		this.buttonDone = this.addButton(new Button(this.width / 2 - 4 - 150, 210, 150, 20, DialogTexts.GUI_DONE, (button) -> {
			this.updateTileEntity();
			this.minecraft.displayGuiScreen((Screen)null);
		}));
		this.buttonAbbort = this.addButton(new Button(this.width / 2 + 4, 210, 150, 20, DialogTexts.GUI_CANCEL, (button) -> {
			this.minecraft.displayGuiScreen((Screen)null);
		}));
		
		super.init();
	}
	
	@Override
	public void closeScreen() {
		this.minecraft.keyboardListener.enableRepeatEvents(false);
		super.closeScreen();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
		
		drawString(matrixStack, this.font, new TranslationTextComponent("redtec.jigsaw_block.pool"), this.width  / 2 - 153, 10, 10526880);
		this.poolName.render(matrixStack, x, y, partialTicks);
		drawString(matrixStack, font, new TranslationTextComponent("redtec.jigsaw_block.name"), this.width  / 2 - 153, 45, 10526880);
		this.name.render(matrixStack, x, y, partialTicks);
		drawString(matrixStack, this.font, new TranslationTextComponent("redtec.jigsaw_block.targetName"), this.width  / 2 - 153, 80, 10526880);
		this.targetName.render(matrixStack, x, y, partialTicks);
		drawString(matrixStack, this.font, new TranslationTextComponent("redtec.jigsaw_block.state"), this.width  / 2 - 153, 115, 10526880);
		this.replaceState.render(matrixStack, x, y, partialTicks);
		if (this.showLockOrientation) drawString(matrixStack, this.font, new TranslationTextComponent("redtec.jigsaw_block.connection"), this.width / 2 - 153, 156, 16777215);
		
	}
	
}
