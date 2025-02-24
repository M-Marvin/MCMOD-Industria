package de.m_marvin.industria.core.electrics.types.blocks;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import de.m_marvin.industria.core.client.util.TooltipAdditions;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.engine.ElectricHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.engine.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.CircuitTemplate.Plotter;
import de.m_marvin.industria.core.registries.Circuits;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility.ShapeType;
import de.m_marvin.industria.core.util.items.ITooltipAdditionsModifier;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WireHolderBlock extends Block implements IElectricBlock, ITooltipAdditionsModifier {
	
	public static final NodePointSupplier NODES = NodePointSupplier.define()
			.addNode(NodeTypes.ELECTRIC, 8, new Vec3i(8, 8, 5))
			.addModifier(BlockStateProperties.FACING, NodePointSupplier.FACING_MODIFIER_DEFAULT_NORTH);
	public static final int NODE_COUNT = 1;
	
	public static final VoxelShape SHAPE = VoxelShapeUtility.box(6, 1, 6, 10, 6, 10);
	
	public WireHolderBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public boolean showTooltipType(String tooltipTypeName) {
		return tooltipTypeName != TooltipAdditions.TOOLTIP_ELECTRICS;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(BlockStateProperties.FACING, pContext.getClickedFace().getOpposite());
	}
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return VoxelShapeUtility.stateCachedShape(ShapeType.MISC, pState, () -> {
			return VoxelShapeUtility.transformation()
					.centered()
					.rotateX(-90)
					.rotateFromNorth(pState.getValue(BlockStateProperties.FACING))
					.uncentered()
					.transform(SHAPE);
		});
	}
	
	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return NODES.getNodes(state);
	}

	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit, Consumer<ICircuitPlot> plotter) {
		
		NodePos node = this.getConnections(level, position, instance)[0];
		List<String[]> cableLanes = ElectricUtility.getLaneLabels(level, node, Component::isWire);
		int laneCount = cableLanes.stream().mapToInt(l -> l.length).max().orElse(0);

		Plotter template = CircuitTemplateManager.getInstance().getTemplate(Circuits.JUNCTION_RESISTOR).plotter();
		
		String[] lt = new String[laneCount];
		for (int i = 0; i < laneCount; i++) {
			for (String[] lanes : cableLanes) {
				if (lanes.length > i) {
					if (lt[i] == null || lt[i].equals(lanes[i])) {
						lt[i] = lanes[i];
						continue;
					} else {
						template.setNetworkNode("NET1", node, i, lt[i]);
						template.setNetworkNode("NET2", node, i, lanes[i]);
						plotter.accept(template);
					}
				}
			}
		}
		
	}
	
	@Override
	public double getCurrentPower(Level level, BlockPos pos, BlockState instance) {
		return 0;
	}
	
	@Override
	public double getMaxPowerGeneration(Level level, BlockPos pos, BlockState instance) {
		return 0;
	}
	
	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return IntStream.range(0, NODE_COUNT).mapToObj(i -> new NodePos(pos, i)).toArray(i -> new NodePos[i]);
	}

	@Override
	public String[] getWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node) {
		return new String[] {};
	}

	@Override
	public void setWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {}
	
	private boolean canAttachTo(BlockGetter pBlockReader, BlockPos pPos, Direction pDirection) {
		BlockState blockstate = pBlockReader.getBlockState(pPos);
		if (!blockstate.isFaceSturdy(pBlockReader, pPos, pDirection.getOpposite())) {
			return pDirection.getAxis().isVertical() && (blockstate.is(BlockTags.FENCES) || blockstate.is(BlockTags.WALLS));
		}
		return true;
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		Direction direction = pState.getValue(BlockStateProperties.FACING);
		return this.canAttachTo(pLevel, pPos.relative(direction), direction);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pFacing == pState.getValue(BlockStateProperties.FACING) && !pState.canSurvive(pLevel, pCurrentPos)) {
			return Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(pState, pFacing, pState, pLevel, pCurrentPos, pFacingPos);
	}
	
}
