package de.industria.blocks;

import de.industria.typeregistys.ModItems;
import de.industria.util.blockfeatures.IBAdvancedStickyBlock;
import de.industria.util.types.AdvancedPistonBlockStructureHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class BlockRLinearConector extends BlockBase implements IBAdvancedStickyBlock {
	
	private static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;
	private static final IntegerProperty RANGE = IntegerProperty.create("range", 1, 12);
	
	public BlockRLinearConector() {
		super("linear_conector", Material.STONE, 1.5F, 0.5F, SoundType.STONE, true);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(AXIS, RANGE);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Axis axis = context.getNearestLookingDirection().getAxis();
		return this.defaultBlockState().setValue(AXIS, axis).setValue(RANGE, 1);
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		if (player.isShiftKeyDown()) {

			int range = state.getValue(RANGE);
			range++;
			if (range > 12) range = 1;
			worldIn.setBlockAndUpdate(pos, state.setValue(RANGE, range));
			
			return ActionResultType.SUCCESS;
			
		}
		
		return ActionResultType.PASS;
	}
	
	@SuppressWarnings("deprecation")
	public boolean addBlocksToMove(AdvancedPistonBlockStructureHelper pistonStructureHelper, BlockPos pos, BlockState state, World world) {
		
		Axis axis = state.getValue(AXIS);
		int range = state.getValue(RANGE);
		
		boolean flag12 = true;
		boolean flag22 = true;
		
		for (int i = 1; i <= range; i++) {
			
			BlockPos pos1 = pos;
			BlockPos pos2 = pos;
			switch (axis) {
			case X:
				if (pistonStructureHelper.getMoveDirection() == Direction.EAST) flag12 = false; // Stop Block Detecting in Push-Direction
				pos1 = pos.relative(Direction.EAST, i);
				if (pistonStructureHelper.getMoveDirection() == Direction.WEST) flag22 = false;
				pos2 = pos.relative(Direction.WEST, i);
				break;
			case Y:
				if (pistonStructureHelper.getMoveDirection() == Direction.UP) flag12 = false;
				pos1 = pos.relative(Direction.UP, i);
				if (pistonStructureHelper.getMoveDirection() == Direction.DOWN) flag22 = false;
				pos2 = pos.relative(Direction.DOWN, i);
				break;
			case Z:
				if (pistonStructureHelper.getMoveDirection() == Direction.SOUTH) flag12 = false;
				pos1 = pos.relative(Direction.SOUTH, i);
				if (pistonStructureHelper.getMoveDirection() == Direction.NORTH) flag22 = false;
				pos2 = pos.relative(Direction.NORTH, i);
				break;
			}
			
			BlockState state1 = world.getBlockState(pos1);
			BlockState state2 = world.getBlockState(pos2);
			if (!BlockRAdvancedPiston.canPush(state1, world, pos1, pistonStructureHelper.getMoveDirection(), true, pistonStructureHelper.getMoveDirection())) flag12 = false; // Check if Block in Push DIrection is Moveable
			if (!BlockRAdvancedPiston.canPush(state2, world, pos2, pistonStructureHelper.getMoveDirection(), true, pistonStructureHelper.getMoveDirection())) flag22 = false;
			if (pos1.equals(pistonStructureHelper.getPistonPos()) || state1.isAir() || state1.getPistonPushReaction() == PushReaction.DESTROY) flag12 = false;
			if (pos2.equals(pistonStructureHelper.getPistonPos()) || state2.isAir() || state2.getPistonPushReaction() == PushReaction.DESTROY) flag22 = false;
			
			boolean flag1 = !flag12 ? true : pistonStructureHelper.addBlockLine(pos1, pistonStructureHelper.getMoveDirection());
			boolean flag2 = !flag22 ? true : pistonStructureHelper.addBlockLine(pos2, pistonStructureHelper.getMoveDirection());
			
			if (state1.getBlock() == ModItems.conector_block) flag12 = false;
			if (state2.getBlock() == ModItems.conector_block) flag22 = false;
			
			if (!flag1 || !flag2) return false;
			
		}

		return true;
		
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
	      switch(rot) {
	      case COUNTERCLOCKWISE_90:
	      case CLOCKWISE_90:
	         switch((Direction.Axis)state.getValue(AXIS)) {
	         case X:
	            return state.setValue(AXIS, Direction.Axis.Z);
	         case Z:
	            return state.setValue(AXIS, Direction.Axis.X);
	         default:
	            return state;
	         }
	      default:
	         return state;
	      }
	}
	
}
