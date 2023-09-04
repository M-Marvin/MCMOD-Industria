package de.m_marvin.industria.content.client.screens;

import de.m_marvin.industria.content.Industria;
import de.m_marvin.industria.content.container.PortableCoalGeneratorContainer;
import de.m_marvin.industria.core.client.electrics.screens.AbstractFluidContainerScreen;
import de.m_marvin.industria.core.client.util.widgets.PowerInfo;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricInfoProvider;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricInfoProvider.ElectricInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;

public class PortableCoalGeneratorScreen extends AbstractFluidContainerScreen<PortableCoalGeneratorContainer> {
	
	public static final ResourceLocation SCREEN_TEXTURE = new ResourceLocation(Industria.MODID, "textures/gui/portable_coal_generator.png");
	
	protected ElectricInfo electricInfo;
	protected PowerInfo powerInfo;
	
	public PortableCoalGeneratorScreen(PortableCoalGeneratorContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
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
		
		this.powerInfo = new PowerInfo(font, this.leftPos + 90, this.topPos + 22, 68, this.electricInfo);
		this.addRenderableWidget(this.powerInfo);
		
	}
	
	@Override
	protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
		
		pGuiGraphics.blit(SCREEN_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		
		if (this.menu.getBlockEntity().canRun()) {
			
			float burnTime = this.menu.getBlockEntity().getBurnTime() / (float) this.menu.getBlockEntity().getMaxBurnTime();
			
			pGuiGraphics.blit(SCREEN_TEXTURE, this.leftPos + 54, this.topPos + 37 + 13 - (int) (burnTime * 13), 176, 13 - (int) (burnTime * 13), 14, (int) (burnTime * 13));
			
		}
		
		this.powerInfo.setStatus(this.electricInfo);
		
	}
	
}
