package de.m_marvin.industria.particleoptions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;

import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.registries.ModRegistries;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ConduitParticleOption implements ParticleOptions {
	
	@SuppressWarnings("deprecation")
	public static final ParticleOptions.Deserializer<ConduitParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<ConduitParticleOption>() {
		public ConduitParticleOption fromCommand(net.minecraft.core.particles.ParticleType<ConduitParticleOption> pParticleType, StringReader pReader) throws CommandSyntaxException {
			pReader.expect(' ');
			return new ConduitParticleOption(pParticleType, ModRegistries.CONDUITS.get().getValue(ResourceLocation.read(pReader)));
		};
		public ConduitParticleOption fromNetwork(net.minecraft.core.particles.ParticleType<ConduitParticleOption> pParticleType, FriendlyByteBuf pBuffer) {
			return new ConduitParticleOption(pParticleType, ModRegistries.CONDUITS.get().getValue(pBuffer.readResourceLocation()));
		};
	};
	
	public static Codec<ConduitParticleOption> codec(ParticleType<ConduitParticleOption> type) {
		return ResourceLocation.CODEC.xmap((key) -> {
			return new ConduitParticleOption(type, ModRegistries.CONDUITS.get().getValue(key));
		}, (option) -> {
			return option.getType().getRegistryName();
		});
	}
	
	private ParticleType<ConduitParticleOption> type;
	private Conduit conduit;
	
	public ConduitParticleOption(ParticleType<ConduitParticleOption> type, Conduit conduit) {
		this.type = type;
		this.conduit = conduit;
	}
	
	public Conduit getConduit() {
		return conduit;
	}
	
	@Override
	public ParticleType<?> getType() {
		return this.type;
	}
	
	@Override
	public void writeToNetwork(FriendlyByteBuf buff) {
		buff.writeResourceLocation(this.conduit.getRegistryName());
	}

	@Override
	public String writeToString() {
	      return this.getType().getRegistryName() + " " + this.conduit.getRegistryName();
	}
		
}
