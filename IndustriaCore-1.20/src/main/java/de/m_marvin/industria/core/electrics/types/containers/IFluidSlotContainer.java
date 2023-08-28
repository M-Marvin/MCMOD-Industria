package de.m_marvin.industria.core.electrics.types.containers;

import net.minecraft.core.NonNullList;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidSlotContainer {
	
	public NonNullList<FluidSlot> getFluidSlots();
	
	public static class FluidSlot {
		
		protected final int x;
		protected final int y;
		
		public FluidSlot(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}

		public FluidStack getFluid() {
			return new FluidStack(Fluids.WATER, 1600);
		}
		
		public int getCapacity() {
			return 3000;
		}
		
	}
	
}
