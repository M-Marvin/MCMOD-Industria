package de.industria.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class LakeFeatureConfig implements IFeatureConfig {
	
	public static final Codec<LakeFeatureConfig> CODEC = RecordCodecBuilder.create((codec) -> {
		return codec.group(Codec.INT.fieldOf("maxDepth").forGetter((config) -> {
			return config.maxDepth;
		}), Codec.INT.fieldOf("maxWidth").forGetter((config) -> {
			return config.maxWidth;
		}), Codec.INT.fieldOf("minWidth").forGetter((config) -> {
			return config.minWidth;
		}), Codec.INT.fieldOf("maxIterationCount").forGetter((config) -> {
			return config.maxIterationCount;
		}), BlockState.CODEC.fieldOf("fillerBlock").forGetter((config) -> {
			return config.fillerBlock;
		}), BlockState.CODEC.fieldOf("crustBlock").forGetter((config) -> {
			return config.crustBlock;
		}), BlockState.CODEC.fieldOf("borderBlock").forGetter((config) -> {
			return config.borderBlock;
		})).apply(codec, LakeFeatureConfig::new);
	});
	
	public int maxDepth;
	public int maxWidth;
	public int minWidth;
	public int maxIterationCount;
	public BlockState fillerBlock;
	public BlockState crustBlock;
	public BlockState borderBlock;
	
	public LakeFeatureConfig(int maxDepth, int maxWidth, int minWidth, int maxIterationCount, BlockState fillerBlock, BlockState crustBlock, BlockState borderBlock) {
		this.maxDepth = maxDepth;
		this.maxWidth = maxWidth;
		this.minWidth = minWidth;
		this.maxIterationCount = maxIterationCount;
		this.fillerBlock = fillerBlock;
		this.crustBlock = crustBlock;
		this.borderBlock = borderBlock;
		
	}
	
}
