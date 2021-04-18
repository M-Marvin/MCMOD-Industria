package de.industria.blocks;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import de.industria.Industria;
import de.industria.items.ItemBlockAdvancedInfo.IBlockToolType;
import de.industria.items.ItemStructureCladdingPane;
import de.industria.tileentity.TileEntityStructureScaffold;
import de.industria.util.blockfeatures.IAdvancedBlockInfo;
import de.industria.util.blockfeatures.IAdvancedStickyBlock;
import de.industria.util.handler.VoxelHelper;
import de.industria.util.types.AdvancedPistonBlockStructureHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class BlockStructureScaffold extends BlockContainerBase implements IAdvancedStickyBlock, IAdvancedBlockInfo {

	public BlockStructureScaffold(String name) {
		super(name, Material.IRON, 3F, 3F, SoundType.NETHERITE);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		VoxelShape shape = VoxelShapes.empty();
		ItemStack heldStack = context.getEntity() instanceof PlayerEntity ? ((PlayerEntity) context.getEntity()).getHeldItemMainhand() : ItemStack.EMPTY;
		if (heldStack.getItem() == Industria.structure_scaffold.getItem(worldIn, pos, state).getItem()) {
			shape = Block.makeCuboidShape(0, 0, 0, 16, 16, 16);
		} else {
			shape = VoxelShapes.combine(shape, Block.makeCuboidShape(0, 0, 0, 16, 1, 1), IBooleanFunction.OR);
			shape = VoxelShapes.combine(shape, Block.makeCuboidShape(0, 0, 0, 1, 1, 16), IBooleanFunction.OR);
			shape = VoxelShapes.combine(shape, Block.makeCuboidShape(15, 0, 0, 16, 1, 16), IBooleanFunction.OR);
			shape = VoxelShapes.combine(shape, Block.makeCuboidShape(0, 0, 15, 16, 1, 16), IBooleanFunction.OR);
			shape = VoxelShapes.combine(shape, Block.makeCuboidShape(0, 15, 0, 16, 16, 1), IBooleanFunction.OR);
			shape = VoxelShapes.combine(shape, Block.makeCuboidShape(0, 15, 0, 1, 16, 16), IBooleanFunction.OR);
			shape = VoxelShapes.combine(shape, Block.makeCuboidShape(15, 15, 0, 16, 16, 16), IBooleanFunction.OR);
			shape = VoxelShapes.combine(shape, Block.makeCuboidShape(0, 15, 15, 16, 16, 16), IBooleanFunction.OR);
			shape = VoxelShapes.combine(shape, Block.makeCuboidShape(0, 1, 0, 1, 15, 1), IBooleanFunction.OR);
			shape = VoxelShapes.combine(shape, Block.makeCuboidShape(15, 1, 0, 16, 15, 1), IBooleanFunction.OR);
			shape = VoxelShapes.combine(shape, Block.makeCuboidShape(0, 1, 15, 1, 15, 16), IBooleanFunction.OR);
			shape = VoxelShapes.combine(shape, Block.makeCuboidShape(15, 1, 15, 16, 15, 16), IBooleanFunction.OR);
			
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof TileEntityStructureScaffold) {
				for (Direction d : ((TileEntityStructureScaffold) tileEntity).getCladdingSides()) {
					VoxelShape claddShape = (d == Direction.UP) ? Block.makeCuboidShape(0, 15, 0, 16, 16, 16) : ((d == Direction.DOWN) ? Block.makeCuboidShape(0, 0, 0, 16, 1, 16) : VoxelHelper.rotateShape(Block.makeCuboidShape(0, 0, 0, 16, 16, 1), d));
					shape = VoxelShapes.combine(shape, claddShape, IBooleanFunction.OR);
				}
			}
		}
		return shape;
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new TileEntityStructureScaffold();
	}

	@Override
	public IBlockToolType getBlockInfo() {
		return (stack, info) -> {};
	}

	@Override
	public Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() {
		return null;
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		
		if (handIn == Hand.MAIN_HAND) {
			
			ItemStack stack = player.getHeldItemMainhand();
			
			if (stack.getItem() == Industria.structure_cladding_pane) {
				
				if (!worldIn.isRemote()) {
					
					TileEntity tileEntity = worldIn.getTileEntity(pos);
					if (tileEntity instanceof TileEntityStructureScaffold) {
						
						Direction side = hit.getFace();
						if (((TileEntityStructureScaffold) tileEntity).setCladding(side, stack)) {
							stack.shrink(1);
							
							SoundEvent placeSound = ItemStructureCladdingPane.getBlockState(stack).getSoundType().getPlaceSound();
							worldIn.playSound(null, pos, placeSound, SoundCategory.BLOCKS, 1, 1);
							
						}
						
					}
					
				}
				
				return ActionResultType.CONSUME;
				
			} else if (stack.getItem() == Industria.hammer) {

				if (!worldIn.isRemote()) {
					
					TileEntity tileEntity = worldIn.getTileEntity(pos);
					if (tileEntity instanceof TileEntityStructureScaffold) {
						
						Direction side = hit.getFace();
						ItemStack removedCladding = ((TileEntityStructureScaffold) tileEntity).removeCladding(side);
						
						if (!removedCladding.isEmpty()) {

							SoundEvent breakSound = ItemStructureCladdingPane.getBlockState(removedCladding).getSoundType().getBreakSound();
							worldIn.playSound(null, pos, breakSound, SoundCategory.BLOCKS, 1, 1);
							
							if (!removedCladding.isEmpty()) player.addItemStackToInventory(removedCladding);
							
						}
						
					}
					
				}
				
				return ActionResultType.CONSUME;
				
			}
			
		}
		
		return ActionResultType.PASS;
		
	}
	
	@Override
	public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, Entity entity) {
		Direction claddingSide = Direction.UP; // This function is only used for stepp sounds, an Entitys normaly walk on top of the Block.
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileEntityStructureScaffold) {
			ItemStack claddingStack = ((TileEntityStructureScaffold) tileEntity).getCladding(claddingSide);
			if (!claddingStack.isEmpty()) {
				BlockState claddingState = ItemStructureCladdingPane.getBlockState(claddingStack);
				return claddingState.getSoundType();
			}
		}
		return super.getSoundType(state, world, pos, entity);
	}
	
	@Override
	public boolean addBlocksToMove(AdvancedPistonBlockStructureHelper pistonStructureHelper, BlockPos pos, BlockState state, World world) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileEntityStructureScaffold) {
			Direction[] claddedSides = ((TileEntityStructureScaffold) tileEntity).getCladdingSides();
			for (Direction d : Direction.values()) {
				boolean flag = true;
				for (Direction nd : claddedSides) {
					if (nd == d) flag = false;
				}
				if (flag) {
					if (!pistonStructureHelper.addBlockLine(pos.offset(d), pistonStructureHelper.getMoveDirection())) return false;
				}
			}
		}
		return true;
	}
	
}
