package de.industria.util.blockfeatures;

import net.minecraft.fluid.Fluid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public interface ITEFluidConnective {
	
	public FluidStack getFluid(int amount);
	public FluidStack insertFluid(FluidStack fluid);
	public Fluid getFluidType();
	public FluidStack getStorage();
	public void setStorage(FluidStack storage);
	
	public boolean canConnect(Direction side);
	
	public default FluidStack pushFluid(FluidStack fluid, World world, BlockPos pos) {
		
		FluidStack fluidIn = fluid.copy();
		
		for (Direction d : Direction.values()) {
			
			if (this.canConnect(d)) {
				
				TileEntity te = world.getBlockEntity(pos.relative(d));
				
				if (te instanceof ITEFluidConnective) {
					
					fluidIn = ((ITEFluidConnective) te).insertFluid(fluidIn);
					
					if (fluidIn.isEmpty()) break;
					
				}
				
			}
			
		}
		
		return fluidIn;
		
	}
	
	public default FluidStack pullFluid(Fluid fluid, int amount, World world, BlockPos pos) {

		FluidStack fluidIn = FluidStack.EMPTY;
		
		for (Direction d : Direction.values()) {
			
			if (this.canConnect(d)) {
				
				TileEntity te = world.getBlockEntity(pos.relative(d));
				
				if (te instanceof ITEFluidConnective) {
					
					if (((ITEFluidConnective) te).getFluidType() == fluid) {
						
						int capacity = amount - fluidIn.getAmount();
						FluidStack fluidN = ((ITEFluidConnective) te).getFluid(capacity);
						
						if (fluidIn.isEmpty()) {
							fluidIn = fluidN;
						} else {
							fluidIn.grow(fluidN.getAmount());
						}
						
					}
					
				}
				
			}
			
		}
		
		return fluidIn;
		
	}
	
}
