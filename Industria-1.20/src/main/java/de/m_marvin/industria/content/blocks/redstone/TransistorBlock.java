package de.m_marvin.industria.content.blocks.redstone;

import java.util.function.Consumer;
import java.util.stream.IntStream;

import de.m_marvin.industria.content.blockentities.redstone.TransistorBlockEntity;
import de.m_marvin.industria.core.client.util.TooltipAdditions;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.CircuitTemplate.Plotter;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricBlock;
import de.m_marvin.industria.core.registries.Circuits;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.items.ITooltipAdditionsModifier;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TransistorBlock extends DiodeLikeBlock implements EntityBlock, IElectricBlock, ITooltipAdditionsModifier {
	
	public static final NodePointSupplier NODES = NodePointSupplier.define()
			.addNode(NodeTypes.ELECTRIC, 2, new Vec3i(8, 2, 2))
			.addNode(NodeTypes.ELECTRIC, 2, new Vec3i(8, 2, 14))
			.addModifier(BlockStateProperties.HORIZONTAL_FACING, NodePointSupplier.FACING_HORIZONTAL_MODIFIER_DEFAULT_NORTH);
	public static final int NODE_COUNT = 2;
	
	public static final double RESISTANCE_ON = 0.001;
	public static final double RESISTANCE_OFF = 20000000;
	
	public TransistorBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public boolean showTooltipType(String tooltipTypeName) {
		return tooltipTypeName != TooltipAdditions.TOOLTIP_ELECTRICS;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new TransistorBlockEntity(pPos, pState);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(BlockStateProperties.POWERED);
	}
	
	@Override
	public BlockState getStateAtViewpoint(BlockState state, BlockGetter level, BlockPos pos, Vec3 viewpoint) {
		return super.getStateAtViewpoint(state, level, pos, viewpoint).setValue(BlockStateProperties.POWER, 0);
	}
	
	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		boolean power = pLevel.getBestNeighborSignal(pPos) > 0;
		pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.POWERED, power));
	}
	
	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return NODES.getNodes(state);
	}

	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		ElectricUtility.updateNetwork(pLevel, pPos);
	}
	
	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit, Consumer<ICircuitPlot> plotter) {
		if (level.getBlockEntity(position) instanceof TransistorBlockEntity regulator && instance.getBlock() == this) {
			
			String[] wireLanes = regulator.getNodeLanes();
			ElectricUtility.plotJoinTogether(plotter, level, this, position, instance, false, wireLanes[0], wireLanes[1]);
			
			boolean active = level.getBlockState(position).getValue(BlockStateProperties.POWERED);
			
			if (active) {
				Plotter resistor = CircuitTemplateManager.getInstance().getTemplate(Circuits.JUNCTION_RESISTOR).plotter();
				resistor.setNetworkLocalNode("NET1", position, wireLanes[0], false);
				resistor.setNetworkLocalNode("NET2", position, wireLanes[1], false);
				plotter.accept(resistor);
			}
			
		}
	}
	
	@Override
	public void onNetworkNotify(Level level, BlockState instance, BlockPos position) {
		GameUtility.triggerClientSync(level, position);
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		return GameUtility.openElectricBlockEntityUI(pLevel, pPos, pPlayer, pHand);
	}
	
	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
		super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
		boolean power = pLevel.getBestNeighborSignal(pPos) > 0;
		if (power != pState.getValue(BlockStateProperties.POWERED)) {
			pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.POWERED, power));
			pLevel.scheduleTick(pPos, this, 2);
		}
	}
	
	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return IntStream.range(0, NODE_COUNT).mapToObj(i -> new NodePos(pos, i)).toArray(i -> new NodePos[i]);
	}

	@Override
	public String[] getWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node) {
		if (level.getBlockEntity(pos) instanceof TransistorBlockEntity regulator) {
			return regulator.getNodeLanes();
		}
		return new String[0];
	}

	@Override
	public void setWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {
		if (level.getBlockEntity(pos) instanceof TransistorBlockEntity regulator) {
			regulator.setNodeLanes(laneLabels);
		}
	}
	
}
