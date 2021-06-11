package de.industria.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;

public class BlockBurnedLog extends BlockBurnedBlock {
	
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

	public BlockBurnedLog() {
		super("burned_log", 2F, 1F);
		this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, Axis.Y));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(AXIS);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(AXIS, context.getFace().getAxis());
	}
	
}
