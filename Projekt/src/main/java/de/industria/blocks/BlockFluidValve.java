package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Stream;

import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.util.blockfeatures.IBAdvancedBlockInfo;
import de.industria.util.blockfeatures.ITEFluidWiring;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockFluidValve extends BlockBase implements IBAdvancedBlockInfo {
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty FULLY_OPENED = BooleanProperty.create("fully_opened");
	public static final IntegerProperty FLOW = IntegerProperty.create("flow", 0, 15);
	
	protected static final VoxelShape SHAPE_UP = Stream.of(
			Block.box(4, -8, 4, 12, 1, 12),
			Block.box(7, 1, 7, 9, 4, 9),
			Block.box(2, 3, 2, 14, 4, 14)
			).reduce((v1, v2) -> {return VoxelShapes.join(v1, v2, IBooleanFunction.OR);}).get();
	protected static final VoxelShape SHAPE_HORIZONTAL = Stream.of(
			Block.box(4, 0, 11, 12, 8, 20),
			Block.box(7, 3, 8, 9, 5, 11),
			Block.box(2, -2, 8, 14, 10, 9)
			).reduce((v1, v2) -> {return VoxelShapes.join(v1, v2, IBooleanFunction.OR);}).get();
	protected static final VoxelShape SHAPE_DOWN = Stream.of(
			Block.box(4, 7, 4, 12, 16, 12),
			Block.box(7, 4, 7, 9, 7, 9),
			Block.box(2, 4, 2, 14, 5, 14)
			).reduce((v1, v2) -> {return VoxelShapes.join(v1, v2, IBooleanFunction.OR);}).get();
	
	public BlockFluidValve() {
		super("fluid_valve", Material.METAL, 2, SoundType.METAL);
	}
		
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, FULLY_OPENED, FLOW);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		Direction facing = state.getValue(FACING).getOpposite();
		if (facing == Direction.UP) {
			return SHAPE_UP;
		} else if (facing == Direction.DOWN) {
			return SHAPE_DOWN;
		} else {
			return VoxelHelper.rotateShape(SHAPE_HORIZONTAL, facing);
		}
	}
	
	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand handIn, BlockRayTraceResult rayTraceResult) {
		if (handIn == Hand.MAIN_HAND) {
			state = state.setValue(FULLY_OPENED, !state.getValue(FULLY_OPENED));
			int flow = updateFlow(state, worldIn, pos);
			setPipesFlow(state, worldIn, pos, flow);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite()).setValue(FULLY_OPENED, false).setValue(FLOW, 0);
		if (canSurvive(state, context.getLevel(), context.getClickedPos()) && countValves(state, context.getLevel(), context.getClickedPos()) == 0) return state;
		return context.getLevel().getBlockState(context.getClickedPos());
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
		if (!canSurvive(state, world, pos)) {
			world.destroyBlock(pos, true);
		} else {
			int flow = updateFlow(state, world, pos);
			setPipesFlow(state, world, pos, flow);
		}
	}
	
	protected void setPipesFlow(BlockState state, World world, BlockPos pos, int flow) {
		TileEntity pipe = world.getBlockEntity(pos.relative(state.getValue(FACING)));
		if (pipe instanceof ITEFluidWiring) {
			((ITEFluidWiring) pipe).overwriteFlow(flow);
		}
	}
	
	protected int updateFlow(BlockState state, World world, BlockPos pos) {
		if (!state.getValue(FULLY_OPENED)) {
			int redstoneSignal = world.getBestNeighborSignal(pos);
			state = state.setValue(FLOW, redstoneSignal);
			
			BlockState pipeState = world.getBlockState(pos.relative(state.getValue(FACING)));
			float flow = redstoneSignal / 15F;
			int maxFlow = (pipeState.getBlock() instanceof BlockFluidPipe) ? ((BlockFluidPipe) pipeState.getBlock()).getMaxFlow() : 0;
			int currentFlow = (int) (maxFlow * flow);
			
			world.setBlock(pos, state, 2);
			return currentFlow;
		} else {
			state = state.setValue(FLOW, 15);
			
			BlockState pipeState = world.getBlockState(pos.relative(state.getValue(FACING)));
			int maxFlow = (pipeState.getBlock() instanceof BlockFluidPipe) ? ((BlockFluidPipe) pipeState.getBlock()).getMaxFlow() : 0;
			
			world.setBlock(pos, state, 2);
			return maxFlow;
		}
	}
	
	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos pipePos = pos.relative(state.getValue(FACING));
		BlockState pipeState = world.getBlockState(pipePos);
		return pipeState.getBlock() instanceof BlockFluidPipe && countValves(state, world, pos) <= 1;
	}
	
	protected int countValves(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos pipePos = pos.relative(state.getValue(FACING));
		int i = 0;
		for (Direction d : Direction.values()) {
			if (world.getBlockState(pipePos.relative(d)).getBlock() == this) i++;
		}
		return i;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (newState.getBlock() != state.getBlock()) {
			BlockPos pipePos = pos.relative(state.getValue(FACING));
			if (world.getBlockState(pipePos).getBlock() instanceof BlockFluidPipe) {
				world.destroyBlock(pipePos, true);
			}
		}
		super.onRemove(state, world, pos, newState, moved);
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.fluidValve"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING)));
	}
	
}
