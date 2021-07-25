package de.industria.worldgen.placements;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.placement.SimplePlacement;

public class VerticalSpreadPlacement extends SimplePlacement<VerticalSpreadPlacementConfig>{
	
	public VerticalSpreadPlacement(Codec<VerticalSpreadPlacementConfig> config) {
		super(config);
	}

	@Override
	protected Stream<BlockPos> place(Random random, VerticalSpreadPlacementConfig config, BlockPos pos) {
				
		int randCount = config.countPerChunk == 1 ? 1 : random.nextInt(config.countPerChunk) + 1;
		
		return IntStream.range(0, randCount).mapToObj((index) -> {
			
			return pos.offset(0, random.nextInt(config.maxHeight), 0);
			
		});
		
	}

}
