package de.industria.blocks;

import java.util.Random;

import de.industria.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class BlockMarpleLeaves extends BlockLeavesBase {

	public BlockMarpleLeaves() {
		super("marple_leaves", Material.LEAVES, 0.2F, 0.2F, SoundType.PLANT);
	}
	
	@Override
	public boolean ticksRandomly(BlockState state) {
		return true;
	}
		
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		
		boolean hasRedNeighbor = false;
		for (Direction d : Direction.values()) {
			if (worldIn.getBlockState(pos.offset(d)).getBlock() == ModItems.marple_leaves_red) hasRedNeighbor = true;
		}
		
		if (random.nextInt(hasRedNeighbor ? 1000 : 2000) == 0 && !state.get(PERSISTENT)) {
			
			worldIn.setBlockState(pos, ModItems.marple_leaves_red.getDefaultState().with(DISTANCE, state.get(DISTANCE)).with(PERSISTENT, false));
			
		} else {

			super.randomTick(state, worldIn, pos, random);
			
		}
		
	}
	
}
