package de.m_marvin.industria.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class StructureFinder {
	
	public static Optional<List<BlockPos>> findStructureInRange(Level level, BlockPos origin, int maxRange, int maxBlocks, Predicate<BlockState> blockPredicate) {
		List<BlockPos> posList = new ArrayList<>();
		if (checkBlockRange(level, origin, origin, maxBlocks, maxRange, posList, blockPredicate)) {
			return Optional.of(posList);
		}
		return Optional.empty();
	}
	
	public static Optional<List<BlockPos>> findStructure(Level level, BlockPos startPos, int maxBlocks, Predicate<BlockState> blockPredicate) {
		List<BlockPos> posList = new ArrayList<>();
		if (checkBlock(level, startPos, maxBlocks, posList, blockPredicate)) {
			return Optional.of(posList);
		}
		return Optional.empty();
	}

	protected static boolean checkBlockRange(Level level, BlockPos pos, BlockPos origin, int scanDepth, int rangeLimit, List<BlockPos> posList, Predicate<BlockState> blockPredicate) {
		if (!pos.closerThan(origin, rangeLimit)) return true;
		if (!posList.contains(pos)) {
			BlockState state = level.getBlockState(pos);
			if (blockPredicate.test(state)) {
				posList.add(pos);
				if (scanDepth > 0) {
					for (Direction d : Direction.values()) {
						if (!checkBlock(level, pos.relative(d), scanDepth - 1, posList, blockPredicate)) {
							return false;
						}
					}
					return true;
				}
				return false;
			}
		}
		return true;
	}
	
	protected static boolean checkBlock(Level level, BlockPos pos, int scanDepth, List<BlockPos> posList, Predicate<BlockState> blockPredicate) {
		if (!posList.contains(pos)) {
			BlockState state = level.getBlockState(pos);
			if (blockPredicate.test(state)) {
				posList.add(pos);
				if (scanDepth > 0) {
					for (Direction d : Direction.values()) {
						if (!checkBlock(level, pos.relative(d), scanDepth - 1, posList, blockPredicate)) {
							return false;
						}
					}
					return true;
				}
				return false;
			}
		}
		return true;
	}
	
}
