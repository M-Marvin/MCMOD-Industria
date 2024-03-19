package de.m_marvin.industria.content.container;

import de.m_marvin.industria.content.blockentities.machines.PortableFuelGeneratorBlockEntity;
import de.m_marvin.industria.content.registries.ModMenuTypes;
import de.m_marvin.industria.core.util.ItemFluidExchangeHelper;
import de.m_marvin.industria.core.util.container.AbstractBlockEntityFluidContainerBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

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
		Slot slot = this.slots.get(pIndex);
		if (slot != null && slot.hasItem()) {
			ItemStack itemStack = slot.getItem();
			if (pIndex < getFirstNonPlayerSlot()) {
				FluidSlot fluidSlot = this.getFluidSlots().get(0);
				if (ItemFluidExchangeHelper.canBeDrained(itemStack, fluidSlot.getFluid()) && fluidSlot.getCapacity() > fluidSlot.getFluid().getAmount()) {
					if (!this.moveItemStackTo(itemStack, getFirstNonPlayerSlot() + 0)) {
						return ItemStack.EMPTY;
					}
				} else if (ItemFluidExchangeHelper.canBeFilled(itemStack, fluidSlot.getFluid()) && fluidSlot.getFluid().getAmount() > 0) {
					if (!this.moveItemStackTo(itemStack, getFirstNonPlayerSlot() + 1)) {
						return ItemStack.EMPTY;
					}
				}
			} else {
				if (!this.moveItemStackTo(itemStack, 0, getFirstNonPlayerSlot(), true)) {
					return ItemStack.EMPTY;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return Container.stillValidBlockEntity(this.blockEntity, pPlayer);
	}
	
}
