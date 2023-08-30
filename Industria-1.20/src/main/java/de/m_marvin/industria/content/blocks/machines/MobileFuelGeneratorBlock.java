package de.m_marvin.industria.content.blocks.machines;

import java.util.function.Consumer;
import java.util.stream.IntStream;

import de.m_marvin.industria.content.blockentities.machines.MobileFuelGeneratorBlockEntity;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.circuits.Circuits;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricConnector;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricInfoProvider;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public class MobileFuelGeneratorBlock extends BaseEntityBlock implements IElectricConnector, IElectricInfoProvider {
	
	public static final NodePointSupplier NODES = NodePointSupplier.define()
			.addNode(NodeTypes.ALL, 4, new Vec3i(8, 8, 16))
			.addModifier(BlockStateProperties.HORIZONTAL_FACING, NodePointSupplier.FACING_HORIZONTAL_MODIFIER_DEFAULT_NORTH);
	public static final int NODE_COUNT = 1;
	
	public MobileFuelGeneratorBlock(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.HORIZONTAL_FACING);
		pBuilder.add(BlockStateProperties.LIT);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.getHorizontalDirection().getOpposite()).setValue(BlockStateProperties.LIT, false);
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		return GameUtility.openElectricBlockEntityUI(pLevel, pPos, pPlayer, pHand);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new MobileFuelGeneratorBlockEntity(pPos, pState);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return createTickerHelper(pBlockEntityType, ModBlockEntityTypes.MOBILE_FUEL_GENERATOR.get(), MobileFuelGeneratorBlockEntity::tick);
	}
	
	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return NODES.getNodes(state);
	}

	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit, Consumer<ICircuitPlot> plotter) {
		
		if (level.getBlockEntity(position) instanceof MobileFuelGeneratorBlockEntity generator) {
			
			String[] wireLanes = generator.getNodeLanes();
			ElectricUtility.plotJoinTogether(plotter, level, this, position, instance, 0, wireLanes[0], 1, wireLanes[1]);
			
			int targetPower = generator.canRun() ? getNominalPower(instance, level, position) : 0;
			int targetVoltage = generator.canRun() ? getNominalVoltage(instance, level, position) : 0;
			int nominalVoltage = getNominalVoltage(instance, level, position);
			
			CircuitTemplate templateSource = CircuitTemplateManager.getInstance().getTemplate(Circuits.CURRENT_LIMITED_VOLTAGE_SOURCE);
			templateSource.setProperty("nominal_current", targetPower / (double) nominalVoltage);
			templateSource.setProperty("nominal_voltage", targetVoltage);
			templateSource.setNetworkNode("SHUNT", new NodePos(position, 0), 2, "power_shunt");
			templateSource.setNetworkNode("VDC", new NodePos(position, 0), 0, wireLanes[0]);
			templateSource.setNetworkNode("GND", new NodePos(position, 0), 1, wireLanes[1]);
			plotter.accept(templateSource);
			
		}
		
	}
	
	@Override
	public double getVoltage(BlockState state, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof MobileFuelGeneratorBlockEntity generator) {
			String[] wireLanes = generator.getNodeLanes();
			return ElectricUtility.getVoltageBetween(level, new NodePos(pos, 0), new NodePos(pos, 0), 0, 1, wireLanes[0], wireLanes[1]);
		}
		return 0.0;
	}
	
	@Override
	public double getPower(BlockState state, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof MobileFuelGeneratorBlockEntity generator) {
			String[] wireLanes = generator.getNodeLanes();
			double shuntVoltage = ElectricUtility.getVoltageBetween(level, new NodePos(pos, 0), new NodePos(pos, 0), 0, 2, wireLanes[0], "power_shunt");
			double outletVoltage = ElectricUtility.getVoltageBetween(level, new NodePos(pos, 0), new NodePos(pos, 0), 1, 0, wireLanes[1], wireLanes[0]);
			return shuntVoltage / Circuits.SHUNT_RESISTANCE * outletVoltage;
		}
		return 0.0;
	}
	
	@Override
	public int getNominalPower(BlockState state, Level level, BlockPos pos) {
		// TODO Auto-generated method stub
		return 1000;
	}
	
	@Override
	public int getNominalVoltage(BlockState state, Level level, BlockPos pos) {
		// TODO Auto-generated method stub
		return 230;
	}
	
	@Override
	public float getPowerTolerance(BlockState state, Level level, BlockPos pos) {
		// TODO Auto-generated method stub
		return 0.1F;
	}
	
	@Override
	public float getVoltageTolerance(BlockState state, Level level, BlockPos pos) {
		// TODO Auto-generated method stub
		return 0.2F;
	}
	
	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return IntStream.range(0, NODE_COUNT).mapToObj(i -> new NodePos(pos, i)).toArray(i -> new NodePos[i]);
	}

	@Override
	public String[] getWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node) {
		if (level.getBlockEntity(pos) instanceof MobileFuelGeneratorBlockEntity generator) {
			return generator.getNodeLanes();
		}
		return new String[0];
	}

	@Override
	public void setWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {
		if (level.getBlockEntity(pos) instanceof MobileFuelGeneratorBlockEntity generator) {
			generator.setNodeLanes(laneLabels);
		}
	}
	
}
