package de.industria.worldgen.placements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.placement.IPlacementConfig;

public class HorizontalSpreadPlacementConfig implements IPlacementConfig {
	
	public static final Codec<HorizontalSpreadPlacementConfig> CODEC = RecordCodecBuilder.create((codec) -> {
		return codec.group(Codec.INT.fieldOf("countPerChunk").forGetter((config) -> {
			return config.countPerChunk;
		})).apply(codec, HorizontalSpreadPlacementConfig::new);
	});
	
	public final int countPerChunk;
	
	public HorizontalSpreadPlacementConfig(int countPerChunk) {
		this.countPerChunk = countPerChunk;
	}
	
}
