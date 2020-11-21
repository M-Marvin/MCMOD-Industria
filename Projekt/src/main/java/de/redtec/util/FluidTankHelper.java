package de.redtec.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidTankHelper {
	
	public static final int MAX_OUTLET_RANGE = 16;
	public static final int MAX_SCANN_DEPTH = 50000;
	protected BlockPos beginPos;
	protected World world;
	
	public FluidTankHelper(World world, BlockPos beginPos) {
		this.world = world;
		this.beginPos = beginPos;
	}
	
	@SuppressWarnings("deprecation")
	public BlockPos insertFluidInTank(Fluid fluid) {
		
		List<BlockPos> tankBlocks = new ArrayList<BlockPos>();
		
		scannForTankBlocks(tankBlocks, beginPos, 0, fluid);
		
		if (tankBlocks.size() > 0) {
			
			int maxY = this.beginPos.getY();
			int minY = 256;
			
			for (BlockPos pos : tankBlocks) {
				
				if ((this.world.getBlockState(pos).isAir() || !this.world.getFluidState(pos).isSource())) {

					if (pos.getY() > maxY) maxY = pos.getY();
					if (pos.getY() < minY) minY = pos.getY();
					
				}
				
			}
			
			for (int y = minY; y <= maxY; y++) {
				
				for (BlockPos outletPos : tankBlocks) {
					
					if (outletPos.getY() == y && (fluid.isEquivalentTo(this.world.getFluidState(outletPos).getFluid()) ? !this.world.getFluidState(outletPos).isSource() : this.world.getBlockState(outletPos).isAir())) {
						
						return outletPos;
						
					}
					
				}
				
			}
			
		}
		
		return null;
		
	}
	
	public BlockPos extractFluidFromTank(Fluid fluidInTank) {
		
		if (fluidInTank != Fluids.EMPTY) {
			
			List<BlockPos> fluidBlocks = new ArrayList<BlockPos>();
			scannForExtracteableFluids(fluidBlocks, beginPos, 0, fluidInTank);
			
			if (fluidBlocks.size() > 0) {
				
				int maxY = this.beginPos.getY();
				for (BlockPos pos : fluidBlocks) {
					if (fluidInTank.getAttributes().isGaseous() ? pos.getY() < maxY : pos.getY() > maxY && this.world.getFluidState(pos).getFluid() == fluidInTank) maxY = pos.getY();
				}
				
				BlockPos sourcePos = null;
				
				for (BlockPos fluidPos : fluidBlocks) {
					
					if (fluidPos.getY() == maxY && this.world.getFluidState(fluidPos).isSource()) {
						
						FluidState extractingFluid = this.world.getFluidState(fluidPos);
						
						if (extractingFluid.isSource() && extractingFluid.getFluid().isEquivalentTo(fluidInTank)) {
							
							sourcePos = fluidPos;
							
						}
						
					}
					
				}
				
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
	
	@SuppressWarnings("deprecation")
	public void scannForTankBlocks(List<BlockPos> scannList, BlockPos scannPos, int scannDepth, Fluid fluid) {
		
		Direction fluidDirection = Direction.UP;
		BlockState state = this.world.getBlockState(scannPos);
		FluidState fluidState = state.getFluidState();
		
		if (fluid.isEquivalentTo(fluidState.getFluid()) || state.isAir()) {
			
			scannList.add(scannPos);
			
			int distX = Math.max(this.beginPos.getX(), scannPos.getX()) - Math.min(this.beginPos.getX(), scannPos.getX());
			int distZ = Math.max(this.beginPos.getZ(), scannPos.getZ()) - Math.min(this.beginPos.getZ(), scannPos.getZ());
			boolean isInRange = distX + distZ < MAX_OUTLET_RANGE;
			
			if (scannDepth <= MAX_SCANN_DEPTH && isInRange) {
				
				for (Direction d : Direction.values()) {
					
					if (d != fluidDirection) {
						
						if (!scannList.contains(scannPos.offset(d))) scannForTankBlocks(scannList, scannPos.offset(d), scannDepth++, fluid);
						
					}
					
				}
				
			}
			
		}
		
	}
	
}
