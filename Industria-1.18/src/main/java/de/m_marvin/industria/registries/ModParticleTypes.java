package de.m_marvin.industria.registries;

import de.m_marvin.industria.Industria;
import de.m_marvin.industria.particleoptions.ConduitParticleOption;
import de.m_marvin.industria.util.types.AdvancedParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid=Industria.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModParticleTypes {
	
	@SuppressWarnings("unchecked")
	public static final ParticleType<ConduitParticleOption> CONDUIT = (ParticleType<ConduitParticleOption>) new AdvancedParticleType<ConduitParticleOption>(false, ConduitParticleOption.DESERIALIZER, ConduitParticleOption::codec).setRegistryName(new ResourceLocation(Industria.MODID, "conduit"));
	
	@SubscribeEvent
	public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event) {
		IForgeRegistry<ParticleType<?>> reg = event.getRegistry();
		reg.register(CONDUIT);
	}
	
}
