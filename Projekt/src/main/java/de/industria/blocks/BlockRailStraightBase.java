package de.industria.blocks;

import de.industria.Industria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class BlockRailStraightBase extends AbstractRailBlock {
	
	public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	  
	public BlockRailStraightBase(String name) {
		super(true, AbstractBlock.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL).harvestTool(ToolType.PICKAXE));
		this.setRegistryName(new ResourceLocation(Industria.MODID, name));
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
	}

	protected boolean findPoweredRailSignal(World worldIn, BlockPos pos, BlockState state, boolean searchForward, int recursionCount) {
		if (recursionCount >= 8) {
			return false;
		} else {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		boolean flag = true;
		RailShape railshape = state.getValue(SHAPE);
		switch(railshape) {
			case NORTH_SOUTH:
			if (searchForward) {
				++k;
			} else {
				--k;
			}
			break;
			case EAST_WEST:
			if (searchForward) {
				--i;
			} else {
				++i;
			}
			break;
			case ASCENDING_EAST:
			if (searchForward) {
				--i;
			} else {
				++i;
				++j;
				flag = false;
			}
	
			railshape = RailShape.EAST_WEST;
			break;
			case ASCENDING_WEST:
				if (searchForward) {
					--i;
					++j;
					flag = false;
				} else {
					++i;
				}
	
				railshape = RailShape.EAST_WEST;
				break;
			case ASCENDING_NORTH:
				if (searchForward) {
					++k;
				} else {
					--k;
					++j;
					flag = false;
				}
	
				railshape = RailShape.NORTH_SOUTH;
				break;
			case ASCENDING_SOUTH:
				if (searchForward) {
					++k;
					++j;
					flag = false;
				} else {
					--k;
				}

				railshape = RailShape.NORTH_SOUTH;
		case NORTH_EAST:
			break;
		case NORTH_WEST:
			break;
		case SOUTH_EAST:
			break;
		case SOUTH_WEST:
			break;
		default:
			break;
			}

			if (this.isSamePoweredRail(worldIn, new BlockPos(i, j, k), searchForward, recursionCount, railshape)) {
				return true;
			} else {
				return flag && this.isSamePoweredRail(worldIn, new BlockPos(i, j - 1, k), searchForward, recursionCount, railshape);
			}
		}
	}

	protected boolean isSamePoweredRail(World world, BlockPos state, boolean searchForward, int recursionCount, RailShape shape) {
		BlockState blockstate = world.getBlockState(state);
		if (!(blockstate.getBlock() instanceof PoweredRailBlock)) {
			return false;
		} else {
			RailShape railshape = getRailDirection(blockstate, world, state, null);
			if (shape != RailShape.EAST_WEST || railshape != RailShape.NORTH_SOUTH && railshape != RailShape.ASCENDING_NORTH && railshape != RailShape.ASCENDING_SOUTH) {
				if (shape != RailShape.NORTH_SOUTH || railshape != RailShape.EAST_WEST && railshape != RailShape.ASCENDING_EAST && railshape != RailShape.ASCENDING_WEST) {
					
					return world.hasNeighborSignal(state) ? true : this.findPoweredRailSignal(world, state, blockstate, searchForward, recursionCount + 1);
					
				} else {
				return false;
				}
			} else {
			return false;
			}
		}
	}

	protected void updateState(BlockState state, World worldIn, BlockPos pos, Block blockIn) {
		boolean flag = state.getValue(POWERED);
		boolean flag1 = worldIn.hasNeighborSignal(pos) || this.findPoweredRailSignal(worldIn, pos, state, true, 0) || this.findPoweredRailSignal(worldIn, pos, state, false, 0);
		if (flag1 != flag) {
			worldIn.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(flag1)), 3);
			worldIn.updateNeighborsAt(pos.below(), this);
			if (state.getValue(SHAPE).isAscending()) {
				worldIn.updateNeighborsAt(pos.above(), this);
			}
		}
	}

	public Property<RailShape> getShapeProperty() {
		return SHAPE;
	}

	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
	 * fine.
	 */
	public BlockState rotate(BlockState state, Rotation rot) {
		switch(rot) {
			case CLOCKWISE_180:
			switch((RailShape)state.getValue(SHAPE)) {
				case ASCENDING_EAST:
				return state.setValue(SHAPE, RailShape.ASCENDING_WEST);
				case ASCENDING_WEST:
				return state.setValue(SHAPE, RailShape.ASCENDING_EAST);
				case ASCENDING_NORTH:
				return state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
				case ASCENDING_SOUTH:
				return state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
				case SOUTH_EAST:
				return state.setValue(SHAPE, RailShape.NORTH_WEST);
				case SOUTH_WEST:
				return state.setValue(SHAPE, RailShape.NORTH_EAST);
				case NORTH_WEST:
				return state.setValue(SHAPE, RailShape.SOUTH_EAST);
				case NORTH_EAST:
				return state.setValue(SHAPE, RailShape.SOUTH_WEST);
				case NORTH_SOUTH: //Forge fix: MC-196102
				case EAST_WEST:
				return state;
			}
			case COUNTERCLOCKWISE_90:
			switch((RailShape)state.getValue(SHAPE)) {
				case NORTH_SOUTH:
				return state.setValue(SHAPE, RailShape.EAST_WEST);
				case EAST_WEST:
				return state.setValue(SHAPE, RailShape.NORTH_SOUTH);
				case ASCENDING_EAST:
				return state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
				case ASCENDING_WEST:
				return state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
				case ASCENDING_NORTH:
				return state.setValue(SHAPE, RailShape.ASCENDING_WEST);
				case ASCENDING_SOUTH:
				return state.setValue(SHAPE, RailShape.ASCENDING_EAST);
				case SOUTH_EAST:
				return state.setValue(SHAPE, RailShape.NORTH_EAST);
				case SOUTH_WEST:
				return state.setValue(SHAPE, RailShape.SOUTH_EAST);
				case NORTH_WEST:
				return state.setValue(SHAPE, RailShape.SOUTH_WEST);
				case NORTH_EAST:
				return state.setValue(SHAPE, RailShape.NORTH_WEST);
			}
			case CLOCKWISE_90:
			switch((RailShape)state.getValue(SHAPE)) {
				case NORTH_SOUTH:
				return state.setValue(SHAPE, RailShape.EAST_WEST);
				case EAST_WEST:
				return state.setValue(SHAPE, RailShape.NORTH_SOUTH);
				case ASCENDING_EAST:
				return state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
				case ASCENDING_WEST:
				return state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
				case ASCENDING_NORTH:
				return state.setValue(SHAPE, RailShape.ASCENDING_EAST);
				case ASCENDING_SOUTH:
				return state.setValue(SHAPE, RailShape.ASCENDING_WEST);
				case SOUTH_EAST:
				return state.setValue(SHAPE, RailShape.SOUTH_WEST);
				case SOUTH_WEST:
				return state.setValue(SHAPE, RailShape.NORTH_WEST);
				case NORTH_WEST:
				return state.setValue(SHAPE, RailShape.NORTH_EAST);
				case NORTH_EAST:
				return state.setValue(SHAPE, RailShape.SOUTH_EAST);
			}
			default:
			return state;
		}
	}
	
	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
	 */
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		RailShape railshape = state.getValue(SHAPE);
		switch(mirrorIn) {
			case LEFT_RIGHT:
			switch(railshape) {
				case ASCENDING_NORTH:
				return state.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
				case ASCENDING_SOUTH:
				return state.setValue(SHAPE, RailShape.ASCENDING_NORTH);
				case SOUTH_EAST:
				return state.setValue(SHAPE, RailShape.NORTH_EAST);
				case SOUTH_WEST:
				return state.setValue(SHAPE, RailShape.NORTH_WEST);
				case NORTH_WEST:
				return state.setValue(SHAPE, RailShape.SOUTH_WEST);
				case NORTH_EAST:
				return state.setValue(SHAPE, RailShape.SOUTH_EAST);
				default:
				return super.mirror(state, mirrorIn);
			}
			case FRONT_BACK:
			switch(railshape) {
				case ASCENDING_EAST:
				return state.setValue(SHAPE, RailShape.ASCENDING_WEST);
				case ASCENDING_WEST:
				return state.setValue(SHAPE, RailShape.ASCENDING_EAST);
				case ASCENDING_NORTH:
				case ASCENDING_SOUTH:
				default:
				break;
				case SOUTH_EAST:
				return state.setValue(SHAPE, RailShape.SOUTH_WEST);
				case SOUTH_WEST:
				return state.setValue(SHAPE, RailShape.SOUTH_EAST);
				case NORTH_WEST:
				return state.setValue(SHAPE, RailShape.NORTH_EAST);
				case NORTH_EAST:
				return state.setValue(SHAPE, RailShape.NORTH_WEST);
			}
		case NONE:
			break;
		default:
			break;
		}
	
		return super.mirror(state, mirrorIn);
	}

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(SHAPE, POWERED);
	}

}
