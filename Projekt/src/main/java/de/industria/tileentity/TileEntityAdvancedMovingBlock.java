package de.industria.tileentity;

import java.util.Iterator;
import java.util.List;

import de.industria.ModItems;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IPostMoveHandledTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AabbHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityAdvancedMovingBlock extends TileEntity implements ITickableTileEntity {
   private BlockState pistonState;
   private CompoundNBT pistonTileEntity;
   private Direction pistonFacing;
   /** if this piston is extending or not */
   private boolean extending;
   private boolean shouldHeadBeRendered;
   private static final ThreadLocal<Direction> MOVING_ENTITY = ThreadLocal.withInitial(() -> {
      return null;
   });
   private float progress;
   /** the progress in (de)extending */
   private float lastProgress;
   private long lastTicked;
   private int deathTicks;

   public TileEntityAdvancedMovingBlock() {
      super(ModTileEntityType.ADVANCED_PISTON);
   }

   public TileEntityAdvancedMovingBlock(BlockState pistonStateIn, CompoundNBT tileEntityIn, Direction pistonFacingIn, boolean extendingIn, boolean shouldHeadBeRenderedIn) {
      this();
      this.pistonState = pistonStateIn;
      this.pistonFacing = pistonFacingIn;
      this.extending = extendingIn;
      this.shouldHeadBeRendered = shouldHeadBeRenderedIn;
      this.pistonTileEntity = tileEntityIn;
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public CompoundNBT getUpdateTag() {
      return this.save(new CompoundNBT());
   }
   
   public CompoundNBT getPistonTileEntity() {
	return pistonTileEntity;
}
   
   /**
    * Returns true if a piston is extending
    */
   public boolean isExtending() {
      return this.extending;
   }

   public Direction getFacing() {
      return this.pistonFacing;
   }

   public boolean shouldPistonHeadBeRendered() {
      return this.shouldHeadBeRendered;
   }

   /**
    * Get interpolated progress value (between lastProgress and progress) given the fractional time between ticks as an
    * argument
    */
   public float getProgress(float ticks) {
      if (ticks > 1.0F) {
         ticks = 1.0F;
      }

      return MathHelper.lerp(ticks, this.lastProgress, this.progress);
   }

   @OnlyIn(Dist.CLIENT)
   public float getOffsetX(float ticks) {
      return (float)this.pistonFacing.getStepX() * this.getExtendedProgress(this.getProgress(ticks));
   }

   @OnlyIn(Dist.CLIENT)
   public float getOffsetY(float ticks) {
      return (float)this.pistonFacing.getStepY() * this.getExtendedProgress(this.getProgress(ticks));
   }

   @OnlyIn(Dist.CLIENT)
   public float getOffsetZ(float ticks) {
      return (float)this.pistonFacing.getStepZ() * this.getExtendedProgress(this.getProgress(ticks));
   }

   private float getExtendedProgress(float p_184320_1_) {
      return this.extending ? p_184320_1_ - 1.0F : 1.0F - p_184320_1_;
   }

   private BlockState getCollisionRelatedBlockState() {
      return !this.isExtending() && this.shouldPistonHeadBeRendered() && this.pistonState.getBlock() instanceof PistonBlock ? Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.SHORT, Boolean.valueOf(this.progress > 0.25F)).setValue(PistonHeadBlock.TYPE, this.pistonState.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT).setValue(PistonHeadBlock.FACING, this.pistonState.getValue(PistonBlock.FACING)) : this.pistonState;
   }

   private void moveCollidedEntities(float p_184322_1_) {
      Direction direction = this.getMotionDirection();
      double d0 = (double)(p_184322_1_ - this.progress);
      VoxelShape voxelshape = this.getCollisionRelatedBlockState().getBlockSupportShape(this.level, this.getBlockPos());
      if (!voxelshape.isEmpty()) {
         AxisAlignedBB axisalignedbb = this.moveByPositionAndProgress(voxelshape.bounds());
         List<Entity> list = this.level.getEntities((Entity)null, AabbHelper.getMovementArea(axisalignedbb, direction, d0).minmax(axisalignedbb));
         if (!list.isEmpty()) {
            List<AxisAlignedBB> list1 = voxelshape.toAabbs();
            boolean flag = this.pistonState.isSlimeBlock(); //TODO: is this patch really needed the logic of the original seems sound revisit later
            @SuppressWarnings("rawtypes")
			Iterator iterator = list.iterator();

            while(true) {
               Entity entity;
               while(true) {
                  if (!iterator.hasNext()) {
                     return;
                  }

                  entity = (Entity)iterator.next();
                  if (entity.getPistonPushReaction() != PushReaction.IGNORE) {
                     if (!flag) {
                        break;
                     }

                     if (!(entity instanceof ServerPlayerEntity)) {
                        Vector3d vector3d = entity.getDeltaMovement();
                        double d1 = vector3d.x;
                        double d2 = vector3d.y;
                        double d3 = vector3d.z;
                        switch(direction.getAxis()) {
                        case X:
                           d1 = (double)direction.getStepX();
                           break;
                        case Y:
                           d2 = (double)direction.getStepY();
                           break;
                        case Z:
                           d3 = (double)direction.getStepZ();
                        }

                        entity.setDeltaMovement(d1, d2, d3);
                        break;
                     }
                  }
               }

               double d4 = 0.0D;

               for(AxisAlignedBB axisalignedbb2 : list1) {
                  AxisAlignedBB axisalignedbb1 = AabbHelper.getMovementArea(this.moveByPositionAndProgress(axisalignedbb2), direction, d0);
                  AxisAlignedBB axisalignedbb3 = entity.getBoundingBox();
                  if (axisalignedbb1.intersects(axisalignedbb3)) {
                     d4 = Math.max(d4, getMovement(axisalignedbb1, direction, axisalignedbb3));
                     if (d4 >= d0) {
                        break;
                     }
                  }
               }

               if (!(d4 <= 0.0D)) {
                  d4 = Math.min(d4, d0) + 0.01D;
                  moveEntityByPiston(direction, entity, d4, direction);
                  if (!this.extending && this.shouldHeadBeRendered) {
                     this.fixEntityWithinPistonBase(entity, direction, d0);
                  }
               }
            }
         }
      }
   }

   private static void moveEntityByPiston(Direction p_227022_0_, Entity p_227022_1_, double p_227022_2_, Direction p_227022_4_) {
      MOVING_ENTITY.set(p_227022_0_);
      p_227022_1_.move(MoverType.PISTON, new Vector3d(p_227022_2_ * (double)p_227022_4_.getStepX(), p_227022_2_ * (double)p_227022_4_.getStepY(), p_227022_2_ * (double)p_227022_4_.getStepZ()));
      MOVING_ENTITY.set((Direction)null);
   }

   private void moveStuckEntities(float p_227024_1_) {
      if (this.isStickyForEntities()) {
         Direction direction = this.getMotionDirection();
         if (direction.getAxis().isHorizontal()) {
            double d0 = this.pistonState.getBlockSupportShape(this.level, this.worldPosition).max(Direction.Axis.Y);
            AxisAlignedBB axisalignedbb = this.moveByPositionAndProgress(new AxisAlignedBB(0.0D, d0, 0.0D, 1.0D, 1.5000000999999998D, 1.0D));
            double d1 = (double)(p_227024_1_ - this.progress);

            for(Entity entity : this.level.getEntities((Entity)null, axisalignedbb, (p_227023_1_) -> {
               return matchesStickyCritera(axisalignedbb, p_227023_1_);
            })) {
               moveEntityByPiston(direction, entity, d1, direction);
            }

         }
      }
   }

   private static boolean matchesStickyCritera(AxisAlignedBB bounds, Entity entity) {
      return entity.getPistonPushReaction() == PushReaction.NORMAL && entity.isPushable() && entity.getX() >= bounds.minX && entity.getX() <= bounds.maxX && entity.getZ() >= bounds.minZ && entity.getZ() <= bounds.maxZ;
   }

   private boolean isStickyForEntities() {
      return this.pistonState.is(Blocks.HONEY_BLOCK);
   }

   public Direction getMotionDirection() {
      return this.extending ? this.pistonFacing : this.pistonFacing.getOpposite();
   }

   private static double getMovement(AxisAlignedBB p_190612_0_, Direction p_190612_1_, AxisAlignedBB facing) {
      switch(p_190612_1_) {
      case EAST:
         return p_190612_0_.maxX - facing.minX;
      case WEST:
         return facing.maxX - p_190612_0_.minX;
      case UP:
      default:
         return p_190612_0_.maxY - facing.minY;
      case DOWN:
         return facing.maxY - p_190612_0_.minY;
      case SOUTH:
         return p_190612_0_.maxZ - facing.minZ;
      case NORTH:
         return facing.maxZ - p_190612_0_.minZ;
      }
   }

   private AxisAlignedBB moveByPositionAndProgress(AxisAlignedBB p_190607_1_) {
      double d0 = (double)this.getExtendedProgress(this.progress);
      return p_190607_1_.move((double)this.worldPosition.getX() + d0 * (double)this.pistonFacing.getStepX(), (double)this.worldPosition.getY() + d0 * (double)this.pistonFacing.getStepY(), (double)this.worldPosition.getZ() + d0 * (double)this.pistonFacing.getStepZ());
   }

   private void fixEntityWithinPistonBase(Entity p_190605_1_, Direction p_190605_2_, double p_190605_3_) {
      AxisAlignedBB axisalignedbb = p_190605_1_.getBoundingBox();
      AxisAlignedBB axisalignedbb1 = VoxelShapes.block().bounds().move(this.worldPosition);
      if (axisalignedbb.intersects(axisalignedbb1)) {
         Direction direction = p_190605_2_.getOpposite();
         double d0 = getMovement(axisalignedbb1, direction, axisalignedbb) + 0.01D;
         double d1 = getMovement(axisalignedbb1, direction, axisalignedbb.intersect(axisalignedbb1)) + 0.01D;
         if (Math.abs(d0 - d1) < 0.01D) {
            d0 = Math.min(d0, p_190605_3_) + 0.01D;
            moveEntityByPiston(p_190605_2_, p_190605_1_, d0, direction);
         }
      }

   }

   public BlockState getPistonState() {
      return this.pistonState;
   }

   /**
    * removes a piston's tile entity (and if the piston is moving, stops it)
    */
   public void clearPistonTileEntity() {
      if (this.level != null && (this.lastProgress < 1.0F || this.level.isClientSide)) {
         this.progress = 1.0F;
         this.lastProgress = this.progress;
         this.level.removeBlockEntity(this.worldPosition);
         this.setRemoved();
         if (this.level.getBlockState(this.worldPosition).is(ModItems.advanced_moving_block)) {
            BlockState blockstate;
            if (this.shouldHeadBeRendered) {
               blockstate = Blocks.AIR.defaultBlockState();
            } else {
               blockstate = Block.updateFromNeighbourShapes(this.pistonState, this.level, this.worldPosition);
            }

            this.level.setBlock(this.worldPosition, blockstate, 3);
            if (this.pistonState.hasTileEntity() && this.pistonTileEntity != null) {
            	TileEntity te = this.level.getBlockEntity(this.worldPosition);
          	  	te.load(this.pistonState, this.pistonTileEntity);
          	  	te.setPosition(this.worldPosition);
          	  	if (te instanceof IPostMoveHandledTE) {
          	  		((IPostMoveHandledTE) te).handlePostMove2(this.worldPosition, this.extending ? this.pistonFacing : this.pistonFacing.getOpposite(), 1, false);
          	  	}
            }
            this.level.neighborChanged(this.worldPosition, blockstate.getBlock(), this.worldPosition);
         }
      }

   }

   @SuppressWarnings("deprecation")
   public void tick() {
      this.lastTicked = this.level.getGameTime();
      this.lastProgress = this.progress;
      if (this.lastProgress >= 1.0F) {
         if (this.level.isClientSide && this.deathTicks < 5) {
            ++this.deathTicks;
         } else {
        	
            if (this.pistonState != null && this.level.getBlockState(this.worldPosition).is(ModItems.advanced_moving_block)) {
            	
               BlockState blockstate = this.pistonState;
               if (blockstate.isAir()) {
                  this.level.setBlock(this.worldPosition, this.pistonState, 84);
                  Block.updateOrDestroy(this.pistonState, blockstate, this.level, this.worldPosition, 3);
                  if (this.pistonState.hasTileEntity() && this.pistonTileEntity != null) {
                  	TileEntity te = this.level.getBlockEntity(this.worldPosition);
              	  	te.load(this.pistonState, this.pistonTileEntity);
              	  	te.setPosition(this.worldPosition);
              	  	if (te instanceof IPostMoveHandledTE) {
            	  		((IPostMoveHandledTE) te).handlePostMove(this.worldPosition, this.extending ? this.pistonFacing : this.pistonFacing.getOpposite(), 1, false);
            	  	}
                  } else {
                	  this.level.removeBlockEntity(this.worldPosition);
                      this.setRemoved();
                  }
               } else {
                  if (blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && blockstate.getValue(BlockStateProperties.WATERLOGGED)) {
                     blockstate = blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false));
                  }
                  
                  
                  if (blockstate.canSurvive(level, this.worldPosition)) {
                	  
                	  this.level.setBlock(this.worldPosition, blockstate, 67);
                      if (this.pistonState.hasTileEntity() && this.pistonTileEntity != null) {
                    	TileEntity te = this.pistonState.createTileEntity(this.level);
                    	te.load(blockstate, this.pistonTileEntity);
                    	te.setPosition(this.worldPosition);
                  	  	this.level.setBlockEntity(this.worldPosition, te);
                  	  	te.clearRemoved();
                  	  	if (te instanceof IPostMoveHandledTE) {
                	  		((IPostMoveHandledTE) te).handlePostMove(this.worldPosition, this.extending ? this.pistonFacing : this.pistonFacing.getOpposite(), 1, false);
                	  	}
                      } else {
                    	  this.level.removeBlockEntity(this.worldPosition);
                          this.setRemoved();
                      }
                      this.level.updateNeighborsAt(worldPosition, blockstate.getBlock());
                      this.level.neighborChanged(this.worldPosition, blockstate.getBlock(), this.worldPosition);
                	  
                  }
                  
               }
            }

         }
      } else {
         float f = this.progress + 0.5F;
         this.moveCollidedEntities(f);
         this.moveStuckEntities(f);
         this.progress = f;
         if (this.progress >= 1.0F) {
            this.progress = 1.0F;
         }

      }
   }

   public void load(BlockState p_230337_1_, CompoundNBT p_230337_2_) {
      super.load(p_230337_1_, p_230337_2_);
      this.pistonState = NBTUtil.readBlockState(p_230337_2_.getCompound("blockState"));
      if (this.pistonState.hasTileEntity()) this.pistonTileEntity = p_230337_2_.getCompound("tileData"); 
      this.pistonFacing = Direction.from3DDataValue(p_230337_2_.getInt("facing"));
      this.progress = p_230337_2_.getFloat("progress");
      this.lastProgress = this.progress;
      this.extending = p_230337_2_.getBoolean("extending");
      this.shouldHeadBeRendered = p_230337_2_.getBoolean("source");
   }
   
   public CompoundNBT save(CompoundNBT compound) {
      super.save(compound);
      compound.put("blockState", NBTUtil.writeBlockState(this.pistonState));
      if (this.pistonTileEntity != null) compound.put("tileData", this.pistonTileEntity);
      compound.putInt("facing", this.pistonFacing.get3DDataValue());
      compound.putFloat("progress", this.lastProgress);
      compound.putBoolean("extending", this.extending);
      compound.putBoolean("source", this.shouldHeadBeRendered);
      return compound;
   }

   public VoxelShape getCollisionShape(IBlockReader p_195508_1_, BlockPos p_195508_2_) {
      VoxelShape voxelshape;
      if (!this.extending && this.shouldHeadBeRendered) {
         voxelshape = this.pistonState.setValue(PistonBlock.EXTENDED, Boolean.valueOf(true)).getBlockSupportShape(p_195508_1_, p_195508_2_);
      } else {
         voxelshape = VoxelShapes.empty();
      }

      Direction direction = MOVING_ENTITY.get();
      if ((double)this.progress < 1.0D && direction == this.getMotionDirection()) {
         return voxelshape;
      } else {
         BlockState blockstate;
         if (this.shouldPistonHeadBeRendered()) {
            blockstate = Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, this.pistonFacing).setValue(PistonHeadBlock.SHORT, Boolean.valueOf(this.extending != 1.0F - this.progress < 0.25F));
         } else {
            blockstate = this.pistonState;
         }

         float f = this.getExtendedProgress(this.progress);
         double d0 = (double)((float)this.pistonFacing.getStepX() * f);
         double d1 = (double)((float)this.pistonFacing.getStepY() * f);
         double d2 = (double)((float)this.pistonFacing.getStepZ() * f);
         return VoxelShapes.or(voxelshape, blockstate.getBlockSupportShape(p_195508_1_, p_195508_2_).move(d0, d1, d2));
      }
   }

   public long getLastTicked() {
      return this.lastTicked;
   }

   @OnlyIn(Dist.CLIENT)
   public double getViewDistance() {
      return 68.0D;
   }
}