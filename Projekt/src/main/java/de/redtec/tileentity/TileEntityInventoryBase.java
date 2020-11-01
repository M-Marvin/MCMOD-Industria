package de.redtec.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;

public class TileEntityInventoryBase extends TileEntity implements IInventory {

	protected NonNullList<ItemStack> itemstacks;
	protected int slots;
	
	public TileEntityInventoryBase(TileEntityType<?> tileEntityTypeIn, int slots) {
		super(tileEntityTypeIn);
		this.slots = slots;
		this.itemstacks = NonNullList.withSize(this.slots, ItemStack.EMPTY);
	}
	
	@Override
	public void clear() {
		for (int i = 0; i < this.slots; i++) {
			this.itemstacks.set(i, ItemStack.EMPTY);
		}
	}

	@Override
	public int getSizeInventory() {
		return this.slots;
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < this.slots; i++) {
			if (!this.itemstacks.get(i).isEmpty()) return false;
		}
		return true;
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
		return true;
	}
	
//	public List<ItemStack> tryToInser(List<ItemStack> items) {
//		
//		for (ItemStack stack2 : items) {
//			
//			for (int i = 0; i < this.slots; i++) {
//
//				ItemStack stack = this.itemstacks.get(i);
//				
//				if (stack.getItem() == stack2.getItem() && !stack2.isEmpty()) {
//					
//					if (stack.hasTag() ? stack.getTag().equals(stack2.getTag()) : !stack2.hasTag()) {
//						
//						int capacity = stack.getMaxStackSize() - stack.getCount();
//						int transfer = Math.min(stack2.getCount(), capacity);
//						
//						if (transfer > 0) {
//							
//							stack.grow(transfer);
//							stack2.shrink(transfer);
//							
//						}
//						
//					}
//					
//				} else if (stack.isEmpty()) {
//					
//					stack = stack2.copy();
//					stack2.shrink(stack2.getCount());
//					
//				}
//				
//				this.itemstacks.set(i, stack);
//				
//				if (stack2.isEmpty()) break;
//				
//			}
//			
//		}
//		
//		List<ItemStack> remainingItems = new ArrayList<ItemStack>();
//		for (ItemStack stack : items) {
//			if (!stack.isEmpty()) remainingItems.add(stack);
//		}
//		
//		return remainingItems;
//		
//	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		net.minecraft.inventory.ItemStackHelper.saveAllItems(compound, this.itemstacks);
		return super.write(compound);
	}
	
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compound) {
		net.minecraft.inventory.ItemStackHelper.loadAllItems(compound, this.itemstacks);
		super.func_230337_a_(state, compound);
	}
	
}
