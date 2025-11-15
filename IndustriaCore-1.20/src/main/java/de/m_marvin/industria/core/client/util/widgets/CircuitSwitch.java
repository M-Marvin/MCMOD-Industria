package de.m_marvin.industria.core.client.util.widgets;

import com.mojang.blaze3d.systems.RenderSystem;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.electrics.events.ElectricNetworkEvent;
import de.m_marvin.industria.core.client.util.ClientTimer;
import de.m_marvin.industria.core.client.util.GraphicsUtility;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.electrics.engine.network.CPlayerSwitchNetworkPackage;
import de.m_marvin.industria.core.electrics.types.IElectric.ElectricReference;
import de.m_marvin.industria.core.util.ConditionalExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = IndustriaCore.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
public class CircuitSwitch extends AbstractWidget {

	protected final ResourceLocation texture = GraphicsUtility.UTILITY_WIDGETS_TEXTURE;
	protected final Level level;
	protected final BlockPos componentPos;
	
	protected float time;
	protected int lampState = 0;
	protected float leverPosition = 0.0F;
	protected float leverReleasePosition;
	protected float leverReleaseTime;
	protected boolean leverGrabbed = false;
	protected double leverGrabPos;
	protected boolean leverState = false;
	
	public CircuitSwitch(int pX, int pY, Level level, BlockPos componentPos) {
		super(pX, pY, 43, 87, Component.literal("power_switch"));
		this.level = level;
		this.componentPos = componentPos;

		ElectricNetwork network = ElectricUtility.findNetworkAt(this.level, ElectricReference.block(this.componentPos));
		updateLeverState(network != null ? network.isOnline() : false);
	}

	@SubscribeEvent
	public static final void onFuseTripped(ElectricNetworkEvent.FuseTripedEvent event) {
		if (!event.getLevel().isClientSide()) return;
		
		Screen screen = Minecraft.getInstance().screen;
		
		if (screen != null) {
			for (GuiEventListener widget : screen.children()) {
				if (widget instanceof CircuitSwitch cswitch) {
					
					BlockPos switchComponent = cswitch.getComponentPos();
					boolean b = event.getNetwork().listComponents().stream()
						.filter(c -> c.isBlock() && c.reference().block().equals(switchComponent))
						.count() > 0;
					
					if (b) {
						cswitch.resetLeverLever();
						break;
					}
					
				}
			}
		}
		
	}
	
	public BlockPos getComponentPos() {
		return componentPos;
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		
		ElectricNetwork network = ElectricUtility.findNetworkAt(this.level, ElectricReference.block(this.componentPos));
		
		// Background
		pGuiGraphics.blit(this.texture, this.getX(), this.getY(), 212, 1, 43, 87);
		
		if (network == null) return;
		this.time = ClientTimer.getRenderTicks();
		
		// Status Lamps
		float lm = (float) Math.sin(this.time % 15 / 15F * Math.PI * 2);
		int ns = network.isOnline() != this.leverState ? 2 : (network.isTripped() ? 3 : (this.leverState ? 1 : 0));
		if (lm <= 0.0F || ns == 1) this.lampState = ns;
		renderLamps(pGuiGraphics, this.lampState, this.lampState == 1 ? 1F : lm);
		
		// Network load bar
		renderBar(pGuiGraphics, network.getNetworkLoad());
		
		// Lever Arrows
		int a = (!this.leverState && network.isTripped()) ? (int) (this.time % 40 / 10) : 0;
		renderArrows(pGuiGraphics, a);
		
		// Lever
		renderLever(pGuiGraphics, this.leverPosition / 33F);
		
		// Lever motion
		if (!this.leverState && this.leverPosition > 0F && !this.leverGrabbed) {
			this.leverPosition = (1F - (this.time - this.leverReleaseTime) / 10F) * this.leverReleasePosition;
		}

	}
	
	public void resetLeverLever() {
		this.leverState = false;
		releaseLever();
	}
	
