package de.m_marvin.industria.core.electrics.types.blocks;

import de.m_marvin.industria.core.client.util.TooltipAdditions;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.CircuitElement;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.CircuitNode;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork.ComponentCircuitContext;
import de.m_marvin.industria.core.electrics.types.blockentities.VoltageSourceBlockEntity;
import de.m_marvin.industria.core.registries.ElectricElements;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.items.ITooltipAdditionsModifier;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import tvnlnna.nodal.NodalElementState;

public class VoltageSourceBlock extends BaseEntityBlock implements IElectricBlock, ITooltipAdditionsModifier {
	
	public static final NodePointSupplier NODES = NodePointSupplier.define()
			.addNode(NodeTypes.ELECTRIC, 8, new Vec3i(8, 8, 0))
			.addModifier(BlockStateProperties.FACING, NodePointSupplier.FACING_MODIFIER_DEFAULT_NORTH);
	
	public VoltageSourceBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public boolean showTooltipType(String tooltipTypeName) {
		return tooltipTypeName != TooltipAdditions.TOOLTIP_ELECTRICS;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return defaultBlockState().setValue(BlockStateProperties.FACING, MathUtility.getFacingDirection(pContext.getPlayer()).getOpposite());
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.FACING);
	}
	
	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return NODES.getNodes(state);
	}
	
	@Override
	public void installCircuitElements(Level level, ElectricReference reference, BlockState instance, ComponentCircuitContext context) {
		
		if (level.getBlockEntity(reference.block()) instanceof VoltageSourceBlockEntity source) {
			
			String[] sourceLanes = source.getNodeLanes();
			NodePos node = new NodePos(reference.block(), 0);
			CircuitNode cnodeGround = CircuitNode.node(node, sourceLanes[1]);
//			CircuitNode cnodeIntern = CircuitNode.internal(reference, "internal");
			CircuitNode cnodeLine = CircuitNode.node(node, sourceLanes[0]);
			NodalElementState sourceState = context.install(
					ElectricElements.VOLTAGE_FIXED.get(), CircuitElement.element(reference, "source"), 
					cnodeLine, cnodeGround);
			sourceState.setParameter("U", source.getVoltage());
			sourceState.setParameter("P", source.getPower());
			
//			String[] sourceLanes = source.getNodeLanes();
//			NodePos node = new NodePos(reference.block(), 0);
//			CircuitNode cnodeGround = CircuitNode.node(node, sourceLanes[0]);
//			CircuitNode cnodeLine = CircuitNode.node(node, sourceLanes[1]);
//			CircuitNode cnodeInternal = CircuitNode.internal(reference, "internal");
//			context.installVoltage(CircuitElement.element(reference, "Ugen"), cnodeGround, cnodeInternal, source.getVoltage());
//			context.installResistor(CircuitElement.element(reference, "Rinternal"), cnodeInternal, cnodeLine, 1);
//			
//			String[] sourceLanes = source.getNodeLanes();
//			NodePos node = new NodePos(reference.block(), 0);
//			CircuitNode cnodeGround = CircuitNode.node(node, sourceLanes[1]);
////			CircuitNode cnodeIntern = CircuitNode.internal(reference, "internal");
//			CircuitNode cnodeLine = CircuitNode.node(node, sourceLanes[0]);
//			context.installVoltage(CircuitElement.element(reference, "PVgen"), cnodeLine, cnodeGround, source.getVoltage(), source.getPower());
////			context.installDiode(CircuitElement.element(reference, "Dbackflow"), cnodeIntern, cnodeLine, Diode.DEFAULT_MODEL);
			
		}
		
	}
	
	
	
	@Override
	public void stepCircuitElements(Level level, ElectricReference reference, BlockState instance, ComponentCircuitContext context) {

		if (level.getBlockEntity(reference.block()) instanceof VoltageSourceBlockEntity source) {
			
			NodalElementState sourceState = context.getElement(CircuitElement.element(reference, "Ugen"));
			sourceState.setParameter("U", source.getVoltage());
			sourceState.setParameter("P", source.getPower());
			
//			NodalElement element;
//
//			CircuitElement sourceElement = CircuitElement.element(reference, "Ugen");
//			NodalElementState s = element.newInstance(sourceElement.elementString());
//			
//			CircuitElement sourceElement = CircuitElement.element(reference, "Ugen");
//			double current = -ElectricUtility.getElementCurrent(level, sourceElement);
//			double voltage = source.getVoltage();
//			double lastVoltage = context.readVoltage(sourceElement);
//			double power = current * lastVoltage;
//			if (power < 0)
//				power = 0;
//			if (power > source.getPower()) {
//				double eqres = lastVoltage / current;
//				voltage = Math.sqrt(eqres * source.getPower());
//			}
//			
//			if (Math.abs(lastVoltage - voltage) > 0.001)
//				context.changeVoltage(sourceElement, lastVoltage + (voltage - lastVoltage) * 0.2);
			
			
//			CircuitElement sourceElement = CircuitElement.element(reference, "Ugen");
//			double lastVoltage = context.readVoltage(sourceElement);
//			double current = source.getDeviceCurrent();
//			double voltage = source.getVoltage();
//			double power = current * lastVoltage;
//			if (power < 0)
//				power = 0;
//			if (power > source.getPower() || lastVoltage > voltage) {
//				lastVoltage -= Math.max(lastVoltage - voltage, (power - source.getPower()) * 0.01);
//			} else if (power < source.getPower() && lastVoltage < voltage) {
//				lastVoltage += Math.max(voltage - lastVoltage, (source.getPower() - power) * 0.01);
//			}
//			
//			if (Math.abs(lastVoltage - voltage) > 0.001)
//				context.changeVoltage(sourceElement, lastVoltage);
			
		}
		
	}
	
	@Override
	public void afterNetworkStep(Level level, ElectricReference reference, BlockState instance, ElectricNetwork network) {
		// TODO Auto-generated method stub
		IElectricBlock.super.afterNetworkStep(level, reference, instance, network);
	}
	
	@Override
	public NodePos[] getElectricConnections(Level level, ElectricReference reference, BlockState instance) {
		return NODES.getNodePositions(reference.block());
	}
	
