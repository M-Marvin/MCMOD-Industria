package de.m_marvin.industria.content.blocks;

import java.util.function.Consumer;

import de.m_marvin.industria.content.blockentities.MotorBlockEntity;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.content.registries.ModBlockStateProperties;
import de.m_marvin.industria.content.types.MotorMode;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricConnector;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ThrusterBlock extends Block implements IElectricConnector {
	
	public static final VoxelShape BLOCK_SHAPE = Block.box(3, 3, 1, 13, 13, 16);
	public static final VoxelShape BLOCK_SHAPE_VERTICAL = Block.box(3, 0, 3, 13, 15, 13);
	
//	public static final MutableConnectionPointSupplier CONDUIT_NODES = MutableConnectionPointSupplier.basedOnOrientation(BlockStateProperties.FACING)
//			.addOnFace(new Vec3i(8, 9, 4), ConduitConnectionTypes.ELECTRIC, 1, Direction.SOUTH)
//			.addOnFace(new Vec3i(8, 9, 4), ConduitConnectionTypes.ELECTRIC, 1, Direction.EAST)
//			.addOnFace(new Vec3i(8, 9, 4), ConduitConnectionTypes.ELECTRIC, 1, Direction.WEST)
//			.rotateBase(Direction.DOWN);
//	public static final MutableConnectionPointSupplier CONDUIT_NODES_VERTICAL = MutableConnectionPointSupplier.basedOnOrientation(BlockStateProperties.FACING)
//			.addOnSidesOfAxis(new Vec3i(8, 10, 4), ConduitConnectionTypes.ELECTRIC, 1, Axis.Y)
//			.rotateBase(Direction.DOWN);
	
	public ThrusterBlock(Properties properties) {
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
		final Direction preferred = null;
		if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
			final Direction nearestLookingDirection = context.getNearestLookingDirection();
			return (BlockState) this.defaultBlockState().setValue(BlockStateProperties.FACING,
					((context.getPlayer() != null && context.getPlayer().isShiftKeyDown())
							? nearestLookingDirection
							: nearestLookingDirection.getOpposite()));
		}
		return (BlockState) this.defaultBlockState().setValue(BlockStateProperties.FACING,
				preferred);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ModBlockStateProperties.MOTOR_MODE, BlockStateProperties.FACING);
	}
	
//	@Override
//	public ConnectionPoint[] getConnections(Level level, BlockPos pos, BlockState instance) {
//		return getConnectionPoints(pos, instance);
//	}

	public BlockEntityType<?> getTileEntityType(BlockState state) {
		return state.getValue(ModBlockStateProperties.MOTOR_MODE) == MotorMode.MOTOR ? ModBlockEntityTypes.MOTOR.get() : ModBlockEntityTypes.GENERATOR.get();
	}
	
	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		if (pNewState.getBlock() == this && pState.getValue(BlockStateProperties.FACING) != pNewState.getValue(BlockStateProperties.FACING)) {
			ConduitUtility.getConduitsAtBlock(pLevel, pPos).forEach(conduit -> ConduitUtility.removeConduit(pLevel, conduit.getPosition(), true));
		}
//		if (	pState.getBlock() == pNewState.getBlock() && pNewState.getBlock() == this && 
//				pState.getValue(ModBlockStateProperties.MOTOR_MODE) != pNewState.getValue(ModBlockStateProperties.MOTOR_MODE)) {
//			pLevel.removeBlockEntity(pPos);
//			pLevel.setBlockEntity(newBlockEntity(pPos, pNewState));
//		} else {
//			super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
//		}
	}
		
//	@Override
//	public ConnectionPoint[] getConnectionPoints(BlockPos pos, BlockState state) {
//		Direction facing = state.getValue(BlockStateProperties.FACING);
//		if (facing.getAxis() == Axis.Y) {
//			return CONDUIT_NODES_VERTICAL.getNodes(pos, state);
//		} else {
//			return CONDUIT_NODES.getNodes(pos, state);
//		}
//	}
		
	@Override
	public void onNetworkNotify(Level level, BlockState instance, BlockPos position) {
		if (instance.getValue(ModBlockStateProperties.MOTOR_MODE) == MotorMode.MOTOR) level.scheduleTick(position, instance.getBlock(), 1);
	}
	
	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (pState.getValue(ModBlockStateProperties.MOTOR_MODE) == MotorMode.GENERATOR) {
			ElectricUtility.updateNetwork(pLevel, pPos);
		} else {
			ElectricNetworkHandlerCapability networkHandler = GameUtility.getCapability(pLevel, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
			BlockEntity entity = pLevel.getBlockEntity(pPos);
			if (entity instanceof MotorBlockEntity motor) {
//				double voltage = networkHandler.getVoltageAt(getConnectionPoints(pPos, pState)[0]);
//				motor.setVoltage(voltage);
			}
		}
	}

	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		// TODO Auto-generated method stub
		return new ConduitNode[] {};
	}

	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		// TODO Auto-generated method stub
		return new NodePos[] {};
	}

	@Override
	public String[] getWireLanes(BlockPos pos, BlockState instance, NodePos node) {
		// TODO Auto-generated method stub
		return new String[] {};
	}

	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit,
			Consumer<CircuitTemplate> plotter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWireLanes(BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {
		// TODO Auto-generated method stub
		
	}
	
}
