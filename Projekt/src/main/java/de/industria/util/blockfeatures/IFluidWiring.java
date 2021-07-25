package de.industria.util.blockfeatures;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidWiring extends IFluidConnective {

	public static final int MAX_PUSH_DEPTH = 5000;
	
	public int maxFlow();
	
	default public FluidStack pushFluidThrougPipes(FluidStack fluidIn, Direction callDirection, World level, BlockPos position) {
		
		return pushFluidThrougPipes0(fluidIn, callDirection, new ArrayList<BlockPos>(), 0, this.maxFlow(), level, position);
		
	}
	
	default FluidStack pushFluidThrougPipes0(FluidStack fluidIn, Direction callDirection, List<BlockPos> scannList, int scannDepth, int maxFlow, World level, BlockPos pos) {
		
		if (!fluidIn.isEmpty()) {
			
			for (Direction d : Direction.values()) {
				
				TileEntity te = level.getBlockEntity(pos.relative(d));
				
				if (te instanceof IFluidWiring && (callDirection != null ? callDirection.getOpposite() != d : true)) {
					
					IFluidWiring pipe = (IFluidWiring) te;
					
					if (pipe.getStorage().isEmpty() || (this.getFluidType() == pipe.getFluidType() && pipe.getStorage().getAmount() < pipe.maxFlow())) {
						
						int transfer = Math.min(Math.min(fluidIn.getAmount(), pipe.maxFlow() - pipe.getStorage().getAmount()), maxFlow);
						
						if (transfer > 0) {
							if (pipe.getStorage().isEmpty()) {
								pipe.setStorage(new FluidStack(fluidIn.getFluid(), transfer));
								pipe.getStorage().setTag(this.getStorage().getTag());
							} else {
								pipe.getStorage().grow(transfer);
								pipe.getStorage().setTag(this.getStorage().getTag());
							}
							fluidIn.shrink(transfer);
						}
						
						if (transfer == 0) return fluidIn;
						
						if (fluidIn.isEmpty()) {
							return FluidStack.EMPTY;
						}
						
					}
					
				}
				
			}
			
			if (!scannList.contains(pos) && scannDepth <= IFluidWiring.MAX_PUSH_DEPTH) {
				
				scannList.add(pos);
				
				for (Direction d : Direction.values()) {
					
					TileEntity te = level.getBlockEntity(pos.relative(d));
					
					if (te instanceof IFluidWiring && (callDirection != null ? callDirection.getOpposite() != d : true)) {
						
						IFluidWiring pipe = (IFluidWiring) te;
						
						fluidIn = pipe.pushFluidThrougPipes0(fluidIn, d, scannList, scannDepth++, maxFlow, level, pos.relative(d));
						
						if (fluidIn.isEmpty()) return FluidStack.EMPTY;
						
					}
					
				}
				
			}
			
		}
		
		return fluidIn;
		
	}
	
}
