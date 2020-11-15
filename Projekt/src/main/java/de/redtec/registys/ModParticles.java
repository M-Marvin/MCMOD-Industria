package de.redtec.registys;

import de.redtec.RedTec;
import de.redtec.particles.ElectricSpark;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

//@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModParticles {
	
	public static final BasicParticleType ELECTRIC_SPARK = register(new ResourceLocation(RedTec.MODID, "electric_spark"), true, ElectricSpark.Factory::new);
	
	private static <T extends IParticleData> BasicParticleType register(ResourceLocation key, boolean alwaysShow, ParticleManager.IParticleMetaFactory<T> particleMetaFactoryIn) {
		BasicParticleType type = new BasicParticleType(alwaysShow);
		type.setRegistryName(key);
		ForgeRegistries.PARTICLE_TYPES.register(type);
		return type;
	}
	
}
