package de.redtec.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class BlockWeathering extends BlockBase {
	
	protected Block weatheredBlock;
	
	public BlockWeathering(String name, Material material, float hardness, float resistance, SoundType sound, Block wetheredBlock) {
		super(name, material, hardness, resistance, sound, true);
		this.weatheredBlock = wetheredBlock;
	}
	
	@Override
	public boolean ticksRandomly(BlockState state) {
		return true;
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		
		if (random.nextInt(2400) == 0) {
			
			worldIn.setBlockState(pos, this.weatheredBlock.getDefaultState());
			
		}
		
	}
	
}