//	@Override
//	public double getVoltage(BlockState state, Level level, BlockPos pos) {
//		if (level.getBlockEntity(pos) instanceof VoltageSourceBlockEntity source) {
//			String[] wireLanes = source.getNodeLanes();
//			NodePos node = new NodePos(pos, 0);
//			return ElectricUtility.getVoltageBetween(level, CircuitNode.node(node, wireLanes[0]), CircuitNode.node(node, wireLanes[1]));
//		}
//		return 0.0;
//	}
	
//	@Override
//	public double getCurrentPower(Level level, ElectricReference reference, BlockState instance) {
//		if (level.getBlockEntity(reference.block()) instanceof VoltageSourceBlockEntity source) {
//			String[] wireLanes = source.getNodeLanes();
//			NodePos node = new NodePos(reference.block(), 0);
//			double current = ElectricUtility.getElementCurrent(level, CircuitElement.element(reference, "Ugen"));
//			double voltage = ElectricUtility.getVoltageBetween(level, CircuitNode.node(node, wireLanes[0]), CircuitNode.node(node, wireLanes[1]));
//			BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(this);
//			double powerUsed = Math.min(voltage * current, parametrics.getPowerMax());
//			return Math.max(powerUsed > 1.0 ? parametrics.getPowerMin() : 0, powerUsed);
//		}
//		return 0.0;
//	}
//	
//	@Override
//	public double getMaxPowerGeneration(Level level, ElectricReference reference, BlockState instance) {
//		if (level.getBlockEntity(reference.block()) instanceof VoltageSourceBlockEntity source) {
//			return source.getPower();
//		}
//		return 0.0;
//	}
	
	@Override
	public String[] getWireLanes(Level level, ElectricReference reference, BlockState instance, NodePos node) {
		if (level.getBlockEntity(reference.block()) instanceof VoltageSourceBlockEntity powerSource) {
			return powerSource.getNodeLanes();
		}
		return new String[0];
	}

	@Override
	public void setWireLanes(Level level, ElectricReference reference, BlockState instance, NodePos node, String[] laneLabels) {
		if (level.getBlockEntity(reference.block()) instanceof VoltageSourceBlockEntity powerSource) {
			powerSource.getNodeLanes(laneLabels);
		}
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		return GameUtility.openElectricBlockEntityUI(pLevel, pPos, pPlayer, pHand);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new VoltageSourceBlockEntity(pPos, pState);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.setValue(BlockStateProperties.FACING, pMirror.mirror(pState.getValue(BlockStateProperties.FACING)));
	}
	
	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(BlockStateProperties.FACING, pRotation.rotate(pState.getValue(BlockStateProperties.FACING)));
	}
	
}
