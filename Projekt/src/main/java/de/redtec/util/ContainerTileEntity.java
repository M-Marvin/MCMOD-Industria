package de.redtec.util;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;

public abstract class ContainerTileEntity<T extends TileEntity> extends Container {
	
	protected PlayerInventory playerInv;
	protected T tileEntity;
	
	@SuppressWarnings("unchecked")
	public ContainerTileEntity(ContainerType<?> type, int id, PlayerInventory playerInv, PacketBuffer data) {
		this(type, id, playerInv,(T) getClientTileEntity(data));
	}
	
	public ContainerTileEntity(ContainerType<?> type, int id, PlayerInventory playerInv, T tileEntity) {
		super(type, id);
		this.tileEntity = tileEntity;
		this.playerInv = playerInv;
		this.init();
	}
	
	@SuppressWarnings("resource")
	private static TileEntity getClientTileEntity(PacketBuffer data) {
		
		BlockPos pos = data.readBlockPos();
		TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
		return te;
		
	}
	
	public abstract int getSlots();
	
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
	      ItemStack itemstack = ItemStack.EMPTY;
	      Slot slot = this.inventorySlots.get(index);
	      if (slot != null && slot.getHasStack()) {
	         ItemStack itemstack1 = slot.getStack();
	         itemstack = itemstack1.copy();
	         if (index == 0) {
	            if (!this.mergeItemStack(itemstack1, this.getSlots(), 36 + this.getSlots(), true)) {
	               return ItemStack.EMPTY;
	            }
	            
	            slot.onSlotChange(itemstack1, itemstack);
	         } else if (index >= this.getSlots() && index < 36 + this.getSlots()) {
	            if (!this.mergeItemStack(itemstack1, 1, this.getSlots(), false)) {
	               if (index < 37) {
	                  if (!this.mergeItemStack(itemstack1, 37, 36 + this.getSlots(), false)) {
	                     return ItemStack.EMPTY;
	                  }
	               } else if (!this.mergeItemStack(itemstack1, this.getSlots(), 37, false)) {
	                  return ItemStack.EMPTY;
	               }
	            }
	         } else if (!this.mergeItemStack(itemstack1, this.getSlots(), 36 + this.getSlots(), false)) {
	            return ItemStack.EMPTY;
	         }

	         if (itemstack1.isEmpty()) {
	            slot.putStack(ItemStack.EMPTY);
	         } else {
	            slot.onSlotChanged();
	         }

	         if (itemstack1.getCount() == itemstack.getCount()) {
	            return ItemStack.EMPTY;
	         }

	         ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
	         if (index == 0) {
	            playerIn.dropItem(itemstack2, false);
	         }
	      }

	      return itemstack;
	}
	
	public abstract void init();
	
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(IWorldPosCallable.DUMMY, playerIn, Blocks.CRAFTING_TABLE) && !this.tileEntity.isRemoved();
	}
	
	public T getTileEntity() {
		return tileEntity;
	}
	
	public static class CraftingResultSlot2 extends Slot {
		
		public CraftingResultSlot2(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}
			
		@Override
		public boolean isItemValid(ItemStack stack) {
			return false;
		}
			
	}
	
}
