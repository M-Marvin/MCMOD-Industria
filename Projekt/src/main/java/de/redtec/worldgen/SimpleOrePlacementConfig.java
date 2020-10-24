package de.redtec.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.placement.IPlacementConfig;

public class SimpleOrePlacementConfig implements IPlacementConfig {
	
	public static final Codec<SimpleOrePlacementConfig> CODEC = RecordCodecBuilder.create((codec) -> {
		return codec.group(Codec.INT.fieldOf("minimumHeight").forGetter((config) -> {
			return config.minimumHeight;
		}), Codec.INT.fieldOf("maximumHeight").forGetter((config) -> {
			return config.maximumHeight;
		}), Codec.INT.fieldOf("countPerChunk").forGetter((config) -> {
			return config.countPerChunk;
		})).apply(codec, SimpleOrePlacementConfig::new);
	});
	
	public final int minimumHeight;
	public final int maximumHeight;
	public final int countPerChunk;
	
	public SimpleOrePlacementConfig(int minimumHeight, int maximumHeight, int countPerChunk) {
		this.maximumHeight = maximumHeight;
		this.minimumHeight = minimumHeight;
		this.countPerChunk = countPerChunk;
	}
	
}
