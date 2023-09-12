package de.m_marvin.industria.content.blocks.machines;

import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.valkyrienskies.core.api.ships.ServerShip;

import de.m_marvin.industria.content.blockentities.machines.IonicThrusterBlockEntity;
import de.m_marvin.industria.content.blocks.AbstractThrusterBlock;
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
import de.m_marvin.industria.core.electrics.types.blocks.IElectricConnector;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricInfoProvider;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IonicThrusterBlock extends AbstractThrusterBlock implements IElectricConnector, IElectricInfoProvider {

	public static final NodePointSupplier NODES = NodePointSupplier.define()
			.addNode(NodeTypes.ALL, 2, new Vec3i(8, 16, 5))
			.addModifier(BlockStateProperties.ATTACH_FACE, NodePointSupplier.ATTACH_FACE_MODIFIER_DEFAULT_WALL)
			.addModifier(BlockStateProperties.HORIZONTAL_FACING, NodePointSupplier.FACING_MODIFIER_DEFAULT_NORTH);
	public static final int NODE_COUNT = 1;
	
	public static final VoxelShape SHAPE = VoxelShapeUtility.box(1, 1, 0, 15, 15, 15);
	
	public IonicThrusterBlock(Properties pProperties) {
		super(pProperties);
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
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new IonicThrusterBlockEntity(pPos, pState);
	}
	
	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return NODES.getNodes(state);
	}

	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return IntStream.range(0, NODE_COUNT).mapToObj(i -> new NodePos(pos, i)).toArray(i -> new NodePos[i]);
	}
	
	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit, Consumer<ICircuitPlot> plotter) {
		if (level.getBlockEntity(position) instanceof IonicThrusterBlockEntity thruster) { // TODO
			
			String[] thrusterLanes = thruster.getNodeLanes();
			ElectricUtility.plotJoinTogether(plotter, level, this, position, instance, 0, thrusterLanes[0], 1, thrusterLanes[1]);
			
			DeviceParametrics parametrics = DeviceParametricsManager.getInstance().getParametrics(this);
			int targetVoltage = parametrics.getNominalVoltage();
			int targetPower = parametrics.getNominalPower();
			
			CircuitTemplate templateSource = CircuitTemplateManager.getInstance().getTemplate(Circuits.CONSTANT_CURRENT_LOAD);
			templateSource.setProperty("nominal_current", targetPower / (double) targetVoltage);
			templateSource.setNetworkNode("VDC", new NodePos(position, 0), 0, thrusterLanes[0]);
			templateSource.setNetworkNode("GND", new NodePos(position, 0), 1, thrusterLanes[1]);
			plotter.accept(templateSource);
			
		}
	}

	@Override
	public DeviceParametrics getParametrics(BlockState state, Level level, BlockPos pos) {
		return DeviceParametricsManager.getInstance().getParametrics(this);
	}
	
	@Override
	public double getVoltage(BlockState state, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof IonicThrusterBlockEntity thruster) {
			String[] wireLanes = thruster.getNodeLanes();
			return ElectricUtility.getVoltageBetween(level, new NodePos(pos, 0), new NodePos(pos, 0), 0, 1, wireLanes[0], wireLanes[1]);
		}
		return 0.0;
	}
	
	@Override
	public double getPower(BlockState state, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof IonicThrusterBlockEntity thruster) {
			String[] wireLanes = thruster.getNodeLanes();
			DeviceParametrics parametrics = DeviceParametricsManager.getInstance().getParametrics(this);
			double voltage = ElectricUtility.getVoltageBetween(level, new NodePos(pos, 0), new NodePos(pos, 0), 0, 1, wireLanes[0], wireLanes[1]);
			return voltage * (parametrics.getNominalPower() / parametrics.getNominalVoltage());
		}
		return 0.0;
	}
	
	@Override
	public void onNetworkNotify(Level level, BlockState instance, BlockPos position) {
		if (level.getBlockEntity(position) instanceof IonicThrusterBlockEntity thruster) {
			
			if (!level.isClientSide()) {

				// TODO triggers force inducer setup
				ServerShip contraption = (ServerShip) PhysicUtility.getContraptionOfBlock(level, position);
				ThrusterInducer inducer = PhysicUtility.getOrCreateForceInducer((ServerLevel) level, contraption, ThrusterInducer.class);
				
				// TODO Force inducer gets not saved
				inducer.addThruster(position);
				
			}
			
		}
	}
	
	@Override
	public String[] getWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node) {
		if (level.getBlockEntity(pos) instanceof IonicThrusterBlockEntity thruster) {
			return thruster.getNodeLanes();
		}
		return new String[0];
	}

	@Override
	public void setWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {
		if (level.getBlockEntity(pos) instanceof IonicThrusterBlockEntity thruster) {
			thruster.setNodeLanes(laneLabels);
		}
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		return GameUtility.openJunctionBlockEntityUI(pLevel, pPos, pPlayer, pHand);
	}
	
	@Override
	public int getThrust(Level level, BlockPos pos, BlockState state) {
		double power = getPower(state, level, pos); // TODO cant get power on physics thread, move to block entity
		System.out.println(power);
		return 0;
	}
	
}
