package de.redtec.worldgen.placements;

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
	protected Stream<BlockPos> getPositions(Random random, HorizontalSpreadPlacementConfig config, BlockPos pos) {
		
		int randCount = random.nextInt(config.countPerChunk);
		
		return IntStream.range(0, randCount).mapToObj((index) -> {
			
			int x = random.nextInt(15);
			int z = random.nextInt(15);
			
			return pos.add(x, 0, z);
			
		});
		
	}

}
