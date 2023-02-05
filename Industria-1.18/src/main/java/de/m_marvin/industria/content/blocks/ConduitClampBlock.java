package de.m_marvin.industria.content.blocks;

import java.util.stream.Stream;

import de.m_marvin.industria.content.registries.ModBlockStateProperties;
import de.m_marvin.industria.content.types.WallOrientations;
import de.m_marvin.industria.core.client.registries.NodeTypes;
import de.m_marvin.industria.core.conduits.engine.NodePointSupplier;
import de.m_marvin.industria.core.conduits.types.ConduitNode;
import de.m_marvin.industria.core.conduits.types.blocks.IConduitConnector;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit.ConduitType;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ConduitClampBlock extends Block implements IConduitConnector {
	
	public static final VoxelShape SHAPE = Stream.of(
			Block.box(12, 6, 0, 15, 10, 1),
			Block.box(1, 6, 0, 4, 10, 1),
			Block.box(4, 6, 0, 5, 10, 7),
			Block.box(11, 6, 0, 12, 10, 7),
			Block.box(4, 6, 7, 12, 10, 8)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	
	public static final NodePointSupplier CONDUIT_NODES = NodePointSupplier.define()
			.addNode(NodeTypes.ALL, 4, new Vec3i(8, 8, 8));
	
	public ConduitClampBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		WallOrientations orientation = state.getValue(ModBlockStateProperties.ORIENTATION);
		VoxelShape shape = null;
		switch (orientation) {
		case EAST_UD:
		case NORHT_UD:
		case SOUTH_UD:
		case WEST_UD:
			shape = VoxelShapeUtility.rotateShape(SHAPE, new Vec3f(8, 8, 8), orientation.getFace(), Axis.Y);
			break;
		case WEST_NS:
		case EAST_NS:
			shape = VoxelShapeUtility.rotateShape(SHAPE, new Vec3f(8, 8, 8), orientation.getFace(), Axis.Y);
			shape = VoxelShapeUtility.rotateShape(shape, new Vec3f(8, 8, 8), 90, true, Axis.X);
			break;
		case NORTH_EW:
		case SOUTH_EW:
			shape = VoxelShapeUtility.rotateShape(SHAPE, new Vec3f(8, 8, 8), orientation.getFace(), Axis.Y);
			shape = VoxelShapeUtility.rotateShape(shape, new Vec3f(8, 8, 8), 90, true, Axis.Z);
			break;
		case CEILING_EW:
		case GROUND_EW:
			shape = VoxelShapeUtility.rotateShape(SHAPE, new Vec3f(8, 8, 8), orientation.getFace(), Axis.X);
			shape = VoxelShapeUtility.rotateShape(shape, new Vec3f(8, 8, 8), 90, true, Axis.Y);
			break;
		case CEILING_NS:
		case GROUND_NS:
			shape = VoxelShapeUtility.rotateShape(SHAPE, new Vec3f(8, 8, 8), orientation.getFace(), Axis.X);
			break;
		default: shape = SHAPE;
		}
		Vec3f clampOffset = null;
		switch (orientation.getAxialOrientation()) {
		case X: clampOffset = new Vec3f((state.getValue(ModBlockStateProperties.CLAMP_OFFSET) - 1) * 6, 0, 0); break;
		case Y: clampOffset = new Vec3f(0, (state.getValue(ModBlockStateProperties.CLAMP_OFFSET) - 1) * 6, 0); break;
		case Z: clampOffset = new Vec3f(0, 0, (state.getValue(ModBlockStateProperties.CLAMP_OFFSET) - 1) * 6); break;
		}
		return VoxelShapeUtility.offsetShape(shape, clampOffset);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> properties) {
		properties.add(ModBlockStateProperties.ORIENTATION, ModBlockStateProperties.CLAMP_OFFSET);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pcont) {
		Direction attachFace = pcont.getClickedFace().getOpposite();
		BlockState attachState = pcont.getLevel().getBlockState(pcont.getClickedPos().relative(attachFace));
		if (attachState.isFaceSturdy(pcont.getLevel(), pcont.getClickedPos().relative(attachFace), attachFace.getOpposite())) {
			Axis axis = null;
			if (attachFace.getAxis() == Axis.Y) {
				axis = pcont.getHorizontalDirection().getAxis();
			} else {
				axis = pcont.getPlayer().isShiftKeyDown() ? attachFace.getClockWise().getAxis() : Axis.Y;
			}
			WallOrientations orientation = WallOrientations.fromFaceAndAxis(attachFace, axis);
			double axisPos = pcont.getClickLocation().get(axis);
			int offset = Math.min((int) ((axisPos - pcont.getClickedPos().get(axis)) * 3), 2);
			return this.defaultBlockState().setValue(ModBlockStateProperties.ORIENTATION, orientation).setValue(ModBlockStateProperties.CLAMP_OFFSET, offset);
		}
		return null;
	}
	
	@Override
	public ConduitNode[] getConduitNodes(Level level, BlockPos pos, BlockState state) {
		return CONDUIT_NODES.updateNodes(state).getNodes();
	}
	
}
