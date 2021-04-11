package de.industria.util.blockfeatures;

import de.industria.util.types.AdvancedPistonBlockStructureHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAdvancedStickyBlock {
	
	public boolean addBlocksToMove(AdvancedPistonBlockStructureHelper pistonStructureHelper, BlockPos pos, BlockState state, World world);
	
}
