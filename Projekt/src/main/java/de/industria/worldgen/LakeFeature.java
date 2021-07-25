package de.industria.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.base.Predicate;
import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class LakeFeature extends Feature<LakeFeatureConfig> {
	
	public LakeFeature(Codec<LakeFeatureConfig> codec) {
		super(codec);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean place(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, LakeFeatureConfig config) {
		
		Random random = new Random(rand.nextLong());
		
		// Make Lake Shape
		int count = random.nextInt(config.maxIterationCount) + 1;
		BlockPos genPos = new BlockPos(0, -1, 0);
		List<BlockPos> positionList = new ArrayList<BlockPos>();
		
		for (int x1 = 0; x1 < count; x1++) {
			
			int maxWidth1 = config.minWidth + random.nextInt(config.maxWidth - config.minWidth);
			int maxWidth2 = config.minWidth + random.nextInt(config.maxWidth - config.minWidth);
			
			int r1 = random.nextInt((int) (maxWidth1 / 2F));
			int r3 = random.nextInt((int) (maxWidth2 / 2F));
			genPos = genPos.offset(r1, 0, r3);
			int depth = random.nextInt(config.maxDepth) + 2;
			
			for (int i0 = 0; i0 < depth; i0++) {
				
				float f0 = i0 / (float) (depth);
				int width1 = (int) (Math.cos(f0 * (Math.PI / 2)) * maxWidth1);
				int width2 = (int) (Math.cos(f0 * (Math.PI / 2)) * maxWidth2);
				
				BlockPos placePos0 = new BlockPos(0, -i0, 0);
				
				for (int i1 = 0; i1 < width1; i1++) {
					
					float f1 = i1 / (float) (width1);
					int rowWidth = (int) (Math.sin(f1 * Math.PI) * width2);
					
					BlockPos placePos1 = placePos0.offset(i1 - width2 / 2, 0, 0);
					
					for (int i4 = 0; i4 < rowWidth; i4++) {
						
						BlockPos placePos2 = placePos1.offset(0, 0, i4 - rowWidth / 2);
						positionList.add(genPos.offset(placePos2));
						
					}
					
				}
				
			}
			
		}
		
		// Make Border Shape
		List<BlockPos> borderPositions = new ArrayList<BlockPos>();
		for (BlockPos position : positionList) {
			for (Direction d : Direction.values()) {
				BlockPos checkPos = position.relative(d);
				if (!positionList.contains(checkPos)) borderPositions.add(checkPos);		
			}
		}
		
		// Check Valid Generation
		if (positionList.size() == 0) return false;
		for (BlockPos position : positionList) {
			BlockState replaceState = reader.getBlockState(position.offset(pos));
			if (!replaceState.getFluidState().isEmpty() || replaceState.isAir() || replaceState.getBlock() == Blocks.BEDROCK) return false;
		}
		
		// Place Blocks
		for (BlockPos position : positionList) {
			BlockState genState = Blocks.CAVE_AIR.defaultBlockState();
			if (position.getY() != -1) {
				genState = config.fillerBlock;
			}
			reader.setBlock(position.offset(pos), genState, 2);
		}

		Predicate<Block> borderReplacePredicate = (block) -> block != config.crustBlock.getBlock() && block != config.fillerBlock.getBlock() && block.getBlock() != Blocks.CAVE_AIR;
		
		for (BlockPos position : borderPositions) {
			if (position.getY() == -2) {
				reader.setBlock(position.offset(pos), config.crustBlock, 2);
			} else if (random.nextInt(5) == 0 && position.getY() != 0) {
				reader.setBlock(position.offset(pos), config.crustBlock, 2);
			} else if (position.getY() != -1 && position.getY() != 0) {
				reader.setBlock(position.offset(pos), config.borderBlock, 2);
			} else if (position.getY() == -1) {
				for (int i = 0; i < 10; i++) {
					BlockPos replacePos = position.offset(random.nextInt(6) - 3, random.nextInt(6) - 3, random.nextInt(6) - 3);
					BlockState replaceState = reader.getBlockState(replacePos.offset(pos));
					if (borderReplacePredicate.apply(replaceState.getBlock())) {
						reader.setBlock(replacePos.offset(pos), Blocks.CAVE_AIR.defaultBlockState(), 2);
					}
				}
			}
		}
		
		return true;
	}
	
}
