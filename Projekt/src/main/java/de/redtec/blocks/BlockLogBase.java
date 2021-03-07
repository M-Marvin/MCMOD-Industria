package de.redtec.blocks;

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

public class BlockLogBase extends BlockBase {
	
	public static EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

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
	
}
