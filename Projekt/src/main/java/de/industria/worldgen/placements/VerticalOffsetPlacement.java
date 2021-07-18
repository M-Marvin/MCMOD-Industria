package de.industria.worldgen.placements;

import java.util.Random;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.placement.SimplePlacement;

public class VerticalOffsetPlacement extends SimplePlacement<VerticalOffsetPlacementConfig>{
	
	public VerticalOffsetPlacement(Codec<VerticalOffsetPlacementConfig> config) {
		super(config);
	}

	@Override
	protected Stream<BlockPos> place(Random random, VerticalOffsetPlacementConfig config, BlockPos pos) {
		return Stream.of(pos.offset(0, config.offset, 0));
	}

}
