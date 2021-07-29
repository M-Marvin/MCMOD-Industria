package de.industria.blocks;

import de.industria.util.blockfeatures.IBBurnableBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockLogBase extends BlockPillarBase implements IBBurnableBlock {
	
	public BlockLogBase(String name, Material material, float hardness, float resistance, SoundType sound) {
		super(name, material, hardness, resistance, sound);
	}
	
	public BlockLogBase(String name, Material material, float hardness, float resistance, SoundType sound, boolean dropsEver) {
		super(name, material, hardness, resistance, sound, dropsEver);
	}
	
	public BlockLogBase(String name, Material material, float hardnessAndResistance, SoundType sound) {
		super(name, material, hardnessAndResistance, sound);
	}
	
	public BlockLogBase(String name, Material material, float hardnessAndResistance, SoundType sound, boolean dropsEver) {
		super(name, material, hardnessAndResistance, sound, dropsEver);
	}
	
	public BlockLogBase(String name, Properties properties) {
		super(name, properties);
	}
	
	@Override
	public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 5;
	}
	
	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 5;
	}

	@Override
	public int getBurnTime() {
		return 750;
	}
	
}
