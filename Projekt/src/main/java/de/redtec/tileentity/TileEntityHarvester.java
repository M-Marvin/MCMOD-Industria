package de.redtec.tileentity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.redtec.gui.ContainerHarvester;
import de.redtec.typeregistys.ModTileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityHarvester extends TileEntity implements IInventory, INamedContainerProvider, ISidedInventory {
	
	NonNullList<ItemStack> itemstacks;
	
	public TileEntityHarvester() {
		super(ModTileEntityType.HARVESTER);
		this.itemstacks = NonNullList.withSize(9, ItemStack.EMPTY);
	}

	@Override
	public void clear() {
		for (int i = 0; i < 9; i++) {
			this.itemstacks.set(i, ItemStack.EMPTY);
		}
	}

	@Override
	public int getSizeInventory() {
		return 9;
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < 9; i++) {
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
	
	public List<ItemStack> tryToInser(List<ItemStack> items) {
		
		for (ItemStack stack2 : items) {
			
			for (int i = 0; i < 9; i++) {

				ItemStack stack = this.itemstacks.get(i);
				
				if (stack.getItem() == stack2.getItem() && !stack2.isEmpty()) {
					
					if (stack.hasTag() ? stack.getTag().equals(stack2.getTag()) : !stack2.hasTag()) {
						
						int capacity = stack.getMaxStackSize() - stack.getCount();
						int transfer = Math.min(stack2.getCount(), capacity);
						
						if (transfer > 0) {
							
							stack.grow(transfer);
							stack2.shrink(transfer);
							
						}
						
					}
					
				} else if (stack.isEmpty()) {
					
					stack = stack2.copy();
					stack2.shrink(stack2.getCount());
					
				}
				
				this.itemstacks.set(i, stack);
				
				if (stack2.isEmpty()) break;
				
			}
			
		}
		
		List<ItemStack> remainingItems = new ArrayList<ItemStack>();
		for (ItemStack stack : items) {
			if (!stack.isEmpty()) remainingItems.add(stack);
		}
		
		return remainingItems;
		
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		net.minecraft.inventory.ItemStackHelper.saveAllItems(compound, this.itemstacks);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		net.minecraft.inventory.ItemStackHelper.loadAllItems(compound, this.itemstacks);
		super.read(state, compound);
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerHarvester(id, playerInv, this);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.redtec.harvester");
	}
	
	net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
           net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.DOWN, Direction.NORTH);

	@Override
	public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
		if (!this.removed && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == Direction.DOWN)
            return handlers[0].cast();
			else
			return handlers[1].cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return true;
	}
	
}
