package de.redtec.worldgen;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.placement.SimplePlacement;

public class SimpleOrePlacement extends SimplePlacement<SimpleOrePlacementConfig>{
	
	public SimpleOrePlacement(Codec<SimpleOrePlacementConfig> config) {
		super(config);
	}

	@Override
	protected Stream<BlockPos> getPositions(Random random, SimpleOrePlacementConfig config, BlockPos pos) {
		
		return IntStream.range(0, config.countPerChunk).mapToObj((index) -> {

			int y = config.minimumHeight + random.nextInt(config.maximumHeight - config.minimumHeight);
			int x = random.nextInt(16) + pos.getX();
			int z = random.nextInt(16) + pos.getZ();
			
			return new BlockPos(x, y, z);
			
		});
		
	}

}
