package de.redtec.worldgen.placements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.placement.IPlacementConfig;

public class VerticalOffsetPlacementConfig implements IPlacementConfig {
	
	public static final Codec<VerticalOffsetPlacementConfig> CODEC = RecordCodecBuilder.create((codec) -> {
		return codec.group(Codec.INT.fieldOf("offset").forGetter((config) -> {
			return config.offset;
		})).apply(codec, VerticalOffsetPlacementConfig::new);
	});
	
	public final int offset;
	
	public VerticalOffsetPlacementConfig(int offset) {
		this.offset = offset;
	}
	
}
