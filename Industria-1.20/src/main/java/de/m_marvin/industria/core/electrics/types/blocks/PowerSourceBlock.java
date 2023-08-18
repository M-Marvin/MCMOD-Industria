package de.m_marvin.industria.core.electrics.types.blocks;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.items.AbstractConduitItem;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.blockentities.PowerSourceBlockEntity;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class PowerSourceBlock extends BaseEntityBlock implements IElectricConnector{
	
	public static final NodePointSupplier NODE_POINTS = NodePointSupplier.define()
			.addNode(NodeTypes.ALL, 8, new Vec3i(8, 8, 0))
			.addModifier(BlockStateProperties.FACING, NodePointSupplier.FACING_MODIFIER_DEFAULT_NORTH);
	public static final int NODE_COUNT = 1;

	public PowerSourceBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return Blocks.POWER_SOURCE.get().defaultBlockState().setValue(BlockStateProperties.FACING, GameUtility.getFacingDirection(pContext.getPlayer()).getOpposite());
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.FACING);
	}
	
	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return NODE_POINTS.getNodes(state);
	}
	
	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit, Consumer<ICircuitPlot> plotter) {

		if (level.getBlockEntity(position) instanceof PowerSourceBlockEntity source) {

			NodePos[] nodes = getConnections(level, position, instance);
			List<String[]> lanes = Stream.of(nodes).map(node -> source.getCableWireLabels(node)).toList();
			String[] sourceLanes = source.getWireLabels();
			
			CircuitTemplate template = CircuitTemplateManager.getInstance().getTemplate(new ResourceLocation(IndustriaCore.MODID, "resistor"));
			template.setProperty("resistance", 0);
			
			for (int i = 0; i < nodes.length; i++) {
				String[] wireLanes = lanes.get(i);
				for (String lane : wireLanes) {
					if (lane.equals(sourceLanes[0])) {
						template.setNetworkNode("NET1", nodes[i], lane);
						template.setNetworkNode("NET2", new NodePos(position, 0), "source_" + position.asLong() + "_P");
						plotter.accept(template);
					} else if (lane.equals(sourceLanes[1])) {
						template.setNetworkNode("NET1", nodes[i], lane);
						template.setNetworkNode("NET2", new NodePos(position, 0), "source_" + position.asLong() + "_N");
						plotter.accept(template);
					}
				}
			}

			CircuitTemplate templateSource = CircuitTemplateManager.getInstance().getTemplate(new ResourceLocation(IndustriaCore.MODID, "source"));
			templateSource.setProperty("nominal_current", 10);
			templateSource.setProperty("nominal_voltage", 100);
			templateSource.setNetworkNode("VDC", new NodePos(position, 0), "source_" + position.asLong() + "_P");
			templateSource.setNetworkNode("GND", new NodePos(position, 0), "source_" + position.asLong() + "_N");
			plotter.accept(templateSource);
			
		}
		
	}

	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return IntStream.range(0, NODE_COUNT).mapToObj(id -> new NodePos(pos, id)).toArray(i -> new NodePos[i]);
	}
	
	@Override
	public String[] getWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node) {
		if (level.getBlockEntity(pos) instanceof PowerSourceBlockEntity powerSource) {
			return powerSource.getWireLabels();
		}
		return new String[0];
	}

	@Override
	public void setWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {
		if (level.getBlockEntity(pos) instanceof PowerSourceBlockEntity powerSource) {
			powerSource.setWireLabels(laneLabels);
		}
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		if (pPlayer.getItemInHand(pHand).getItem() instanceof AbstractConduitItem) return InteractionResult.PASS; // TODO Solve with tags in future
		if (!pLevel.isClientSide()) {
			BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
			if (blockEntity instanceof MenuProvider provider) NetworkHooks.openScreen((ServerPlayer) pPlayer, provider, pPos);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new PowerSourceBlockEntity(pPos, pState);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
}
