package de.redtec.blocks;

import java.util.ArrayList;
import java.util.List;

import de.redtec.RedTec;
import de.redtec.util.AdvancedPistonBlockStructureHelper;
import de.redtec.util.IAdvancedStickyBlock;
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

public class BlockRConectorBlock extends BlockBase implements IAdvancedStickyBlock {
	
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	public static final BooleanProperty[] SIDES = new BooleanProperty[] {DOWN, UP, NORTH, SOUTH, WEST, EAST};
	
	public BlockRConectorBlock() {
		super("conector_block", Material.WOOD, 1.5F, 0.5F, SoundType.WOOD, true);
		this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, false).with(SOUTH, false).with(EAST, false).with(WEST, false).with(UP, false).with(DOWN, false));
	}
	
	@Override
	protected void fillStateContainer(net.minecraft.state.StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		Direction face = hit.getFace();
		boolean sticky = state.get(SIDES[face.getIndex()]);
		
		if (!sticky) {
			
			ItemStack item = player.getHeldItemMainhand();
			
			if (item.getItem() == Items.SLIME_BALL) {
				worldIn.setBlockState(pos, state.with(SIDES[face.getIndex()], true));
				worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_SLIME_BLOCK_STEP, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
				if (!player.isCreative()) item.shrink(1);
				return ActionResultType.SUCCESS;
			}
			
		} else if (player.isSneaking()) {
			
			worldIn.setBlockState(pos, state.with(SIDES[face.getIndex()], false));
			worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_SLIME_BLOCK_BREAK, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
			if (!player.isCreative()) worldIn.addEntity(new ItemEntity(worldIn, pos.getX() + 0.5F + face.getXOffset(), pos.getY() + 0.5F + face.getYOffset(), pos.getZ() + 0.5F + face.getZOffset(), new ItemStack(Items.SLIME_BALL)));
			return ActionResultType.SUCCESS;
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(Item.getItemFromBlock(this), 1));
		int slimeCount = 0;
		for (Direction face : Direction.values()) {
			if (state.get(SIDES[face.getIndex()])) slimeCount++;
		}
		if (slimeCount > 0) drops.add(new ItemStack(Items.SLIME_BALL, slimeCount));
		return drops;
	}
	
	public boolean attachBlock(AdvancedPistonBlockStructureHelper pistonBlockStructureHelper, BlockPos attachPos, World world) {
		
		BlockState attachState = world.getBlockState(attachPos);
		
		if (attachState.getBlock() != RedTec.rail_piston) {

			return pistonBlockStructureHelper.addBlockLine(attachPos, pistonBlockStructureHelper.getMoveDirection());
			
		}
		
		return true;
		
	}
	
	@Override
	public boolean addBlocksToMove(AdvancedPistonBlockStructureHelper pistonStructureHelper, BlockPos pos, BlockState state, World world) {
		boolean canMove = true;
		if (state.get(NORTH)) canMove = attachBlock(pistonStructureHelper, pos.offset(Direction.NORTH), world) ? canMove : false;
		if (state.get(SOUTH)) canMove = attachBlock(pistonStructureHelper, pos.offset(Direction.SOUTH), world) ? canMove : false;
		if (state.get(EAST)) canMove = attachBlock(pistonStructureHelper, pos.offset(Direction.EAST), world) ? canMove : false;
		if (state.get(WEST)) canMove = attachBlock(pistonStructureHelper, pos.offset(Direction.WEST), world) ? canMove : false;
		if (state.get(UP)) canMove = attachBlock(pistonStructureHelper, pos.offset(Direction.UP), world) ? canMove : false;
		if (state.get(DOWN)) canMove = attachBlock(pistonStructureHelper, pos.offset(Direction.DOWN), world) ? canMove : false;
		return canMove;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
	      switch(mirrorIn) {
	      case LEFT_RIGHT:
	         return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
	      case FRONT_BACK:
	         return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
	      default:
	         return super.mirror(state, mirrorIn);
	      }
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
	      switch(rot) {
	      case CLOCKWISE_180:
	         return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
	      case COUNTERCLOCKWISE_90:
	         return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
	      case CLOCKWISE_90:
	         return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
	      default:
	         return state;
	      }
	}
	
}
