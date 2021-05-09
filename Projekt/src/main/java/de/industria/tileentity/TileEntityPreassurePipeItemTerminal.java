package de.industria.tileentity;

import java.util.List;

import de.industria.Industria;
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
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return index == 0 && isInput();
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return index == 1 && !isInput();
	}
	
	@Override
	public VoxelShape getItemDetectBounds() {
		Direction facing = this.getBlockState().get(BlockPipePreassurizer.FACING);
		int extraHeight = (int) (this.preassure * 16);
		boolean flag1 = !((BlockPreassurePipe) Industria.preassure_pipe).canConnect(getBlockState(), world, pos, getInputSide());
		boolean flag2 = !((BlockPreassurePipe) Industria.preassure_pipe).canConnect(getBlockState(), world, pos, getOutputSide());
		int extraHeight1 = flag1 ? extraHeight : 0;
		int extraHeight2 = flag2 ? extraHeight : 0;
		if (facing.getAxis().isVertical()) {
			return Block.makeCuboidShape(2, -extraHeight1, 2, 14, 16 + extraHeight2, 14);
		} else {
			return VoxelHelper.rotateShape(Block.makeCuboidShape(2, 2, -extraHeight1, 14, 14, 16 + extraHeight2), facing);
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
		return this.getBlockState().get(BlockPreassurePipeItemTerminal.INPUT);
	}
	
	@Override
	public void tick() {
		
		if (!this.world.isRemote) {
			
			if (this.isPreassurized()) {
				
				if (!isInput()) {
					
					List<AxisAlignedBB> itemBounds = getItemDetectBounds().toBoundingBoxList();
					itemBounds.forEach((boundBox) -> {
						boundBox = boundBox.offset(this.pos);
						this.world.getEntitiesInAABBexcluding(null, boundBox, null).forEach((entity) -> {
							
							if (entity instanceof ItemEntity) {
								
								ItemStack item = ((ItemEntity) entity).getItem();
								
								if (ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(1), item)) {
									
									if (this.getStackInSlot(1).isEmpty()) {
										this.setInventorySlotContents(1, item.copy());
									} else {
										this.getStackInSlot(1).grow(item.getCount());
									}
									entity.remove();
									
								}
								
							}
							
						});
					});
					
				} else {

					ItemStack itemOut = this.getStackInSlot(0);
					
					if (!itemOut.isEmpty()) {
						
						ItemEntity itemEntity = new ItemEntity(this.world, this.pos.getX() + 0.5F, this.pos.getY() + 0.5F, this.pos.getZ() + 0.5F, itemOut);
						itemEntity.setDefaultPickupDelay();
						this.world.addEntity(itemEntity);
						this.setInventorySlotContents(0, ItemStack.EMPTY);
						
					}
					
				}
				
			}
			
		}
		
		super.tick();
		
	}
	
	public boolean isBlocked() {
		return !this.getStackInSlot(1).isEmpty();
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
			
			if (!pipeStreamList.contains(this.pos)) {
				
				Direction outletDirection = getOtherOutlet(inputDirection);
				
				if (outletDirection != null) {

					this.inputSide = inputDirection.getOpposite();
					this.outputSide = outletDirection;
					this.preassure = preassure;
					this.lastPressurizing = this.world.getGameTime();
					pipeStreamList.add(this.pos);

					TileEntity nextPipe = this.world.getTileEntity(pos.offset(outletDirection));
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
	public void clear() {
		this.itemstacks.clear();
	}
	
	@Override
	public int getSizeInventory() {
		return 2;
	}
	
	@Override
	public boolean isEmpty() {
		return this.itemstacks.isEmpty();
	}
	
	@Override
	public ItemStack getStackInSlot(int index) {
		return this.itemstacks.get(index);
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack = this.itemstacks.get(index).copy();
		this.itemstacks.get(index).shrink(count);
		stack.setCount(count);
		return stack;
	}
	
	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = this.itemstacks.get(index);
		this.itemstacks.set(index, ItemStack.EMPTY);
		return stack;
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.itemstacks.set(index, stack);
	}
	
	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return false;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("InsertionTimer", this.insertionTimer);
		compound.putInt("ExtractionTimer", this.extractionTimer);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		this.extractionTimer = nbt.getInt("ExtractionTimer");
		this.insertionTimer = nbt.getInt("InsertionTimer");
		super.read(state, nbt);
	}
	
}
