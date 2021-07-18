package de.industria.blocks;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class BlockWeatheringStairs extends BlockStairsBase {

	protected Block weatheredBlock;
	
	public BlockWeatheringStairs(Supplier<BlockState> modelBlock, String name, Material material, float hardness, float resistance, SoundType sound, Block wetheredBlock) {
		super(modelBlock, name, material, hardness, resistance, sound, true);
		this.weatheredBlock = wetheredBlock;
	}
	
	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		
		if (random.nextInt(2400) == 0) {
			
			worldIn.setBlockAndUpdate(pos, this.weatheredBlock.defaultBlockState().setValue(SHAPE, state.getValue(SHAPE)).setValue(HALF, state.getValue(HALF)).setValue(FACING, state.getValue(FACING)));
			
		}
		
	}
	
}
