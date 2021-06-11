package de.industria.blocks;

import de.industria.Industria;
import de.industria.util.blockfeatures.IBurnableBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockLogBase extends RotatedPillarBlock implements IBurnableBlock {
	
	public BlockLogBase(String name, Material material, float hardnessAndResistance, SoundType sound) {
		super(Properties.create(material).hardnessAndResistance(hardnessAndResistance).sound(sound).harvestTool(BlockBase.getDefaultToolType(material)));
		this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, Axis.Y));
		this.setRegistryName(Industria.MODID, name);
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
