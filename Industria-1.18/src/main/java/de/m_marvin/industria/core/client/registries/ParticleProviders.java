package de.m_marvin.industria.core.client.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.conduits.ConduitBreakParticle;
import de.m_marvin.industria.core.registries.ParticleTypes;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ParticleProviders {
	
	@SubscribeEvent
	public static void registerParticleProvoiders(RegisterParticleProvidersEvent event) {
		event.registerSpecial(ParticleTypes.CONDUIT.get(), new ConduitBreakParticle.Provider()); // TODO Maybe json based ?
	}
	
}
