package de.m_marvin.industria.content.blocks.machines;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.industria.content.blockentities.machines.TransformerCoilBlockEntity;
import de.m_marvin.industria.content.registries.ModBlockStateProperties;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TransformerCoilBlock extends BaseEntityBlock {
	
	public static final VoxelShape CORE_SHAPE = VoxelShapeUtility.box(2, 0, 2, 14, 16, 14);
	public static final VoxelShape BOTTOM_SHAPE =VoxelShapeUtility.box(0, 0, 0, 16, 2, 16);
	public static final VoxelShape TOP_SHAPE = VoxelShapeUtility.box(0, 14, 0, 16, 16, 16);
	public static final VoxelShape NORTH_SHAPE = VoxelShapeUtility.box(2, 0, 0, 14, 16, 2);
	public static final VoxelShape SOUTH_SHAPE = VoxelShapeUtility.box(2, 0, 14, 14, 16, 16);
	public static final VoxelShape WEST_SHAPE = VoxelShapeUtility.box(0, 0, 2, 2, 16, 14);
	public static final VoxelShape EAST_SHAPE = VoxelShapeUtility.box(14, 0, 2, 16, 16, 14);
	
	public TransformerCoilBlock(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new TransformerCoilBlockEntity(pPos, pState);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(BlockStateProperties.NORTH);
		pBuilder.add(BlockStateProperties.SOUTH);
		pBuilder.add(BlockStateProperties.EAST);
		pBuilder.add(BlockStateProperties.WEST);
		pBuilder.add(BlockStateProperties.BOTTOM);
		pBuilder.add(ModBlockStateProperties.TOP);
		pBuilder.add(ModBlockStateProperties.CORE);
		pBuilder.add(BlockStateProperties.FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return super.getStateForPlacement(pContext)
				.setValue(BlockStateProperties.NORTH, false)
				.setValue(BlockStateProperties.SOUTH, false)
				.setValue(BlockStateProperties.EAST, false)
				.setValue(BlockStateProperties.WEST, false)
				.setValue(BlockStateProperties.BOTTOM, true)
				.setValue(ModBlockStateProperties.TOP, true)
				.setValue(ModBlockStateProperties.CORE, true)
				.setValue(BlockStateProperties.FACING, pContext.getClickedFace().getOpposite());
	}
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return Shapes.or(
				pState.getValue(ModBlockStateProperties.CORE) ? CORE_SHAPE : Shapes.empty(),
				pState.getValue(ModBlockStateProperties.TOP) ? TOP_SHAPE : Shapes.empty(),
				pState.getValue(BlockStateProperties.BOTTOM) ? BOTTOM_SHAPE : Shapes.empty(),
				pState.getValue(BlockStateProperties.NORTH) ? NORTH_SHAPE : Shapes.empty(),
				pState.getValue(BlockStateProperties.SOUTH) ? SOUTH_SHAPE : Shapes.empty(),
				pState.getValue(BlockStateProperties.EAST) ? EAST_SHAPE : Shapes.empty(),
				pState.getValue(BlockStateProperties.WEST) ? WEST_SHAPE : Shapes.empty()
				);
	}
	
	public boolean findConnectedBlocks(Level level, BlockPos pos, BlockState state, Direction relative, boolean attached, int limit, int depth, List<BlockPos> connectedBlocks) {
		if (state.getBlock() != this) return true;
		if (depth >= limit || connectedBlocks.size() > limit) return false;
		Direction attachFacing = state.getValue(BlockStateProperties.FACING);
		if (attachFacing.getOpposite() != relative && !attached) return true;
		connectedBlocks.add(pos);
		for (Direction d : Direction.values()) {
			BlockPos attachedPos = pos.relative(d);
			BlockState attachState = level.getBlockState(attachedPos);
			if (connectedBlocks.contains(attachedPos)) continue;
			if (!findConnectedBlocks(level, attachedPos, attachState, d, d == attachFacing, limit, depth + 1, connectedBlocks)) return false;
		}
		return true;
	}
	
	public List<BlockPos> findTransformerBlocks(Level level, BlockPos pos, BlockState state) {
		List<BlockPos> connectedBlocks = new ArrayList<>();
		boolean toLarge = !findConnectedBlocks(level, pos, state, null, true, 36, 0, connectedBlocks);
		if (toLarge) {
			connectedBlocks.clear();
			connectedBlocks.add(pos);
		}
		return connectedBlocks;
	}
	
	public void updateConnections(Level level, BlockPos pos, BlockState state) {
		
		List<BlockPos> connectedBlocks = new ArrayList<>();
		boolean toLarge = !findConnectedBlocks(level, pos, state, null, true, 36, 0, connectedBlocks);
		
		boolean hasInvalid = toLarge;
		if (!toLarge) {

			BlockPos min = connectedBlocks.stream().reduce(MathUtility::getMinCorner).get();
			BlockPos max = connectedBlocks.stream().reduce(MathUtility::getMaxCorner).get();
			
			// Reset all masters and drop items
			for (BlockPos pos2 : connectedBlocks) {
				if (level.getBlockEntity(pos2) instanceof TransformerCoilBlockEntity transformer) {
					transformer.setMaster(false);
					transformer.dropWires();
					GameUtility.triggerClientSync(level, pos2);
				}
			}
			
			outer: for (int y = min.getY(); y <= max.getY(); y++) {
				for (int z = min.getZ(); z <= max.getZ(); z++) {
					for (int x = min.getX(); x <= max.getX(); x++) {
						BlockPos pos2 = new BlockPos(x, y, z);
						BlockState state2 = level.getBlockState(pos2);
						
						if (state2.getBlock() == this && connectedBlocks.contains(pos2)) {
							
							boolean outerBlock = x == min.getX() || x == max.getX() || z == min.getZ() || z == max.getZ();
							BlockState connectedState = state2
									.setValue(BlockStateProperties.NORTH, z == min.getZ() ? false : outerBlock)
									.setValue(BlockStateProperties.SOUTH, z == max.getZ() ? false : outerBlock)
									.setValue(BlockStateProperties.EAST, x == max.getX() ? false : outerBlock)
									.setValue(BlockStateProperties.WEST, x == min.getX() ? false : outerBlock)
									.setValue(ModBlockStateProperties.TOP, y == max.getY() ? true : false)
									.setValue(BlockStateProperties.BOTTOM, y == min.getY() ? true : false)
									.setValue(ModBlockStateProperties.CORE, outerBlock);
							if (!connectedState.equals(state2)) level.setBlock(pos2, connectedState, 2);
							
						} else {
							hasInvalid = true;
							break outer;
						}
						
					}
				}
			}
			
			// Set one block as master
			if (level.getBlockEntity(min) instanceof TransformerCoilBlockEntity transformer) {
				transformer.setMaster(true);
				GameUtility.triggerClientSync(level, min);
			}
			
		}
		
		if (hasInvalid) {
			
			for (BlockPos pos2 : connectedBlocks) {
				
				BlockState state2 = level.getBlockState(pos2);
				BlockState unconnectedState = state2
						.setValue(BlockStateProperties.NORTH, false)
						.setValue(BlockStateProperties.SOUTH, false)
						.setValue(BlockStateProperties.EAST, false)
						.setValue(BlockStateProperties.WEST, false)
						.setValue(ModBlockStateProperties.TOP, true)
						.setValue(BlockStateProperties.BOTTOM, true)
						.setValue(ModBlockStateProperties.CORE, true);
				if (!unconnectedState.equals(state2)) level.setBlock(pos2, unconnectedState, 2);

				// Set block as master (of it self)
				if (level.getBlockEntity(pos2) instanceof TransformerCoilBlockEntity transformer) {
					transformer.setMaster(true);
					GameUtility.triggerClientSync(level, pos2);
				}
				
			}
			
		}
		
	}
	
	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		updateConnections(pLevel, pPos, pState);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
		super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
		if (pNeighborBlock != this) {
			if (pPos.north().equals(pNeighborPos) && !pState.getValue(BlockStateProperties.NORTH)) return;
			if (pPos.south().equals(pNeighborPos) && !pState.getValue(BlockStateProperties.SOUTH)) return;
			if (pPos.east().equals(pNeighborPos) && !pState.getValue(BlockStateProperties.EAST)) return;
			if (pPos.west().equals(pNeighborPos) && !pState.getValue(BlockStateProperties.WEST)) return;
			if (pPos.above().equals(pNeighborPos) && pState.getValue(ModBlockStateProperties.TOP)) return;
			if (pPos.below().equals(pNeighborPos) && pState.getValue(BlockStateProperties.BOTTOM)) return;
		}
		updateConnections(pLevel, pPos, pState);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		if (pLevel.getBlockEntity(pPos) instanceof TransformerCoilBlockEntity transformer) {
			TransformerCoilBlockEntity transformerMaster = transformer.getMaster();
			ItemStack wireItem = pPlayer.getMainHandItem();
			int wiresPerWinding = transformerMaster.getWiresPerWinding();
			
			if (transformerMaster.isValidWireItem(wireItem) && wireItem.getCount() >= wiresPerWinding && transformerMaster.getWindings() < transformer.getMaxWindings()) {
				
				ItemStack wires = transformerMaster.getWires();
				if (wires.isEmpty()) {
					wires = wireItem.copy();
					wires.setCount(wiresPerWinding);
				} else {
					wires.grow(wiresPerWinding);
				}
				transformerMaster.setWires(wires);
				
				SoundType soundType = transformerMaster.getWireConduit().getSoundType();
				pLevel.playLocalSound(pPos.getX(), pPos.getY(), pPos.getZ(), soundType.getBreakSound(), SoundSource.BLOCKS, soundType.getVolume(), soundType.getPitch(), false);
				
				if (!pPlayer.isCreative()) {
					wireItem.shrink(wiresPerWinding);
				}
				
				GameUtility.triggerClientSync(pLevel, transformerMaster.getBlockPos());
				
				return InteractionResult.SUCCESS;
			}
		}
		return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
	}
	
	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		switch (pRotation) {
		 case CLOCKWISE_180:
			return pState.setValue(BlockStateProperties.NORTH, pState.getValue(BlockStateProperties.SOUTH)).setValue(BlockStateProperties.EAST, pState.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.SOUTH, pState.getValue(BlockStateProperties.NORTH)).setValue(BlockStateProperties.WEST, pState.getValue(BlockStateProperties.EAST)).setValue(BlockStateProperties.FACING, pRotation.rotate(pState.getValue(BlockStateProperties.FACING)));
		 case COUNTERCLOCKWISE_90:
			return pState.setValue(BlockStateProperties.NORTH, pState.getValue(BlockStateProperties.EAST)).setValue(BlockStateProperties.EAST, pState.getValue(BlockStateProperties.SOUTH)).setValue(BlockStateProperties.SOUTH, pState.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.WEST, pState.getValue(BlockStateProperties.NORTH)).setValue(BlockStateProperties.FACING, pRotation.rotate(pState.getValue(BlockStateProperties.FACING)));
		 case CLOCKWISE_90:
			return pState.setValue(BlockStateProperties.NORTH, pState.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.EAST, pState.getValue(BlockStateProperties.NORTH)).setValue(BlockStateProperties.SOUTH, pState.getValue(BlockStateProperties.EAST)).setValue(BlockStateProperties.WEST, pState.getValue(BlockStateProperties.SOUTH)).setValue(BlockStateProperties.FACING, pRotation.rotate(pState.getValue(BlockStateProperties.FACING)));
		 default:
			return pState;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		switch (pMirror) {
			case LEFT_RIGHT:
				return pState.setValue(BlockStateProperties.NORTH, pState.getValue(BlockStateProperties.SOUTH)).setValue(BlockStateProperties.SOUTH, pState.getValue(BlockStateProperties.NORTH)).setValue(BlockStateProperties.FACING, pMirror.mirror(pState.getValue(BlockStateProperties.FACING)));
			case FRONT_BACK:
				return pState.setValue(BlockStateProperties.EAST, pState.getValue(BlockStateProperties.WEST)).setValue(BlockStateProperties.WEST, pState.getValue(BlockStateProperties.EAST)).setValue(BlockStateProperties.FACING, pMirror.mirror(pState.getValue(BlockStateProperties.FACING)));
			default:
				return super.mirror(pState, pMirror);
		}
	}
	
}
