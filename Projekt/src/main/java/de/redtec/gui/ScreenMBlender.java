package de.redtec.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.redtec.RedTec;
import de.redtec.tileentity.TileEntityMBlender;
import de.redtec.util.gui.FluidBarTexture;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

public class ScreenMBlender extends ContainerScreen<ContainerMBlender> {
	
	public static final ResourceLocation BLENDER_GUI_TEXTURES = new ResourceLocation(RedTec.MODID, "textures/gui/blender.png");
	
	public ScreenMBlender(ContainerMBlender screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BLENDER_GUI_TEXTURES);
		int i = this.guiLeft;
		int j = (this.height - this.ySize) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
		
		TileEntityMBlender te = this.container.getTileEntity();
		float progress = (float) te.progress / (float) this.container.getTileEntity().progressTotal;
		float tankFillState = (float) te.tankFillState;
		
		this.blit(matrixStack, i + 133, j + 34, 176, 16, (int) (progress * 16), 17);
		this.blit(matrixStack, i + 63, j + 34, 176, 33, (int) (tankFillState * 18), 18);
		
		if (te.hasPower) this.blit(matrixStack, i + 63, j + 55, 176, 0, 16, 16);
		
		FluidStack fluid1 = this.container.getTileEntity().fluidIn1;
		if (!fluid1.isEmpty()) {
			float fluidA1 = fluid1.getAmount() / (float) this.container.getTileEntity().maxFluidStorage;
			FluidBarTexture.drawFluidTexture(matrixStack, this, fluid1.getFluid(), i + 0, j + 71, 16, (int) (fluidA1 * 56));
		}
		
		FluidStack fluid2 = this.container.getTileEntity().fluidIn2;
		if (!fluid2.isEmpty()) {
			float fluidA2 = fluid2.getAmount() / (float) this.container.getTileEntity().maxFluidStorage;
			FluidBarTexture.drawFluidTexture(matrixStack, this, fluid2.getFluid(), i + 19, j + 71, 16, (int) (fluidA2 * 56));
		}
		
		FluidStack fluid3 = this.container.getTileEntity().fluidOut;
		if (!fluid3.isEmpty()) {
			float fluidA3 = fluid3.getAmount() / (float) this.container.getTileEntity().maxFluidStorage;
			FluidBarTexture.drawFluidTexture(matrixStack, this, fluid3.getFluid(), i + 144, j + 71, 16, (int) (fluidA3 * 56));
		}

		if (!fluid2.isEmpty() && tankFillState > 0) {
			FluidBarTexture.drawFluidTexture(matrixStack, this, fluid2.getFluid(), i + 72, j + 71, 52, (int) (tankFillState * 55), 100);
		}
		if (!fluid1.isEmpty() && tankFillState > 0) {
			FluidBarTexture.drawFluidTexture(matrixStack, this, fluid1.getFluid(), i + 72, j + 71, 52, (int) (tankFillState * 55), 100);
		}
		
	}
	
}