package de.m_marvin.industria.content.blocks.machines;

import de.m_marvin.industria.content.blockentities.machines.ConveyorBeltBlockEntity;
import de.m_marvin.industria.content.blocks.kinetics.BaseBeltBlock;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.content.registries.ModBlocks;
import de.m_marvin.industria.core.compound.types.blocks.CompoundBlock;
import de.m_marvin.industria.core.kinetics.types.blockentities.BeltBlockEntity;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility.ShapeType;
import de.m_marvin.industria.core.util.types.DiagonalDirection;
import de.m_marvin.industria.core.util.types.DiagonalPlanarDirection;
import de.m_marvin.univec.impl.Vec2i;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConveyorBeltBlock extends BaseBeltBlock {
	
	// NOTE: Some functions here are copied from the base belt because of the changed definition of the ORIENTATION block state field !
	
	public static final EnumProperty<Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	public static final EnumProperty<DiagonalPlanarDirection> ORIENTATION = ModBlocks.ORIENTATION_NON_VERTICAL;
	
	public static final VoxelShape SHAPE_STRAIGHT_END = Shapes.or(box(1, 3, 1, 15, 4, 16), box(1, 12, 1, 15, 13, 16), box(1, 4, 0, 15, 12, 1));
	
	public static final VoxelShape SHAPE_SLOPE_END = Shapes.or(
				box(1, 2, 6, 15, 4, 8),
				box(1, 3, 1, 15, 4, 6),
				box(1, 12, 1, 15, 13, 10),
				box(1, 6, 14, 15, 8, 16),
				box(1, 8, 12, 15, 10, 14),
				box(1, 10, 11.5, 15, 12, 12),
				box(1, 11.5, 10, 15, 12, 12),
				box(1, -6, 14, 15, -4, 16),
				box(1, -4, 12, 15, -2, 14),
				box(1, -2, 10, 15, 0, 12),
				box(1, 4, 0, 15, 12, 1),
				box(1, 0, 8, 15, 2, 10)
			);
	
	public ConveyorBeltBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new ConveyorBeltBlockEntity(pPos, pState);
	}
	
	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
		if (!pState.is(pNewState.getBlock())) {
			BlockEntity blockentity = pLevel.getBlockEntity(pPos);
			if (blockentity instanceof Container) {
				Containers.dropContents(pLevel, pPos, (Container) blockentity);
			}
		}
		
		super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return createTickerHelper(pBlockEntityType, ModBlockEntityTypes.CONVEYOR_BELT.get(), ConveyorBeltBlockEntity::moveItemsTick);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(AXIS, ORIENTATION, IS_END, BlockStateProperties.WATERLOGGED);
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
		return state.getValue(AXIS) != Axis.Y && state.getValue(ORIENTATION).isDiagonal();
	}
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		
		return VoxelShapeUtility.stateCachedShape(ShapeType.MISC, pState, () -> {
			
			Axis axis = pState.getValue(AXIS);
			DiagonalPlanarDirection orientation = pState.getValue(ORIENTATION);
			
			if (orientation.isDiagonal()) {
				
				if (pState.getValue(IS_END)) {
					
					switch (pState.getValue(ORIENTATION)) {
					case X_POS_Y_NEG: return VoxelShapeUtility.transformation()
							.centered()
							.rotateY(pState.getValue(AXIS) == Axis.Z ? 90 : 0)
							.uncentered()
							.transform(SHAPE_SLOPE_END);
					case X_NEG_Y_NEG: return VoxelShapeUtility.transformation()
							.centered()
							.rotateY(180)
							.rotateY(pState.getValue(AXIS) == Axis.Z ? 90 : 0)
							.uncentered()
							.transform(SHAPE_SLOPE_END);
					case X_NEG_Y_POS: return VoxelShapeUtility.transformation()
							.centered()
							.rotateX(180)
							.rotateY(pState.getValue(AXIS) == Axis.Z ? 90 : 0)
							.uncentered()
							.transform(SHAPE_SLOPE_END);
					case X_POS_Y_POS: return VoxelShapeUtility.transformation()
							.centered()
							.rotateX(180)
							.rotateY(180)
							.rotateY(pState.getValue(AXIS) == Axis.Z ? 90 : 0)
							.uncentered()
							.transform(SHAPE_SLOPE_END);
					default: return SHAPE_STRAIGHT;
					}
					
				} else {
					
					int angle = orientation.getAngleFromPositiveX() + 45;
					if (axis == Axis.Z) angle = -angle + 90;
					if (axis == Axis.Y) angle -= 90;
					if (orientation.getNormal().x == orientation.getNormal().x);
					
					return VoxelShapeUtility.transformation()
							.centered()
							.rotateFromAxisX(axis)
							.rotateAround(axis, -angle)
							.uncentered()
							.transform(SHAPE_SLOPE);
					
				}
				
			} else {

				VoxelShape shape = pState.getValue(IS_END) ? SHAPE_STRAIGHT_END : SHAPE_STRAIGHT;

				int angle = orientation.getAngleFromPositiveX();
				if (axis == Axis.X) angle += 90;
				if (axis == Axis.Z) angle -= 90;
				
				return VoxelShapeUtility.transformation()
						.centered()
						.rotateFromAxisX(axis)
						.rotateAround(axis, -angle)
						.uncentered()
						.transform(shape);
				
			}
			
		});
		
	}
	
	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		DiagonalDirection direction1 = DiagonalDirection.fromPlanarAndAxis(pState.getValue(ORIENTATION), pState.getValue(AXIS));
		DiagonalDirection direction2 = direction1.getOposite();
		
		BlockPos pos1 = pPos.offset(direction1.getNormal().x, direction1.getNormal().y, direction1.getNormal().z);
		boolean valid1 = CompoundBlock.performOnAllAndCombine(pLevel, pos1, 
				() -> isValidConnectedBelt(pLevel.getBlockState(pos1), direction2), 
				(compound, part) -> isValidConnectedBelt(part.getState(), direction2), 
				CompoundBlock::trueIfAny);
		
		if (!valid1) return false;
		
		if (!pState.getValue(IS_END)) {
			BlockPos pos2 = pPos.offset(direction2.getNormal().x, direction2.getNormal().y, direction2.getNormal().z);
			boolean valid2 = CompoundBlock.performOnAllAndCombine(pLevel, pos2, 
					() -> isValidConnectedBelt(pLevel.getBlockState(pos2), direction1), 
					(compound, part) -> isValidConnectedBelt(part.getState(), direction1), 
					CompoundBlock::trueIfAny);
			
			return valid2;
		}
		
		return true;
	}
	
	public boolean isValidConnectedBelt(BlockState pState, DiagonalDirection direction) {
		if (pState.getBlock() instanceof ConveyorBeltBlock) {
			DiagonalDirection d = DiagonalDirection.fromPlanarAndAxis(pState.getValue(ORIENTATION), pState.getValue(AXIS));
			if (direction == d) return true;
			if (direction == d.getOposite() && !pState.getValue(IS_END)) return true;
		}
		return false;
	}
	
	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {		
		DiagonalDirection direction1 = DiagonalDirection.fromPlanarAndAxis(pState.getValue(ORIENTATION), pState.getValue(AXIS));
		BlockPos pos1 = pPos.offset(direction1.getNormal().x, direction1.getNormal().y, direction1.getNormal().z);
		DiagonalDirection direction2 = direction1.getOposite();
		BlockPos pos2 = pPos.offset(direction2.getNormal().x, direction2.getNormal().y, direction2.getNormal().z);
		
		if (pNeighborPos.equals(pos1) || (!pState.getValue(IS_END) && pNeighborPos.equals(pos2)))
			pLevel.scheduleTick(pPos, this, 1);
	}
	
	public void updateDiagonalBelts(BlockState pState, Level pLevel, BlockPos pPos) {
		DiagonalDirection direction1 = DiagonalDirection.fromPlanarAndAxis(pState.getValue(ORIENTATION), pState.getValue(AXIS));
		BlockPos pos1 = pPos.offset(direction1.getNormal().x, direction1.getNormal().y, direction1.getNormal().z);
		BlockState state1 = pLevel.getBlockState(pos1);
		pLevel.scheduleTick(pos1, state1.getBlock(), 1);
		
		if (!pState.getValue(IS_END)) {
			DiagonalDirection direction2 = direction1.getOposite();
			BlockPos pos2 = pPos.offset(direction2.getNormal().x, direction2.getNormal().y, direction2.getNormal().z);
			BlockState state2 = pLevel.getBlockState(pos2);
			pLevel.scheduleTick(pos2, state2.getBlock(), 1);
		}
	}
	
	@Override
	public TransmissionNode[] getTransmissionNodes(LevelAccessor level, BlockPos pos, BlockState state) {
		Axis axis = state.getValue(AXIS);
		DiagonalPlanarDirection orientation = state.getValue(ORIENTATION);
		DiagonalDirection connectDirection = DiagonalDirection.fromPlanarAndAxis(orientation, axis);
		if (state.getValue(IS_END)) {
			return new TransmissionNode[] {
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, axis, null, connectDirection, BELT),
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, axis, null, null, BELT_ATTACHMENT)
			};
		} else {
			return new TransmissionNode[] {
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, axis, null, connectDirection, BELT),
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, axis, null, connectDirection.getOposite(), BELT),
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, axis, null, null, BELT_ATTACHMENT)
			};
		}
	}
	
	@Override
	public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		
		if (pLevel.getBlockEntity(pPos) instanceof BeltBlockEntity belt) {
			
			if (belt instanceof ConveyorBeltBlockEntity conveyor && pEntity instanceof ItemEntity itemEntity) {
				ItemStack item = itemEntity.getItem();
				for (int slot = 0; slot < conveyor.getContainerSize(); slot++) {
					if (!conveyor.canPlaceItem(slot, item)) continue;
					ItemStack itemInSlot = conveyor.getItem(slot);
					if (itemInSlot.isEmpty()) {
						conveyor.setItem(slot, item);
						itemEntity.remove(RemovalReason.KILLED);
						return;
					}
				}
			}
			
			Axis axis = pState.getValue(AXIS);
			DiagonalPlanarDirection orientation = pState.getValue(ORIENTATION);
			double rpm = belt.getRPM(0);
			
			Vec2i vdir = new Vec2i(orientation.getNormal());
			vdir.x = Math.abs(vdir.x);
			
			Vec3i pushDirection;
			switch (axis) {
			case Z: pushDirection = new Vec3i(-vdir.x, vdir.y, 0); break;
			case X: pushDirection = new Vec3i(0, vdir.y, vdir.x); break;
			default: return;
			}
			
			Vec3d force = new Vec3d(pushDirection).mul(rpm * 0.0012F, rpm * 0.00012F, rpm * 0.0012F);
			Vec3 motion = pEntity.getDeltaMovement();
			pEntity.setDeltaMovement(
					force.x == 0 ? motion.x : force.x, 
					force.y == 0 ? motion.y : force.y, 
					force.z == 0 ? motion.z : force.z
			);
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		
		if (pLevel.getBlockEntity(pPos) instanceof ConveyorBeltBlockEntity conveyor) {
			
			ItemStack itemInHand = pPlayer.getItemInHand(pHand);
			if (itemInHand.isEmpty()) {
				
				boolean tookItem = false;
				for (int slot = conveyor.getContainerSize(); slot >= 0; slot--) {
					ItemStack itemInSlot = conveyor.getItem(slot);
					if (itemInSlot.isEmpty()) continue;
					if (!conveyor.canTakeItem(pPlayer.getInventory(), slot, itemInSlot)) continue;
					if (!pPlayer.getInventory().add(itemInSlot)) continue;
					conveyor.removeItem(slot, itemInHand.getCount());
					tookItem = true;
				}
				return tookItem ? InteractionResult.SUCCESS : InteractionResult.PASS;
				
			} else {
				
				for (int slot = 0; slot < conveyor.getContainerSize(); slot++) {
					if (!conveyor.canPlaceItem(slot, itemInHand)) continue;
					ItemStack itemInSlot = conveyor.getItem(slot);
					if (itemInSlot.isEmpty()) {
						if (!pLevel.isClientSide)
							conveyor.setItem(slot, itemInHand);
						pPlayer.setItemInHand(pHand, ItemStack.EMPTY);
						return InteractionResult.CONSUME;
					}
				}
				
			}
			
		}
		
		return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
	}
	
}
