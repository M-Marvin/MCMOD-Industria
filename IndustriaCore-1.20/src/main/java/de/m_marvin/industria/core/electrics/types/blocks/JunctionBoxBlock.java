package de.m_marvin.industria.core.electrics.types.blocks;

import java.util.function.Consumer;

import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.blockentities.JunctionBoxBlockEntity;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.registries.NodeTypes;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class JunctionBoxBlock extends BaseEntityBlock implements IElectricBlock {
	
	public static final VoxelShape BLOCK_SHAPE = Block.box(3, 0, 3, 13, 3, 13);
	
	public static final NodePointSupplier NODES = NodePointSupplier.define()
			.addNodesAround(Axis.Z, NodeTypes.ELECTRIC, 1, new Vec3i(8, 3, 1))
			.addModifier(BlockStateProperties.FACING, NodePointSupplier.FACING_MODIFIER_DEFAULT_NORTH);
	
	public JunctionBoxBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new JunctionBoxBlockEntity(pPos, pState);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.FACING);
	}
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		Direction facing = pState.getValue(BlockStateProperties.FACING);
		return VoxelShapeUtility.transformation()
				.centered()
				.rotateX(-90)
				.rotateFromNorth(facing)
				.uncentered()
				.transform(BLOCK_SHAPE);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		final Direction preferred = context.getClickedFace().getOpposite();
		return (BlockState) this.defaultBlockState().setValue(BlockStateProperties.FACING, preferred);
	}

	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return NODES.getNodes(state);
	}
	
	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return NODES.getNodePositions(pos);
	}
	
	@Override
	public String[] getWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node) {
		return new String[0];
	}

	@Override
	public void setWireLanes(Level level, BlockPos pos, BlockState instance, NodePos node, String[] laneLabels) {}
	
	@Override
	public void plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit, Consumer<ICircuitPlot> plotter) {
		
		if (level.getBlockEntity(position) instanceof IJunctionEdit junction) {

			ElectricUtility.plotConnectEquealNamed(plotter, level, this, position, instance);
			
		}
		
	}
	
	public Direction getBlockFacing(Level level, BlockState state, BlockPos position) {
		return PhysicUtility.optionalContraptionTransform(level, position, (transform, direction) -> PhysicUtility.toWorldDirection(transform, direction), state.getValue(BlockStateProperties.FACING));
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}
	
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		return GameUtility.openElectricBlockEntityUI(pLevel, pPos, pPlayer, pHand);
	}

	private boolean canAttachTo(BlockGetter pBlockReader, BlockPos pPos, Direction pDirection) {
		BlockState blockstate = pBlockReader.getBlockState(pPos);
		return blockstate.isFaceSturdy(pBlockReader, pPos, pDirection.getOpposite());
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
