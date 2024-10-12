package de.m_marvin.industria.content.blocks.machines;

import java.util.function.Consumer;

import de.m_marvin.industria.content.blockentities.machines.PortableCoalGeneratorBlockEntity;
import de.m_marvin.industria.content.blockentities.machines.PortableFuelGeneratorBlockEntity;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.CircuitTemplate.Plotter;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricBlock;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricInfoProvider;
import de.m_marvin.industria.core.parametrics.BlockParametrics;
import de.m_marvin.industria.core.parametrics.engine.BlockParametricsManager;
import de.m_marvin.industria.core.parametrics.properties.FloatParameter;
import de.m_marvin.industria.core.registries.Circuits;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.industria.core.util.blocks.BaseEntityFixedMultiBlock;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
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

public class PortableCoalGeneratorBlock extends BaseEntityFixedMultiBlock implements IElectricBlock, IElectricInfoProvider {

	public static final IntegerProperty[] MBPOS = createMultiBlockProperties(2, 1, 1);
	
	public static final NodePointSupplier NODES = NodePointSupplier.define()
			.addNode(NodeTypes.ELECTRIC, 4, new Vec3i(8, 8, 16))
			.addModifier(BlockStateProperties.HORIZONTAL_FACING, NodePointSupplier.FACING_HORIZONTAL_MODIFIER_DEFAULT_NORTH);
	
	public static final VoxelShape SHAPE = Shapes.or(VoxelShapeUtility.box(0, 4, 3, 14, 14, 13), VoxelShapeUtility.box(0, 3, 0, 11, 12, 3), VoxelShapeUtility.box(7, 14, 5, 13, 17, 11));
	
