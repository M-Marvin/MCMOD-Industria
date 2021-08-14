package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;

import de.industria.typeregistys.ModItems;
import de.industria.util.blockfeatures.IBAdvancedStickyBlock;
import de.industria.util.types.AdvancedPistonBlockStructureHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class BlockRConectorBlock extends BlockBase implements IBAdvancedStickyBlock {
	
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	public static final BooleanProperty[] SIDES = new BooleanProperty[] {DOWN, UP, NORTH, SOUTH, WEST, EAST};
	
	public BlockRConectorBlock() {
		super("conector_block", Material.WOOD, 1.5F, 0.5F, SoundType.WOOD, true);
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(SOUTH, false).setValue(EAST, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
	}
	
	@Override
	protected void createBlockStateDefinition(net.minecraft.state.StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		Direction face = hit.getDirection();
		boolean sticky = state.getValue(SIDES[face.get3DDataValue()]);
		
		if (!sticky) {
			
			ItemStack item = player.getMainHandItem();
			
			if (item.getItem() == Items.SLIME_BALL) {
				worldIn.setBlockAndUpdate(pos, state.setValue(SIDES[face.get3DDataValue()], true));
				worldIn.playSound((PlayerEntity)null, pos, SoundEvents.SLIME_BLOCK_STEP, SoundCategory.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.25F + 0.6F);
				if (!player.isCreative()) item.shrink(1);
				return ActionResultType.SUCCESS;
			}
			
		} else if (player.isShiftKeyDown()) {
			
			worldIn.setBlockAndUpdate(pos, state.setValue(SIDES[face.get3DDataValue()], false));
			worldIn.playSound((PlayerEntity)null, pos, SoundEvents.SLIME_BLOCK_BREAK, SoundCategory.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.25F + 0.6F);
			if (!player.isCreative()) worldIn.addFreshEntity(new ItemEntity(worldIn, pos.getX() + 0.5F + face.getStepX(), pos.getY() + 0.5F + face.getStepY(), pos.getZ() + 0.5F + face.getStepZ(), new ItemStack(Items.SLIME_BALL)));
			return ActionResultType.SUCCESS;
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(Item.byBlock(this), 1));
		int slimeCount = 0;
		for (Direction face : Direction.values()) {
			if (state.getValue(SIDES[face.get3DDataValue()])) slimeCount++;
		}
		if (slimeCount > 0) drops.add(new ItemStack(Items.SLIME_BALL, slimeCount));
		return drops;
	}
	
	public boolean attachBlock(AdvancedPistonBlockStructureHelper pistonBlockStructureHelper, BlockPos attachPos, World world) {
		
		BlockState attachState = world.getBlockState(attachPos);
		
		if (attachState.getBlock() != ModItems.rail_piston) {

			return pistonBlockStructureHelper.addBlockLine(attachPos, pistonBlockStructureHelper.getMoveDirection());
			
		}
		
		return true;
		
	}
	
	@Override
	public boolean addBlocksToMove(AdvancedPistonBlockStructureHelper pistonStructureHelper, BlockPos pos, BlockState state, World world) {
		boolean canMove = true;
		if (state.getValue(NORTH)) canMove = attachBlock(pistonStructureHelper, pos.relative(Direction.NORTH), world) ? canMove : false;
		if (state.getValue(SOUTH)) canMove = attachBlock(pistonStructureHelper, pos.relative(Direction.SOUTH), world) ? canMove : false;
		if (state.getValue(EAST)) canMove = attachBlock(pistonStructureHelper, pos.relative(Direction.EAST), world) ? canMove : false;
		if (state.getValue(WEST)) canMove = attachBlock(pistonStructureHelper, pos.relative(Direction.WEST), world) ? canMove : false;
		if (state.getValue(UP)) canMove = attachBlock(pistonStructureHelper, pos.relative(Direction.UP), world) ? canMove : false;
		if (state.getValue(DOWN)) canMove = attachBlock(pistonStructureHelper, pos.relative(Direction.DOWN), world) ? canMove : false;
		return canMove;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
	      switch(mirrorIn) {
	      case LEFT_RIGHT:
	         return state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
	      case FRONT_BACK:
	         return state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST));
	      default:
	         return super.mirror(state, mirrorIn);
	      }
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
	      switch(rot) {
	      case CLOCKWISE_180:
	         return state.setValue(NORTH, state.getValue(SOUTH)).setValue(EAST, state.getValue(WEST)).setValue(SOUTH, state.getValue(NORTH)).setValue(WEST, state.getValue(EAST));
	      case COUNTERCLOCKWISE_90:
	         return state.setValue(NORTH, state.getValue(EAST)).setValue(EAST, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(WEST)).setValue(WEST, state.getValue(NORTH));
	      case CLOCKWISE_90:
	         return state.setValue(NORTH, state.getValue(WEST)).setValue(EAST, state.getValue(NORTH)).setValue(SOUTH, state.getValue(EAST)).setValue(WEST, state.getValue(SOUTH));
	      default:
	         return state;
	      }
	}
	
}
