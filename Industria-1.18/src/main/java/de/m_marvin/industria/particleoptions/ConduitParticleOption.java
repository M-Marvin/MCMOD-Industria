package de.m_marvin.industria.particleoptions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.Codec;

import de.m_marvin.industria.conduits.Conduit;
import de.m_marvin.industria.registries.Conduits;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ConduitParticleOption implements ParticleOptions {
	
	public static final DynamicCommandExceptionType ERROR_UNKNOWN_CONDUIT = new DynamicCommandExceptionType((key) -> {
		return new TranslatableComponent("industria.argument.conduit.id.invalid", key);
	});
	
	@SuppressWarnings("deprecation")
	public static final ParticleOptions.Deserializer<ConduitParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<ConduitParticleOption>() {
		public ConduitParticleOption fromCommand(net.minecraft.core.particles.ParticleType<ConduitParticleOption> pParticleType, StringReader pReader) throws CommandSyntaxException {
			pReader.expect(' ');
			ResourceLocation key = ResourceLocation.read(pReader);
			Conduit conduit = Conduits.CONDUITS_REGISTRY.get().getValue(key);
			if (conduit == null) throw ERROR_UNKNOWN_CONDUIT.create(key.toString());
			return new ConduitParticleOption(pParticleType, conduit);
		};
		public ConduitParticleOption fromNetwork(net.minecraft.core.particles.ParticleType<ConduitParticleOption> pParticleType, FriendlyByteBuf pBuffer) {
			return new ConduitParticleOption(pParticleType, Conduits.CONDUITS_REGISTRY.get().getValue(pBuffer.readResourceLocation()));
		};
	};
	
	public static Codec<ConduitParticleOption> codec(ParticleType<ConduitParticleOption> type) {
		return ResourceLocation.CODEC.xmap((key) -> {
			return new ConduitParticleOption(type, Conduits.CONDUITS_REGISTRY.get().getValue(key));
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
