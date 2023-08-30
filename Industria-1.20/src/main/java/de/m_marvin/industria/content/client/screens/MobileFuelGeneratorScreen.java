package de.m_marvin.industria.content.client.screens;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.container.MobileFuelGeneratorContainer;
import de.m_marvin.industria.core.client.electrics.screens.AbstractFluidContainerScreen;
import de.m_marvin.industria.core.client.util.widgets.PowerInfo;
import de.m_marvin.industria.core.client.util.widgets.StatusBar;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricInfoProvider;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricInfoProvider.ElectricInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;

public class MobileFuelGeneratorScreen extends AbstractFluidContainerScreen<MobileFuelGeneratorContainer> {
	
	public static final ResourceLocation SCREEN_TEXTURE = new ResourceLocation(Industria.MODID, "textures/gui/mobile_fuel_generator.png");
	
	protected ElectricInfo electricInfo;
	protected StatusBar bar;
	protected PowerInfo powerInfo;
	
	public MobileFuelGeneratorScreen(MobileFuelGeneratorContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
		super(pMenu, pPlayerInventory, pTitle);
	}

	@Override
	protected void init() {
		super.init();
		
		BlockState blockState = this.menu.getBlockEntity().getBlockState();
		if (blockState.getBlock() instanceof IElectricInfoProvider provider) {
			this.electricInfo = provider.getInfo(blockState, this.menu.getBlockEntity().getLevel(), this.menu.getBlockEntity().getBlockPos());
		}
		
		if (this.electricInfo == null) return;
		
		this.powerInfo = new PowerInfo(font, this.leftPos + 27, this.topPos + 20, this.electricInfo);
		this.addRenderableWidget(this.powerInfo);
		
	}
	
	@Override
	protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
		
		pGuiGraphics.blit(SCREEN_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

		this.powerInfo.setStatus(this.electricInfo);
		
	}
	
	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		
	}
	
}
