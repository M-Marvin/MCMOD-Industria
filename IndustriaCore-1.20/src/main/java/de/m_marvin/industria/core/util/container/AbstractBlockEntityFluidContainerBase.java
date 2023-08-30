package de.m_marvin.industria.core.util.container;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.SoundActions;

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
	
	public void playDrainSound(FluidContainer container, Fluid fluid) {
		SoundEvent sound = fluid.getFluidType().getSound(SoundActions.BUCKET_FILL);
		if (sound != null) this.playerInv.player.playSound(sound);
	}
	
	public void playFillSound(FluidContainer container, Fluid fluid) {
		SoundEvent sound = fluid.getFluidType().getSound(SoundActions.BUCKET_EMPTY);
		if (sound != null) this.playerInv.player.playSound(sound);
	}
	
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		// TODO Auto-generated method stub
		
		return super.quickMoveStack(playerIn, index);
	}
	
}
