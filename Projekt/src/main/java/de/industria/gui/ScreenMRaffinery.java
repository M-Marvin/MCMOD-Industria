package de.industria.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.tileentity.TileEntityMRaffinery;
import de.industria.util.gui.FluidBarTexture;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

public class ScreenMRaffinery extends ContainerScreen<ContainerMRaffinery> {
	
	public static final ResourceLocation RAFFFINERY_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/raffinery.png");
	
	public ScreenMRaffinery(ContainerMRaffinery screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(RAFFFINERY_GUI_TEXTURES);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		TileEntityMRaffinery te = this.menu.getTileEntity();
		float progress1 = (float) te.progress1 / (float) this.menu.getTileEntity().progressTotal;
		float progress2 = (float) te.progress2 / (float) this.menu.getTileEntity().progressTotal;
		float progress3 = (float) te.progress3 / (float) this.menu.getTileEntity().progressTotal;
		float progress4 = (float) te.progress4 / (float) this.menu.getTileEntity().progressTotal;
		
		this.blit(matrixStack, i + 27, j + 31, 176, 16, (int) (progress1 * 36), 22);
		this.blit(matrixStack, i + 53, j + 31, 176, 38, (int) (progress2 * 36), 22);
		this.blit(matrixStack, i + 89, j + 31, 176, 60, (int) (progress3 * 36), 22);
		this.blit(matrixStack, i + 125, j + 31, 176, 82, (int) (progress4 * 26), 22);
		
		if (te.hasPower) this.blit(matrixStack, i + 25, j + 55, 176, 0, 16, 16);
		
		FluidStack fluid1 = this.menu.getTileEntity().fluidIn;
		if (!fluid1.isEmpty()) {
			float fluidA1 = fluid1.getAmount() / (float) this.menu.getTileEntity().maxFluidStorage;
			FluidBarTexture.drawFluidTexture(matrixStack, this, fluid1.getFluid(), i + 0, j + 71, 16, (int) (fluidA1 * 56));
		}
		
		FluidStack fluid3 = this.menu.getTileEntity().fluidOut;
		if (!fluid3.isEmpty()) {
			float fluidA3 = fluid3.getAmount() / (float) this.menu.getTileEntity().maxFluidStorage;
			FluidBarTexture.drawFluidTexture(matrixStack, this, fluid3.getFluid(), i + 144, j + 71, 16, (int) (fluidA3 * 56));
		}
		
	}
	
}