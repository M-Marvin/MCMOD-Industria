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
	
	@Override
	public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
		this.renderBackground(p_230430_1_);
		super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
		this.renderTooltip(p_230430_1_, p_230430_2_, p_230430_3_);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(BLAST_FURNACE_GUI_TEXTURES);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		TileEntityMBlastFurnace te = this.menu.getTileEntity();
		float progress = (float) te.progress / this.menu.getTileEntity().progressTotal;
		
		this.blit(matrixStack, i + 97, j + 62, 176, 26, (int) (progress * 21), 5);
		if (te.hasPower) this.blit(matrixStack, i + 145, j + 59, 176, 0, 7, 13);
		if (te.isWorking) this.blit(matrixStack, i + 101, j + 37, 176, 13, 13, 13);
		if (!te.hasHeat && te.hasHeater) this.blit(matrixStack, i + 95, j + 69, 176, 40, 25, 9);
		if (te.hasHeat && te.hasHeater) this.blit(matrixStack, i + 95, j + 69, 176, 31, 25, 9);
		
		if (!te.gasStorage.isEmpty()) {
			float fluidA = te.gasStorage.getAmount() / (float) this.menu.getTileEntity().maxFluidStorage;
			FluidBarTexture.drawFluidTexture(matrixStack, this, te.gasStorage.getFluid(), i + 15, j + 71, 16, (int) (fluidA * 56));
		}
		
	}
	
}