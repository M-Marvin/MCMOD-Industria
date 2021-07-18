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
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Axis.Y));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(AXIS);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(AXIS, context.getClickedFace().getAxis());
	}
	
}
