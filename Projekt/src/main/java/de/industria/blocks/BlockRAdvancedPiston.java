package de.industria.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.industria.Industria;
import de.industria.tileentity.TileEntityAdvancedMovingBlock;
import de.industria.typeregistys.ModItems;
import de.industria.util.types.AdvancedPistonBlockStructureHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.PistonTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockRAdvancedPiston extends DirectionalBlock {
   public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
   protected static final VoxelShape PISTON_BASE_EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_BASE_WEST_AABB = Block.box(4.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_BASE_SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 12.0D);
   protected static final VoxelShape PISTON_BASE_NORTH_AABB = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_BASE_UP_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
   protected static final VoxelShape PISTON_BASE_DOWN_AABB = Block.box(0.0D, 4.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   /** This piston is the sticky one? */
   private final boolean isSticky;
   private int maxPushBlocks;
   
   public BlockRAdvancedPiston(boolean sticky, String name) {
      super(Properties.of(Material.STONE).strength(1.5F, 0.5F).sound(SoundType.STONE));
      this.setRegistryName(Industria.MODID, name);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(EXTENDED, Boolean.valueOf(false)));
      this.isSticky = sticky;
      this.maxPushBlocks = 1000;
   }
   
	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder) {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(Item.byBlock(this), 1));
		return drops;
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      if (state.getValue(EXTENDED)) {
         switch((Direction)state.getValue(FACING)) {
         case DOWN:
            return PISTON_BASE_DOWN_AABB;
         case UP:
         default:
            return PISTON_BASE_UP_AABB;
         case NORTH:
            return PISTON_BASE_NORTH_AABB;
         case SOUTH:
            return PISTON_BASE_SOUTH_AABB;
         case WEST:
            return PISTON_BASE_WEST_AABB;
         case EAST:
            return PISTON_BASE_EAST_AABB;
         }
      } else {
         return VoxelShapes.block();
      }
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      if (!worldIn.isClientSide) {
         this.checkForMove(worldIn, pos, state);
      }

   }

   public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (!worldIn.isClientSide) {
         this.checkForMove(worldIn, pos, state);
      }

   }

   public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (!oldState.is(state.getBlock())) {
         if (!worldIn.isClientSide && worldIn.getBlockEntity(pos) == null) {
            this.checkForMove(worldIn, pos, state);
         }

      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(EXTENDED, Boolean.valueOf(false));
   }
   
   @Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return state.getValue(EXTENDED) ? PushReaction.BLOCK : PushReaction.NORMAL;
	}

   private void checkForMove(World worldIn, BlockPos pos, BlockState state) {
      Direction direction = state.getValue(FACING);
      boolean flag = this.shouldBeExtended(worldIn, pos, direction);
      if (flag && !state.getValue(EXTENDED)) {
         if ((new AdvancedPistonBlockStructureHelper(worldIn, pos, direction, true, this.maxPushBlocks)).canMove()) {
            worldIn.blockEvent(pos, this, 0, direction.get3DDataValue());
         }
      } else if (!flag && state.getValue(EXTENDED)) {
         BlockPos blockpos = pos.relative(direction, 2);
         BlockState blockstate = worldIn.getBlockState(blockpos);
         int i = 1;
         if (blockstate.is(ModItems.advanced_moving_block) && blockstate.getValue(FACING) == direction) {
            TileEntity tileentity = worldIn.getBlockEntity(blockpos);
            if (tileentity instanceof PistonTileEntity) {
               PistonTileEntity pistontileentity = (PistonTileEntity)tileentity;
               if (pistontileentity.isExtending() && (pistontileentity.getProgress(0.0F) < 0.5F || worldIn.getGameTime() == pistontileentity.getLastTicked() || ((ServerWorld)worldIn).isHandlingTick())) {
                  i = 2;
               }
            }
         }

         worldIn.blockEvent(pos, this, i, direction.get3DDataValue());
      }

   }

   private boolean shouldBeExtended(World worldIn, BlockPos pos, Direction facing) {
      for(Direction direction : Direction.values()) {
         if (direction != facing && worldIn.hasSignal(pos.relative(direction), direction)) {
            return true;
         }
      }

      if (worldIn.hasSignal(pos, Direction.DOWN)) {
         return true;
      } else {
         BlockPos blockpos = pos.above();

         for(Direction direction1 : Direction.values()) {
            if (direction1 != Direction.DOWN && worldIn.hasSignal(blockpos.relative(direction1), direction1)) {
               return true;
            }
         }

         return false;
      }
   }

   /**
    * Called on server when World#addBlockEvent is called. If server returns true, then also called on the client. On
    * the Server, this may perform additional changes to the world, like pistons replacing the block with an extended
    * base. On the client, the update may involve replacing tile entities or effects such as sounds or particles
    * @deprecated call via {@link IBlockState#onBlockEventReceived(World,BlockPos,int,int)} whenever possible.
    * Implementing/overriding is fine.
    */
   public boolean triggerEvent(BlockState state, World worldIn, BlockPos pos, int id, int param) {
      Direction direction = state.getValue(FACING);
      if (!worldIn.isClientSide) {
         boolean flag = this.shouldBeExtended(worldIn, pos, direction);
         if (flag && (id == 1 || id == 2)) {
            worldIn.setBlock(pos, state.setValue(EXTENDED, Boolean.valueOf(true)), 2);
            return false;
         }

         if (!flag && id == 0) {
            return false;
         }
      }

      if (id == 0) {
         if (net.minecraftforge.event.ForgeEventFactory.onPistonMovePre(worldIn, pos, direction, true)) return false;
         if (!this.doMove(worldIn, pos, direction, true)) {
            return false;
         }

         worldIn.setBlock(pos, state.setValue(EXTENDED, Boolean.valueOf(true)), 67);
         worldIn.playSound((PlayerEntity)null, pos, SoundEvents.PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.25F + 0.6F);
      } else if (id == 1 || id == 2) {
         if (net.minecraftforge.event.ForgeEventFactory.onPistonMovePre(worldIn, pos, direction, false)) return false;
         TileEntity tileentity1 = worldIn.getBlockEntity(pos.relative(direction));
         if (tileentity1 instanceof TileEntityAdvancedMovingBlock) {
            ((TileEntityAdvancedMovingBlock)tileentity1).clearPistonTileEntity();
         }

         BlockState blockstate = ModItems.advanced_moving_block.defaultBlockState().setValue(BlockAdvancedMovingBlock.FACING, direction).setValue(BlockAdvancedMovingBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
         worldIn.setBlock(pos, blockstate, 20);
         worldIn.setBlockEntity(pos, BlockAdvancedMovingBlock.createTilePiston(this.defaultBlockState().setValue(FACING, Direction.from3DDataValue(param & 7)), null, direction, false, true));
         worldIn.blockUpdated(pos, blockstate.getBlock());
         blockstate.updateNeighbourShapes(worldIn, pos, 2);
         if (this.isSticky) {
            BlockPos blockpos = pos.offset(direction.getStepX() * 2, direction.getStepY() * 2, direction.getStepZ() * 2);
            BlockState blockstate1 = worldIn.getBlockState(blockpos);
            boolean flag1 = false;
            if (blockstate1.is(ModItems.advanced_moving_block)) {
               TileEntity tileentity = worldIn.getBlockEntity(blockpos);
               if (tileentity instanceof PistonTileEntity) {
                  PistonTileEntity pistontileentity = (PistonTileEntity)tileentity;
                  if (pistontileentity.getDirection() == direction && pistontileentity.isExtending()) {
                     pistontileentity.finalTick();
                     flag1 = true;
                  }
               }
            }

            if (!flag1) {
               if (id != 1 || blockstate1.isAir() || !canPush(blockstate1, worldIn, blockpos, direction.getOpposite(), false, direction) || blockstate1.getPistonPushReaction() != PushReaction.NORMAL && !blockstate1.is(Blocks.PISTON) && !blockstate1.is(Blocks.STICKY_PISTON)) {
                  worldIn.removeBlock(pos.relative(direction), false);
               } else {
                  this.doMove(worldIn, pos, direction, false);
               }
            }
         } else {
            worldIn.removeBlock(pos.relative(direction), false);
         }

         worldIn.playSound((PlayerEntity)null, pos, SoundEvents.PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.15F + 0.6F);
      }

      net.minecraftforge.event.ForgeEventFactory.onPistonMovePost(worldIn, pos, direction, (id == 0));
      return true;
   }

   /**
    * Checks if the piston can push the given BlockState.
    */
   @SuppressWarnings({ "incomplete-switch", "deprecation" })
public static boolean canPush(BlockState blockStateIn, World worldIn, BlockPos pos, Direction facing, boolean destroyBlocks, Direction p_185646_5_) {
      if (pos.getY() >= 0 && pos.getY() <= worldIn.getMaxBuildHeight() - 1 && worldIn.getWorldBorder().isWithinBounds(pos)) {
         if (blockStateIn.isAir()) {
            return true;
         } else if (!blockStateIn.is(Blocks.OBSIDIAN) && !blockStateIn.is(Blocks.CRYING_OBSIDIAN)) {
        	if (facing == Direction.DOWN && pos.getY() == 0) {
               return false;
            } else if (facing == Direction.UP && pos.getY() == worldIn.getMaxBuildHeight() - 1) {
               return false;
            } else {
               if (!blockStateIn.is(Blocks.PISTON) && !blockStateIn.is(Blocks.STICKY_PISTON) && !blockStateIn.is(ModItems.advanced_piston) && !blockStateIn.is(ModItems.advanced_sticky_piston)) {
                  
                  switch(blockStateIn.getPistonPushReaction()) {
                  case BLOCK:
                     return false;
                  case DESTROY:
                     return destroyBlocks;
                  case PUSH_ONLY:
                     return facing == p_185646_5_;
                  }
               } else if (blockStateIn.getValue(EXTENDED)) {
                  return false;
               }
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @SuppressWarnings("deprecation")
   private boolean doMove(World worldIn, BlockPos pos, Direction directionIn, boolean extending) {
      BlockPos blockpos = pos.relative(directionIn);
      if (!extending && worldIn.getBlockState(blockpos).is(ModItems.advanced_piston_head)) {
         worldIn.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 20);
      }
      
      AdvancedPistonBlockStructureHelper pistonblockstructurehelper = new AdvancedPistonBlockStructureHelper(worldIn, pos, directionIn, extending, maxPushBlocks);
      if (!pistonblockstructurehelper.canMove()) {
         return false;
      } else {
         Map<BlockPos, BlockState> map = Maps.newHashMap();
         List<BlockPos> list = pistonblockstructurehelper.getBlocksToMove();
         List<BlockState> list1 = Lists.newArrayList();
         List<CompoundNBT> list12 = Lists.newArrayList();
         
         for(int i = 0; i < list.size(); ++i) {
            BlockPos blockpos1 = list.get(i);
            BlockState blockstate = worldIn.getBlockState(blockpos1);
            list1.add(blockstate);
            TileEntity tileEntity = worldIn.getBlockEntity(blockpos1);
            if (tileEntity != null && blockstate.getBlock() != ModItems.advanced_moving_block) {
            	list12.add(tileEntity.save(new CompoundNBT()));
                worldIn.removeBlockEntity(blockpos1);
            } else {
            	list12.add(null);
            }
            map.put(blockpos1, blockstate);
         }
         
         List<BlockPos> list2 = pistonblockstructurehelper.getBlocksToDestroy();
         BlockState[] ablockstate = new BlockState[list.size() + list2.size()];
         Direction direction = extending ? directionIn : directionIn.getOpposite();
         int j = 0;

         for(int k = list2.size() - 1; k >= 0; --k) {
            BlockPos blockpos2 = list2.get(k);
            BlockState blockstate1 = worldIn.getBlockState(blockpos2);
            TileEntity tileentity = blockstate1.hasTileEntity() ? worldIn.getBlockEntity(blockpos2) : null;
            dropResources(blockstate1, worldIn, blockpos2, tileentity);
            worldIn.setBlock(blockpos2, Blocks.AIR.defaultBlockState(), 18 );
            ablockstate[j++] = blockstate1;
         }
         
         for(int l = list.size() - 1; l >= 0; --l) {
            BlockPos blockpos3 = list.get(l);
            BlockState blockstate5 = worldIn.getBlockState(blockpos3);
            blockpos3 = blockpos3.relative(direction);
            map.remove(blockpos3);
            if (worldIn.hasChunkAt(blockpos3)) {
            	worldIn.setBlock(blockpos3, ModItems.advanced_moving_block.defaultBlockState().setValue(FACING, directionIn), 68 );
            	worldIn.removeBlockEntity(blockpos3);
                worldIn.setBlockEntity(blockpos3, BlockAdvancedMovingBlock.createTilePiston(list1.get(l), list12.get(l), directionIn, extending, false));
            }
            
            ablockstate[j++] = blockstate5;
         }

         if (extending) {
            PistonType pistontype = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState blockstate4 = ModItems.advanced_piston_head.defaultBlockState().setValue(BlockRAdvancedPistonHead.FACING, directionIn).setValue(BlockRAdvancedPistonHead.TYPE, pistontype);
            BlockState blockstate6 = ModItems.advanced_moving_block.defaultBlockState().setValue(BlockAdvancedMovingBlock.FACING, directionIn).setValue(BlockAdvancedMovingBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            map.remove(blockpos);
            worldIn.setBlock(blockpos, blockstate6, 68 );
            worldIn.setBlockEntity(blockpos, BlockAdvancedMovingBlock.createTilePiston(blockstate4, null, directionIn, true, true));
         }

         BlockState blockstate3 = Blocks.AIR.defaultBlockState();
         
         for(BlockPos blockpos4 : map.keySet()) {
            worldIn.setBlock(blockpos4, blockstate3, 82);
         }

         for(Entry<BlockPos, BlockState> entry : map.entrySet()) {
            BlockPos blockpos5 = entry.getKey();
            BlockState blockstate2 = entry.getValue();
            blockstate2.updateIndirectNeighbourShapes(worldIn, blockpos5, 2);
            blockstate3.updateNeighbourShapes(worldIn, blockpos5, 2);
            blockstate3.updateIndirectNeighbourShapes(worldIn, blockpos5, 2);
         }

         j = 0;

         for(int i1 = list2.size() - 1; i1 >= 0; --i1) {
            BlockState blockstate7 = ablockstate[j++];
            BlockPos blockpos6 = list2.get(i1);
            blockstate7.updateIndirectNeighbourShapes(worldIn, blockpos6, 2);
            worldIn.updateNeighborsAt(blockpos6, blockstate7.getBlock());
         }

         for(int j1 = list.size() - 1; j1 >= 0; --j1) {
            worldIn.updateNeighborsAt(list.get(j1), ablockstate[j++].getBlock());
         }

         if (extending) {
            worldIn.updateNeighborsAt(blockpos, ModItems.advanced_piston_head);
         }

         return true;
      }
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

   public BlockState rotate(BlockState state, net.minecraft.world.IWorld world, BlockPos pos, Rotation direction) {
       return state.getValue(EXTENDED) ? state : super.rotate(state, world, pos, direction);
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
      builder.add(FACING, EXTENDED);
   }

   public boolean useShapeForLightOcclusion(BlockState state) {
      return state.getValue(EXTENDED);
   }

   public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
   
}