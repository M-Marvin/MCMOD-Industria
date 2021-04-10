package de.redtec.blocks;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import de.redtec.RedTec;
import de.redtec.tileentity.TileEntityAdvancedMovingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockAdvancedMovingBlock extends ContainerBlock {
		
   public static final DirectionProperty FACING = PistonHeadBlock.FACING;
   public static final EnumProperty<PistonType> TYPE = PistonHeadBlock.TYPE;

   public BlockAdvancedMovingBlock(String name) {
      super(Properties.create(Material.PISTON).doesNotBlockMovement());
      this.setRegistryName(RedTec.MODID, name);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(TYPE, PistonType.DEFAULT));
   }
   
   @Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, TYPE);
		super.fillStateContainer(builder);
	}
   
   @Nullable
   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return null;
   }

   public static TileEntity createTilePiston(BlockState state, CompoundNBT tileEntity, Direction direction, boolean p_196343_2_, boolean p_196343_3_) {
      return new TileEntityAdvancedMovingBlock(state, tileEntity, direction, p_196343_2_, p_196343_3_);
   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.isIn(newState.getBlock())) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityAdvancedMovingBlock) {
            ((TileEntityAdvancedMovingBlock)tileentity).clearPistonTileEntity();
         }

      }
   }

   /**
    * Called after a player destroys this Block - the posiiton pos may no longer hold the state indicated.
    */
   public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
      BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());
      BlockState blockstate = worldIn.getBlockState(blockpos);
      if (blockstate.getBlock() instanceof BlockRAdvancedPiston && blockstate.get(BlockRAdvancedPiston.EXTENDED)) {
         worldIn.removeBlock(blockpos, false);
      }

   }
   
   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
         worldIn.removeBlock(pos, false);
         return ActionResultType.CONSUME;
      } else {
         return ActionResultType.PASS;
      }
   }

   public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
      TileEntityAdvancedMovingBlock TileEntityAdvancedMovingBlock = this.getTileEntity(builder.getWorld(), new BlockPos(builder.assertPresent(LootParameters.field_237457_g_)));
      return TileEntityAdvancedMovingBlock == null ? Collections.emptyList() : TileEntityAdvancedMovingBlock.getPistonState().getDrops(builder);
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return VoxelShapes.empty();
   }

   public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
	   TileEntityAdvancedMovingBlock TileEntityAdvancedMovingBlock = this.getTileEntity(worldIn, pos);
      return TileEntityAdvancedMovingBlock != null ? TileEntityAdvancedMovingBlock.getCollisionShape(worldIn, pos) : VoxelShapes.empty();
   }

   @Nullable
   private TileEntityAdvancedMovingBlock getTileEntity(IBlockReader p_220170_1_, BlockPos p_220170_2_) {
      TileEntity tileentity = p_220170_1_.getTileEntity(p_220170_2_);
      return tileentity instanceof TileEntityAdvancedMovingBlock ? (TileEntityAdvancedMovingBlock)tileentity : null;
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return ItemStack.EMPTY;
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }
   
   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
   
}