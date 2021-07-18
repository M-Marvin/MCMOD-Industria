package de.industria.blocks;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import de.industria.Industria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockCornerBlockBase extends Block implements IWaterLoggable {
   public static final DirectionProperty FACING = HorizontalBlock.FACING;
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
   public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape NWU_CORNER = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
   protected static final VoxelShape SWU_CORNER = Block.box(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
   protected static final VoxelShape NWD_CORNER = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
   protected static final VoxelShape SWD_CORNER = Block.box(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
   protected static final VoxelShape NEU_CORNER = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
   protected static final VoxelShape SEU_CORNER = Block.box(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
   protected static final VoxelShape NED_CORNER = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
   protected static final VoxelShape SED_CORNER = Block.box(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape[] SLAB_TOP_SHAPES = makeShapes(NWD_CORNER, NED_CORNER, SWD_CORNER, SED_CORNER);
   protected static final VoxelShape[] SLAB_BOTTOM_SHAPES = makeShapes(NWU_CORNER, NEU_CORNER, SWU_CORNER, SEU_CORNER);
   private static final int[] SHAPE_BY_STATE = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};
   private final Block modelBlock;
   private final BlockState modelState;

   private static VoxelShape[] makeShapes(VoxelShape nwCorner, VoxelShape neCorner, VoxelShape swCorner, VoxelShape seCorner) {
      return IntStream.range(0, 16).mapToObj((p_199780_5_) -> {
         return combineShapes(p_199780_5_, nwCorner, neCorner, swCorner, seCorner);
      }).toArray((p_199778_0_) -> {
         return new VoxelShape[p_199778_0_];
      });
   }

   /**
    * combines the shapes according to the mode set in the bitfield
    */
   private static VoxelShape combineShapes(int bitfield, VoxelShape nwCorner, VoxelShape neCorner, VoxelShape swCorner, VoxelShape seCorner) {
      VoxelShape voxelshape = VoxelShapes.empty();
      if ((bitfield & 1) != 0) {
         voxelshape = VoxelShapes.or(voxelshape, nwCorner);
      }
      
      if ((bitfield & 2) != 0) {
         voxelshape = VoxelShapes.or(voxelshape, neCorner);
      }
      
      if ((bitfield & 4) != 0) {
         voxelshape = VoxelShapes.or(voxelshape, swCorner);
      }

      if ((bitfield & 8) != 0) {
         voxelshape = VoxelShapes.or(voxelshape, seCorner);
      }

      return voxelshape;
   }
   
   public BlockCornerBlockBase(String name, Supplier<BlockState> state, Properties properties) {
	   super(properties);
	   this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, Half.BOTTOM).setValue(SHAPE, StairsShape.STRAIGHT).setValue(WATERLOGGED, Boolean.valueOf(false)));
	   this.modelBlock = Blocks.AIR; // These are unused, fields are redirected
	   this.modelState = Blocks.AIR.defaultBlockState();
	   this.stateSupplier = state;
	   this.setRegistryName(new ResourceLocation(Industria.MODID, name));
   }

   public boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }
   
   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return (state.getValue(HALF) == Half.TOP ? SLAB_TOP_SHAPES : SLAB_BOTTOM_SHAPES)[SHAPE_BY_STATE[this.getShapeIndex(state)]];
   }

   private int getShapeIndex(BlockState state) {
      return state.getValue(SHAPE).ordinal() * 4 + state.getValue(FACING).get2DDataValue();
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      this.modelBlock.animateTick(stateIn, worldIn, pos, rand);
   }

   public void attack(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
      this.modelState.attack(worldIn, pos, player);
   }

   /**
    * Called after a player destroys this Block - the posiiton pos may no longer hold the state indicated.
    */
   public void destroy(IWorld worldIn, BlockPos pos, BlockState state) {
      this.modelBlock.destroy(worldIn, pos, state);
   }

   /**
    * Returns how much this block can resist explosions from the passed in entity.
    */
   @SuppressWarnings("deprecation")
   public float getExplosionResistance() {
      return this.modelBlock.getExplosionResistance();
   }

   @SuppressWarnings("deprecation")
   public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (!state.is(state.getBlock())) {
         this.modelState.neighborChanged(worldIn, pos, Blocks.AIR, pos, false);
         this.modelBlock.onPlace(this.modelState, worldIn, pos, oldState, false);
      }
   }

   public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         this.modelState.onRemove(worldIn, pos, newState, isMoving);
      }
   }

   /**
    * Called when the given entity walks on this Block
    */
   public void stepOn(World worldIn, BlockPos pos, Entity entityIn) {
      this.modelBlock.stepOn(worldIn, pos, entityIn);
   }

   /**
    * Returns whether or not this block is of a type that needs random ticking. Called for ref-counting purposes by
    * ExtendedBlockStorage in order to broadly cull a chunk from the random chunk update list for efficiency's sake.
    */
   public boolean isRandomlyTicking(BlockState state) {
      return this.modelBlock.isRandomlyTicking(state);
   }

   /**
    * Performs a random tick on a block.
    */
   @SuppressWarnings("deprecation")
   public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
      this.modelBlock.randomTick(state, worldIn, pos, random);
   }

   @SuppressWarnings("deprecation")
   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      this.modelBlock.tick(state, worldIn, pos, rand);
   }

   public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      return this.modelState.use(worldIn, player, handIn, hit);
   }

   /**
    * Called when this Block is destroyed by an Explosion
    */
   public void wasExploded(World worldIn, BlockPos pos, Explosion explosionIn) {
      this.modelBlock.wasExploded(worldIn, pos, explosionIn);
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      Direction direction = context.getClickedFace();
      BlockPos blockpos = context.getClickedPos();
      FluidState fluidstate = context.getLevel().getFluidState(blockpos);
      BlockState blockstate = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HALF, direction != Direction.DOWN && (direction == Direction.UP || !(context.getClickLocation().y - (double)blockpos.getY() > 0.5D)) ? Half.BOTTOM : Half.TOP).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
      return blockstate.setValue(SHAPE, getShapeProperty(blockstate, context.getLevel(), blockpos));
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   @SuppressWarnings("deprecation")
public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.getValue(WATERLOGGED)) {
         worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
      }

      return facing.getAxis().isHorizontal() ? stateIn.setValue(SHAPE, getShapeProperty(stateIn, worldIn, currentPos)) : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   /**
    * Returns a stair shape property based on the surrounding stairs from the given blockstate and position
    */
   private static StairsShape getShapeProperty(BlockState state, IBlockReader worldIn, BlockPos pos) {
      Direction direction = state.getValue(FACING);
      BlockState blockstate = worldIn.getBlockState(pos.relative(direction));
      if (isBlockCorner(blockstate) && state.getValue(HALF) == blockstate.getValue(HALF)) {
         Direction direction1 = blockstate.getValue(FACING);
         if (direction1.getAxis() != state.getValue(FACING).getAxis() && isDifferentStairs(state, worldIn, pos, direction1.getOpposite())) {
            if (direction1 == direction.getCounterClockWise()) {
               return StairsShape.OUTER_LEFT;
            }

            return StairsShape.OUTER_RIGHT;
         }
      }

      BlockState blockstate1 = worldIn.getBlockState(pos.relative(direction.getOpposite()));
      if (isBlockCorner(blockstate1) && state.getValue(HALF) == blockstate1.getValue(HALF)) {
         Direction direction2 = blockstate1.getValue(FACING);
         if (direction2.getAxis() != state.getValue(FACING).getAxis() && isDifferentStairs(state, worldIn, pos, direction2)) {
            if (direction2 == direction.getCounterClockWise()) {
               return StairsShape.INNER_LEFT;
            }

            return StairsShape.INNER_RIGHT;
         }
      }

      return StairsShape.STRAIGHT;
   }

   private static boolean isDifferentStairs(BlockState state, IBlockReader worldIn, BlockPos pos, Direction face) {
      BlockState blockstate = worldIn.getBlockState(pos.relative(face));
      return !isBlockCorner(blockstate) || blockstate.getValue(FACING) != state.getValue(FACING) || blockstate.getValue(HALF) != state.getValue(HALF);
   }

   public static boolean isBlockCorner(BlockState state) {
      return state.getBlock() instanceof BlockCornerBlockBase;
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   @SuppressWarnings("incomplete-switch")
public BlockState mirror(BlockState state, Mirror mirrorIn) {
      Direction direction = state.getValue(FACING);
      StairsShape stairsshape = state.getValue(SHAPE);
      switch(mirrorIn) {
      case LEFT_RIGHT:
         if (direction.getAxis() == Direction.Axis.Z) {
            switch(stairsshape) {
            case INNER_LEFT:
               return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
            case INNER_RIGHT:
               return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
            case OUTER_LEFT:
               return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
            default:
               return state.rotate(Rotation.CLOCKWISE_180);
            }
         }
         break;
      case FRONT_BACK:
         if (direction.getAxis() == Direction.Axis.X) {
            switch(stairsshape) {
            case INNER_LEFT:
               return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
            case INNER_RIGHT:
               return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
            case OUTER_LEFT:
               return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
            case OUTER_RIGHT:
               return state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
            case STRAIGHT:
               return state.rotate(Rotation.CLOCKWISE_180);
            }
         }
      }

      return super.mirror(state, mirrorIn);
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING, HALF, SHAPE, WATERLOGGED);
   }

   @SuppressWarnings("deprecation")
public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }

   // Forge Start
   private final java.util.function.Supplier<BlockState> stateSupplier;
   @SuppressWarnings("unused")
   private Block getModelBlock() {
       return getModelState().getBlock();
   }
   private BlockState getModelState() {
       return stateSupplier.get();
   }
   // Forge end
}