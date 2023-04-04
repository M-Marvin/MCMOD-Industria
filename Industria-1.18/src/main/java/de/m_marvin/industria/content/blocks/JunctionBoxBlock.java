package de.m_marvin.industria.content.blocks;

import java.util.stream.IntStream;

import de.m_marvin.industria.core.client.registries.NodeTypes;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplate;
import de.m_marvin.industria.core.electrics.circuits.CircuitTemplateManager;
import de.m_marvin.industria.core.electrics.types.ElectricNetwork;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricConnector;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class JunctionBoxBlock extends Block implements IElectricConnector {
	
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
	public CircuitTemplate plotCircuit(Level level, BlockState instance, BlockPos position, ElectricNetwork circuit) {
		// TODO Auto-generated method stub
		return CircuitTemplateManager.DEFAULT_TEMPLATE;
	}
	
	@Override
	public NodePos[] getConnections(Level level, BlockPos pos, BlockState instance) {
		return IntStream.range(0, NODE_COUNT).mapToObj(id -> new NodePos(pos, id)).toArray(i -> new NodePos[i]);
	}
	
}
