package de.industria.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.tileentity.TileEntityMSchredder;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenMSchredder extends ContainerScreen<ContainerMSchredder> {

public static final ResourceLocation SCHREDDER_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/schredder.png");

	public ScreenMSchredder(ContainerMSchredder screenContainer, PlayerInventory inv, ITextComponent titleIn) {
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
		this.minecraft.getTextureManager().bind(SCHREDDER_GUI_TEXTURES);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		TileEntityMSchredder te = this.menu.getTileEntity();
		float progress = (float) te.progress / this.menu.getTileEntity().progressTotal;
		
		this.blit(matrixStack, i + 64, j + 32, 176, 0, (int) (progress * 48), 19);
		if (te.hasPower) this.blit(matrixStack, i + 44, j + 53, 176, 19, 16, 16);
		
	}
	
}