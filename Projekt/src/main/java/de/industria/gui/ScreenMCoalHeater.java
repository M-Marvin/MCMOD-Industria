package de.industria.gui;

import java.awt.Color;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.tileentity.TileEntityMCoalHeater;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;

public class ScreenMCoalHeater extends ContainerScreen<ContainerMCoalHeater> {

public static final ResourceLocation COAL_HEATER_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/coal_heater.png");

	public ScreenMCoalHeater(ContainerMCoalHeater screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(COAL_HEATER_GUI_TEXTURES);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		TileEntityMCoalHeater te = this.menu.getTileEntity();
		int burnProgress = (int) (te.burnTime / te.fuelTime * 14);
		int remainingFuel = (int) te.burnTime + (te.hasFuelItems() ? te.getItem(0).getCount() * ForgeHooks.getBurnTime(te.getItem(0)) : 0);
		
		this.blit(matrixStack, i + 53, j + 54 + 14 - burnProgress, 176, 0 + 14 - burnProgress, 14, burnProgress);
		
		int red = new Color(255, 0, 0).getRGB();
		int white = new Color(63, 63, 63).getRGB();
		this.font.draw(matrixStack, new TranslationTextComponent("industria.generator.remainingFuel"), i + 90, j + 36, white);
		this.font.draw(matrixStack, new StringTextComponent("" + remainingFuel), i + 90, j + 46, remainingFuel < 500 ? red : white);
		
	}
	
}
