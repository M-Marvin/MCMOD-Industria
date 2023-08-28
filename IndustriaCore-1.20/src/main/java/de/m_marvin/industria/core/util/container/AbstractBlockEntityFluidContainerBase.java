package de.m_marvin.industria.core.util.container;

import de.m_marvin.industria.core.electrics.types.containers.IFluidSlotContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractBlockEntityFluidContainerBase<T extends BlockEntity> extends AbstractBlockEntityContainerBase<T> implements IFluidSlotContainer {
	
	public final NonNullList<FluidSlot> fluidSlots = NonNullList.create();
	
	public AbstractBlockEntityFluidContainerBase(MenuType<?> type, int id, Inventory playerInv, FriendlyByteBuf data) {
		super(type, id, playerInv, data);
	}

	public AbstractBlockEntityFluidContainerBase(MenuType<?> type, int id, Inventory playerInv, T tileEntity) {
		super(type, id, playerInv, tileEntity);
	}
	
	@Override
	public NonNullList<FluidSlot> getFluidSlots() {
		return this.fluidSlots;
	}
	
	public void addFluidSlot(FluidSlot fluidSlot) {
		this.fluidSlots.add(fluidSlot);
	}
	
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		// TODO Auto-generated method stub
		
		return super.quickMoveStack(playerIn, index);
	}
	
}
