package de.redtec.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockPowerEmiting extends BlockBase {
	
	public final int powerStrength;
	
	public BlockPowerEmiting(String name, Material material, float hardness, float resistance, SoundType sound, int powerStrength) {
		super(name, material, hardness, resistance, sound);
		this.powerStrength = powerStrength;
	}
	
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return this.powerStrength;
	}
	
}
