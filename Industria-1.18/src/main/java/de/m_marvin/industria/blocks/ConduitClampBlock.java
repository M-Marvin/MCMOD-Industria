package de.m_marvin.industria.blocks;

import com.jozufozu.flywheel.repack.joml.Vector3i;

import de.m_marvin.industria.registries.ModBlockStateProperties;
import de.m_marvin.industria.types.WallOrientations;
import de.m_marvin.industria.util.IFlexibleConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.Vec3;

public class ConduitClampBlock extends Block implements IFlexibleConnection {
	
	public ConduitClampBlock(Properties properties) {
		super(properties);
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
	public ConnectionPoint[] getConnectionPoints(Level level, BlockPos pos, BlockState state) {
		WallOrientations orientation = state.getValue(ModBlockStateProperties.ORIENTATION);
		Direction attachFace = orientation.getFace();
		Axis connectionAxis = orientation.getAxialOrientation();
		int clampOffset = state.getValue(ModBlockStateProperties.CLAMP_OFFSET);
		Vector3i offset = null;
		float angle = 0;
		switch (connectionAxis) {
		case X:
			switch (attachFace.getAxis()) {
			default:
			case Y:
				offset = new Vector3i(clampOffset * 8, 0, 8);
				angle = 0;
				break;
			case Z:
				offset = new Vector3i(clampOffset * 8, 8, 0);
				angle = 90;
				break;
			}
			break;
		case Y:
			switch (attachFace.getAxis()) {
			default:
			case X:
				offset = new Vector3i(0, clampOffset * 8, 8);
				angle = 0;
				break;
			case Z:
				offset = new Vector3i(8, clampOffset * 8, 0);
				angle = 90;
				break;
			}
			break;
		case Z:
			switch (attachFace.getAxis()) {
			default:
			case X:
				offset = new Vector3i(0, 8, clampOffset * 8);
				angle = 90;
				break;
			case Y:
				offset = new Vector3i(8, 0, clampOffset * 8);
				angle = 0;
				break;
			}
			break;
		}
		return new ConnectionPoint[] {new ConnectionPoint(pos, 0, offset, angle, attachFace), new ConnectionPoint(pos, 1, offset, angle + 180, attachFace)};
	}
	
}
