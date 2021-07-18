package de.industria.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.tileentity.TileEntityMAlloyFurnace;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenMAlloyFurnace extends ContainerScreen<ContainerMAlloyFurnace> {

public static final ResourceLocation ALLOY_FURNACE_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/alloy_furnace.png");

	public ScreenMAlloyFurnace(ContainerMAlloyFurnace screenContainer, PlayerInventory inv, ITextComponent titleIn) {
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
		this.minecraft.getTextureManager().bind(ALLOY_FURNACE_GUI_TEXTURES);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		TileEntityMAlloyFurnace te = this.menu.getTileEntity();
		float progress = (float) te.progress / this.menu.getTileEntity().progressTotal;
		
		this.blit(matrixStack, i + 64, j + 19, 176, 0, (int) (progress * 49), 46);
		if (te.hasPower) this.blit(matrixStack, i + 116, j + 53, 176, 46, 16, 16);
		
	}
	
}