package de.m_marvin.industria.content.blocks;

import java.util.Random;

import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.foundation.block.ITE;

import de.m_marvin.industria.content.blockentities.GeneratorBlockEntity;
import de.m_marvin.industria.content.blockentities.MotorBlockEntity;
import de.m_marvin.industria.content.registries.ModBlockEntities;
import de.m_marvin.industria.content.registries.ModBlockStateProperties;
import de.m_marvin.industria.content.registries.ModCapabilities;
import de.m_marvin.industria.content.types.MotorMode;
import de.m_marvin.industria.core.GameUtility;
import de.m_marvin.industria.core.VoxelShapeUtility;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.engine.MutableConnectionPointSupplier;
import de.m_marvin.industria.core.conduits.engine.MutableConnectionPointSupplier.ConnectionPoint;
import de.m_marvin.industria.core.conduits.registy.ConduitConnectionTypes;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricConnector;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MotorBlock extends BaseDirectionalKineticBlock implements EntityBlock, IElectricConnector, ITE<KineticTileEntity> {
	
	public static final VoxelShape BLOCK_SHAPE = Block.box(3, 3, 1, 13, 13, 16);
	public static final VoxelShape BLOCK_SHAPE_VERTICAL = Block.box(3, 0, 3, 13, 15, 13);
	
	public static final MutableConnectionPointSupplier CONDUIT_NODES = MutableConnectionPointSupplier.basedOnOrientation(FACING)
			.addOnFace(new Vec3i(8, 9, 4), ConduitConnectionTypes.ELECTRIC, 1, Direction.SOUTH)
			.addOnFace(new Vec3i(8, 9, 4), ConduitConnectionTypes.ELECTRIC, 1, Direction.EAST)
			.addOnFace(new Vec3i(8, 9, 4), ConduitConnectionTypes.ELECTRIC, 1, Direction.WEST)
			.rotateBase(Direction.DOWN);
	public static final MutableConnectionPointSupplier CONDUIT_NODES_VERTICAL = MutableConnectionPointSupplier.basedOnOrientation(FACING)
			.addOnSidesOfAxis(new Vec3i(8, 10, 4), ConduitConnectionTypes.ELECTRIC, 1, Axis.Y)
			.rotateBase(Direction.DOWN);
	
	public MotorBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction facing = state.getValue(BlockStateProperties.FACING);
		if (facing.getAxis() == Axis.Y) {
			return facing.getAxisDirection() == AxisDirection.POSITIVE ? BLOCK_SHAPE_VERTICAL : VoxelShapeUtility.rotateShape(BLOCK_SHAPE_VERTICAL, new Vec3f(8, 8, 8), Math.PI, false, Axis.X);
		} else {
			return VoxelShapeUtility.rotateShape(BLOCK_SHAPE, new Vec3f(8, 8, 8), facing, Axis.Y);
		}
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		final Direction preferred = this.getPreferredFacing(context);
		if (preferred == null || (context.getPlayer() != null && context.getPlayer().isShiftKeyDown())) {
			final Direction nearestLookingDirection = context.getNearestLookingDirection();
			return (BlockState) this.defaultBlockState().setValue(DirectionalKineticBlock.FACING,
					((context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
							? nearestLookingDirection
							: nearestLookingDirection.getOpposite()));
		}
		return (BlockState) this.defaultBlockState().setValue(DirectionalKineticBlock.FACING,
				preferred);
	}
	
//	@Override
//	public boolean isAdjustable(BlockState targetBlock, Direction face, Vec3 position) {
//		return true;
//	}
//	
//	@Override
//	public InteractionResult onScrewDriveAdjusting(BlockState targetBlock, UseOnContext context, double delta) {
//		
//		Level level = context.getLevel();
//		
//		if (!level.isClientSide()) {
//			BlockState otherModeState = targetBlock.setValue(ModBlockStateProperties.MOTOR_MODE, targetBlock.getValue(ModBlockStateProperties.MOTOR_MODE) == MotorMode.MOTOR ? MotorMode.GENERATOR : MotorMode.MOTOR);
//			level.setBlockAndUpdate(context.getClickedPos(), otherModeState);
//		}
//		
//		return InteractionResult.SUCCESS;
//	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ModBlockStateProperties.MOTOR_MODE);
	}
	
	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(FACING).getAxis();
	}
	
	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(BlockStateProperties.FACING);
	}
	
	@Override
	public ConnectionPoint[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return getConnectionPoints(pos, instance);
	}

	public BlockEntityType<?> getTileEntityType(BlockState state) {
		return state.getValue(ModBlockStateProperties.MOTOR_MODE) == MotorMode.MOTOR ? ModBlockEntities.MOTOR.get() : ModBlockEntities.GENERATOR.get();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		if (pNewState.getBlock() == this && pState.getValue(BlockStateProperties.FACING) != pNewState.getValue(BlockStateProperties.FACING)) {
			ConduitUtility.getConduitsAtBlock(pLevel, pPos).forEach(conduit -> ConduitUtility.removeConduit(pLevel, conduit.getConduitPosition(), true));
		}
		if (	pState.getBlock() == pNewState.getBlock() && pNewState.getBlock() == this && 
				pState.getValue(ModBlockStateProperties.MOTOR_MODE) != pNewState.getValue(ModBlockStateProperties.MOTOR_MODE)) {
			pLevel.removeBlockEntity(pPos);
			pLevel.setBlockEntity(newBlockEntity(pPos, pNewState));
		} else {
			super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
		}
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return getTileEntityType(state).create(pos, state);
	}
	
	@Override
	public ConnectionPoint[] getConnectionPoints(BlockPos pos, BlockState state) {
		Direction facing = state.getValue(BlockStateProperties.FACING);
		if (facing.getAxis() == Axis.Y) {
			return CONDUIT_NODES_VERTICAL.getNodes(pos, state);
		} else {
			return CONDUIT_NODES.getNodes(pos, state);
		}
	}
	
	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit) {
		ConnectionPoint[] points = CONDUIT_NODES.getNodes(position, instance);
		BlockEntity blockEntity = level.getBlockEntity(position);
		if (instance.getValue(ModBlockStateProperties.MOTOR_MODE) == MotorMode.GENERATOR && blockEntity instanceof GeneratorBlockEntity) {
			circuit.addSource(points[0], ((GeneratorBlockEntity) blockEntity).getVoltage(), ((GeneratorBlockEntity) blockEntity).getCompensatedCurrent());
		} else if (instance.getValue(ModBlockStateProperties.MOTOR_MODE) == MotorMode.MOTOR && blockEntity instanceof MotorBlockEntity) {
			circuit.addLoad(points[0], ((MotorBlockEntity) blockEntity).getCurrent());
		}
		circuit.addSerialResistance(points[0], points[1], 0);
		circuit.addSerialResistance(points[1], points[2], 0);
	}
	
	@Override
	public void onNetworkNotify(Level level, BlockState instance, BlockPos position) {
		if (instance.getValue(ModBlockStateProperties.MOTOR_MODE) == MotorMode.MOTOR) level.scheduleTick(position, instance.getBlock(), 1);
	}
	
	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
		if (pState.getValue(ModBlockStateProperties.MOTOR_MODE) == MotorMode.GENERATOR) {
			ElectricUtility.updateElectricNetwork(pLevel, pPos);
		} else {
			ElectricNetworkHandlerCapability networkHandler = GameUtility.getCapability(pLevel, ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
			BlockEntity entity = pLevel.getBlockEntity(pPos);
			if (entity instanceof MotorBlockEntity motor) {
				double voltage = networkHandler.getVoltageAt(getConnectionPoints(pPos, pState)[0]);
				motor.setVoltage(voltage);
			}
		}
		super.tick(pState, pLevel, pPos, pRandom);
	}

	@Override
	public Class<KineticTileEntity> getTileEntityClass() {
		return KineticTileEntity.class;
	}

	@Override
	public BlockEntityType<? extends MotorBlockEntity> getTileEntityType() {
		return null;
	}
	
}
