package de.m_marvin.industria.content.blocks.machines;

import java.util.function.Consumer;

import de.m_marvin.industria.content.blockentities.machines.FloodlightBlockEntity;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.circuits.Circuits;
import de.m_marvin.industria.core.electrics.parametrics.DeviceParametrics;
import de.m_marvin.industria.core.electrics.parametrics.DeviceParametricsManager;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricBlock;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricInfoProvider;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FloodlightBlock extends BaseEntityBlock implements IElectricBlock, IElectricInfoProvider {
	
	public static final NodePointSupplier NODES = NodePointSupplier.define()
			.addNode(NodeTypes.ELECTRIC, 2, new Vec3i(0, 8, 13))
			.addNode(NodeTypes.ELECTRIC, 2, new Vec3i(8, 8, 16))
			.addNode(NodeTypes.ELECTRIC, 2, new Vec3i(16, 8, 13))
			.addModifier(BlockStateProperties.ATTACH_FACE, NodePointSupplier.ATTACH_FACE_MODIFIER_DEFAULT_WALL)
			.addModifier(BlockStateProperties.HORIZONTAL_FACING, NodePointSupplier.FACING_MODIFIER_DEFAULT_NORTH);
	
	public static final VoxelShape SHAPE = Shapes.or(VoxelShapeUtility.box(0, 1, 0, 16, 15, 6), VoxelShapeUtility.box(0, 0, 6, 16, 16, 10));
	
	public FloodlightBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.HORIZONTAL_FACING);
		pBuilder.add(BlockStateProperties.ATTACH_FACE);
		pBuilder.add(BlockStateProperties.LIT);
		pBuilder.add(BlockStateProperties.WATERLOGGED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Direction direction = pContext.getNearestLookingDirection();
		BlockState blockstate;
		if (direction.getAxis() == Direction.Axis.Y) {
			blockstate = this.defaultBlockState().setValue(BlockStateProperties.ATTACH_FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.getHorizontalDirection());
		} else {
			blockstate = this.defaultBlockState().setValue(BlockStateProperties.ATTACH_FACE, AttachFace.WALL).setValue(BlockStateProperties.HORIZONTAL_FACING, direction.getOpposite());
		}
		return blockstate.setValue(BlockStateProperties.LIT, false);
	}
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		if (pState.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.WALL) {
			return VoxelShapeUtility.transformation()
					.centered()
					.rotateY(pState.getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue() * 90)
					.uncentered()
					.transform(SHAPE);
		} else {
			VoxelShape s = VoxelShapeUtility.transformation()
					.centered()
					.rotateX(pState.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING ? -90 : 90)
					.rotateY(pState.getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue() * 90)
					.uncentered()
					.transform(SHAPE);
			return s;
		}
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return NODES.getNodes(state);
	}

	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit, Consumer<ICircuitPlot> plotter) {
		if (level.getBlockEntity(position) instanceof FloodlightBlockEntity lamp) {
			
			String[] lampLanes = lamp.getNodeLanes();
			ElectricUtility.plotJoinTogether(plotter, level, this, position, instance, 0, lampLanes[0], 1, lampLanes[1]);
			
			DeviceParametrics parametrics = DeviceParametricsManager.getInstance().getParametrics(this);
			int targetVoltage = parametrics.getNominalVoltage();
			int targetPower = parametrics.getNominalPower();
			
			CircuitTemplate templateSource = CircuitTemplateManager.getInstance().getTemplate(Circuits.CONSTANT_CURRENT_LOAD);
			templateSource.setProperty("nominal_current", targetPower / (double) targetVoltage);
			templateSource.setNetworkNode("VDC", new NodePos(position, 0), 0, lampLanes[0]);
			templateSource.setNetworkNode("GND", new NodePos(position, 0), 1, lampLanes[1]);
			plotter.accept(templateSource);
			
		}
	}
	
	@Override
	public DeviceParametrics getParametrics(BlockState state, Level level, BlockPos pos) {
		return DeviceParametricsManager.getInstance().getParametrics(this);
	}
	
	@Override
	public double getVoltage(BlockState state, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof FloodlightBlockEntity floodlight) {
			String[] wireLanes = floodlight.getNodeLanes();
			return ElectricUtility.getVoltageBetween(level, new NodePos(pos, 0), new NodePos(pos, 0), 0, 1, wireLanes[0], wireLanes[1]);
		}
		return 0.0;
	}
	
	@Override
	public double getPower(BlockState state, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof FloodlightBlockEntity floodlight) {
			String[] wireLanes = floodlight.getNodeLanes();
			DeviceParametrics parametrics = DeviceParametricsManager.getInstance().getParametrics(this);
			double voltage = ElectricUtility.getVoltageBetween(level, new NodePos(pos, 0), new NodePos(pos, 0), 0, 1, wireLanes[0], wireLanes[1]);
			return voltage * (parametrics.getNominalPower() / parametrics.getNominalVoltage());
		}
		return 0.0;
	}
	
	@Override
	public void onNetworkNotify(Level level, BlockState instance, BlockPos position) {
		GameUtility.triggerClientSync(level, position);
		level.scheduleTick(position, this, 1);
	}
	
	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (pLevel.getBlockEntity(pPos) instanceof FloodlightBlockEntity lamp) {
			lamp.updateLight();
		}
	}
	
	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return NODES.getNodePositions(pos);
	}
	
	@Override
	public String[] getWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node) {
		if (level.getBlockEntity(pos) instanceof FloodlightBlockEntity lamp) {
			return lamp.getNodeLanes();
		}
		return new String[0];
	}

	@Override
	public void setWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {
		if (level.getBlockEntity(pos) instanceof FloodlightBlockEntity lamp) {
			lamp.setNodeLanes(laneLabels);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new FloodlightBlockEntity(pPos, pState);
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		return GameUtility.openJunctionBlockEntityUI(pLevel, pPos, pPlayer, pHand);
	}
	
	public Direction getLightDirection(BlockState pState) {
		switch (pState.getValue(BlockStateProperties.ATTACH_FACE)) {
		case CEILING: return Direction.DOWN;
		case FLOOR: return Direction.UP;
		default: return pState.getValue(BlockStateProperties.HORIZONTAL_FACING);
		}
	}
	
	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
		if (!(pNewState.getBlock() instanceof FloodlightBlock)) {
			BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
			if (blockEntity instanceof FloodlightBlockEntity lamp) {
				lamp.clearLightBlocks();
			}
		}
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(BlockStateProperties.HORIZONTAL_FACING, pRotation.rotate(pState.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.setValue(BlockStateProperties.HORIZONTAL_FACING, pMirror.mirror(pState.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}
	
}
