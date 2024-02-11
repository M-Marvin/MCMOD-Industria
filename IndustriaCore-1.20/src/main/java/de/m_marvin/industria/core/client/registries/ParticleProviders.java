package de.m_marvin.industria.core.client.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.conduits.ConduitBreakParticle;
import de.m_marvin.industria.core.registries.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
public class ParticleProviders {
	
	@SubscribeEvent
	public static void registerParticleProvoiders(RegisterParticleProvidersEvent event) {
		event.registerSpecial(ParticleTypes.CONDUIT.get(), new ConduitBreakParticle.Provider());
	}
	
}
