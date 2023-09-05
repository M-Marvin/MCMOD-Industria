package de.m_marvin.industria.content.client.screens;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.container.PortableFuelGeneratorContainer;
import de.m_marvin.industria.core.client.electrics.screens.AbstractFluidContainerScreen;
import de.m_marvin.industria.core.client.util.widgets.PowerInfo;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricInfoProvider;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricInfoProvider.ElectricInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;

public class PortableFuelGeneratorScreen extends AbstractFluidContainerScreen<PortableFuelGeneratorContainer> {
	
	public static final ResourceLocation SCREEN_TEXTURE = new ResourceLocation(Industria.MODID, "textures/gui/portable_fuel_generator.png");
	
	protected ElectricInfo electricInfo;
	protected PowerInfo powerInfo;
	protected int animationFrame = 0;
	
	public PortableFuelGeneratorScreen(PortableFuelGeneratorContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}

	@Override
	protected void init() {
		super.init();
		
		BlockState blockState = this.menu.getBlockEntity().getBlockState();
		if (blockState.getBlock() instanceof IElectricInfoProvider provider) {
			this.electricInfo = provider.getInfo(blockState, this.menu.getBlockEntity().getJunctionLevel(), this.menu.getBlockEntity().getJunctionBlockPos());
		}
		
		if (this.electricInfo == null) return;
		
		this.powerInfo = new PowerInfo(font, this.leftPos + 90, this.topPos + 22, 68, this.electricInfo);
		this.addRenderableWidget(this.powerInfo);
		
	}
	
	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
	}
	
	@Override
	protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {

		super.renderBackground(pGuiGraphics);
		
		pGuiGraphics.blit(SCREEN_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		
		if (this.menu.getBlockEntity().canRun()) {

			animationFrame++;
			if (animationFrame > 40) animationFrame = 0;
			
			pGuiGraphics.blit(SCREEN_TEXTURE, this.leftPos + 54, this.topPos + 38, 176 + 13 * (animationFrame / 10), 0, 13, 12);
			
		}
		
		this.powerInfo.setStatus(this.electricInfo);
		
	}
	
}
