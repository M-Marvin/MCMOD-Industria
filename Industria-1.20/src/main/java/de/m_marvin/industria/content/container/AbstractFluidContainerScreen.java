package de.m_marvin.industria.content.container;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.types.containers.IFluidSlotContainer;
import de.m_marvin.industria.core.electrics.types.containers.IFluidSlotContainer.FluidSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.material.Fluid;

public abstract class AbstractFluidContainerScreen<T extends AbstractContainerMenu & IFluidSlotContainer> extends AbstractContainerScreen<T> {
	
	protected ResourceLocation fluidSlotTexture = IndustriaCore.UTILITY_WIDGETS_TEXTURE;
	
	public AbstractFluidContainerScreen(T pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	public void setFluidSlotTexture(ResourceLocation fluidSlotTexture) {
		this.fluidSlotTexture = fluidSlotTexture;
	}
	
	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		
		for (FluidSlot fluidSlot : this.menu.getFluidSlots()) {
			renderFluidSlot(pGuiGraphics, fluidSlot.getX(), fluidSlot.getY(), fluidSlot.getFluid().getFluid(), fluidSlot.getFluid().getAmount(), fluidSlot.getCapacity());
		}
	}
	
	public void renderFluidSlot(GuiGraphics guiGraphics, int x, int y, Fluid fluid, int amount, int maxAmount) {
		
		guiGraphics.blit(this.fluidSlotTexture, x, y, 18, 54, 0, 0);
		
	}
	
}
