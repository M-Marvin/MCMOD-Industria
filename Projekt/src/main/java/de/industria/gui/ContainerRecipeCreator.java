package de.industria.gui;

import de.industria.typeregistys.ModContainerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ContainerRecipeCreator extends Container {
	
	protected PlayerInventory playerInv;
	protected Inventory inventory;
	protected BlockPos pos;
	
	public ContainerRecipeCreator(int id, PlayerInventory playerInv, PacketBuffer data) {
		this(id, playerInv, data.readBlockPos());
	}
	
	public ContainerRecipeCreator(int id, PlayerInventory playerInv, BlockPos pos) {
		super(ModContainerType.RECIPE_CREATOR, id);
		this.playerInv = playerInv;
		this.inventory = new Inventory(10);
		this.pos = pos;
		this.init();
	}
		
	public int getSlots() {
		return 10;
	}
	
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
	
	public void init() {
		
		this.addSlot(new Slot(this.inventory, 9, 124, 35));
		
		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 3; ++j) {
				this.addSlot(new Slot(this.inventory, j + i * 3, 30 + j * 18, 17 + i * 18));
			}
		}
		
		for(int k = 0; k < 3; ++k) {
			for(int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInv, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}
		
		for(int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInv, l, 8 + l * 18, 142));
		}
	    
	}
	
	public boolean stillValid(PlayerEntity playerIn) {
		return true;
	}
	
}
