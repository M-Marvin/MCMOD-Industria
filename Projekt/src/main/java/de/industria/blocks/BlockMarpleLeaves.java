package de.industria.blocks;

import java.util.Random;

import de.industria.typeregistys.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class BlockMarpleLeaves extends BlockLeavesBase {

	public BlockMarpleLeaves() {
		super("marple_leaves", Material.LEAVES, 0.2F, 0.2F, SoundType.GRASS);
	}
	
	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}
		
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		
		boolean hasRedNeighbor = false;
		for (Direction d : Direction.values()) {
			if (worldIn.getBlockState(pos.relative(d)).getBlock() == ModItems.marple_leaves_red) hasRedNeighbor = true;
		}
		
		if (random.nextInt(hasRedNeighbor ? 1000 : 2000) == 0 && !state.getValue(PERSISTENT)) {
			
			worldIn.setBlockAndUpdate(pos, ModItems.marple_leaves_red.defaultBlockState().setValue(DISTANCE, state.getValue(DISTANCE)).setValue(PERSISTENT, false));
			
		} else {

			super.randomTick(state, worldIn, pos, random);
			
		}
		
	}
	
}
