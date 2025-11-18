package de.m_marvin.industria.core.conduits.engine.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;

import de.m_marvin.industria.core.conduits.engine.ConduitStateParser;
import de.m_marvin.industria.core.conduits.types.ConduitState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

public class ConduitParticleOption implements ParticleOptions {
	
	@SuppressWarnings("deprecation")
	public static final ParticleOptions.Deserializer<ConduitParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<ConduitParticleOption>() {
		public ConduitParticleOption fromCommand(net.minecraft.core.particles.ParticleType<ConduitParticleOption> pParticleType, StringReader pReader) throws CommandSyntaxException {
			pReader.expect(' ');
			ConduitState conduit = ConduitStateParser.parseForConduit(pReader, false).conduitState();
			return new ConduitParticleOption(pParticleType, conduit);
		};
		public ConduitParticleOption fromNetwork(net.minecraft.core.particles.ParticleType<ConduitParticleOption> pParticleType, FriendlyByteBuf pBuffer) {
			return new ConduitParticleOption(pParticleType, ConduitState.readBuff(pBuffer));
		};
	};
	
	public static Codec<ConduitParticleOption> codec(ParticleType<ConduitParticleOption> type) {
		return ConduitState.CODEC.xmap((state) -> {
			return new ConduitParticleOption(type, state);
		}, (option) -> {
			return option.getConduit();
		});
	}
	
	private ParticleType<ConduitParticleOption> type;
	private ConduitState conduit;
	
	public ConduitParticleOption(ParticleType<ConduitParticleOption> type, ConduitState conduit) {
		this.type = type;
		this.conduit = conduit;
	}
	
	public ConduitState getConduit() {
		return conduit;
	}
	
	@Override
	public ParticleType<?> getType() {
		return this.type;
	}
	
	@Override
	public void writeToNetwork(FriendlyByteBuf buff) {
		this.conduit.writeBuff(buff);
	}

	@Override
	public String writeToString() {
	      return ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()) + " " + this.conduit.toString();
	}
		
}
