package de.m_marvin.industria.content.blocks.machines;

import java.util.function.Consumer;
import java.util.stream.IntStream;

import de.m_marvin.industria.content.blockentities.machines.PortableCoalGeneratorBlockEntity;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
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
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.industria.core.util.blocks.BaseEntityMultiBlock;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PortableCoalGeneratorBlock extends BaseEntityMultiBlock implements IElectricConnector, IElectricInfoProvider {

	public static final IntegerProperty[] MBPOS = createMultiBlockProperties(2, 1, 1);
	
	public static final NodePointSupplier NODES = NodePointSupplier.define()
			.addNode(NodeTypes.ALL, 4, new Vec3i(8, 8, 16))
			.addModifier(BlockStateProperties.HORIZONTAL_FACING, NodePointSupplier.FACING_HORIZONTAL_MODIFIER_DEFAULT_NORTH);
	public static final int NODE_COUNT = 1;
	
	public static final VoxelShape SHAPE = Shapes.or(VoxelShapeUtility.box(0, 4, 3, 14, 14, 13), VoxelShapeUtility.box(0, 3, 0, 11, 12, 3), VoxelShapeUtility.box(7, 14, 5, 13, 17, 11));
	
	public PortableCoalGeneratorBlock(Properties pProperties) {
		super(pProperties, 2, 1, 1);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		if (getMBPos(pState).equals(new Vec3i(0, 0, 0))) {
			return VoxelShapeUtility.transformation()
					.centered()
					.rotateFromNorth(pState.getValue(BlockStateProperties.HORIZONTAL_FACING))
					.uncentered()
					.transform(SHAPE);
		}
		return super.getShape(pState, pLevel, pPos, pContext);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.LIT);
		addMultiBlockProperties(pBuilder, MBPOS);
		super.createBlockStateDefinition(pBuilder);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = super.getStateForPlacement(pContext);
		if (state == null) return null;
		return state.setValue(BlockStateProperties.LIT, false);
	}
	
	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		if (pStack.hasTag() && pStack.getTag().contains("BlockEntityTag")) {
			CompoundTag tag = pStack.getTag().getCompound("BlockEntityTag");
			BlockPos center = getCenterBlock(pPos, pState);
			if (pLevel.getBlockEntity(center) instanceof PortableCoalGeneratorBlockEntity generator) {
				generator.load(tag);
			}
		}
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		return GameUtility.openElectricBlockEntityUI(pLevel, getMasterBlockEntityBlock(pState, pPos), pPlayer, pHand);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return getMBPos(pState).equals(new Vec3i(0, 0, 0)) ? new PortableCoalGeneratorBlockEntity(pPos, pState) : null;
	}
	
	@Override
	public BlockPos getMasterBlockEntityBlock(BlockState state, BlockPos pos) {
		return getCenterBlock(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return getMBPos(pState).equals(new Vec3i(0, 0, 0)) ? createTickerHelper(pBlockEntityType, ModBlockEntityTypes.PORTABLE_COAL_GENERATOR.get(), PortableCoalGeneratorBlockEntity::tick) : null;
	}
	
	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return getMBPos(state).equals(new Vec3i(1, 0, 0)) ? NODES.getNodes(state) : new ConduitNode[0];
	}

	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit, Consumer<ICircuitPlot> plotter) {
		
		if (!getMBPos(instance).equals(new Vec3i(1, 0, 0))) return;
		
		BlockPos bePosition = getMasterBlockEntityBlock(instance, position);
		if (level.getBlockEntity(bePosition) instanceof PortableCoalGeneratorBlockEntity generator) {
			
			String[] wireLanes = generator.getNodeLanes();
			ElectricUtility.plotJoinTogether(plotter, level, this, position, instance, 0, wireLanes[0], 1, wireLanes[1]);
			
			DeviceParametrics parametrics = DeviceParametricsManager.getInstance().getParametrics(this);
			int targetPower = generator.canRun() ? parametrics.getNominalPower() : 0;
			int targetVoltage = generator.canRun() ? parametrics.getNominalVoltage() : 0;
			
			CircuitTemplate templateSource = CircuitTemplateManager.getInstance().getTemplate(Circuits.CURRENT_LIMITED_VOLTAGE_SOURCE);
			templateSource.setProperty("nominal_current", targetPower / (double) parametrics.getNominalVoltage());
			templateSource.setProperty("nominal_voltage", targetVoltage);
			templateSource.setNetworkNode("SHUNT", new NodePos(position, 0), 2, "power_shunt");
			templateSource.setNetworkNode("VDC", new NodePos(position, 0), 0, wireLanes[0]);
			templateSource.setNetworkNode("GND", new NodePos(position, 0), 1, wireLanes[1]);
			plotter.accept(templateSource);
			
		}
		
	}
	
	@Override
	public void onNetworkNotify(Level level, BlockState instance, BlockPos position) {
		GameUtility.triggerUpdate(level, position);
	}
	
	@Override
	public double getVoltage(BlockState state, Level level, BlockPos pos) {
		BlockPos connectorBlock = getBlockAt(pos, state, new Vec3i(1, 0, 0));
		if (level.getBlockEntity(pos) instanceof PortableCoalGeneratorBlockEntity generator) {
			String[] wireLanes = generator.getNodeLanes();
			return ElectricUtility.getVoltageBetween(level, new NodePos(connectorBlock, 0), new NodePos(connectorBlock, 0), 0, 1, wireLanes[0], wireLanes[1]);
		}
		return 0.0;
	}
	
	@Override
	public double getPower(BlockState state, Level level, BlockPos pos) {
		BlockPos connectorBlock = getBlockAt(pos, state, new Vec3i(1, 0, 0));
		if (level.getBlockEntity(pos) instanceof PortableCoalGeneratorBlockEntity generator) {
			String[] wireLanes = generator.getNodeLanes();
			double shuntVoltage = ElectricUtility.getVoltageBetween(level, new NodePos(connectorBlock, 0), new NodePos(connectorBlock, 0), 2, 0, "power_shunt", wireLanes[0]);
			DeviceParametrics parametrics = DeviceParametricsManager.getInstance().getParametrics(this);
			double powerUsed = (shuntVoltage / Circuits.SHUNT_RESISTANCE) * parametrics.getNominalVoltage();
			return Math.max(powerUsed > 1.0 ? parametrics.getPowerMin() : 0, powerUsed);
		}
		return 0.0;
	}
	
	@Override
	public DeviceParametrics getParametrics(BlockState state, Level level, BlockPos pos) {
		return DeviceParametricsManager.getInstance().getParametrics(this);
	}
	
	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		BlockPos connector = getBlockAt(getCenterBlock(pos, instance), instance, new Vec3i(1, 0, 0));
		return IntStream.range(0, NODE_COUNT).mapToObj(i -> new NodePos(connector, i)).toArray(i -> new NodePos[i]);
	}

	@Override
	public String[] getWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node) {
		BlockPos blockEntityPos = getMasterBlockEntityBlock(instance, pos);
		if (level.getBlockEntity(blockEntityPos) instanceof PortableCoalGeneratorBlockEntity generator) {
			return generator.getNodeLanes();
		}
		return new String[0];
	}

	@Override
	public void setWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {
		BlockPos blockEntityPos = getMasterBlockEntityBlock(instance, pos);
		if (level.getBlockEntity(blockEntityPos) instanceof PortableCoalGeneratorBlockEntity generator) {
			generator.setNodeLanes(laneLabels);
		}
	}
	
}
