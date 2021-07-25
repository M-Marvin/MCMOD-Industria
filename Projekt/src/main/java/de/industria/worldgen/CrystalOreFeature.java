package de.industria.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.serialization.Codec;

import de.industria.util.handler.UtilHelper;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class CrystalOreFeature extends Feature<CrystalOreFeatureConfig> {

	public CrystalOreFeature(Codec<CrystalOreFeatureConfig> codec) {
		super(codec);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean place(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, CrystalOreFeatureConfig config) {
		
		// Get valid positions in range
		List<BlockPos> crystalPositions = new ArrayList<BlockPos>();
		for (int i1 = -config.size / 2; i1 < config.size / 2; i1++) {
			for (int i2 = -config.size / 2; i2 < config.size / 2; i2++) {
				for (int i3 = -config.size / 2; i3 < config.size / 2; i3++) {
					BlockPos checkPos = new BlockPos(i1, i2, i3).offset(pos);
					BlockState checkState = reader.getBlockState(checkPos);
					if (checkState.isAir() || checkState.getFluidState().is(FluidTags.WATER)) {
						for (Direction d : Direction.values()) {
							if (config.oreState.getBlock().canSurvive(config.oreState.setValue(BlockStateProperties.FACING, d), reader, checkPos)) {
								BlockPos basePos = checkPos.relative(d);
								BlockState baseState = reader.getBlockState(basePos);
								if (config.target.test(baseState, rand)) crystalPositions.add(checkPos);
							}
						}
					}
				}
			}
		}
		
		// Place crystals
		if (crystalPositions.size() > 0) {
			for (BlockPos position : crystalPositions) {
				if (rand.nextFloat() <= config.chancePerBlock / 100F) {
					for (Direction d : UtilHelper.sortRandom(Direction.class, Direction.values(), rand)) {
						if (config.oreState.getBlock().canSurvive(config.oreState.setValue(BlockStateProperties.FACING, d), reader, position)) {
							boolean waterlogged = reader.getFluidState(position).is(FluidTags.WATER);
							reader.setBlock(position, config.oreState.setValue(BlockStateProperties.FACING, d).setValue(BlockStateProperties.WATERLOGGED, waterlogged), 2);
						}
					}
				}
			}
			return true;
		}
		
		return false;
		
	}

}
