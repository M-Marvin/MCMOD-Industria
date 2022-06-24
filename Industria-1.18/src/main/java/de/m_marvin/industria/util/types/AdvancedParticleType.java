package de.m_marvin.industria.util.types;

import com.mojang.serialization.Codec;

import java.util.function.Function;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleOptions.Deserializer;
import net.minecraft.core.particles.ParticleType;

@SuppressWarnings("deprecation")
public class AdvancedParticleType <T extends ParticleOptions> extends ParticleType<T> {
	
	protected Function<ParticleType<T>, Codec<T>> codecSupplier;
	
	public AdvancedParticleType(boolean pOverrideLimiter, Deserializer<T> pDeserializer, Function<ParticleType<T>, Codec<T>> codecSupplier) {
		super(pOverrideLimiter, pDeserializer);
		this.codecSupplier = codecSupplier;
	}
	
	@Override
	public Codec<T> codec() {
		return codecSupplier.apply(this);
	}

}
