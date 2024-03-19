package de.m_marvin.industria.core.util.container;

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

public abstract class AbstractBlockEntityContainerBase<T extends BlockEntity> extends AbstractContainerMenu {
	
	protected Inventory playerInv;
	protected T blockEntity;
	
	@SuppressWarnings("unchecked")
	public AbstractBlockEntityContainerBase(MenuType<?> type, int id, Inventory playerInv, FriendlyByteBuf data) {
		this(type, id, playerInv, (T) getClientBlockEntity(data));
	}
	
	public AbstractBlockEntityContainerBase(MenuType<?> type, int id, Inventory playerInv, T tileEntity) {
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

	public int getFirstNonPlayerSlot() {
		return this.slots.size() - getSlots();
	}
	
	protected boolean moveItemStackTo(ItemStack pStack, int pIndex) {
		return super.moveItemStackTo(pStack, pIndex, pIndex + 1, false);
	}
	
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < this.getFirstNonPlayerSlot()) {
				if (!this.moveItemStackTo(itemstack1, this.getFirstNonPlayerSlot(), this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, 0, this.getFirstNonPlayerSlot(), false)) {
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
	
	public void initPlayerInventory(Inventory playerInventory, int offsetX, int offsetY) {
		for(int k = 0; k < 3; ++k) {
			for(int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, offsetX + 8 + i1 * 18, offsetY + 84 + k * 18));
			}
		}
		for(int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventory, l, offsetX + 8 + l * 18, offsetY + 142));
		}
	}
	
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
