package de.industria.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.tileentity.TileEntityMAirCompressor;
import de.industria.util.gui.FluidBarTexture;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

public class ScreenMAirCompressor extends ContainerScreen<ContainerMAirCompressor> {
	
	public static final ResourceLocation AIR_COMPRESSOR_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/air_compressor.png");
	
	public ScreenMAirCompressor(ContainerMAirCompressor screenContainer, PlayerInventory inv, ITextComponent titleIn) {
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
		this.minecraft.getTextureManager().bind(AIR_COMPRESSOR_GUI_TEXTURES);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		TileEntityMAirCompressor te = this.menu.getTileEntity();
		float progress1 = (float) te.progress1 / (float) 10;
		float progress2 = (float) te.progress2 / (float) 10;
		float progressTank = (float) te.tankProgress / (float) 5;
		
		this.blit(matrixStack, i + 45, j + 39, 176, 15, (int) (progress1 * 18), 6);
		this.blit(matrixStack, i + 96, j + 34, 176, 21, (int) (progress2 * 18), 16);
		this.blit(matrixStack, i + 67, j + 41, 176, 37, (int) (progressTank * 24), 7);
		
		if (te.hasPower) this.blit(matrixStack, i + 74, j + 56, 176, 0, 10, 14);
		
		FluidStack fluid = this.menu.getTileEntity().compressedAir;
		if (!fluid.isEmpty()) {
			float fluidA = fluid.getAmount() / (float) this.menu.getTileEntity().maxStorage;
			FluidBarTexture.drawFluidTexture(matrixStack, this, fluid.getFluid(), i + 108, j + 71, 16, (int) (fluidA * 56));
		}
		
	}
	
}