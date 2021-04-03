package de.redtec.blocks;

import de.redtec.util.blockfeatures.IBurnableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockLogBase extends BlockBase implements IBurnableBlock {
	
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

	public BlockLogBase(String name, Material material, float hardnessAndResistance, SoundType sound) {
		super(name, material, hardnessAndResistance, sound, true);
		this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, Axis.Y));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(AXIS);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(AXIS, context.getFace().getAxis());
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
