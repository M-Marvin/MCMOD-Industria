package de.m_marvin.industria.content.blocks;

import java.util.function.Consumer;
import java.util.stream.IntStream;

import de.m_marvin.industria.content.blockentities.JunctionBoxBlockEntity;
import de.m_marvin.industria.core.client.registries.NodeTypes;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.conduits.types.items.AbstractConduitItem;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricConnector;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class JunctionBoxBlock extends BaseEntityBlock implements IElectricConnector {
	
	public static final VoxelShape BLOCK_SHAPE = Block.box(3, 0, 3, 13, 3, 13);
	
	public static final NodePointSupplier NODE_POINTS = NodePointSupplier.define()
			.addNodesAround(Axis.Z, NodeTypes.ALL, 1, new Vec3i(8, 3, 1))
			.addModifier(BlockStateProperties.FACING, NodePointSupplier.FACING_MODIFIER_DEFAULT_NORTH);
	public static final int NODE_COUNT = 4;
	
	public JunctionBoxBlock(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.FACING);
	}
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		Direction facing = pState.getValue(BlockStateProperties.FACING);
		if (facing.getAxis() == Axis.Y) {
			return facing.getAxisDirection() == AxisDirection.POSITIVE ? VoxelShapeUtility.rotateShape(BLOCK_SHAPE, new Vec3f(8, 8, 8), 180, true, Axis.X) : BLOCK_SHAPE;
		} else {
			return VoxelShapeUtility.rotateShape(VoxelShapeUtility.rotateShape(BLOCK_SHAPE, new Vec3f(8, 8, 8), 90, true, Axis.X), new Vec3f(8, 8, 8), facing, Axis.Y);
		}
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		final Direction preferred = context.getClickedFace().getOpposite();
		return (BlockState) this.defaultBlockState().setValue(BlockStateProperties.FACING, preferred);
	}

	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return NODE_POINTS.getNodes(state);
	}
	
	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return IntStream.range(0, NODE_COUNT).mapToObj(id -> new NodePos(pos, id)).toArray(i -> new NodePos[i]);
	}
	
	@Override
	public void neighborRewired(Level level, BlockState instance, BlockPos position, Component<?, ?, ?> neighbor) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String[] getWireLanes(BlockPos pos, BlockState instance, NodePos node) {
		// TODO Auto-generated method stub
		return new String[] {};
	}

	@Override
	public void setWireLanes(BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit, Consumer<CircuitTemplate> plotter) {
		// TODO Auto-generated method stub
		
	}
	
	public Direction getBlockFacing(Level level, BlockState state, BlockPos position) {
		return PhysicUtility.optionalContraptionTransform(level, position, (transform, direction) -> PhysicUtility.toWorldDirection(transform, direction), state.getValue(BlockStateProperties.FACING));
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new JunctionBoxBlockEntity(pPos, pState);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		if (pPlayer.getItemInHand(pHand).getItem() instanceof AbstractConduitItem) return InteractionResult.PASS; // TODO Solve with tags in future
		if (!pLevel.isClientSide()) {
			BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
			if (blockEntity instanceof MenuProvider provider) NetworkHooks.openGui((ServerPlayer) pPlayer, provider, pPos);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.SUCCESS;
	}
	
}
