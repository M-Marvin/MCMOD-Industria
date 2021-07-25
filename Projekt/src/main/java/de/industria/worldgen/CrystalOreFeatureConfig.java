package de.industria.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;

public class CrystalOreFeatureConfig implements IFeatureConfig {
	
	public static final Codec<CrystalOreFeatureConfig> CODEC = RecordCodecBuilder.create((codec) -> {
		return codec.group(RuleTest.CODEC.fieldOf("target").forGetter((config) -> {
			return config.target;
		}), BlockState.CODEC.fieldOf("oreState").forGetter((config) ->  {
			return config.oreState;
		}), Codec.INT.fieldOf("size").forGetter((config) -> {
			return config.size;
		}), Codec.INT.fieldOf("chancePerBlock").forGetter((config) -> {
			return config.chancePerBlock;
		})).apply(codec, CrystalOreFeatureConfig::new);
	});
	
	public final RuleTest target;
	public final int size;
	public final int chancePerBlock;
	public final BlockState oreState;
	
	public CrystalOreFeatureConfig(RuleTest target, BlockState oreState, int size, int chancePerBlock) {
		this.target = target;
		this.size = size;
		this.oreState = oreState;
		this.chancePerBlock = chancePerBlock;
	}
	
}
