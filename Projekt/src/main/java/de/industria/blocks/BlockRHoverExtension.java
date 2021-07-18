package de.industria.blocks;

import de.industria.typeregistys.ModSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockRHoverExtension extends BlockBase {
	
	public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");
	
	public BlockRHoverExtension() {
		super("hover_extension", Material.METAL, 6, SoundType.ANVIL);
		this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVATED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(ACTIVATED);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		boolean power = worldIn.hasNeighborSignal(pos) || worldIn.getBestNeighborSignal(pos) > 0;
		boolean powered = state.getValue(ACTIVATED);
		
		if (power != powered) {
			
			worldIn.setBlockAndUpdate(pos, state.setValue(ACTIVATED, power));
			worldIn.playSound(null, pos, ModSoundEvents.BLOCK_HOVER_EXTENSION_ACTIVATED, SoundCategory.BLOCKS, 1, 1);
			
		}
		
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return true;
	}
	
}
