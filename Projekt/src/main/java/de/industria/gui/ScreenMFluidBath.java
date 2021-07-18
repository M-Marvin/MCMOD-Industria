package de.industria.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.tileentity.TileEntityMFluidBath;
import de.industria.util.gui.FluidBarTexture;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

public class ScreenMFluidBath extends ContainerScreen<ContainerMFluidBath> {
	
	public static final ResourceLocation FLUID_BATH_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/fluid_bath.png");
	
	public ScreenMFluidBath(ContainerMFluidBath screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(FLUID_BATH_GUI_TEXTURES);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		TileEntityMFluidBath te = this.menu.getTileEntity();
		float progress = (float) te.progress / (float) this.menu.getTileEntity().progressTotal;
		float fluidDurabillity = (float) te.fluidBufferState;
		
		this.blit(matrixStack, i + 53, j + 15, 176, 16, (int) (progress * 71), 16);
		this.blit(matrixStack, i + 58, j + 60, 176, 33, (int) (fluidDurabillity * 52), 6);
		
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
		
		if (this.menu.getTileEntity().fluidBufferState > 0 && this.menu.getTileEntity().lastRecipe != null) {
			FluidBarTexture.drawFluidTexture(matrixStack, this, this.menu.getTileEntity().lastRecipe.getFluidIn().getFluid(), i + 50, j + 55, 52, 29, 40 + (int) (240F * this.menu.getTileEntity().fluidBufferState));
			FluidBarTexture.drawFluidTexture(matrixStack, this, this.menu.getTileEntity().lastRecipe.getFluidIn().getFluid(), i + 50, j + 21, 52, 6, 40 + (int) (240F * this.menu.getTileEntity().fluidBufferState));
		}
		
	}
	
}