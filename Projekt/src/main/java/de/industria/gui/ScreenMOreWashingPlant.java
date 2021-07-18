package de.industria.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.tileentity.TileEntityMOreWashingPlant;
import de.industria.util.gui.FluidBarTexture;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;

public class ScreenMOreWashingPlant extends ContainerScreen<ContainerMOreWashingPlant> {
	
	public static final ResourceLocation ORE_WASHING_PLANT_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/ore_washing_plant.png");
	
	public ScreenMOreWashingPlant(ContainerMOreWashingPlant screenContainer, PlayerInventory inv, ITextComponent titleIn) {
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
		this.minecraft.getTextureManager().bind(ORE_WASHING_PLANT_GUI_TEXTURES);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		TileEntityMOreWashingPlant te = this.menu.getTileEntity();
		float progress = (float) te.progress / (float) this.menu.getTileEntity().progressTotal;
		
		this.blit(matrixStack, i + 26, j + 15, 0, 166, (int) (progress * 125), 37);
		
		if (te.hasPower) this.blit(matrixStack, i + 25, j + 55, 176, 0, 16, 16);
		
		FluidStack fluid1 = this.menu.getTileEntity().inputFluid;
		if (!fluid1.isEmpty()) {
			float fluidA1 = fluid1.getAmount() / (float) this.menu.getTileEntity().maxFluidStorage;
			FluidBarTexture.drawFluidTexture(matrixStack, this, fluid1.getFluid(), i + 0, j + 71, 16, (int) (fluidA1 * 56));
		}
		
		FluidStack fluid3 = this.menu.getTileEntity().wasteFluid;
		if (!fluid3.isEmpty()) {
			float fluidA3 = fluid3.getAmount() / (float) this.menu.getTileEntity().maxFluidStorage;
			FluidBarTexture.drawFluidTexture(matrixStack, this, fluid3.getFluid(), i + 144, j + 71, 16, (int) (fluidA3 * 56));
		}
		
	}
	
}