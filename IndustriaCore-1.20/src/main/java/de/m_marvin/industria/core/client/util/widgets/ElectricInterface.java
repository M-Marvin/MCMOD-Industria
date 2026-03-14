package de.m_marvin.industria.core.client.util.widgets;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.electrics.engine.network.CSwitchNetworkStatePackage;
import de.m_marvin.industria.core.electrics.events.ElectricNetworkEvent;
import de.m_marvin.industria.core.electrics.types.IElectric.ElectricReference;
import de.m_marvin.industria.core.util.ConditionalExecutor;
import de.m_marvin.industria.core.util.types.EventStage;
import de.m_marvin.industria.core.util.types.PowerNetState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber()
public class ElectricInterface extends AbstractCompoundWidget implements Tickable {
	
	protected final AbstractContainerMenu menu;
	protected final LargeLever mainSwitch;
	protected final StatusIndicator powerIndicator;
	protected final StatusIndicator errorIndicator;
	protected Boolean lastState;
	protected ElectricReference reference;
	
	public ElectricInterface(int pX, int pY, AbstractContainerMenu menu, Component pMessage) {
		super(pX, pY, 50, 60, pMessage);
		this.menu = menu;
		
		this.mainSwitch = new LargeLever(pX + 0, pY + 0, Component.empty());
		this.mainSwitch.setAction(this::onSwitchNetwork);
		this.addRenderableWidget(this.mainSwitch);
		
		this.powerIndicator = new StatusIndicator(pX + 0, pY + 56, Component.empty());
		this.addRenderableWidget(this.powerIndicator);
		
		this.errorIndicator = new StatusIndicator(pX + 20, pY + 56, Component.empty());
		this.addRenderableWidget(this.errorIndicator);
		
	}
	
	public void setReference(ElectricReference reference) {
		this.reference = reference;
		tick();
	}
	
	public ElectricNetwork getNetwork() {
		return ElectricUtility.findNetworkAt(Minecraft.getInstance().level, this.reference);
	}
	
	protected void onSwitchNetwork(boolean state) {
		if (state) {
			ConditionalExecutor.CLIENT_TICK_EXECUTOR.executeAfterDelay(() -> {
				if (this.mainSwitch.getLeverState())
					IndustriaCore.NETWORK.sendToServer(new CSwitchNetworkStatePackage(this.menu.containerId, state));
			}, 50);
		} else {
			IndustriaCore.NETWORK.sendToServer(new CSwitchNetworkStatePackage(this.menu.containerId, state));
		}
	}
	
	@SubscribeEvent
	public static void onNetworkStateChange(ElectricNetworkEvent.StateChangeEvent event) {
		if (event.getStage() == EventStage.PRE || !event.getLevel().isClientSide() || Minecraft.getInstance().screen == null) return;
		for (var child : Minecraft.getInstance().screen.children())
			if (child instanceof ElectricInterface interfaceWidget && interfaceWidget.getNetwork() == event.getNetwork())
				interfaceWidget.onNetworkStateChange(event.getNewState());
	}
	
	public void onNetworkStateChange(PowerNetState state) {
		if (state == PowerNetState.FAILED) {
			this.mainSwitch.resetLever();
			this.mainSwitch.setIndicator();
		} else {
			this.mainSwitch.setLeverState(state == PowerNetState.ACTIVE);
		}
	}
	
	@Override
	public void tick() {
		
		ElectricNetwork network = getNetwork();
		if (network == null) return;
		

		if (this.lastState == null) {
			this.lastState = network.isOnline();
			this.mainSwitch.setLeverState(this.lastState);
			if (!this.lastState)
				this.mainSwitch.setIndicator();
		}
		
		if (network.isTripped()) {
			this.errorIndicator.setStatus(StatusIndicator.STATUS_WARNING, true);
		} else {
			this.errorIndicator.setStatus(0, false);
		}
		
		if (network.isOnline() && this.mainSwitch.getLeverState()) {
			this.powerIndicator.setStatus(StatusIndicator.STATUS_POWER_ACTIVE, false);
		} else if (network.isOnline() != this.mainSwitch.getLeverState()) {
			this.powerIndicator.setStatus(StatusIndicator.STATUS_POWER_PENDING, true);
		} else {
			this.powerIndicator.setStatus(0, false);
		}
		
	}
	
	@Override
	protected boolean clicked(double pMouseX, double pMouseY) {
		return false;
	}
	
	@Override
	protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		
		
		
		super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
	}
	
}