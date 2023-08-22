package de.m_marvin.industria.content.blocks;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.content.blockentities.ElectricLampBlockEntity;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.items.AbstractConduitItem;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricConnector;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class ElectricLampBlock extends BaseEntityBlock implements IElectricConnector {
	
	public static final NodePointSupplier NODES = NodePointSupplier.define()
			.addNode(NodeTypes.ALL, 1, new Vec3i(8, 8, 0))
			.addModifier(BlockStateProperties.FACING, NodePointSupplier.FACING_MODIFIER_DEFAULT_NORTH);
	
	public ElectricLampBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return NODES.getNodes(state);
	}

	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit, Consumer<ICircuitPlot> plotter) {
		if (level.getBlockEntity(position) instanceof ElectricLampBlockEntity lamp) {

			NodePos[] nodes = getConnections(level, position, instance);
			List<String[]> lanes = Stream.of(nodes).map(node -> lamp.getCableWireLabels(node)).toList();
			String[] lampLanes = lamp.getNodeLanes();
			
			CircuitTemplate template = CircuitTemplateManager.getInstance().getTemplate(new ResourceLocation(IndustriaCore.MODID, "resistor"));
			template.setProperty("resistance", 0);
			
			for (int i = 0; i < nodes.length; i++) {
				String[] wireLanes = lanes.get(i);
				for (String lane : wireLanes) {
					if (lane.equals(lampLanes[0])) {
						template.setNetworkNode("NET1", nodes[i], lane);
						template.setNetworkNode("NET2", new NodePos(position, 0), "electric_lamp_P");
						plotter.accept(template);
					} else if (lane.equals(lampLanes[1])) {
						template.setNetworkNode("NET1", nodes[i], lane);
						template.setNetworkNode("NET2", new NodePos(position, 0), "electric_lamp_N");
						plotter.accept(template);
					}
				}
			}

			CircuitTemplate templateSource = CircuitTemplateManager.getInstance().getTemplate(new ResourceLocation(IndustriaCore.MODID, "current_load"));
			templateSource.setProperty("nominal_current", 2);
			templateSource.setNetworkNode("VDC", new NodePos(position, 0), "electric_lamp_P");
			templateSource.setNetworkNode("GND", new NodePos(position, 0), "electric_lamp_N");
			plotter.accept(templateSource);
			
		}
	}
	
	@Override
	public void onNetworkNotify(Level level, BlockState instance, BlockPos position) {
		
		double v1 = ElectricUtility.getFloatingNodeVoltage(level, new NodePos(position, 0), "electric_lamp_P");
		double v2 = ElectricUtility.getFloatingNodeVoltage(level, new NodePos(position, 0), "electric_lamp_N");
		double voltage = v1 - v2;
		
		System.out.println("Lamp voltage: " + voltage + "V");
		System.out.println("Lamp current: " + 2 + "A");
		System.out.println("Lamp power: " + (2 * voltage) + "W");
		
	}
	
	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return new NodePos[] {new NodePos(pos, 0)};
	}
	
	@Override
	public String[] getWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node) {
		if (level.getBlockEntity(pos) instanceof ElectricLampBlockEntity lamp) {
			return lamp.getNodeLanes();
		}
		return new String[0];
	}

	@Override
	public void setWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {
		if (level.getBlockEntity(pos) instanceof ElectricLampBlockEntity lamp) {
			lamp.setNodeLanes(laneLabels);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new ElectricLampBlockEntity(pPos, pState);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.FACING);
		pBuilder.add(BlockStateProperties.POWERED);
	}
	
	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(BlockStateProperties.FACING, pContext.getClickedFace()).setValue(BlockStateProperties.POWERED, false);
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		if (pPlayer.getItemInHand(pHand).getItem() instanceof AbstractConduitItem) return InteractionResult.PASS; // TODO Solve with tags in future
		if (!pLevel.isClientSide()) {
			BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
			if (blockEntity instanceof MenuProvider provider) {
				NetworkHooks.openScreen((ServerPlayer) pPlayer, provider, pPos);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.SUCCESS;
	}
	
}
