package de.industria.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import de.industria.Industria;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenMStoredCrafting extends ContainerScreen<ContainerMStoredCrafting> {
	
	public static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(Industria.MODID, "textures/gui/storing_crafting_table.png");
	
	public ScreenMStoredCrafting(ContainerMStoredCrafting screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
		 RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		 this.minecraft.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
		 int i = this.guiLeft;
		 int j = (this.height - this.ySize) / 2;
		 this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
	}
	
}