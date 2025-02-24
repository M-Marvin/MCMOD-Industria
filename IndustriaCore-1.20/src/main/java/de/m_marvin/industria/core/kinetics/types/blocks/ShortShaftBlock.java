 package de.m_marvin.industria.core.kinetics.types.blocks;

import java.util.function.Function;

import de.m_marvin.industria.core.kinetics.types.blockentities.SimpleKineticBlockEntity;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility.ShapeType;
import de.m_marvin.industria.core.util.types.AxisOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShortShaftBlock extends BaseEntityBlock implements IKineticBlock {

	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

	public static final Function<Boolean, VoxelShape> SHAPE = isLong -> VoxelShapeUtility.box(6, 6, 0, 10, 10, isLong ? 10 : 5);
	
	protected final boolean isLong;
	
	public ShortShaftBlock(boolean isLong, Properties pProperties) {
		super(pProperties);
		this.isLong = isLong;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FACING);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return VoxelShapeUtility.stateCachedShape(ShapeType.MISC, pState, () -> {
			return VoxelShapeUtility.transformation()
					.centered()
					.rotateFromNorth(pState.getValue(FACING))
					.uncentered()
					.transform(SHAPE.apply(this.isLong));
		});
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Direction facing = pContext.getClickedFace().getOpposite();
		if (pContext.replacingClickedOnBlock()) {
			Vec3 v = pContext.getHitResult().getLocation().subtract(pContext.getClickedPos().getX(), pContext.getClickedPos().getY(), pContext.getClickedPos().getZ());
			double d = facing.getAxis().choose(v.x, v.y, v.z);
			if (d > 0) facing = facing.getOpposite();
		}
		return this.defaultBlockState().setValue(FACING, facing);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new SimpleKineticBlockEntity(pPos, pState);
	}

	@Override
	public TransmissionNode[] getTransmissionNodes(LevelAccessor level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(FACING);
		boolean positive = facing.getAxisDirection() == AxisDirection.POSITIVE;
		if (this.isLong) {
			return new TransmissionNode[] {
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, facing.getAxis(), null, facing.getAxisDirection(), SHAFT),
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, facing.getAxis(), positive ? AxisOffset.FRONT : AxisOffset.BACK, null, AXLE),
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, facing.getAxis(), AxisOffset.CENTER, null, AXLE)
			};
		} else {
			return new TransmissionNode[] {
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, facing.getAxis(), null, facing.getAxisDirection(), SHAFT),
				new TransmissionNode(KineticReference.simple(pos), pos, 1.0, facing.getAxis(), positive ? AxisOffset.FRONT : AxisOffset.BACK, null, AXLE)
			};
		}
	}
	
	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.setValue(FACING, pMirror.mirror(pState.getValue(FACING)));
	}
	
}