	public static final FloatParameter PARAMETER_WATTS_PER_WATER_MB = new FloatParameter("wattsPerWaterMB", 100000);
	public static final FloatParameter PARAMETER_WATTS_PER_FUEL_TICK = new FloatParameter("wattsPerFuelTick", 1000);
	
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
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		return GameUtility.openElectricBlockEntityUI(pLevel, pPos, pPlayer, pHand);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new PortableCoalGeneratorBlockEntity(pPos, pState);
	}
	
	@Override
	public BlockPos getMasterBlockEntityBlock(BlockState state, BlockPos pos) {
		return getOriginBlock(pos, state);
	}
	
	@Override
	public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
		
		if (pState.getValue(BlockStateProperties.LIT)) {
			
			double d0 = (double)pPos.getX() + 0.5D;
			double d1 = (double)pPos.getY();
			double d2 = (double)pPos.getZ() + 0.5D;
			
			BlockPos connectorBlock = getBlockAtMBPos(getOriginBlock(pPos, pState), pState, new Vec3i(1, 0, 0));
			double power = getPower(pState, pLevel, connectorBlock);
			BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(this);
			double loadP = Math.max(0, parametrics.getPowerPercentageP(power) - 1);
			
			if (loadP < pRandom.nextFloat()) return;
			
			if (getMBPos(pState).equals(new Vec3i(1, 0, 0))) {
				
				pLevel.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
				
				Direction direction = pState.getValue(BlockStateProperties.HORIZONTAL_FACING);
				Direction.Axis direction$axis = direction.getAxis();
				double d4 = pRandom.nextDouble() * 0.3D - 0.04D;
				double d5 = direction$axis == Direction.Axis.X ? (double)direction.getStepX() * 0.52D : d4;
				double d6 = pRandom.nextDouble() * 0.2D  + 0.2;
				double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.52D : d4;
				pLevel.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
				pLevel.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
				
			} else {
				
				// TODO coal generator sound
				
				for (int i = 0; i < 10; i++) {
					Direction direction = pState.getValue(BlockStateProperties.HORIZONTAL_FACING);
					Direction.Axis direction$axis = direction.getAxis();
					double d4 = pRandom.nextDouble() * 0.3D - 0.04D;
					double d5 = direction$axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.2D : d4;
					double d6 = pRandom.nextDouble() * 0.2D  + 1.2;
					double d7 = direction$axis == Direction.Axis.X ? (double)direction.getStepX() * 0.2D : d4;
					pLevel.addParticle(ParticleTypes.POOF, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.2D, 0.0D);
				}
				
			}
		}
		
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
		
		if (level.getBlockEntity(position) instanceof PortableCoalGeneratorBlockEntity generator && generator.getMaster() != null) {
			
			String[] wireLanes = generator.getNodeLanes();
			ElectricUtility.plotJoinTogether(plotter, level, this, position, instance, 0, wireLanes[0], 1, wireLanes[1]);
			
			BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(this);
			int targetPower = generator.canRun() ? parametrics.getNominalPower() : 0;
			int targetVoltage = generator.canRun() ? parametrics.getNominalVoltage() : 0;
			
			if (targetPower > 0) {
				Plotter templateSource = CircuitTemplateManager.getInstance().getTemplate(Circuits.VOLTAGE_SOURCE).plotter();
				templateSource.setProperty("nominal_voltage", targetVoltage);
				templateSource.setProperty("power_limit", targetVoltage > 0 ? targetPower : 0);
				templateSource.setNetworkNode("VDC", new NodePos(position, 0), 0, wireLanes[0]);
				templateSource.setNetworkNode("GND", new NodePos(position, 0), 1, wireLanes[1]);
				templateSource.setNetworkNode("SHUNT", new NodePos(position, 0), 2, "SHUNT");
				plotter.accept(templateSource);
			}
			
		}
		
	}
	
	@Override
	public void onNetworkNotify(Level level, BlockState instance, BlockPos position) {
		GameUtility.triggerClientSync(level, position);
	}
	
	@Override
	public BlockPos getConnectorMasterPos(Level level, BlockPos pos, BlockState state) {
		return getBlockAtMBPos(getOriginBlock(pos, state), state, new Vec3i(1, 0, 0));
	}
	
	@Override
	public double getVoltage(BlockState state, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof PortableCoalGeneratorBlockEntity generator && generator.getMaster() != null) {
			String[] wireLanes = generator.getNodeLanes();
			return ElectricUtility.getVoltageBetween(level, new NodePos(pos, 0), new NodePos(pos, 0), 0, 1, wireLanes[0], wireLanes[1]);
		}
		return 0.0;
	}
	
	@Override
	public double getPower(BlockState state, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof PortableCoalGeneratorBlockEntity generator && generator.getMaster() != null) {
			String[] wireLanes = generator.getNodeLanes();
			double shuntVoltage = ElectricUtility.getVoltageBetween(level, new NodePos(pos, 0), new NodePos(pos, 0), 2, 0, "SHUNT", wireLanes[0]); // TODO may be negative ?
			double sourceVoltage = ElectricUtility.getVoltageBetween(level, new NodePos(pos, 0), new NodePos(pos, 0), 0, 1, wireLanes[0], wireLanes[1]);
			double sourceCurrent = shuntVoltage * Circuits.SHUNT_RESISTANCE;
			double powerUsed = sourceVoltage * sourceCurrent;
			BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(this);
			return Math.max(powerUsed > 1.0 ? parametrics.getPowerMin() : 0, powerUsed);
			// TODO fix generator
		}
		return 0.0;
	}
	
	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return getMBPos(instance).equals(new Vec3i(1, 0, 0)) ? NODES.getNodePositions(pos) : new NodePos[0];
	}

	@Override
	public String[] getWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node) {
		if (level.getBlockEntity(pos) instanceof PortableCoalGeneratorBlockEntity generator) {
			return generator.getNodeLanes();
		}
		return new String[0];
	}

	@Override
	public void setWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {
		if (level.getBlockEntity(pos) instanceof PortableCoalGeneratorBlockEntity generator) {
			generator.setNodeLanes(laneLabels);
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
