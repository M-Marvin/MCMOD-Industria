package de.industria.blocks;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.industria.typeregistys.ModItems;
import de.industria.util.blockfeatures.IBAdvancedStickyBlock;
import de.industria.util.types.AdvancedPistonBlockStructureHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockRRailPiston extends BlockBase implements IBAdvancedStickyBlock {
	
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	
	private int maxPushBlocks;
	
	public BlockRRailPiston() {
		super("rail_piston", Material.STONE, 1.5F, 0.5F, SoundType.STONE, true);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
		this.maxPushBlocks = 1000;
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
		return updateState(state, context.getLevel(), context.getClickedPos(), false);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
		BlockState newState = updateState(state, worldIn, pos, true);
		worldIn.setBlock(pos, newState, 34);
		
	}
	
	public BlockState updateState(BlockState state, World world, BlockPos pos, boolean placed) {
		
		boolean power = world.hasNeighborSignal(pos);
		boolean powered = state.getValue(POWERED);
		
		if (power != powered) {
			state = state.setValue(POWERED, power);
			if (power && placed) this.onTrigger(state, world, pos);
		}
		
		return state;
		
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		
		if (state.getValue(FACING).getAxis() == Axis.X) {
			VoxelShape shape = Block.box(1, 1, 1, 15, 15, 15);
			
			return shape;
		} else {
			VoxelShape shape = Block.box(1, 1, 1, 15, 15, 15);
			
			return shape;
		}
		
	}
	
	public void onTrigger(BlockState state, World world, BlockPos pos) {

		world.blockEvent(pos, this, 0, 0);
		
	}
	
	@Override
	public boolean triggerEvent(BlockState state, World worldIn, BlockPos pos, int id, int param) {
		
		for (Direction d : Direction.values()) {
			
			BlockPos moveablePos = pos.relative(d);
			BlockState moveableState = worldIn.getBlockState(moveablePos);
			
			if (moveableState.getBlock() instanceof BlockRConectorBlock) {
				
				Direction moveDirection = getMoveDirectionForFace(d, state.getValue(FACING));
				AdvancedPistonBlockStructureHelper struktureHelper = new AdvancedPistonBlockStructureHelper(worldIn, moveablePos, moveDirection, this.maxPushBlocks, pos);
				
				if (struktureHelper.canMove()) {
					
					boolean flag = this.doMove(worldIn, pos, moveablePos, moveDirection, false);
					
					if (flag) {
						
						worldIn.playSound((PlayerEntity)null, pos, SoundEvents.PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, worldIn.random.nextFloat() * 0.25F + 0.6F);
						return true;
						
					}
					
				}
				
			}
			
		}
		
		return false;
		
	}
	
	@SuppressWarnings("deprecation")
   private boolean doMove(World worldIn, BlockPos pistonPos, BlockPos pos, Direction directionIn, boolean extendings) {
      AdvancedPistonBlockStructureHelper pistonblockstructurehelper = new AdvancedPistonBlockStructureHelper(worldIn, pos, directionIn, maxPushBlocks, pistonPos);
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
         Direction direction = directionIn;
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
            	worldIn.setBlock(blockpos3, ModItems.advanced_moving_block.defaultBlockState().setValue(BlockAdvancedMovingBlock.FACING, directionIn), 68 );
            	worldIn.removeBlockEntity(blockpos3);
                worldIn.setBlockEntity(blockpos3, BlockAdvancedMovingBlock.createTilePiston(list1.get(l), list12.get(l), directionIn, true, false));
            }
            
            ablockstate[j++] = blockstate5;
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
         
         return true;
      }
   }
	
	public Direction getMoveDirectionForFace(Direction face, Direction blockFacing) {
		
		if (blockFacing.getAxis() == Axis.Z) {
			Direction moveDirection = null;
			switch (face) {
			case NORTH: moveDirection = Direction.WEST; break;
			case SOUTH: moveDirection = Direction.WEST; break;
			case EAST: moveDirection = Direction.DOWN; break;
			case WEST: moveDirection = Direction.UP; break;
			case UP: moveDirection = Direction.NORTH; break;
			case DOWN: moveDirection = Direction.SOUTH; break;
			}
			if (blockFacing == Direction.SOUTH) {
				return moveDirection.getOpposite();
			} else {
				return moveDirection;
			}
		} else {
			Direction moveDirection = null;
			switch (face) {
			case NORTH: moveDirection = Direction.DOWN; break;
			case SOUTH: moveDirection = Direction.UP; break;
			case EAST: moveDirection = Direction.SOUTH; break;
			case WEST: moveDirection = Direction.SOUTH; break;
			case UP: moveDirection = Direction.WEST; break;
			case DOWN: moveDirection = Direction.EAST; break;
			}
			if (blockFacing == Direction.EAST) {
				return moveDirection.getOpposite();
			} else {
				return moveDirection;
			}
		}
		
	}
	
	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
	
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.setValue(FACING, mirrorIn.mirror(state.getValue(FACING)));
	}

	@Override
	public boolean addBlocksToMove(AdvancedPistonBlockStructureHelper pistonStructureHelper, BlockPos pos, BlockState state, World world) {
		for (Direction d : Direction.values()) {
			BlockPos pos2 = pos.relative(d);
			if (world.getBlockState(pos2).getBlock() == ModItems.conector_block) {
				Direction moveDirection = getMoveDirectionForFace(d, state.getValue(FACING));
				pistonStructureHelper.addBlockLine(pos2, moveDirection);
			}
		}
		return true;
	}
	
}
