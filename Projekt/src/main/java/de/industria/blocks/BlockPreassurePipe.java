package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.Industria;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockPreassurePipe extends BlockContainerBase implements IAdvancedBlockInfo {
	
	public static final VoxelShape SHAPE_CORNER_DOWN = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 0, 0, 14, 14, 14), VoxelShapes.combineAndSimplify(Block.makeCuboidShape(3, 3, 0, 13, 13, 13), Block.makeCuboidShape(3, 0, 3, 13, 3, 13), IBooleanFunction.OR), IBooleanFunction.ONLY_FIRST);
	public static final VoxelShape SHAPE_CORNER_UP = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 2, 0, 14, 16, 14), VoxelShapes.combineAndSimplify(Block.makeCuboidShape(3, 3, 0, 13, 13, 13), Block.makeCuboidShape(3, 13, 3, 13, 16, 13), IBooleanFunction.OR), IBooleanFunction.ONLY_FIRST);
	public static final VoxelShape SHAPE_CORNER_HORIZONTAL = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 2, 0, 16, 14, 14), VoxelShapes.combineAndSimplify(Block.makeCuboidShape(3, 3, 0, 13, 13, 13), Block.makeCuboidShape(13, 3, 3, 16, 13, 13), IBooleanFunction.OR), IBooleanFunction.ONLY_FIRST);
	public static final VoxelShape SHAPE_STRIGHT = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 0, 2, 14, 16, 14), Block.makeCuboidShape(3, 0, 3, 13, 16, 13), IBooleanFunction.ONLY_FIRST);
	public static final VoxelShape SHAPE_STRIGHT_HORIZONTAL = VoxelShapes.combineAndSimplify(Block.makeCuboidShape(2, 2, 0, 14, 14, 16), Block.makeCuboidShape(3, 3, 0, 13, 13, 16), IBooleanFunction.ONLY_FIRST);
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final DirectionProperty CONNECTION = DirectionProperty.create("connection", Direction.values());
	
	public BlockPreassurePipe() {
		super("preassure_pipe", Material.GLASS, 1F, 2F, SoundType.GLASS);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.DOWN).with(CONNECTION, Direction.UP));
	}
	
	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, CONNECTION);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction facing = context.getFace().getOpposite();
		Direction connection = context.getNearestLookingDirection().getOpposite();
		
		BlockState state = this.getDefaultState().with(FACING, facing).with(CONNECTION, connection);
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		
		if (!canConnect(state, world, pos, facing.getOpposite())) {
			for (Direction d : Direction.values()) {
				if (d != facing && d != facing.getOpposite() && canConnect(state, world, pos, d)) {
					connection = d;
					break;
				}
			}
			
		}
		
		if (connection.getAxis().isVertical() && facing.getAxis().isHorizontal()) {
			state = state.with(CONNECTION, state.get(FACING)).with(FACING, connection);
		} else {
			state = state.with(CONNECTION, connection);
		}
		
		return state;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		if (context.getEntity() instanceof PlayerEntity) {
			if (((PlayerEntity) context.getEntity()).getHeldItemMainhand().getItem() == Item.getItemFromBlock(Industria.preassure_pipe)) return Block.makeCuboidShape(0, 0, 0, 16, 16, 16);
		}
		
		Direction facing = state.get(FACING);
		Direction connection = state.get(CONNECTION);
		VoxelShape shape = SHAPE_STRIGHT;
		if (isStright(state)) {
			shape = facing.getAxis().isVertical() ? SHAPE_STRIGHT : VoxelHelper.rotateShape(SHAPE_STRIGHT_HORIZONTAL, facing);
		} else {
			if (facing == Direction.UP) shape = SHAPE_CORNER_UP;
			if (facing == Direction.DOWN) shape = SHAPE_CORNER_DOWN;
			if (facing.getAxis().isHorizontal()) {
				shape = SHAPE_CORNER_HORIZONTAL;
				if (facing == connection.rotateYCCW()) shape = VoxelHelper.rotateShape(shape, Direction.WEST);
			}
			shape = VoxelHelper.rotateShape(shape, connection);
		}
		return shape;
		
	}
	
	public boolean isStright(BlockState state) {
		return state.get(FACING).getAxis() == state.get(CONNECTION).getAxis() || state.get(CONNECTION).getAxis().isVertical();
	}
	
	public boolean canConnect(BlockState state, World world, BlockPos pos, Direction direction) {
		BlockPos connectPos = pos.offset(direction);
		BlockState connectState = world.getBlockState(connectPos);
		if (connectState.getBlock() == Industria.preassure_pipe) {
			return connectState.get(FACING) == direction.getOpposite() || connectState.get(CONNECTION) == direction.getOpposite();
		} else if (connectState.getBlock() == Industria.pipe_preassurizer) {
			return direction == connectState.get(FACING);
		}
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
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
	
}
