package de.m_marvin.industria.core.client.registries;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.content.registries.ModParticleTypes;
import de.m_marvin.industria.core.client.conduits.ConduitBreakParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModParticleProvoiders {
	
	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void registerParticleProvoiders(ParticleFactoryRegisterEvent event) {
		ParticleEngine reg = Minecraft.getInstance().particleEngine;
		reg.register(ModParticleTypes.CONDUIT.get(), new ConduitBreakParticle.Provider());
	}
	
}
