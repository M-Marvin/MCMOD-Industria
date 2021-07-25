package de.industria.worldgen.placements;

import java.util.Random;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.HeightmapWorldSurfacePlacement;
import net.minecraft.world.gen.placement.NoPlacementConfig;

public class ModNextTopSurfacePlacement extends HeightmapWorldSurfacePlacement {
	
	public ModNextTopSurfacePlacement(Codec<NoPlacementConfig> config) {
		super(config);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public Stream<BlockPos> getPositions(WorldDecoratingHelper decorator, Random p_241857_2_, NoPlacementConfig p_241857_3_, BlockPos pos) {
		
		boolean wasInAirDown = false;
		boolean wasInAirUp = false;
		
		boolean outOfBoundsTop = false;
		boolean outOfBoundsBottom = false;
		
		int scannDist = 0;
		while (true) {
			
			BlockPos checkPosUp = pos.above(scannDist);
			BlockPos checkPosDown = pos.below(scannDist);

			scannDist++;
			
			if (checkPosUp.getY() <= 255 && !outOfBoundsTop) {

				BlockState checkStateUp = decorator.getBlockState(checkPosUp);
				boolean isAirUp = checkStateUp.isAir();
				if (isAirUp && !wasInAirUp) wasInAirUp = true;
				if (wasInAirUp && !isAirUp) return Stream.of(checkPosUp);
				
			} else {
				outOfBoundsTop = true;
			}
			
			if (checkPosDown.getY() >= 0 && !outOfBoundsBottom) {

				BlockState checkStateDown = decorator.getBlockState(checkPosDown);
				boolean isAirDown = checkStateDown.isAir();
				if (isAirDown && !wasInAirDown) wasInAirUp = true;
				if (wasInAirDown && !isAirDown) return Stream.of(checkPosDown);
				
			} else {
				outOfBoundsBottom = true;
			}
			
			if (outOfBoundsTop && outOfBoundsBottom) break;
			
		}
		
		return Stream.empty();
		
	}

}
