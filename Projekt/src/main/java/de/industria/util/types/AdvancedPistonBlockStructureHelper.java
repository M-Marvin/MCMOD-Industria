package de.industria.util.types;

import java.util.List;

import com.google.common.collect.Lists;

import de.industria.blocks.BlockRAdvancedPiston;
import de.industria.util.blockfeatures.IBAdvancedStickyBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AdvancedPistonBlockStructureHelper {
   private final World world;
   private final BlockPos pistonPos;
   private final boolean extending;
   private final BlockPos blockToMove;
   private final Direction moveDirection;
   /** This is a List<BlockPos> of all blocks that will be moved by the piston. */
   private final List<BlockPos> toMove = Lists.newArrayList();
   /** This is a List<BlockPos> of blocks that will be destroyed when a piston attempts to move them. */
   private final List<BlockPos> toDestroy = Lists.newArrayList();
   private final Direction facing;
   private int maxPushBlocks;
   private boolean doIgnorPushBeahavior;
   
   /** Constructor for AdvancedStruktures.
    * Needs no Piston Position and detects all Blocks, ignore PushRaction (except "BLOCK")
    * 
    * @param worldIn World of the Structure
    * @param posIn Begin Position for the Structure Detection
    * @param pushDirection Push Direction, to detect if not connected Blocks are pushed too
    * @param maxPushBlocks Maximum Blocks who can Pushed/Detected
    */
   public AdvancedPistonBlockStructureHelper(World worldIn, BlockPos posIn, Direction pushDirection, int maxPushBlocks) {
	   this.world = worldIn;
	   this.pistonPos = BlockPos.ZERO;
	   this.facing = pushDirection;
	   this.extending = true;
	   this.moveDirection = pushDirection;
	   this.blockToMove = posIn;
	   this.maxPushBlocks = maxPushBlocks;
	   this.doIgnorPushBeahavior = true;
   }
   
   /** Constructur for Pistons.
    * Piston Position is calculated from Strukture Position and Piston Facing
    * 
    * @param worldIn World of the Structure
    * @param posIn Begin Position for the Structure Detection
    * @param pistonFacing Piston Facing (and Push Direction)
    * @param extending If the Piston is Extending (place the Piston-Head or not)
    * @param maxPushBlocks Maximum Blocks who can Pushed/Detected
    */
   public AdvancedPistonBlockStructureHelper(World worldIn, BlockPos posIn, Direction pistonFacing, boolean extending, int maxPushBlocks) {
      this.world = worldIn;
      this.pistonPos = posIn;
      this.facing = pistonFacing;
      this.extending = extending;
      if (extending) {
         this.moveDirection = pistonFacing;
         this.blockToMove = posIn.relative(pistonFacing);
      } else {
         this.moveDirection = pistonFacing.getOpposite();
         this.blockToMove = posIn.relative(pistonFacing, 2);
      }
      this.maxPushBlocks = maxPushBlocks;
      this.doIgnorPushBeahavior = false;
   }
   
   /** Constructur for non-linear Pistons. (Piston Position != Position of the Structure)
    * 
    * @param worldIn World of the Structure
    * @param posIn Begin Position for the Structure Detection
    * @param pushDirection Push Direction, to detect if not connected Blocks are pushed too
    * @param maxPushBlocks Maximum Blocks who can Pushed/Detected
    * @param pistonPos Position of the Piston, to detect if the Structure Movement is Blocked by its Piston
    */
   public AdvancedPistonBlockStructureHelper(World worldIn, BlockPos posIn, Direction pushDirection, int maxPushBlocks, BlockPos pistonPos) {
	   this.world = worldIn;
	   this.pistonPos = pistonPos;
	   this.facing = pushDirection;
	   this.extending = true;
	   this.moveDirection = pushDirection;
	   this.blockToMove = posIn;
	   this.maxPushBlocks = maxPushBlocks;
	   this.doIgnorPushBeahavior = false;
   }
   
   public boolean canMove() {
      this.toMove.clear();
      this.toDestroy.clear();
      BlockState blockstate = this.world.getBlockState(this.blockToMove);
      
      if (!BlockRAdvancedPiston.canPush(blockstate, this.world, this.blockToMove, this.moveDirection, false, this.facing)) {
         if (this.extending && blockstate.getPistonPushReaction() == PushReaction.DESTROY) {
            this.toDestroy.add(this.blockToMove);
            return true;
         } else {
            return false;
         }
      } else if (!this.addBlockLine(this.blockToMove, this.moveDirection)) {
         return false;
      } else {
         for(int i = 0; i < this.toMove.size(); ++i) {
            BlockPos blockpos = this.toMove.get(i);
            if (this.world.getBlockState(blockpos).isStickyBlock() && !this.addBranchingBlocks(blockpos)) {
               return false;
            }
         }

         return true;
      }
   }

   @SuppressWarnings("deprecation")
   public boolean addBlockLine(BlockPos origin, Direction facingIn) {
      BlockState blockstate = this.world.getBlockState(origin);
      if (world.isEmptyBlock(origin) || this.toMove.contains(origin)) {
         return true;
      } else if (!BlockRAdvancedPiston.canPush(blockstate, this.world, origin, this.moveDirection, false, facingIn) && !(this.doIgnorPushBeahavior && !blockstate.isAir())) {
    	  return true;
      } if (origin.equals(this.pistonPos)) {
         return true;
      } else if (this.toMove.contains(origin)) {
         return true;
      } else {
         int i = 1;
         if (i + this.toMove.size() > maxPushBlocks) {
            return false;
         } else {
        	
            BlockState oldState;
            while(blockstate.isStickyBlock()) {
               BlockPos blockpos = origin.relative(this.moveDirection.getOpposite(), i);
               oldState = blockstate;
               blockstate = this.world.getBlockState(blockpos);
               if (blockstate.isAir(this.world, blockpos) || !oldState.canStickTo(blockstate) || !(BlockRAdvancedPiston.canPush(blockstate, this.world, blockpos, this.moveDirection, false, this.moveDirection.getOpposite()) || this.doIgnorPushBeahavior) || blockpos.equals(this.pistonPos)) {
            	   break;
               }
               
               ++i;
               if (i + this.toMove.size() > maxPushBlocks) {
                  return false;
               }
            }

            int l = 0;

            for(int i1 = i - 1; i1 >= 0; --i1) {
               
               BlockPos pos = origin.relative(this.moveDirection.getOpposite(), i1);
               BlockState state = this.world.getBlockState(pos);

               /** Check If Piston is in moveDirection **/
               for (int i2 = 0; i2 < maxPushBlocks; i2++) {
            	   BlockState checkState = this.world.getBlockState(pos.relative(moveDirection, i2));
            	   if (checkState.isAir() || checkState.getPistonPushReaction() == PushReaction.DESTROY) break;
            	   if (pos.relative(moveDirection, i2).equals(pistonPos)) return false;
               }
               
               if (state.getBlock() instanceof IBAdvancedStickyBlock && !this.toMove.contains(pos)) {
            	   
                   this.toMove.add(pos);
                   boolean flag = ((IBAdvancedStickyBlock) state.getBlock()).addBlocksToMove(this, pos, state, this.world);
                   if (!flag) return false;
                   
               } else {
            	   
                   this.toMove.add(origin.relative(this.moveDirection.getOpposite(), i1));
                   
               }
               
               ++l;
            }

            int j1 = 1;

            while(true) {
               BlockPos blockpos1 = origin.relative(this.moveDirection, j1);
               int j = this.toMove.indexOf(blockpos1);
               if (j > -1) {
                  this.reorderListAtCollision(l, j);

                  for(int k = 0; k <= j + l; ++k) {
                     
                	  if (this.toMove.size() > k) {
                		  
                		  BlockPos blockpos2 = this.toMove.get(k);
                          if (this.world.getBlockState(blockpos2).isStickyBlock() && !this.addBranchingBlocks(blockpos2)) {
                             return false;
                          }
                		  
                	  }
                	  
                  }

                  return true;
               }

               blockstate = this.world.getBlockState(blockpos1);
               if (blockstate.isAir(world, blockpos1)) {
                  return true;
               }

               if (!(BlockRAdvancedPiston.canPush(blockstate, this.world, blockpos1, this.moveDirection, true, this.moveDirection) || this.doIgnorPushBeahavior) || blockpos1.equals(this.pistonPos)) {
            	   return false;
               }

               if (blockstate.getPistonPushReaction() == PushReaction.DESTROY) {
                  this.toDestroy.add(blockpos1);
                  return true;
               }

               if (this.toMove.size() >= maxPushBlocks) {
                  return false;
               }
               

               /** Check If Piston is in moveDirection **/
               for (int i2 = 0; i2 < maxPushBlocks; i2++) {
            	   BlockState checkState = this.world.getBlockState(blockpos1.relative(moveDirection, i2));
            	   if (checkState.isAir() || checkState.getPistonPushReaction() == PushReaction.DESTROY) break;
            	   if (blockpos1.relative(moveDirection, i2).equals(pistonPos)) return false;
               }
               
               BlockState state = world.getBlockState(blockpos1);
               
               if (state.getBlock() instanceof IBAdvancedStickyBlock && !toMove.contains(blockpos1)) {

                   this.toMove.add(blockpos1);
                   boolean flag =  ((IBAdvancedStickyBlock) state.getBlock()).addBlocksToMove(this, blockpos1, state, this.world);
            	   if (!flag) return false;
                   
               } else {
            	   
            	   this.toMove.add(blockpos1);
            	   
               }
               
               ++l;
               ++j1;
            }
         }
      }
   }

   private void reorderListAtCollision(int p_177255_1_, int p_177255_2_) {
      List<BlockPos> list = Lists.newArrayList();
      List<BlockPos> list1 = Lists.newArrayList();
      List<BlockPos> list2 = Lists.newArrayList();
      list.addAll(this.toMove.subList(0, p_177255_2_));
      
      try {
    	  list1.addAll(this.toMove.subList(this.toMove.size() - p_177255_1_, this.toMove.size()));
          list2.addAll(this.toMove.subList(p_177255_2_, this.toMove.size() - p_177255_1_));
      } catch (IllegalArgumentException e) {
    	  // TODO wird sowiso neu gemacht
    	  //System.err.println("Error on move Block-Strukture at " + this.pistonPos + "!");
      };
      
      this.toMove.clear();
      this.toMove.addAll(list);
      this.toMove.addAll(list1);
      this.toMove.addAll(list2);
   }

   private boolean addBranchingBlocks(BlockPos fromPos) {
      BlockState blockstate = this.world.getBlockState(fromPos);

      for(Direction direction : Direction.values()) {
         if (direction.getAxis() != this.moveDirection.getAxis()) {
            BlockPos blockpos = fromPos.relative(direction);
            BlockState blockstate1 = this.world.getBlockState(blockpos);
            if (blockstate1.canStickTo(blockstate) && !this.addBlockLine(blockpos, direction)) {
               return false;
            }
         }
      }

      return true;
   }

   /**
    * Returns a List<BlockPos> of all the blocks that are being moved by the piston.
    */
   public List<BlockPos> getBlocksToMove() {
      return this.toMove;
   }

   /**
    * Returns an List<BlockPos> of all the blocks that are being destroyed by the piston.
    */
   public List<BlockPos> getBlocksToDestroy() {
      return this.toDestroy;
   }
   
   public Direction getMoveDirection() {
	return moveDirection;
   }
   
   public boolean isExtending() {
	return extending;
   }
   
   public BlockPos getPistonPos() {
	return pistonPos;
}
   
}