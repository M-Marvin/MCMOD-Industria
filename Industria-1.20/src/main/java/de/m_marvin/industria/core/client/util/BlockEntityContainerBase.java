package de.m_marvin.industria.core.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BlockEntityContainerBase<T extends BlockEntity> extends AbstractContainerMenu {
	
	protected Inventory playerInv;
	protected T blockEntity;
	
	@SuppressWarnings("unchecked")
	public BlockEntityContainerBase(MenuType<?> type, int id, Inventory playerInv, FriendlyByteBuf data) {
		this(type, id, playerInv, (T) getClientBlockEntity(data));
	}
	
	public BlockEntityContainerBase(MenuType<?> type, int id, Inventory playerInv, T tileEntity) {
		super(type, id);
		this.blockEntity = tileEntity;
		this.playerInv = playerInv;
		this.init();
	}
	
	@SuppressWarnings("resource")
	private static BlockEntity getClientBlockEntity(FriendlyByteBuf data) {
		
		BlockPos pos = data.readBlockPos();
		BlockEntity te = Minecraft.getInstance().level.getBlockEntity(pos);
		return te;
		
	}
	
	public abstract int getSlots();
	
	public ItemStack quickMoveStack(Player playerIn, int index) {
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
	
	public boolean stillValid(Player pPlayer) {
		return pPlayer.distanceToSqr((double)this.blockEntity.getBlockPos().getX() + 0.5D, (double)this.blockEntity.getBlockPos().getY() + 0.5D, (double)this.blockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
	}
	
	public T getBlockEntity() {
		return blockEntity;
	}
	
	public static class CraftingResultSlot extends Slot {
		
		public CraftingResultSlot(Inventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}
			
		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}
			
	}
	
}
