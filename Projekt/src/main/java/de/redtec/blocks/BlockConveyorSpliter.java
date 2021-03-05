package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockConveyorSpliter extends BlockConveyorBelt {
	
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	
	public BlockConveyorSpliter() {
		super("conveyor_spliter");
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(RIGHT, BeltState.CLOSE).with(LEFT, BeltState.CLOSE));
	}
	
	@Override
	public List<ITextComponent> getBlockInfo() {
		List<ITextComponent> info = new ArrayList<ITextComponent>();
		info.add(new TranslationTextComponent("redtec.block.info.maxItems", "~1.3"));
		info.add(new TranslationTextComponent("redtec.block.info.conveyorSpliter"));
		return info;
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(ACTIVE);
		super.fillStateContainer(builder);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction facing = context.getPlacementHorizontalFacing();
		return this.getDefaultState().with(FACING, facing).with(RIGHT, getHingeSide(context) ? BeltState.OPEN : BeltState.CLOSE).with(LEFT, !getHingeSide(context) ? BeltState.OPEN : BeltState.CLOSE).with(ACTIVE, false);
	}
	
	// Copied placement-code from vanilla DoorBlock
	private boolean getHingeSide(BlockItemUseContext p_208073_1_) {
		IBlockReader iblockreader = p_208073_1_.getWorld();
		BlockPos blockpos = p_208073_1_.getPos();
		Direction direction = p_208073_1_.getPlacementHorizontalFacing();
		BlockPos blockpos1 = blockpos.up();
		Direction direction1 = direction.rotateYCCW();
		BlockPos blockpos2 = blockpos.offset(direction1);
		BlockState blockstate = iblockreader.getBlockState(blockpos2);
		BlockPos blockpos3 = blockpos1.offset(direction1);
		BlockState blockstate1 = iblockreader.getBlockState(blockpos3);
		Direction direction2 = direction.rotateY();
		BlockPos blockpos4 = blockpos.offset(direction2);
		BlockState blockstate2 = iblockreader.getBlockState(blockpos4);
		BlockPos blockpos5 = blockpos1.offset(direction2);
		BlockState blockstate3 = iblockreader.getBlockState(blockpos5);
		int i = (blockstate.func_235785_r_(iblockreader, blockpos2) ? -1 : 0) + (blockstate1.func_235785_r_(iblockreader, blockpos3) ? -1 : 0) + (blockstate2.func_235785_r_(iblockreader, blockpos4) ? 1 : 0) + (blockstate3.func_235785_r_(iblockreader, blockpos5) ? 1 : 0);
		if (i <= 0) {
			if (i >= 0) {
				int j = direction.getXOffset();
				int k = direction.getZOffset();
				Vector3d vector3d = p_208073_1_.getHitVec();
				double d0 = vector3d.x - (double)blockpos.getX();
				double d1 = vector3d.z - (double)blockpos.getZ();
				return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? false : true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	
	@Override
	public BlockState updateState(World world, BlockPos pos, BlockState state) {
		boolean powered = world.isBlockPowered(pos) || world.getRedstonePowerFromNeighbors(pos) > 0;
		if (powered != state.get(ACTIVE)) {
			world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.2F, 1F);
		}
		return state.with(ACTIVE, powered);
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return true;
	}
	
}
