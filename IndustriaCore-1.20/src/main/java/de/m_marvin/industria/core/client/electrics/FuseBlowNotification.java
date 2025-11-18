package de.m_marvin.industria.core.client.electrics;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.electrics.events.ElectricNetworkEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = IndustriaCore.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
public class FuseBlowNotification {

	@SubscribeEvent
	public static final void onFuseTripped(ElectricNetworkEvent.FuseTripedEvent event) {
		if (!event.getLevel().isClientSide()) return;
		
		// TODO fuse blow notification 
		System.err.println("FUSE TRIPPED !!!!!!!!!!!!!!");
		System.err.println("FUSE TRIPPED !!!!!!!!!!!!!!");
		System.err.println("FUSE TRIPPED !!!!!!!!!!!!!!");
		
	}
	
}
