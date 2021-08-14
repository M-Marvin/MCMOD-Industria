package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;

import de.industria.typeregistys.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBurnedSlab extends BlockSlabBase {

	public static final BooleanProperty PERSISTANT = BooleanProperty.create("persistant");
	public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 1, 8);
	
	public BlockBurnedSlab() {
		super("burned_wood_slab", Material.WOOD, 1F, 3, SoundType.LADDER);
		this.registerDefaultState(this.defaultBlockState().setValue(PERSISTANT, true).setValue(DISTANCE, 1));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(PERSISTANT, DISTANCE);
		super.createBlockStateDefinition(builder);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = super.getStateForPlacement(context).setValue(PERSISTANT, true);;
		return updateState(context.getLevel(), context.getClickedPos(), state);
	}
	
	public BlockState getNotPersistant() {
		return this.defaultBlockState().setValue(PERSISTANT, false);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		BlockState newState = updateState(worldIn, pos, state);
		if (newState != state) {
			worldIn.setBlockAndUpdate(pos, newState);
		}
		if (newState.getValue(DISTANCE) == 8 && !newState.getValue(PERSISTANT)) {
			dropBlock(worldIn, pos, newState);
		}
	}
	
	public int getDustDropAmount(World world, BlockPos pos, BlockState state) {
		return world.random.nextInt(state.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.DOUBLE ? 8 : 2) + 1;
	}
	
	public void dropBlock(World world, BlockPos pos, BlockState state) {
		world.destroyBlock(pos, false);
		BlockState ashDrops = ModItems.ash.defaultBlockState().setValue(BlockFallingDust.LAYERS, getDustDropAmount(world, pos, state));
		world.setBlockAndUpdate(pos, ashDrops);
	}
	
	public BlockState updateState(World world, BlockPos pos, BlockState state) {
		int distance = scann(world, pos, new ArrayList<BlockPos>(), 0);
		if (distance != state.getValue(DISTANCE)) {
			state = state.setValue(DISTANCE, distance);
		}
		return state;
	}
	
	@SuppressWarnings("deprecation")
	public static int scann(World world, BlockPos pos, List<BlockPos> posList, int depth) {
		if (depth < 8 && !posList.contains(pos)) {
			int dist = 8;
			posList.add(pos);
			for (Direction d : Direction.values()) {
				BlockPos scanPos = pos.relative(d);
				BlockState scanState = world.getBlockState(scanPos);
				
				if (BlockBurnedBlock.canHoldBurnedBlocks(world, pos, scanState)) {
					return depth + 1;
				} else if ((scanState.isSolidRender(world, pos) || scanState.getBlock() instanceof FenceBlock || scanState.getBlock() instanceof TrapDoorBlock || scanState.getBlock() instanceof DoorBlock) && !scanState.isAir()) {
					int i = scann(world, scanPos, posList, depth + 1);
					if (i < dist) dist = i;
				}
			}
			return dist;
		}
		return 8;
	}
	
}
