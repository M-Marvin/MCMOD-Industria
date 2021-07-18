package de.industria.blocks;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockConveyorSpliter extends BlockConveyorBelt {
	
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	
	public BlockConveyorSpliter() {
		super("conveyor_spliter");
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(RIGHT, BeltState.CLOSE).setValue(LEFT, BeltState.CLOSE).setValue(WATERLOGGED, false));
	}

	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.maxItems", "~1.3"));
			info.add(new TranslationTextComponent("industria.block.info.conveyorSpliter"));
		};
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(ACTIVE);
		super.createBlockStateDefinition(builder);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction facing = context.getHorizontalDirection();
		return this.defaultBlockState().setValue(FACING, facing).setValue(RIGHT, getHingeSide(context) ? BeltState.OPEN : BeltState.CLOSE).setValue(LEFT, !getHingeSide(context) ? BeltState.OPEN : BeltState.CLOSE).setValue(ACTIVE, false);
	}
	
	// Copied placement-code from vanilla DoorBlock
	private boolean getHingeSide(BlockItemUseContext p_208073_1_) {
		BlockPos blockpos = p_208073_1_.getClickedPos();
		Direction direction = p_208073_1_.getHorizontalDirection();
		
		int j = direction.getStepX();
		int k = direction.getStepZ();
		Vector3d vector3d = p_208073_1_.getClickLocation();
		double d0 = vector3d.x - (double)blockpos.getX();
		double d1 = vector3d.z - (double)blockpos.getZ();
		return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? false : true;
	}
	
	@Override
	public BlockState updateState(World world, BlockPos pos, BlockState state) {
		boolean powered = world.hasNeighborSignal(pos) || world.getBestNeighborSignal(pos) > 0;
		if (powered != state.getValue(ACTIVE)) {
			world.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundCategory.BLOCKS, 0.2F, 1F);
		}
		return state.setValue(ACTIVE, powered);
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return true;
	}
	
}
