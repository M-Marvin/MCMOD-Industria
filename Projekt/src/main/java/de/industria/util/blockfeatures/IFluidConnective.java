package de.industria.util.blockfeatures;

import net.minecraft.fluid.Fluid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidConnective {
	
	public FluidStack getFluid(int amount);
	public FluidStack insertFluid(FluidStack fluid);
	public Fluid getFluidType();
	public FluidStack getStorage();
	
	public boolean canConnect(Direction side);
	
	public default FluidStack pushFluid(FluidStack fluid, World world, BlockPos pos) {
		
		FluidStack fluidIn = fluid.copy();
		
		for (Direction d : Direction.values()) {
			
			if (this.canConnect(d)) {
				
				TileEntity te = world.getBlockEntity(pos.relative(d));
				
				if (te instanceof IFluidConnective) {
					
					fluidIn = ((IFluidConnective) te).insertFluid(fluidIn);
					
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
				
				if (te instanceof IFluidConnective) {
					
					if (((IFluidConnective) te).getFluidType() == fluid) {
						
						int capacity = amount - fluidIn.getAmount();
						FluidStack fluidN = ((IFluidConnective) te).getFluid(capacity);
						
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
