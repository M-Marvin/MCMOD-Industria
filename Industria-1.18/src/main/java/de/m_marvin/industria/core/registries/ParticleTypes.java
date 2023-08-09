package de.m_marvin.industria.core.registries;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.particles.AdvancedParticleType;
import de.m_marvin.industria.core.conduits.engine.particles.ConduitParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ParticleTypes {
	
	private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, IndustriaCore.MODID);
	public static void register() {
		PARTICLE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static final RegistryObject<ParticleType<ConduitParticleOption>> CONDUIT = PARTICLE_TYPES.register("conduit", () -> (ParticleType<ConduitParticleOption>) new AdvancedParticleType<ConduitParticleOption>(false, ConduitParticleOption.DESERIALIZER, ConduitParticleOption::codec));
	
}
