package de.m_marvin.industria.core.electrics.types.blocks;

import java.util.function.Consumer;
import java.util.stream.IntStream;

import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.items.AbstractConduitItem;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.blockentities.PowerSourceBlockEntity;
import de.m_marvin.industria.core.registries.Blocks;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
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
	public void neighborRewired(Level level, BlockState instance, BlockPos position, Component<?, ?, ?> neighbor) {}

	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit, Consumer<CircuitTemplate> plotter) {}

	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return IntStream.range(0, NODE_COUNT).mapToObj(id -> new NodePos(pos, id)).toArray(i -> new NodePos[i]);
	}
	
	@Override
	public String[] getWireLanes(BlockPos pos, BlockState instance, NodePos node) {
		return new String[] {};
	}

	@Override
	public void setWireLanes(BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {}
	
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
