package de.m_marvin.industria.core.util.container;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import de.m_marvin.industria.core.util.ItemFluidExchangeHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidSlotContainer {
	
	public NonNullList<FluidSlot> getFluidSlots();
	
	public static class FluidSlot {
		
		protected final FluidContainer container;
		protected final int x;
		protected final int y;
		protected final int fluidSlot;
		
		public FluidSlot(FluidContainer container, int id, int x, int y) {
			this.x = x;
			this.y = y;
			this.container = container;
			this.fluidSlot = id;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public boolean mayPickup(ItemStack containerItem) {
			return true;
		}
		
		public boolean mayPlace(FluidStack fluid) {
			return true;
		}

		public FluidStack getFluid() {
			return this.container.getFluid(fluidSlot);
		}
		
		public int getCapacity() {
			return this.container.getMaxFluidAmount();
		}
		
		public FluidExchangeSlot makeFillSlot(FluidContainer exchangeContainer) {
			return new FluidExchangeSlot(exchangeContainer, this, fluidSlot * 2 + 0, x, y + 3, false);
		}

		public FluidExchangeSlot makeDrainSlot(FluidContainer exchangeContainer) {
			return new FluidExchangeSlot(exchangeContainer, this, fluidSlot * 2 + 1, x, y + 36, true);
		}
		
	}
	
	public static class FluidExchangeSlot extends Slot {
		
		protected FluidSlot fluidSlot;
		protected boolean drainSlot;
		
		public FluidExchangeSlot(Container pContainer, FluidSlot fluidSlot, int pSlot, int pX, int pY, boolean drainSlot) {
			super(pContainer, pSlot, pX, pY);
			this.drainSlot = drainSlot;
			this.fluidSlot = fluidSlot;
		}
		
		@Override
		public boolean mayPlace(ItemStack pStack) {
			if (this.drainSlot) {
				if (!this.fluidSlot.mayPickup(pStack)) return false;
				return ItemFluidExchangeHelper.canBeFilled(pStack, this.fluidSlot.getFluid()) && this.fluidSlot.getFluid().getAmount() > 0;
			} else {
				if (!this.fluidSlot.mayPlace(ItemFluidExchangeHelper.getFluidContent(pStack))) return false;
				return ItemFluidExchangeHelper.canBeDrained(pStack, this.fluidSlot.getFluid()) && this.fluidSlot.getCapacity() > this.fluidSlot.getFluid().getAmount();
			}
		}
		
		@Override
		public int getMaxStackSize() {
			return 1;
		}
		
	}
	
	public static class FluidContainer implements Container {
		
		protected int additionalSlots;
		protected int size;
		protected int maxFluidAmount;
		protected NonNullList<FluidStack> fluids;
		protected NonNullList<ItemStack> items;
		@Nullable
		private List<ContainerListener> listeners;
		private List<FluidContainerListener> fillListeners;
		private List<FluidContainerListener> drainListeners;
		
		@FunctionalInterface
		public static interface FluidContainerListener {
			void containerChanged(FluidContainer pContainer, Fluid fluid);
		}
		
		public FluidContainer(int fluidSlots) {
			this(fluidSlots, 0, 5000);
		}

		public FluidContainer(int fluidSlots, int additionalSlots) {
			this(fluidSlots, additionalSlots, 5000);
		}
		
		public FluidContainer(int fluidSlots, int additionalSlots, int maxFluidStackAmount) {
			this.additionalSlots = additionalSlots;
			this.size = fluidSlots;
			this.items = NonNullList.withSize(fluidSlots * 2 + additionalSlots, ItemStack.EMPTY);
			this.fluids = NonNullList.withSize(fluidSlots, FluidStack.EMPTY);
			this.maxFluidAmount = maxFluidStackAmount;
		}

		public void addListener(ContainerListener pListener) {
			if (this.listeners == null) {
				this.listeners = Lists.newArrayList();
			}
			this.listeners.add(pListener);
		}

		public void removeListener(ContainerListener pListener) {
			if (this.listeners != null) {
				this.listeners.remove(pListener);
			}
		}

		public void addFillListener(FluidContainerListener pListener) {
			if (this.fillListeners == null) {
				this.fillListeners = Lists.newArrayList();
			}
			this.fillListeners.add(pListener);
		}

		public void removeFillListener(FluidContainerListener pListener) {
			if (this.fillListeners != null) {
				this.fillListeners.remove(pListener);
			}
		}

		public void addDrainListener(FluidContainerListener pListener) {
			if (this.drainListeners == null) {
				this.drainListeners = Lists.newArrayList();
			}
			this.drainListeners.add(pListener);
		}

		public void removeDrainListener(FluidContainerListener pListener) {
			if (this.drainListeners != null) {
				this.drainListeners.remove(pListener);
			}
		}

		public FluidStack getFluid(int fluidSlot) {
			return fluids.get(fluidSlot);
		}
		
		public void setFluid(int fluidSlot, FluidStack fluidStack) {
			this.fluids.set(fluidSlot, fluidStack);
		}
		
		public int getFirstAdditional() {
			return this.fluids.size() * 2;
		}
		
		public int getMaxFluidAmount() {
			return maxFluidAmount;
		}
		
		@Override
		public void clearContent() {
			this.items.clear();
			this.fluids.clear();
		}
		
		@Override
		public int getContainerSize() {
			return this.size * 2 + this.additionalSlots;
		}
		
		@Override
		public boolean isEmpty() {
			for(ItemStack itemstack : this.items) {
				if (!itemstack.isEmpty()) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		public ItemStack getItem(int pSlot) {
			return pSlot >= 0 && pSlot < this.items.size() ? this.items.get(pSlot) : ItemStack.EMPTY;
		}
		
		@Override
		public ItemStack removeItem(int pSlot, int pAmount) {
			ItemStack itemstack = ContainerHelper.removeItem(this.items, pSlot, pAmount);
			if (!itemstack.isEmpty()) {
				this.setChanged();
			}
			return itemstack;
		}
		
		@Override
		public ItemStack removeItemNoUpdate(int pSlot) {
			ItemStack itemstack = this.items.get(pSlot);
			if (itemstack.isEmpty()) {
				return ItemStack.EMPTY;
			} else {
				this.items.set(pSlot, ItemStack.EMPTY);
				return itemstack;
			}
		}
		
		@Override
		public void setItem(int pSlot, ItemStack pStack) {
			if (pSlot < this.fluids.size() * 2) pStack = exchangeFluid(pSlot, pStack);
			this.items.set(pSlot, pStack);
			if (!pStack.isEmpty() && pStack.getCount() > this.getMaxStackSize()) {
				pStack.setCount(this.getMaxStackSize());
			}
			this.setChanged();
		}
		
		public ItemStack exchangeFluid(int slot, ItemStack stack) {
			int fluidSlot = slot / 2;
			FluidStack fluidStack = getFluid(fluidSlot);
			if (slot > fluidSlot) {
				// Drain FluidSlot
				Fluid fluid = fluidStack.getFluid();
				ItemStack result = ItemFluidExchangeHelper.fillFluid(stack, fluidStack, fs -> this.setFluid(fluidSlot, fs));
				if (!result.equals(stack)) {
					if (this.drainListeners != null) for (FluidContainerListener drainListener : this.drainListeners) drainListener.containerChanged(this, fluid);
				}
				return result;
			} else {
				// Fill FluidSlot
				ItemStack result = ItemFluidExchangeHelper.drainFluid(stack, fluidStack, this.maxFluidAmount, fs -> this.setFluid(fluidSlot, fs));
				if (!result.equals(stack)) {
					if (this.fillListeners != null) for (FluidContainerListener fillListener : this.fillListeners) fillListener.containerChanged(this, this.getFluid(fluidSlot).getFluid());
				}
				return result;
			}
		}
		
		@Override
		public void setChanged() {
			if (this.listeners != null) {
				for(ContainerListener containerlistener : this.listeners) {
					containerlistener.containerChanged(this);
				}
			}
		}
		
		@Override
		public boolean stillValid(Player pPlayer) {
			return true;
		}
		
	}
	
}
