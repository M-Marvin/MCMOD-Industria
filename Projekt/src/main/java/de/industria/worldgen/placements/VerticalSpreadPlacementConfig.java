package de.industria.worldgen.placements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.placement.IPlacementConfig;

public class VerticalSpreadPlacementConfig implements IPlacementConfig {
	
	public static final Codec<VerticalSpreadPlacementConfig> CODEC = RecordCodecBuilder.create((codec) -> {
		return codec.group(Codec.INT.fieldOf("countPerChunk").forGetter((config) -> {
			return config.countPerChunk;
		}), Codec.INT.fieldOf("maxHeight").forGetter((config) -> {
			return config.maxHeight;
		})).apply(codec, VerticalSpreadPlacementConfig::new);
	});
	
	public final int countPerChunk;
	public final int maxHeight;
	
	public VerticalSpreadPlacementConfig(int countPerChunk, int maxHeight) {
		this.countPerChunk = countPerChunk;
		this.maxHeight = maxHeight;
	}
	
}
