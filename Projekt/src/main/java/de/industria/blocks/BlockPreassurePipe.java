package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.ModItems;
import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.tileentity.TileEntityPreassurePipe;
import de.industria.util.blockfeatures.IAdvancedBlockInfo;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockPreassurePipe extends BlockContainerBase implements IAdvancedBlockInfo {
	
	public static final VoxelShape SHAPE_CORNER_DOWN = VoxelShapes.join(Block.box(2, 0, 0, 14, 14, 14), VoxelShapes.join(Block.box(3, 3, 0, 13, 13, 13), Block.box(3, 0, 3, 13, 3, 13), IBooleanFunction.OR), IBooleanFunction.ONLY_FIRST);
	public static final VoxelShape SHAPE_CORNER_UP = VoxelShapes.join(Block.box(2, 2, 0, 14, 16, 14), VoxelShapes.join(Block.box(3, 3, 0, 13, 13, 13), Block.box(3, 13, 3, 13, 16, 13), IBooleanFunction.OR), IBooleanFunction.ONLY_FIRST);
	public static final VoxelShape SHAPE_CORNER_HORIZONTAL = VoxelShapes.join(Block.box(2, 2, 0, 16, 14, 14), VoxelShapes.join(Block.box(3, 3, 0, 13, 13, 13), Block.box(13, 3, 3, 16, 13, 13), IBooleanFunction.OR), IBooleanFunction.ONLY_FIRST);
	public static final VoxelShape SHAPE_STRIGHT = VoxelShapes.join(Block.box(2, 0, 2, 14, 16, 14), Block.box(3, 0, 3, 13, 16, 13), IBooleanFunction.ONLY_FIRST);
	public static final VoxelShape SHAPE_STRIGHT_HORIZONTAL = VoxelShapes.join(Block.box(2, 2, 0, 14, 14, 16), Block.box(3, 3, 0, 13, 13, 16), IBooleanFunction.ONLY_FIRST);
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final DirectionProperty CONNECTION = DirectionProperty.create("connection", Direction.values());
	
	public BlockPreassurePipe() {
		super("preassure_pipe", Material.GLASS, 1F, 2F, SoundType.GLASS);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.DOWN).setValue(CONNECTION, Direction.UP));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, CONNECTION);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction facing = context.getClickedFace().getOpposite();
		Direction connection = context.getNearestLookingDirection().getOpposite();
		
		BlockState state = this.defaultBlockState().setValue(FACING, facing).setValue(CONNECTION, connection);
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		
		if (!canConnect(state, world, pos, facing.getOpposite())) {
			for (Direction d : Direction.values()) {
				if (d != facing && d != facing.getOpposite() && canConnect(state, world, pos, d)) {
					connection = d;
					break;
				}
			}
			
		}
		
		if (connection.getAxis().isVertical() && facing.getAxis().isHorizontal()) {
			state = state.setValue(CONNECTION, state.getValue(FACING)).setValue(FACING, connection);
		} else {
			state = state.setValue(CONNECTION, connection);
		}
		
		return state;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		if (context.getEntity() instanceof PlayerEntity) {
			if (((PlayerEntity) context.getEntity()).getMainHandItem().getItem() == Item.byBlock(ModItems.preassure_pipe)) return Block.box(0, 0, 0, 16, 16, 16);
		}
		
		Direction facing = state.getValue(FACING);
		Direction connection = state.getValue(CONNECTION);
		VoxelShape shape = SHAPE_STRIGHT;
		if (isStright(state)) {
			shape = facing.getAxis().isVertical() ? SHAPE_STRIGHT : VoxelHelper.rotateShape(SHAPE_STRIGHT_HORIZONTAL, facing);
		} else {
			if (facing == Direction.UP) shape = SHAPE_CORNER_UP;
			if (facing == Direction.DOWN) shape = SHAPE_CORNER_DOWN;
			if (facing.getAxis().isHorizontal()) {
				shape = SHAPE_CORNER_HORIZONTAL;
				if (facing == connection.getCounterClockWise()) shape = VoxelHelper.rotateShape(shape, Direction.WEST);
			}
			shape = VoxelHelper.rotateShape(shape, connection);
		}
		return shape;
		
	}
	
	public boolean isStright(BlockState state) {
		return state.getValue(FACING).getAxis() == state.getValue(CONNECTION).getAxis() || state.getValue(CONNECTION).getAxis().isVertical();
	}
	
	public boolean canConnect(BlockState state, World world, BlockPos pos, Direction direction) {
		BlockPos connectPos = pos.relative(direction);
		BlockState connectState = world.getBlockState(connectPos);
		if (connectState.getBlock() == ModItems.preassure_pipe) {
			return connectState.getValue(FACING) == direction.getOpposite() || connectState.getValue(CONNECTION) == direction.getOpposite();
		} else if (connectState.getBlock() == ModItems.pipe_preassurizer) {
			return direction.getAxis() == connectState.getValue(FACING).getAxis();
		} else if (connectState.getBlock() == ModItems.preassure_pipe_item_terminal) {
			return direction.getAxis() == connectState.getValue(FACING).getAxis();
		}
		return false;
	}
	
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn) {
		return new TileEntityPreassurePipe();
	}
	
	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {
			info.add(new TranslationTextComponent("industria.block.info.preassurePipe"));
		};
	}
	
	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING))).setValue(CONNECTION, rot.rotate(state.getValue(CONNECTION)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING))).setValue(CONNECTION, mirrorIn.mirror(state.getValue(CONNECTION)));
	}
	
}
