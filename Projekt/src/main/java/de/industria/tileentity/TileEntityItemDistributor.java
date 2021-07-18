package de.industria.tileentity;

import de.industria.gui.ContainerItemDistributor;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.handler.ItemStackHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityItemDistributor extends TileEntityInventoryBase implements ISidedInventory, ITickableTileEntity, INamedContainerProvider {
	
	public TileEntityItemDistributor() {
		super(ModTileEntityType.ITEM_DISTRIBUTOR, 27);
	}
	
	@Override
	public void tick() {
		
		if (!this.level.isClientSide) {
			
			int maxTransferAmount = 64;
			boolean matches = true;
			for (int i = 9; i < 18; i++) {
				ItemStack stack = this.getItem(i);
				ItemStack layout = getLayoutItem(i);
				if (stack.getCount() < maxTransferAmount && !stack.isEmpty()) maxTransferAmount = stack.getCount();
				if (layout.isEmpty() && stack.isEmpty() || ItemStackHelper.isItemStackItemEqual(layout, stack, false) && stack.getCount() >= layout.getCount()) continue;
				matches = false;
				break;
			}
			
			boolean outputEmpty = true;
			for (int i = 0; i < 9; i++) {
				if (!getItem(i).isEmpty()) {
					outputEmpty = false;
					break;
				}
			}
			
			if (matches && outputEmpty) {
				
				for (int i = 0; i < 9; i++) {
					int i1 = i + 9;
					ItemStack stackIn = getItem(i1);
					ItemStack stackOut = stackIn.copy();
					stackOut.setCount(maxTransferAmount);
					stackIn.shrink(maxTransferAmount);
					this.setItem(i, stackOut);
				}
				
			}
			
		}
		
	}
	
	public int tryFindEmptySlot(ItemStack stack) {
		for (int i = 9; i < 18; i++) {
			ItemStack layout = getLayoutItem(i);
			if (ItemStackHelper.isItemStackItemEqual(layout, stack, false)) {
				ItemStack stackInSlot = getItem(i);
				if (stackInSlot.isEmpty()) return i;
			}
		}
		return -1;
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		if (index > 8) {
			int nextFreeSlot = tryFindEmptySlot(itemStackIn);
			if (nextFreeSlot != -1 ? index == nextFreeSlot : true) {
				ItemStack layoutStack = getLayoutItem(index);
				return ItemStackHelper.isItemStackItemEqual(layoutStack, itemStackIn, false);
			}
		}
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return index < 9;
	}
	
	public ItemStack getLayoutItem(int storeSlot) {
		int layoutSlot = storeSlot + 9;
		return this.getItem(layoutSlot);
	}

	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerItemDistributor(id, playerInv, this);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.item_distributor");
	}

}
