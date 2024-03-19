package de.m_marvin.industria.content.container;

import de.m_marvin.industria.content.blockentities.machines.PortableFuelGeneratorBlockEntity;
import de.m_marvin.industria.content.registries.ModMenuTypes;
import de.m_marvin.industria.core.util.container.AbstractBlockEntityFluidContainerBase;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.FurnaceBlock;

public class PortableFuelGeneratorContainer extends AbstractBlockEntityFluidContainerBase<PortableFuelGeneratorBlockEntity> {
	
	public PortableFuelGeneratorContainer(int id, Inventory playerInv, PortableFuelGeneratorBlockEntity tileEntity) {
		super(ModMenuTypes.PORTABLE_FUEL_GENERATOR.get(), id, playerInv, tileEntity);
	}

	public PortableFuelGeneratorContainer(int id, Inventory playerInv, FriendlyByteBuf data) {
		super(ModMenuTypes.PORTABLE_FUEL_GENERATOR.get(), id, playerInv, data);
	}
	
	@Override
	public int getSlots() {
		return this.blockEntity.getFluidContainer().getContainerSize();
	}
	
	@Override
	public void init() {
		super.init();
		
		initPlayerInventory(playerInv, 0, 0);
		
		FluidSlot fluidSlot = addFluidSlot(new FluidSlot(this.blockEntity.getFluidContainer(), 0, 17, 18));
		addSlot(fluidSlot.makeFillSlot(this.blockEntity.getFluidContainer()));
		addSlot(fluidSlot.makeDrainSlot(this.blockEntity.getFluidContainer()));
		
		this.blockEntity.getFluidContainer().addFillListener(this::playFillSound);
		this.blockEntity.getFluidContainer().addDrainListener(this::playDrainSound);
	}
	
	@Override
	public void removed(Player pPlayer) {
		super.removed(pPlayer);
		this.clearContainer(pPlayer, this.blockEntity.getFluidContainer());
	}
	
	@Override
	public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
		// TODO quick swap inventory
		
		return ItemStack.EMPTY;
		
//	      ItemStack itemstack = ItemStack.EMPTY;
//	      Slot slot = this.slots.get(pIndex);
//	      if (slot != null && slot.hasItem()) {
//	         ItemStack itemstack1 = slot.getItem();
//	         itemstack = itemstack1.copy();
//	         if (pIndex == 2) {
//	            if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
//	               return ItemStack.EMPTY;
//	            }
//
//	            slot.onQuickCraft(itemstack1, itemstack);
//	         } else if (pIndex != 1 && pIndex != 0) {
//	            if (this.canSmelt(itemstack1)) {
//	               if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
//	                  return ItemStack.EMPTY;
//	               }
//	            } else if (this.isFuel(itemstack1)) {
//	               if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
//	                  return ItemStack.EMPTY;
//	               }
//	            } else if (pIndex >= 3 && pIndex < 30) {
//	               if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
//	                  return ItemStack.EMPTY;
//	               }
//	            } else if (pIndex >= 30 && pIndex < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
//	               return ItemStack.EMPTY;
//	            }
//	         } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
//	            return ItemStack.EMPTY;
//	         }
//
//	         if (itemstack1.isEmpty()) {
//	            slot.setByPlayer(ItemStack.EMPTY);
//	         } else {
//	            slot.setChanged();
//	         }
//
//	         if (itemstack1.getCount() == itemstack.getCount()) {
//	            return ItemStack.EMPTY;
//	         }
//
//	         slot.onTake(pPlayer, itemstack1);
//	      }
//
//	      return itemstack;
		
	}

//	   /**
//	    * Determines whether supplied player can use this container
//	    */
//	   public boolean stillValid(Player pPlayer) {
//	      return this.container.stillValid(pPlayer);
//	   }

	@Override
	public boolean stillValid(Player pPlayer) {
		// TODO inventory still valid
		return true;
	}
	
}
