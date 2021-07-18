package de.industria.worldgen.placements;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.placement.SimplePlacement;

public class HorizontalSpreadPlacement extends SimplePlacement<HorizontalSpreadPlacementConfig>{
	
	public HorizontalSpreadPlacement(Codec<HorizontalSpreadPlacementConfig> config) {
		super(config);
	}

	@Override
	protected Stream<BlockPos> place(Random random, HorizontalSpreadPlacementConfig config, BlockPos pos) {
		
		int randCount = config.countPerChunk == 1 ? 1 : random.nextInt(config.countPerChunk) + 1;
		
		return IntStream.range(0, randCount).mapToObj((index) -> {
			
			int x = random.nextInt(16);
			int z = random.nextInt(16);
			return pos.offset(x, 0, z);
			
		});
		
	}

}
