package de.redtec.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.redtec.RedTec;
import de.redtec.tileentity.TileEntityMThermalZentrifuge;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenMThermalZentrifuge extends ContainerScreen<ContainerMThermalZentrifuge> {

public static final ResourceLocation THERMAL_ZENTRIFUGE_GUI_TEXTURES = new ResourceLocation(RedTec.MODID, "textures/gui/thermal_zentrifuge.png");

	public ScreenMThermalZentrifuge(ContainerMThermalZentrifuge screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(THERMAL_ZENTRIFUGE_GUI_TEXTURES);
		int i = this.guiLeft;
		int j = (this.height - this.ySize) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
		
		TileEntityMThermalZentrifuge te = this.container.getTileEntity();
		float progress = (float) te.progress / this.container.getTileEntity().progressTotal;
		
		this.blit(matrixStack, i + 62, j + 13, 176, 0, (int) (progress * 51), 59);
		this.blit(matrixStack, i + 62, j + 72, 176, 59, (int) (te.temp * 51), 6);
		if (te.hasPower) this.blit(matrixStack, i + 44, j + 53, 176, 65, 16, 16);
		
	}
	
}