package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.industria.ModItems;
import de.industria.blocks.BlockRubberLog.RipeState;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTags;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockTreeTap extends BlockBase {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);
	
	public BlockTreeTap() {
		super("tree_tap", Material.METAL, 1F, 0.5F, SoundType.METAL, true);
		this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		VoxelShape shape = Block.box(5, 2, 2, 11, 6, 8);
		shape = VoxelShapes.or(shape, Block.box(7, 8, 4, 9, 10, 6));
		shape = VoxelShapes.or(shape, Block.box(7, 10, 4, 9, 12, 16));
		shape = VoxelShapes.or(shape, Block.box(6, 10, 8, 10, 13, 12));
		shape = VoxelShapes.or(shape, Block.box(7, 13, 9, 9, 15, 11));
		shape = VoxelShapes.or(shape, Block.box(5, 15, 9, 11, 16, 11));
		return VoxelHelper.rotateShape(shape, state.getValue(FACING));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, STAGE);
	}
	
	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		
		int stage = state.getValue(STAGE);
		
		if (stage < 3) {

			Direction logSide = state.getValue(FACING).getOpposite();
			BlockState logState = worldIn.getBlockState(pos.relative(logSide));
			
			if (ModTags.RUBBER_LOGS.contains(logState.getBlock())) {
				
				List<BlockPos> logs = getLogs(pos.relative(logSide), worldIn);
				
				for (BlockPos logPos : logs) {
					
					logState = worldIn.getBlockState(logPos);
					boolean isRipe = logState.getValue(BlockRubberLog.RIPE_STATE) == RipeState.IS_RIPE;
					
					if (isRipe && stage < 4) {
						
						worldIn.setBlockAndUpdate(pos, state.setValue(STAGE, stage + 1));
						worldIn.setBlockAndUpdate(logPos, logState.setValue(BlockRubberLog.RIPE_STATE, RipeState.CAN_BE_RIPE));
						break;
						
					}
					
				}
				
			}
			
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		int stage = state.getValue(STAGE);
		ItemStack itemstack = player.getMainHandItem();
		
		if (itemstack.getItem() == Items.GLASS_BOTTLE && stage > 0) {
			
			itemstack.shrink(1);
			worldIn.playSound(player, player.getX(), player.getY(), player.getZ(), ModSoundEvents.TREE_TAP_HARVEST, SoundCategory.NEUTRAL, 1.0F, 1.0F);
			if (itemstack.isEmpty()) {
			   player.setItemInHand(handIn, new ItemStack(ModItems.raw_rubber_bottle));
			} else if (!player.inventory.add(new ItemStack(ModItems.raw_rubber_bottle))) {
			   player.drop(new ItemStack(ModItems.raw_rubber_bottle), false);
			}
			
			worldIn.setBlockAndUpdate(pos, state.setValue(STAGE, stage - 1));
			
			return ActionResultType.CONSUME;
			
		}
		
		return super.use(state, worldIn, pos, player, handIn, hit);
	}
	
	public List<BlockPos> getLogs(BlockPos pos, World world) {
		List<BlockPos> logs = new ArrayList<BlockPos>();
		this.scannAt(logs, world, pos, 0);
		return logs;
	}
	
	protected void scannAt(List<BlockPos> posList, World world, BlockPos scannPos, int scannDepth) {
		if (!posList.contains(scannPos) && scannDepth < 128) {
			BlockState scannState = world.getBlockState(scannPos);
			if (ModTags.RUBBER_LOGS.contains(scannState.getBlock())) {
				posList.add(scannPos);
				for (Direction d : Direction.values()) {
					this.scannAt(posList, world, scannPos.relative(d), scannDepth + 1);
				}
			}
		}
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction side = context.getClickedFace();
		BlockState logState = context.getLevel().getBlockState(context.getClickedPos().relative(side.getOpposite()));
		if (ModTags.RUBBER_LOGS.contains(logState.getBlock())) {
			return this.defaultBlockState().setValue(FACING, side);	
		}
		return context.getLevel().getBlockState(context.getClickedPos());
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		Direction logSide = state.getValue(FACING).getOpposite();
		BlockState logState = worldIn.getBlockState(pos.relative(logSide));
		
		if (fromPos.equals(pos.relative(logSide)) && !ModTags.RUBBER_LOGS.contains(logState.getBlock())) {
			
			worldIn.destroyBlock(pos, true);
			
		}
		
	}
	
}
