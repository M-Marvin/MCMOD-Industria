package de.industria.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.industria.Industria;
import de.industria.typeregistys.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockRAdvancedPistonHead extends DirectionalBlock {
   public static final EnumProperty<PistonType> TYPE = BlockStateProperties.PISTON_TYPE;
   public static final BooleanProperty SHORT = BlockStateProperties.SHORT;
   protected static final VoxelShape PISTON_EXTENSION_EAST_AABB = Block.box(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_EXTENSION_WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_EXTENSION_SOUTH_AABB = Block.box(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_EXTENSION_NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
   protected static final VoxelShape PISTON_EXTENSION_UP_AABB = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_EXTENSION_DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
   protected static final VoxelShape UP_ARM_AABB = Block.box(6.0D, -4.0D, 6.0D, 10.0D, 12.0D, 10.0D);
   protected static final VoxelShape DOWN_ARM_AABB = Block.box(6.0D, 4.0D, 6.0D, 10.0D, 20.0D, 10.0D);
   protected static final VoxelShape SOUTH_ARM_AABB = Block.box(6.0D, 6.0D, -4.0D, 10.0D, 10.0D, 12.0D);
   protected static final VoxelShape NORTH_ARM_AABB = Block.box(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 20.0D);
   protected static final VoxelShape EAST_ARM_AABB = Block.box(-4.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   protected static final VoxelShape WEST_ARM_AABB = Block.box(4.0D, 6.0D, 6.0D, 20.0D, 10.0D, 10.0D);
   protected static final VoxelShape SHORT_UP_ARM_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
   protected static final VoxelShape SHORT_DOWN_ARM_AABB = Block.box(6.0D, 4.0D, 6.0D, 10.0D, 16.0D, 10.0D);
   protected static final VoxelShape SHORT_SOUTH_ARM_AABB = Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 12.0D);
   protected static final VoxelShape SHORT_NORTH_ARM_AABB = Block.box(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 16.0D);
   protected static final VoxelShape SHORT_EAST_ARM_AABB = Block.box(0.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   protected static final VoxelShape SHORT_WEST_ARM_AABB = Block.box(4.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
   private static final VoxelShape[] SHAPES_SHORT = makeShapes(true);
   private static final VoxelShape[] SHAPES_LONG = makeShapes(false);

   private static VoxelShape[] makeShapes(boolean p_242694_0_) {
      return Arrays.stream(Direction.values()).map((p_242695_1_) -> {
         return calculateShape(p_242695_1_, p_242694_0_);
      }).toArray((p_242696_0_) -> {
         return new VoxelShape[p_242696_0_];
      });
   }
   
	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(Item.byBlock(this), 1));
		return drops;
	}
	
   private static VoxelShape calculateShape(Direction p_242693_0_, boolean p_242693_1_) {
      switch(p_242693_0_) {
      case DOWN:
      default:
         return VoxelShapes.or(PISTON_EXTENSION_DOWN_AABB, p_242693_1_ ? SHORT_DOWN_ARM_AABB : DOWN_ARM_AABB);
      case UP:
         return VoxelShapes.or(PISTON_EXTENSION_UP_AABB, p_242693_1_ ? SHORT_UP_ARM_AABB : UP_ARM_AABB);
      case NORTH:
         return VoxelShapes.or(PISTON_EXTENSION_NORTH_AABB, p_242693_1_ ? SHORT_NORTH_ARM_AABB : NORTH_ARM_AABB);
      case SOUTH:
         return VoxelShapes.or(PISTON_EXTENSION_SOUTH_AABB, p_242693_1_ ? SHORT_SOUTH_ARM_AABB : SOUTH_ARM_AABB);
      case WEST:
         return VoxelShapes.or(PISTON_EXTENSION_WEST_AABB, p_242693_1_ ? SHORT_WEST_ARM_AABB : WEST_ARM_AABB);
      case EAST:
         return VoxelShapes.or(PISTON_EXTENSION_EAST_AABB, p_242693_1_ ? SHORT_EAST_ARM_AABB : EAST_ARM_AABB);
      }
   }

   public BlockRAdvancedPistonHead(String name) {
      super(Properties.of(Material.STONE).strength(1.5F, 0.5F).sound(SoundType.STONE).noCollission());
      this.setRegistryName(Industria.MODID, name);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, PistonType.DEFAULT).setValue(SHORT, Boolean.valueOf(false)));
   }
   
   @Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.BLOCK;
	}

   public boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return (state.getValue(SHORT) ? SHAPES_SHORT : SHAPES_LONG)[state.getValue(FACING).ordinal()];
   }

   private boolean isFittingBase(BlockState p_235682_1_, BlockState p_235682_2_) {
      Block block = p_235682_1_.getValue(TYPE) == PistonType.DEFAULT ? ModItems.advanced_piston : ModItems.advanced_sticky_piston;
      return p_235682_2_.is(block) && p_235682_2_.getValue(BlockRAdvancedPiston.EXTENDED) && p_235682_2_.getValue(FACING) == p_235682_1_.getValue(FACING);
   }
   
   /**
    * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
    * this block
    */
   public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
      if (!worldIn.isClientSide && player.abilities.instabuild) {
         BlockPos blockpos = pos.relative(state.getValue(FACING).getOpposite());
         if (this.isFittingBase(state, worldIn.getBlockState(blockpos))) {
            worldIn.destroyBlock(blockpos, false);
         }
      }

      super.playerWillDestroy(worldIn, pos, state, player);
   }

   @SuppressWarnings("deprecation")
public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         super.onRemove(state, worldIn, pos, newState, isMoving);
         BlockPos blockpos = pos.relative(state.getValue(FACING).getOpposite());
         if (this.isFittingBase(state, worldIn.getBlockState(blockpos))) {
            worldIn.destroyBlock(blockpos, true);
         }

      }
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   @SuppressWarnings("deprecation")
   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing.getOpposite() == stateIn.getValue(FACING) && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }
   
   public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
      BlockState blockstate = worldIn.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));
      return this.isFittingBase(state, blockstate) || blockstate.is(ModItems.advanced_moving_block) && blockstate.getValue(FACING) == state.getValue(FACING);
   }
   
   public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (state.canSurvive(worldIn, pos)) {
         BlockPos blockpos = pos.relative(state.getValue(FACING).getOpposite());
         worldIn.getBlockState(blockpos).neighborChanged(worldIn, blockpos, blockIn, fromPos, false);
      }

   }

   public ItemStack getCloneItemStack(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return new ItemStack(state.getValue(TYPE) == PistonType.STICKY ? ModItems.advanced_sticky_piston : ModItems.advanced_piston);
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
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING, TYPE, SHORT);
   }

   public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}