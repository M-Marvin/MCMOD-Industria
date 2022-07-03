package de.m_marvin.industria.blocks;

import java.util.stream.Stream;

import de.m_marvin.industria.registries.ConduitConnectionTypes;
import de.m_marvin.industria.registries.ModBlockStateProperties;
import de.m_marvin.industria.util.UtilityHelper;
import de.m_marvin.industria.util.block.IConduitConnector;
import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier;
import de.m_marvin.industria.util.conduit.MutableConnectionPointSupplier.ConnectionPoint;
import de.m_marvin.industria.util.types.WallOrientations;
import de.m_marvin.industria.util.unifiedvectors.Vec3f;
import de.m_marvin.industria.util.unifiedvectors.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
	
	public static final MutableConnectionPointSupplier CONDUIT_NODES = MutableConnectionPointSupplier.staticOrientation()
			.addPoint(new Vec3i(8, 8, 8), ConduitConnectionTypes.ELECTRIC, 4);
	
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
			shape = UtilityHelper.rotateShape(SHAPE, new Vec3f(8, 8, 8), orientation.getFace(), Axis.Y);
			break;
		case WEST_NS:
		case EAST_NS:
			shape = UtilityHelper.rotateShape(SHAPE, new Vec3f(8, 8, 8), orientation.getFace(), Axis.Y);
			shape = UtilityHelper.rotateShape(shape, new Vec3f(8, 8, 8), Math.toRadians(90), Axis.X);
			break;
		case NORTH_EW:
		case SOUTH_EW:
			shape = UtilityHelper.rotateShape(SHAPE, new Vec3f(8, 8, 8), orientation.getFace(), Axis.Y);
			shape = UtilityHelper.rotateShape(shape, new Vec3f(8, 8, 8), Math.toRadians(90), Axis.Z);
			break;
		case CEILING_EW:
		case GROUND_EW:
			shape = UtilityHelper.rotateShape(SHAPE, new Vec3f(8, 8, 8), orientation.getFace(), Axis.X);
			shape = UtilityHelper.rotateShape(shape, new Vec3f(8, 8, 8), Math.toRadians(90), Axis.Y);
			break;
		case CEILING_NS:
		case GROUND_NS:
			shape = UtilityHelper.rotateShape(SHAPE, new Vec3f(8, 8, 8), orientation.getFace(), Axis.X);
			break;
		default: shape = SHAPE;
		}
		Vec3f clampOffset = null;
		switch (orientation.getAxialOrientation()) {
		case X: clampOffset = new Vec3f((state.getValue(ModBlockStateProperties.CLAMP_OFFSET) - 1) * 6, 0, 0); break;
		case Y: clampOffset = new Vec3f(0, (state.getValue(ModBlockStateProperties.CLAMP_OFFSET) - 1) * 6, 0); break;
		case Z: clampOffset = new Vec3f(0, 0, (state.getValue(ModBlockStateProperties.CLAMP_OFFSET) - 1) * 6); break;
		}
		return UtilityHelper.offsetShape(shape, clampOffset);
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
	public ConnectionPoint[] getConnectionPoints(BlockPos pos, BlockState state) {
//		WallOrientations orientation = state.getValue(ModBlockStateProperties.ORIENTATION);
//		Direction attachFace = orientation.getFace();
//		Axis connectionAxis = orientation.getAxialOrientation();
//		int clampOffset = state.getValue(ModBlockStateProperties.CLAMP_OFFSET);
//		Vec3i offset = null;
//		int axisOffset = attachFace.getAxisDirection() == AxisDirection.POSITIVE ? 12 : 4;
//		switch (connectionAxis) {
//		case X:
//			switch (attachFace.getAxis()) {
//			default:
//			case Y:
//				offset = new Vec3i(2 + clampOffset * 6, axisOffset, 8);
//				break;
//			case Z:
//				offset = new Vec3i(2 + clampOffset * 6, 8, axisOffset);
//				break;
//			}
//			break;
//		case Y:
//			switch (attachFace.getAxis()) {
//			default:
//			case X:
//				offset = new Vec3i(axisOffset, 2 + clampOffset * 6, 8);
//				break;
//			case Z:
//				offset = new Vec3i(8, 2 + clampOffset * 6, axisOffset);
//				break;
//			}
//			break;
//		case Z:
//			switch (attachFace.getAxis()) {
//			default:
//			case X:
//				offset = new Vec3i(axisOffset, 8, 2 + clampOffset * 6);
//				break;
//			case Y:
//				offset = new Vec3i(8, axisOffset, 2 + clampOffset * 6);
//				break;
//			}
//			break;
//		}
//		return new ConnectionPoint[] {new ConnectionPoint(pos, 0, offset, attachFace), new ConnectionPoint(pos, 1, offset, attachFace)};
		return CONDUIT_NODES.getNodes(pos, state);
	}
	
}
