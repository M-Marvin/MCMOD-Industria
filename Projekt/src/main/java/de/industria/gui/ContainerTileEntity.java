package de.industria.gui;

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
		TileEntity te = Minecraft.getInstance().level.getBlockEntity(pos);
		return te;
		
	}
	
	public abstract int getSlots();
	
	public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < this.getSlots()) {
				if (!this.moveItemStackTo(itemstack1, this.getSlots(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, 0, this.getSlots(), false)) {
				return ItemStack.EMPTY;
			}
			
			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		
		return itemstack;
	}
	
	public abstract void init();
	
	public boolean stillValid(PlayerEntity playerIn) {
		return stillValid(IWorldPosCallable.NULL, playerIn, Blocks.CRAFTING_TABLE) && !this.tileEntity.isRemoved();
	}
	
	public T getTileEntity() {
		return tileEntity;
	}
	
	public static class CraftingResultSlot extends Slot {
		
		public CraftingResultSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}
			
		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}
			
	}
	
}
