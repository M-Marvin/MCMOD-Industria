package de.m_marvin.industria.core.util.container;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractBlockEntityFluidContainerBase<T extends BlockEntity> extends AbstractBlockEntityContainerBase<T> implements IFluidSlotContainer {
	
	private NonNullList<FluidSlot> fluidSlots;
	
	public AbstractBlockEntityFluidContainerBase(MenuType<?> type, int id, Inventory playerInv, FriendlyByteBuf data) {
		super(type, id, playerInv, data);
		
	}

	public AbstractBlockEntityFluidContainerBase(MenuType<?> type, int id, Inventory playerInv, T tileEntity) {
		super(type, id, playerInv, tileEntity);
	}
	
	@Override
	public void init() {
		this.fluidSlots = NonNullList.create();
	}
	
	@Override
	public NonNullList<FluidSlot> getFluidSlots() {
		return this.fluidSlots;
	}
	
	public FluidSlot addFluidSlot(FluidSlot fluidSlot) {
		this.fluidSlots.add(fluidSlot);
		return fluidSlot;
	}
	
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		// TODO Auto-generated method stub
		
		return super.quickMoveStack(playerIn, index);
	}
	
}