	public void releaseLever() {
		this.leverGrabbed = false;
		this.leverReleasePosition = this.leverPosition;
		this.leverReleaseTime = this.time;
	}
	
	public void onLeverChanges() {
		if (!this.leverState) {
			ConditionalExecutor.CLIENT_TICK_EXECUTOR.executeAfterDelay(() -> IndustriaCore.NETWORK.sendToServer(new CPlayerSwitchNetworkPackage(this.componentPos, this.leverState)), 40);
		} else {
			IndustriaCore.NETWORK.sendToServer(new CPlayerSwitchNetworkPackage(this.componentPos, !this.leverState));
		}
	}
	
	public void updateLeverState(boolean state) {
		this.leverState = state;
		this.leverPosition = this.leverState ? 33F : 0F;
	}
	
	@Override
	public void onRelease(double pMouseX, double pMouseY) {
		super.onRelease(pMouseX, pMouseY);
		releaseLever();
	}
	
	@Override
	public void onClick(double pMouseX, double pMouseY) {
		super.onClick(pMouseX, pMouseY);
		this.leverGrabbed = true;
		this.leverGrabPos = pMouseY - this.leverPosition;
	}
	
	@Override
	protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
		super.onDrag(pMouseX, pMouseY, pDragX, pDragY);
		if (this.leverGrabbed) this.leverPosition = (float) Math.min(33F, Math.max(0F, pMouseY - this.leverGrabPos));
		
		if (this.leverPosition > 30F) {
			if (this.leverState == false) onLeverChanges();
			this.leverState = true;
			this.leverPosition = 33.0F;
		} else {
			if (this.leverState == true) onLeverChanges();
			this.leverState = false;
		}
	}
	
	public void renderBar(GuiGraphics pGuiGraphics, float state) {
		
		int bp = Math.round(state * 33);
		
		pGuiGraphics.blit(this.texture, this.getX() + 3 + bp, this.getY() + 59, 184, 65, 3, 7);
		
	}
	
	public void renderLamps(GuiGraphics pGuiGraphics, int state, float brightness) {
		
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1F, 1F, 1F, brightness);
		
		switch (state) {
		case 1:
			pGuiGraphics.blit(this.texture, this.getX() + 4, this.getY() + 69, 184, 21, 15, 13);
			break;
		case 2:
			pGuiGraphics.blit(this.texture, this.getX() + 4, this.getY() + 69, 184, 49, 15, 13);
			break;
		case 3:
			pGuiGraphics.blit(this.texture, this.getX() + 22, this.getY() + 69, 184, 35, 15, 13);
			break;
		}
		
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderSystem.disableBlend();
		
	}
	
	public void renderArrows(GuiGraphics pGuiGraphics, int lit) {

		RenderSystem.enableBlend();
		pGuiGraphics.blit(this.texture, this.getX() + 12, this.getY() + 18, 184, lit == 1 ? 11 : 2, 17, 7);
		pGuiGraphics.blit(this.texture, this.getX() + 12, this.getY() + 28, 184, lit == 2 ? 11 : 2, 17, 7);
		pGuiGraphics.blit(this.texture, this.getX() + 12, this.getY() + 38, 184, lit == 3 ? 11 : 2, 17, 7);
		RenderSystem.disableBlend();
		
	}
	
	public void renderLever(GuiGraphics pGuiGraphics, float position) {

		float a = (float) Math.cos((1 - position) * Math.PI);
		int sp = Math.round(a * 19) + 19;
		int bp = Math.round(a * 1.5F + 0.2F) + 21;
		int bh = Math.round(a * 15);
		
		RenderSystem.disableCull();
		pGuiGraphics.blit(this.texture, this.getX() + 5, this.getY() + 9 + bp, 31, bh, 217, 117, 31, 15, 256, 256);
		RenderSystem.enableCull();
		pGuiGraphics.blit(this.texture, this.getX() + 5, this.getY() + 9 + sp, 217, 94, 31, 5);
		
	}
	
	@Override
	protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {}
	
}
