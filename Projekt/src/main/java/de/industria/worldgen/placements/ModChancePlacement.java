package de.industria.worldgen.placements;

import java.util.Random;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.placement.SimplePlacement;

public class ModChancePlacement extends SimplePlacement<ModChancePlacementConfig>{
	
	public ModChancePlacement(Codec<ModChancePlacementConfig> config) {
		super(config);
	}
	
	@Override
	protected Stream<BlockPos> place(Random random, ModChancePlacementConfig config, BlockPos pos) {
		return random.nextFloat() <= config.chance / 100F ? Stream.of(pos) : Stream.empty();
	}
	
}
