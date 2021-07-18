package de.industria.tileentity;

import java.util.List;

import de.industria.ModItems;
import de.industria.blocks.BlockPipePreassurizer;
import de.industria.blocks.BlockPreassurePipe;
import de.industria.blocks.BlockPreassurePipeItemTerminal;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.handler.ItemStackHelper;
import de.industria.util.handler.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;

public class TileEntityPreassurePipeItemTerminal extends TileEntityPreassurePipe implements ISidedInventory, ITickableTileEntity {
	
	protected NonNullList<ItemStack> itemstacks;
	public int extractionTimer;
	public int insertionTimer;
	
	public TileEntityPreassurePipeItemTerminal() {
		super(ModTileEntityType.PREASSURE_PIPE_ITEM_TERMINAL);
		this.itemstacks = NonNullList.withSize(2, ItemStack.EMPTY);
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1};
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return index == 0 && isInput();
	}
	
	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return index == 1 && !isInput();
	}
	
	@Override
	public VoxelShape getItemDetectBounds() {
		Direction facing = this.getBlockState().getValue(BlockPipePreassurizer.FACING);
		int extraHeight = (int) (this.preassure * 16);
		boolean flag1 = !((BlockPreassurePipe) ModItems.preassure_pipe).canConnect(getBlockState(), level, worldPosition, getInputSide());
		boolean flag2 = !((BlockPreassurePipe) ModItems.preassure_pipe).canConnect(getBlockState(), level, worldPosition, getOutputSide());
		int extraHeight1 = flag1 ? extraHeight : 0;
		int extraHeight2 = flag2 ? extraHeight : 0;
		if (facing.getAxis().isVertical()) {
			return Block.box(2, -extraHeight1, 2, 14, 16 + extraHeight2, 14);
		} else {
			return VoxelHelper.rotateShape(Block.box(2, 2, -extraHeight1, 14, 14, 16 + extraHeight2), facing);
		}
	}
	
	@Override
	public Direction getInputSide() {
		return super.getInputSide();
	}
	
	@Override
	public Direction getOutputSide() {
		return super.getOutputSide();
	}
	
	public boolean isInput() {
		return this.getBlockState().getValue(BlockPreassurePipeItemTerminal.INPUT);
	}
	
	@Override
	public void tick() {
		
		if (!this.level.isClientSide) {
			
			if (this.isPreassurized()) {
				
				if (!isInput()) {
					
					List<AxisAlignedBB> itemBounds = getItemDetectBounds().toAabbs();
					itemBounds.forEach((boundBox) -> {
						boundBox = boundBox.move(this.worldPosition);
						this.level.getEntities(null, boundBox).forEach((entity) -> {
							
							if (entity instanceof ItemEntity) {
								
								ItemStack item = ((ItemEntity) entity).getItem();
								
								if (ItemStackHelper.canMergeRecipeStacks(this.getItem(1), item)) {

									if (this.getItem(1).isEmpty()) {
										this.setItem(1, item.copy());
									} else {
										this.getItem(1).grow(item.getCount());
									}
									entity.remove();
									
								}
								
							}
							
						});
					});
					
				} else {

					ItemStack itemOut = this.getItem(0);
					
					if (!itemOut.isEmpty()) {
						
						ItemEntity itemEntity = new ItemEntity(this.level, this.worldPosition.getX() + 0.5F, this.worldPosition.getY() + 0.5F, this.worldPosition.getZ() + 0.5F, itemOut);
						itemEntity.setDefaultPickUpDelay();
						this.level.addFreshEntity(itemEntity);
						this.setItem(0, ItemStack.EMPTY);
						
					}
					
				}
				
			}
			
		}
		
		super.tick();
		
	}
	
	public boolean isBlocked() {
		return !this.getItem(1).isEmpty();
	}
	
	@Override
	public boolean isPreassurized() {
		return isBlocked() ? false : super.isPreassurized();
	}
	
	@Override
	public Direction getOtherOutlet(Direction inputDirection) {
		return inputDirection;
	}
	
	@Override
	public boolean preassurizePipe(Direction inputDirection, float preassure, List<BlockPos> pipeStreamList) {
		if (isBlocked()) {
			return false;
		} else {
			
			if (!pipeStreamList.contains(this.worldPosition)) {
				
				Direction outletDirection = getOtherOutlet(inputDirection);
				
				if (outletDirection != null) {

					this.inputSide = inputDirection.getOpposite();
					this.outputSide = outletDirection;
					this.preassure = preassure;
					this.lastPressurizing = this.level.getGameTime();
					pipeStreamList.add(this.worldPosition);

					TileEntity nextPipe = this.level.getBlockEntity(worldPosition.relative(outletDirection));
					if (nextPipe instanceof TileEntityPreassurePipe) {
						if (!((TileEntityPreassurePipe) nextPipe).preassurizePipe(outletDirection, preassure, pipeStreamList)) {
							this.inputSide = null;
							this.outputSide = null;
							this.preassure = 0;
							return false;
						}
					}
					
					return true;
					
				}
				
			}
			
			this.inputSide = null;
			this.outputSide = null;
			this.preassure = 0;
			return true;
			
		}
		
	}
	
	@Override
	public void clearContent() {
		this.itemstacks.clear();
	}
	
	@Override
	public int getContainerSize() {
		return 2;
	}
	
	@Override
	public boolean isEmpty() {
		return this.itemstacks.isEmpty();
	}
	
	@Override
	public ItemStack getItem(int index) {
		return this.itemstacks.get(index);
	}
	
	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack stack = this.itemstacks.get(index).copy();
		this.itemstacks.get(index).shrink(count);
		stack.setCount(count);
		return stack;
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack stack = this.itemstacks.get(index);
		this.itemstacks.set(index, ItemStack.EMPTY);
		return stack;
	}
	
	@Override
	public void setItem(int index, ItemStack stack) {
		this.itemstacks.set(index, stack);
	}
	
	@Override
	public boolean stillValid(PlayerEntity player) {
		return false;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putInt("InsertionTimer", this.insertionTimer);
		compound.putInt("ExtractionTimer", this.extractionTimer);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		this.extractionTimer = nbt.getInt("ExtractionTimer");
		this.insertionTimer = nbt.getInt("InsertionTimer");
		super.load(state, nbt);
	}
	
}
