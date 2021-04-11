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

public static final ResourceLocation SCHREDDER_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/alloy_furnace.png");

	public ScreenMAlloyFurnace(ContainerMAlloyFurnace screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {

		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(SCHREDDER_GUI_TEXTURES);
		int i = this.guiLeft;
		int j = (this.height - this.ySize) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
		
		TileEntityMAlloyFurnace te = this.container.getTileEntity();
		float progress = (float) te.progress / this.container.getTileEntity().progressTotal;
		
		this.blit(matrixStack, i + 64, j + 19, 176, 0, (int) (progress * 49), 46);
		if (te.hasPower) this.blit(matrixStack, i + 116, j + 53, 176, 46, 16, 16);
		
	}
	
}