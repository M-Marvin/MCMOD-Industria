package de.industria.worldgen.placements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.placement.IPlacementConfig;

public class ModChancePlacementConfig implements IPlacementConfig {
	
	public static final Codec<ModChancePlacementConfig> CODEC = RecordCodecBuilder.create((codec) -> {
		return codec.group(Codec.FLOAT.fieldOf("chance").forGetter((config) -> {
			return config.chance;
		})).apply(codec, ModChancePlacementConfig::new);
	});
	
	public final float chance;
	
	public ModChancePlacementConfig(float chance) {
		this.chance = chance;
	}
	
}
