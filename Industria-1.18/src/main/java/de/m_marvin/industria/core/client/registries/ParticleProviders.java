package de.m_marvin.industria.core.client.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.conduits.ConduitBreakParticle;
import de.m_marvin.industria.core.registries.ParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ParticleProviders {
	
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void registerParticleProvoiders(ParticleFactoryRegisterEvent event) {
		ParticleEngine reg = Minecraft.getInstance().particleEngine;
		reg.register(ParticleTypes.CONDUIT.get(), new ConduitBreakParticle.Provider());
	}
	
}
