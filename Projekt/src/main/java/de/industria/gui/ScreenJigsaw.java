package de.industria.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.industria.Industria;
import de.industria.blocks.BlockJigsaw;
import de.industria.blocks.BlockJigsaw.JigsawType;
import de.industria.packet.CEditJigsawTileEntityPacket;
import de.industria.packet.CGenerateJigsaw;
import de.industria.typeregistys.ModItems;
import de.industria.util.handler.ItemStackHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.command.arguments.BlockStateParser;
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
		boolean stateVliad = false;
		try {
			BlockStateParser parser = new BlockStateParser(new StringReader(this.replaceState.getValue()), true);
			parser.parse(false);
			stateVliad = true;
		} catch (CommandSyntaxException e) {}
		return	ResourceLocation.isValidResourceLocation(this.poolName.getValue()) &&
				ResourceLocation.isValidResourceLocation(this.name.getValue()) &&
				ResourceLocation.isValidResourceLocation(this.targetName.getValue()) &&
				stateVliad;
		
	}
	
	private void updateTileEntity() {
		
		ResourceLocation poolName = ResourceLocation.tryParse(this.poolName.getValue());
		ResourceLocation name = ResourceLocation.tryParse(this.name.getValue());
		ResourceLocation targetName = ResourceLocation.tryParse(this.targetName.getValue());
		boolean lock = this.lock;
		
		BlockState replaceState;
		try {
			BlockStateParser parser = new BlockStateParser(new StringReader(this.replaceState.getValue()), true);
			parser.parse(false);
			replaceState = parser.getState();
		} catch (CommandSyntaxException e) {
			replaceState = Blocks.AIR.defaultBlockState();
			Industria.LOGGER.error("Cant parse BlockState!");
			e.printStackTrace();
		}
		
		Industria.NETWORK.sendToServer(new CEditJigsawTileEntityPacket(this.menu.getTileEntity().getBlockPos(), poolName, name, targetName, replaceState, lock));
		
	}
	
	private void sendGenerate() {
		
		if (isInputValid()) this.updateTileEntity();
		
		int levels = this.levels;
		boolean keepJigsaws = this.keepJigsaws;
		
		Industria.NETWORK.sendToServer(new CGenerateJigsaw(this.menu.getTileEntity().getBlockPos(), levels, keepJigsaws));
		
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

		if (poolName.canConsumeInput()) {
			return poolName.keyPressed(keyCode, scanCode, modifiers);
		} else if (name.canConsumeInput()) {
			return name.keyPressed(keyCode, scanCode, modifiers);
		} else if (targetName.canConsumeInput()) {
			return targetName.keyPressed(keyCode, scanCode, modifiers);
		} else if (replaceState.canConsumeInput()) {
			return replaceState.keyPressed(keyCode, scanCode, modifiers);
		}
		
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
	
	@Override
	protected void init() {

		BlockState jigsawState = this.menu.getTileEntity().getBlockState();
		this.showLockOrientation = jigsawState.getBlock() == ModItems.jigsaw ? jigsawState.getValue(BlockJigsaw.TYPE) != JigsawType.HORIZONTAL : false;
		
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		
		this.poolName = new TextFieldWidget(this.font, this.width / 2 - 152, 20, 300, 20, new TranslationTextComponent("jigsaw.pool"));
		this.poolName.setMaxLength(128);
		this.poolName.setValue(this.menu.getTileEntity().poolFile.toString());
		this.poolName.setResponder((string) -> {
			this.onChangeField();
		});
		this.children.add(poolName);
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		this.name = new TextFieldWidget(this.font, this.width / 2 - 152, 55, 300, 20, new TranslationTextComponent("jigsaw.name"));
		this.name.setMaxLength(128);
		this.name.setValue(this.menu.getTileEntity().name.toString());
		this.name.setResponder((string) -> {
			this.onChangeField();
		});
		this.children.add(name);
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		this.targetName = new TextFieldWidget(this.font, this.width / 2 - 152, 90, 300, 20, new TranslationTextComponent("jigsaw.name"));
		this.targetName.setMaxLength(128);
		this.targetName.setValue(this.menu.getTileEntity().targetName.toString());
		this.targetName.setResponder((string) -> {
			this.onChangeField();
		});
		this.children.add(targetName);
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		this.replaceState = new TextFieldWidget(this.font, this.width / 2 - 152, 125, 300, 20, new TranslationTextComponent("jigsaw.replaceState"));
		this.replaceState.setMaxLength(128);
		this.replaceState.setValue(ItemStackHelper.getBlockStateString(this.menu.getTileEntity().replaceState));
		this.replaceState.setResponder((string) -> {
			this.onChangeField();
		});
		this.children.add(replaceState);
		
		this.lock = this.menu.getTileEntity().lockOrientation;
		if (this.showLockOrientation) {
			ITextComponent infoName = new TranslationTextComponent("industria.jigsaw_block.connection");
			int i = this.font.width(infoName) + 10;
			this.buttonLockRotation = this.addButton(new Button(this.width / 2 - 152 + i, 150, 300 - i, 20, new TranslationTextComponent("industria.jigsaw_block.orientation." + (this.menu.getTileEntity().lockOrientation ? "locked" : "rotateable")), (button) -> {
				this.lock = !this.lock;
				button.setMessage(new TranslationTextComponent("industria.jigsaw_block.orientation." + (this.lock ? "locked" : "rotateable")));
			}));
		}
		
		this.keepJigsaws = true;
		this.buttonKeepJigsaws = this.addButton(new Button(this.width / 2 - 50, 180, 100, 20, new TranslationTextComponent("industria.jigsaw_block.keepJigsaws." + (this.keepJigsaws ? "on" : "off")), (button) -> {
			this.keepJigsaws = !this.keepJigsaws;
			button.setMessage(new TranslationTextComponent("industria.jigsaw_block.keepJigsaws." + (this.keepJigsaws ? "on" : "off")));
		}));
		this.buttonGenerate = this.addButton(new Button(this.width / 2 + 54, 180, 100, 20, new TranslationTextComponent("industria.jigsaw_block.generate"), (button) -> {
			this.sendGenerate();
			this.minecraft.setScreen((Screen)null);
		}));
		
		this.generationLevels = this.addButton(new AbstractSlider(this.width / 2 - 154, 180, 100, 20, StringTextComponent.EMPTY, 1.0D) {
			
			{
				this.updateMessage();
			}
			
			@Override
			protected void updateMessage() {
				this.setMessage(new TranslationTextComponent("industria.jigsaw_block.levels", ScreenJigsaw.this.levels));
			}
			
			@Override
			protected void applyValue() {
				ScreenJigsaw.this.levels = (int) (this.value * 20);
			}
			
		});
		
		this.buttonDone = this.addButton(new Button(this.width / 2 - 4 - 150, 210, 150, 20, DialogTexts.GUI_DONE, (button) -> {
			this.updateTileEntity();
			this.minecraft.setScreen((Screen)null);
		}));
		this.buttonAbbort = this.addButton(new Button(this.width / 2 + 4, 210, 150, 20, DialogTexts.GUI_CANCEL, (button) -> {
			this.minecraft.setScreen((Screen)null);
		}));
		
		super.init();
	}
	
	@Override
	public void onClose() {
		this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
		super.onClose();
	}
	
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
		
		drawString(matrixStack, this.font, new TranslationTextComponent("industria.jigsaw_block.pool"), this.width  / 2 - 153, 10, 10526880);
		this.poolName.render(matrixStack, x, y, partialTicks);
		drawString(matrixStack, font, new TranslationTextComponent("industria.jigsaw_block.name"), this.width  / 2 - 153, 45, 10526880);
		this.name.render(matrixStack, x, y, partialTicks);
		drawString(matrixStack, this.font, new TranslationTextComponent("industria.jigsaw_block.targetName"), this.width  / 2 - 153, 80, 10526880);
		this.targetName.render(matrixStack, x, y, partialTicks);
		drawString(matrixStack, this.font, new TranslationTextComponent("industria.jigsaw_block.state"), this.width  / 2 - 153, 115, 10526880);
		this.replaceState.render(matrixStack, x, y, partialTicks);
		if (this.showLockOrientation) drawString(matrixStack, this.font, new TranslationTextComponent("industria.jigsaw_block.connection"), this.width / 2 - 153, 156, 16777215);
		
	}
	
	@Override
	protected void renderLabels(MatrixStack matrixStack, int x, int y) {
		this.font.draw(matrixStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);
	}
	
}
