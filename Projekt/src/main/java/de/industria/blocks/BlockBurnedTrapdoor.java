package de.industria.blocks;

import java.util.ArrayList;

import de.industria.Industria;
import de.industria.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.Half;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBurnedTrapdoor extends TrapDoorBlock
{
	
	public static final BooleanProperty PERSISTANT = BooleanProperty.create("persistant");
	public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 1, 8);
	
	public BlockBurnedTrapdoor() {
		super(Properties.of(Material.WOOD).strength(0.8F, 0.2F).sound(SoundType.LADDER).harvestTool(BlockBase.getDefaultToolType(Material.WOOD)).requiresCorrectToolForDrops().noOcclusion());
		this.setRegistryName(Industria.MODID, "burned_trapdoor");
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, Boolean.valueOf(false)).setValue(HALF, Half.BOTTOM).setValue(POWERED, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(PERSISTANT, true).setValue(DISTANCE, 1));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(PERSISTANT, DISTANCE);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = super.getStateForPlacement(context).setValue(PERSISTANT, true);
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
		if (state.getValue(DISTANCE) == 8 && !state.getValue(PERSISTANT)) {
			dropBlock(worldIn, pos, newState);
			return;
		}
		super.neighborChanged(newState, worldIn, pos, blockIn, fromPos, isMoving);
	}
	
	public int getDustDropAmount(World world, BlockPos pos, BlockState state) {
		return world.random.nextInt(2) + 1;
	}
	
	public void dropBlock(World world, BlockPos pos, BlockState state) {
		world.destroyBlock(pos, false);
		BlockState ashDrops = ModItems.ash.defaultBlockState().setValue(BlockFallingDust.LAYERS, getDustDropAmount(world, pos, state));
		world.setBlockAndUpdate(pos, ashDrops);
	}
	
	public BlockState updateState(World world, BlockPos pos, BlockState state) {
		int distance = BlockBurnedBlock.scann(world, pos, new ArrayList<BlockPos>(), 0);
		if (distance != state.getValue(DISTANCE)) {
			state = state.setValue(DISTANCE, distance);
		}
		return state;
	}
	
}
