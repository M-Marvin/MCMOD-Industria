package de.industria.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.tileentity.TileEntityMElectricFurnace;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenMElectricFurnace extends ContainerScreen<ContainerMElectricFurnace> {

public static final ResourceLocation COAL_HEATER_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/electric_furnace.png");

	public ScreenMElectricFurnace(ContainerMElectricFurnace screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(COAL_HEATER_GUI_TEXTURES);
		int i = this.guiLeft;
		int j = (this.height - this.ySize) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
		
		TileEntityMElectricFurnace te = this.container.getTileEntity();
		float progress = (float) te.cookTime / (te.cookTimeTotal - 1);
		
		this.blit(matrixStack, i + 79, j + 34, 176, 0, (int) (progress * 24), 16);
		if (te.hasPower) this.blit(matrixStack, i + 56, j + 53, 176, 17, 16, 16);
		
	}
	
}