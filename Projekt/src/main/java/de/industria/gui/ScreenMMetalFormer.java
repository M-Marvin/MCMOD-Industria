package de.industria.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import de.industria.tileentity.TileEntityMMetalFormer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenMMetalFormer extends ContainerScreen<ContainerMMetalFormer> {

public static final ResourceLocation COAL_HEATER_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/electric_furnace.png");

	public ScreenMMetalFormer(ContainerMMetalFormer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
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
		this.minecraft.getTextureManager().bind(COAL_HEATER_GUI_TEXTURES);
		int i = this.leftPos;
		int j = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		
		TileEntityMMetalFormer te = this.menu.getTileEntity();
		float progress = (float) te.processTime / (te.processTimeTotal - 1);
		
		this.blit(matrixStack, i + 79, j + 34, 176, 0, (int) (progress * 24), 16);
		if (te.hasPower) this.blit(matrixStack, i + 56, j + 53, 176, 17, 16, 16);
		
	}
	
}