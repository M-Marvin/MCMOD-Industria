package de.m_marvin.industria.content.blocks;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.content.blockentities.FloodlightBlockEntity;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.items.AbstractConduitItem;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricConnector;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import net.minecraftforge.network.NetworkHooks;

public class FloodlightBlock extends BaseEntityBlock implements IElectricConnector {
	
	public static final NodePointSupplier NODES = NodePointSupplier.define()
			.addNode(NodeTypes.ALL, 1, new Vec3i(8, 8, 0))
			.addModifier(BlockStateProperties.HORIZONTAL_FACING, NodePointSupplier.FACING_MODIFIER_DEFAULT_NORTH);
	public static final int NODE_COUNT = 1;
	public static final int TARGET_VOLTAGE = 100;
	public static final int TARGET_POWER = 200;
	
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
		return blockstate;
	}
	
	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getValue(BlockStateProperties.LIT) ? 15 : 0;
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
					.rotateZ(pState.getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue() * 90)
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
			templateSource.setProperty("nominal_current", TARGET_POWER / TARGET_VOLTAGE);
			templateSource.setNetworkNode("VDC", new NodePos(position, 0), "electric_lamp_P");
			templateSource.setNetworkNode("GND", new NodePos(position, 0), "electric_lamp_N");
			plotter.accept(templateSource);
			
		}
	}
	
	@Override
	public void onNetworkNotify(Level level, BlockState instance, BlockPos position) {
		Optional<FloodlightBlockEntity> lamp = level.getBlockEntity(position, ModBlockEntityTypes.FLOODLIGHT.get());
		if (lamp.isPresent()) {
			lamp.get().updateLight();
		}
	}
	
	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return new NodePos[] {new NodePos(pos, 0)};
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
