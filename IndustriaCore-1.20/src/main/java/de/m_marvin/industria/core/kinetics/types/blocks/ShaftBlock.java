 package de.m_marvin.industria.core.kinetics.types.blocks;

import de.m_marvin.industria.core.kinetics.types.blockentities.SimpleKineticBlockEntity;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility;
import de.m_marvin.industria.core.util.VoxelShapeUtility.ShapeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShaftBlock extends BaseEntityBlock implements IKineticBlock, SimpleWaterloggedBlock {

	public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;

	public static final VoxelShape SHAPE = box(6, 0, 6, 10, 16, 10);
	
	public ShaftBlock(Properties pProperties) {
		super(pProperties);
		registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(AXIS, BlockStateProperties.WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource().defaultFluidState() : Fluids.EMPTY.defaultFluidState();
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
					.rotateFromAxisY(pState.getValue(AXIS))
					.uncentered()
					.transform(SHAPE);
		});
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Axis axis = pContext.getClickedFace().getAxis();
		boolean waterlogged = pContext.getLevel().getFluidState(pContext.getClickedPos()).isSourceOfType(Fluids.WATER);
		return this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, waterlogged).setValue(AXIS, axis);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new SimpleKineticBlockEntity(pPos, pState);
	}

	@Override
	public TransmissionNode[] getTransmissionNodes(LevelAccessor level, BlockPos pos, BlockState state) {
		return new TransmissionNode[] {
			new TransmissionNode(KineticReference.simple(pos), pos, 1.0, state.getValue(AXIS), null, null, SHAFT),
			new TransmissionNode(KineticReference.simple(pos), pos, 1.0, state.getValue(AXIS), null, null, AXLE)
		};
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(AXIS, MathUtility.rotate(pRotation, pState.getValue(AXIS)));
	}
	
	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState;
	}
	
}
