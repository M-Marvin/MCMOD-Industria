package de.industria.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.tileentity.TileEntityMBlastFurnace;
import de.industria.util.gui.FluidBarTexture;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenMBlastFurnace extends ContainerScreen<ContainerMBlastFurnace> {

public static final ResourceLocation BLAST_FURNACE_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/blast_furnace.png");

	public ScreenMBlastFurnace(ContainerMBlastFurnace screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BLAST_FURNACE_GUI_TEXTURES);
		int i = this.guiLeft;
		int j = (this.height - this.ySize) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
		
		TileEntityMBlastFurnace te = this.container.getTileEntity();
		float progress = (float) te.progress / this.container.getTileEntity().progressTotal;
		
		this.blit(matrixStack, i + 97, j + 62, 176, 26, (int) (progress * 21), 5);
		if (te.hasPower) this.blit(matrixStack, i + 145, j + 59, 176, 0, 7, 13);
		if (te.isWorking) this.blit(matrixStack, i + 101, j + 37, 176, 13, 13, 13);
		if (!te.hasHeat && te.hasHeater) this.blit(matrixStack, i + 95, j + 69, 176, 40, 25, 9);
		if (te.hasHeat && te.hasHeater) this.blit(matrixStack, i + 95, j + 69, 176, 31, 25, 9);
		
		if (!te.oxygenStorage.isEmpty()) {
			float fluidA = te.oxygenStorage.getAmount() / (float) this.container.getTileEntity().maxFluidStorage;
			FluidBarTexture.drawFluidTexture(matrixStack, this, te.oxygenStorage.getFluid(), i + 15, j + 71, 16, (int) (fluidA * 56));
		}
		
	}
	
}