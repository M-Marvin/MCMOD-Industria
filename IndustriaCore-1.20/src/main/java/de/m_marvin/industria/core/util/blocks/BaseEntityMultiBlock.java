package de.m_marvin.industria.core.util.blocks;

import java.util.stream.IntStream;

import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public abstract class BaseEntityMultiBlock extends BaseEntityBlock {
	
	public static final IntegerProperty MBPOS_X = IntegerProperty.create("mbpos_x", 0, 2);
	public static final IntegerProperty MBPOS_Y = IntegerProperty.create("mbpos_y", 0, 2);
	public static final IntegerProperty MBPOS_Z = IntegerProperty.create("mbpos_z", 0, 2);
	
	protected final int width;
	protected final int height;
	protected final int depth;
	
	protected BaseEntityMultiBlock(Properties pProperties, int width, int height, int depth) {
		super(pProperties);
		if (width > 3 || height > 3 || depth > 3) throw new IllegalArgumentException("An multi-block canÄt be larger than 6x6x6!");
		this.width = width;
		this.height = height;
		this.depth = depth;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		pBuilder.add(MBPOS_X);
		pBuilder.add(MBPOS_Y);
		pBuilder.add(MBPOS_Z);
		pBuilder.add(BlockStateProperties.HORIZONTAL_FACING);
	}
	
	public Vec3i getMBPosAtIndex(int i) {
		return new Vec3i(
				i % getWidth(), 
				(i / (getWidth() * getDepth())) % getHeight(), 
				(i / getWidth()) % getDepth()
		);
	}
	
	public BlockPos[] getPlacementPositions(BlockPos center, Direction orientation) {
		int rotation = orientation.get2DDataValue() * -90;
		return IntStream.range(0, getWidth() * getHeight() * getDepth())
			.mapToObj(i -> 
				getMBPosAtIndex(i).sub(
						this.getWidth() / 2, 
						0, 
						this.getDepth() / 2
				)
			)
			.map(pos -> MathUtility.rotatePoint(pos, rotation, true, Axis.Y))
			.map(pos -> 
				new BlockPos(
						pos.x + center.getX(), 
						pos.y + center.getY(), 
						pos.z + center.getZ()
				)
			)
			.toArray(i -> new BlockPos[i]);
	}
	
	public boolean canPlace(BlockPlaceContext context, BlockPos[] positions) {
		for (BlockPos pos : positions) {
			BlockState state = context.getLevel().getBlockState(pos);
			if (!state.canBeReplaced(context)) return false;
		}
		return true;
	}
	
	public BlockState stateAt(Vec3i position, Direction orientation) {
		return defaultBlockState()
				.setValue(MBPOS_X, position.getX())
				.setValue(MBPOS_Y, position.getY())
				.setValue(MBPOS_Z, position.getZ())
				.setValue(BlockStateProperties.HORIZONTAL_FACING, orientation);
	}
	
	public Vec3i getMBPos(BlockState state) {
		return new Vec3i(
				state.getValue(MBPOS_X),
				state.getValue(MBPOS_Y),
				state.getValue(MBPOS_Z)
		);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Direction facing = pContext.getHorizontalDirection().getOpposite();
		BlockPos centerBlock = pContext.getClickedPos();
		BlockPos[] positions = getPlacementPositions(centerBlock, facing);
		
		if (!canPlace(pContext, positions)) return null;
		
		Vec3i centerBlockMBPos = new Vec3i(getWidth() / 2, 0, getDepth() / 2);
		return stateAt(centerBlockMBPos, facing);
	}
	
	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		BlockPos[] positions = getPlacementPositions(pPos, pState.getValue(BlockStateProperties.HORIZONTAL_FACING));
		for (int i = 0; i < positions.length; i++) {
			BlockPos placementPos = positions[i];
			BlockState state = pLevel.getBlockState(placementPos);
			if (state.canBeReplaced()) {
				Vec3i mbPos = getMBPosAtIndex(i);
				pLevel.setBlockAndUpdate(placementPos, stateAt(mbPos, pState.getValue(BlockStateProperties.HORIZONTAL_FACING)));
			}
		}
	}
	
	public void breakMultiBlock(Level level, BlockPos pos, BlockState state, boolean removeClicked, boolean makeParticles) {

		Vec3i mbPos = getMBPos(state);
		Direction orientation = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		Vec3i mbOffset = MathUtility.rotatePoint(mbPos, orientation.get2DDataValue() * -90, true, Axis.Y);
		BlockPos originPos = new BlockPos(pos.getX() - mbOffset.x, pos.getY() - mbOffset.y, pos.getZ() - mbOffset.z);
		
		for (int x = 0; x < getWidth(); x++) {
			for (int z = 0; z < getDepth(); z++) {
				for (int y = 0; y < getHeight(); y++) {
					mbOffset = MathUtility.rotatePoint(new Vec3i(x, y, z), orientation.get2DDataValue() * -90, true, Axis.Y);
					BlockPos breakPos = originPos.offset(mbOffset.x, mbOffset.y, mbOffset.z);
					BlockState breakState = level.getBlockState(breakPos);
					if (!removeClicked && breakPos.equals(pos)) continue;
					if (breakState.getBlock() == this) {
						if (makeParticles) {
							level.destroyBlock(breakPos, false);
						} else {
							level.setBlock(breakPos, Blocks.AIR.defaultBlockState(), 3);
						}
					}
				}
			}
		}
		
	}
	
	public boolean stillValid(Level level, BlockState state, BlockPos pos, BlockPos neighbor) {

		Direction orientation = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		Vec3i mbPos = getMBPos(state);
		Vec3i neighborDirection = Vec3i.fromVec(neighbor).sub(Vec3i.fromVec(pos));
		Vec3i mbNeighbor = MathUtility.rotatePoint(neighborDirection, orientation.get2DDataValue() * 90, true, Axis.Y).add(mbPos);
		
		boolean shouldBeMultiBlock = 
				mbNeighbor.x >= 0 && mbNeighbor.x < getWidth() &&
				mbNeighbor.y >= 0 && mbNeighbor.y < getHeight() &&
				mbNeighbor.z >= 0 && mbNeighbor.z < getDepth();
		if (shouldBeMultiBlock) {
			BlockState neighborState = level.getBlockState(neighbor);
			if (neighborState.getBlock() != this) return false;
		}
		return true;
	}
	
	@Override
	public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
		if (!stillValid(pLevel, pState, pPos, pNeighborPos)) {
			breakMultiBlock(pLevel, pPos, pState, true, true);
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getDepth() {
		return depth;
	}
	
}
