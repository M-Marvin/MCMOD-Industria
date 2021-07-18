package de.industria.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.tileentity.TileEntityMThermalZentrifuge;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenMThermalZentrifuge extends ContainerScreen<ContainerMThermalZentrifuge> {

public static final ResourceLocation THERMAL_ZENTRIFUGE_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/thermal_zentrifuge.png");

	public ScreenMThermalZentrifuge(ContainerMThermalZentrifuge screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(THERMAL_ZENTRIFUGE_GUI_TEXTURES);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		TileEntityMThermalZentrifuge te = this.menu.getTileEntity();
		float progress = (float) te.progress / this.menu.getTileEntity().progressTotal;
		
		this.blit(matrixStack, i + 62, j + 13, 176, 0, (int) (progress * 51), 59);
		this.blit(matrixStack, i + 62, j + 72, 176, 59, (int) (te.temp * 51), 6);
		if (te.hasPower) this.blit(matrixStack, i + 44, j + 53, 176, 65, 16, 16);
		
	}
	
}