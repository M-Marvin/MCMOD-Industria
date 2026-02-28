package de.m_marvin.industria.core.util.container;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBlockContainerMenu extends AbstractContainerMenu {
	
	protected final BlockPos blockPos;
	protected final Inventory playerInv;
	protected final Container container;
	
	public AbstractBlockContainerMenu(MenuType<?> type, int id, Inventory playerInv, FriendlyByteBuf extraData, Container container) {
		super(type, id);
		this.playerInv = playerInv;
		this.blockPos = extraData.readBlockPos();
		this.container = container;
	}
	
	public AbstractBlockContainerMenu(MenuType<?> type, int id, Inventory playerInv, BlockPos blockPos, Container container) {
		super(type, id);
		this.blockPos = blockPos;
		this.container = container;
		this.playerInv = playerInv;
	}
	
	public BlockPos getBlockPos() {
		return blockPos;
	}
	
	public Level getLevel() {
		return playerInv.player.level();
	}
	
	public BlockEntity getBlockEntity() {
		return getLevel().getBlockEntity(getBlockPos());
	}
	
	public BlockState getBlockState() {
		return getLevel().getBlockState(getBlockPos());
	}
	
	public Container getContainer() {
		return container;
	}
	
	public int getFirstNonPlayerSlot() {
		return this.slots.size() - container.getContainerSize();
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
		return this.container.stillValid(pPlayer);
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
	
	public static class DummyContainer extends SimpleContainer {
		
		private final BlockPos pos;
		
		public DummyContainer(BlockPos pos) {
			this.pos = pos;
		}
		
		@Override
		public boolean stillValid(Player pPlayer) {
			return pPlayer.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
		}
		
	}
	
}
