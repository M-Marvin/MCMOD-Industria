package de.redtec.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidTankHelper {
	
	public static final int MAX_SCANN_DEPTH = Integer.MAX_VALUE;
	protected BlockPos beginPos;
	protected World world;
	
	public FluidTankHelper(World world, BlockPos beginPos) {
		this.world = world;
		this.beginPos = beginPos;
	}
	
	public BlockPos insertFluidInTank(Fluid fluid) {
		
		return null;
		
	}
	
	public BlockPos extractFluidFromTank() {
		
		Fluid fluidInTank = this.world.getFluidState(beginPos).getFluid();
		if (fluidInTank instanceof FlowingFluid) fluidInTank = ((FlowingFluid) fluidInTank).getStillFluid();
		
		if (fluidInTank != Fluids.EMPTY) {
			
			List<BlockPos> fluidBlocks = new ArrayList<BlockPos>();
			scannForExtracteableFluids(fluidBlocks, beginPos, 0, fluidInTank);
			
			if (fluidBlocks.size() > 0) {
				
				int maxY = this.beginPos.getY();
				for (BlockPos pos : fluidBlocks) {
					if (pos.getY() > maxY && this.world.getFluidState(pos).getFluid() == fluidInTank) maxY = pos.getY();
				}
				
				BlockPos sourcePos = null;
				
				for (BlockPos fluidPos : fluidBlocks) {
					
					if (fluidPos.getY() == maxY && this.world.getFluidState(fluidPos).isSource()) {
						
						FluidState extractingFluid = this.world.getFluidState(fluidPos);
						
						if (extractingFluid.isSource() && extractingFluid.getFluid() == fluidInTank) {
							
							sourcePos = fluidPos;
							
						}
						
					}
					
				}

				if (this.beginPos.getY() == 68) System.out.println(sourcePos);
				
				return sourcePos;
				
			}
			
		}
		
		return null;
		
	}
	
	public void scannForExtracteableFluids(List<BlockPos> scannList, BlockPos scannPos, int scannDepth, Fluid fluid) {
		
		Direction fluidDirection = fluid.getAttributes().isGaseous() ? Direction.DOWN : Direction.UP;
		FluidState state = this.world.getFluidState(scannPos);
		
		if (!state.isEmpty() ? state.getFluid().isEquivalentTo(fluid) : false) {
			
			scannList.add(scannPos);
			
			if (scannDepth <= MAX_SCANN_DEPTH) {
				
				for (Direction d : Direction.values()) {
					
					if (d != fluidDirection.getOpposite()) {
						
						if (!scannList.contains(scannPos.offset(d))) scannForExtracteableFluids(scannList, scannPos.offset(d), scannDepth++, fluid);
						
					}
					
				}
				
			}
			
		}
		
	}
		
}
