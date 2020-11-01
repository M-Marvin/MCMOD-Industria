package de.redtec.util;

import de.redtec.RedTec;
import de.redtec.particles.ElectricSpark;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ModParticles {
	
	// Not Working, 1.16 Bug
	
	public static final BasicParticleType ELECTRIC_SPARK = register(new ResourceLocation(RedTec.MODID, "electric_spark"), true, ElectricSpark.Factory::new);
	
	@SuppressWarnings({ "deprecation", "resource", "unchecked" })
	private static <T extends IParticleData> BasicParticleType register(ResourceLocation key, boolean alwaysShow, ParticleManager.IParticleMetaFactory<T> particleMetaFactoryIn) {
		BasicParticleType type = new BasicParticleType(alwaysShow);
		Registry.register(Registry.PARTICLE_TYPE, key, type);
		Minecraft.getInstance().particles.registerFactory((ParticleType<T>) type, particleMetaFactoryIn);
		return type;
	}
	
}
