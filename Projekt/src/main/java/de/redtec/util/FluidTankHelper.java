package de.redtec.util;

import java.util.ArrayList;
import java.util.List;

import de.redtec.fluids.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidTankHelper {
	
	public static final int MAX_TANK_RANGE = 64;
	public static final int MAX_SCANN_DEPTH = 128;
	protected BlockPos beginPos;
	protected World world;
	
	public FluidTankHelper(World world, BlockPos beginPos) {
		this.world = world;
		this.beginPos = beginPos;
	}
	
	@SuppressWarnings("deprecation")
	public BlockPos insertFluidInTank(Fluid fluid) {
		
		int bottomY = this.beginPos.getY();
		for (; bottomY > 0; bottomY--) {
			BlockPos testPos = new BlockPos(this.beginPos.getX(), bottomY, this.beginPos.getZ());
			BlockState blockState = this.world.getBlockState(testPos);
			
			if (!blockState.isAir() && !ModFluids.isFluidBlock(blockState.getBlock())) {
				bottomY = Math.min(bottomY + 1, this.beginPos.getY());
				break;
			}
		}
		
		List<BlockPos> outletPosList = new ArrayList<BlockPos>();
		for (int y = bottomY; y <= this.beginPos.getY(); y++) {
			
			BlockPos scannPos = new BlockPos(this.beginPos.getX(), y, this.beginPos.getZ());
			
			this.scannDepth = 0;
			scannForTankBlocks(outletPosList, scannPos);
			
			if (outletPosList.size() > 0) {
				
				for (BlockPos outletPos : outletPosList) {
					
					FluidState fluidState = this.world.getFluidState(outletPos);
					
					if (fluidState.isEmpty() ? true : !fluidState.isSource()) return outletPos;
					
				}
				
			}
			
			outletPosList.clear();
			
		}
		
		return null;
		
	}
	
	@SuppressWarnings("deprecation")
	public BlockPos extractFluidFromTank(Fluid fluidInTank) {
		
		boolean gas = fluidInTank.getAttributes().isGaseous();
		
		int topY = this.beginPos.getY();
		for (; gas ? (topY > 0) : (topY < 256); topY += gas ? -1 : +1) {
			
			BlockPos testPos = new BlockPos(this.beginPos.getX(), topY, this.beginPos.getZ());
			BlockState blockState = this.world.getBlockState(testPos);
			
			if (!blockState.isAir() && !ModFluids.isFluidBlock(blockState.getBlock())) {
				
				topY = gas ? Math.min(this.beginPos.getY(), topY + 1) : Math.max(this.beginPos.getY(), topY - 1);
				break;
			}
		}
		
		List<BlockPos> sourcePosList = new ArrayList<BlockPos>();
		for (int y = this.beginPos.getY(); gas ? y >= topY : y <= topY; y += gas ? -1 : +1) {
			
			BlockPos scannPos = new BlockPos(this.beginPos.getX(), y, this.beginPos.getZ());
			
			this.scannDepth = 0;
			scannForTankBlocks(sourcePosList, scannPos);
			
			if (sourcePosList.size() > 0) {
				
				for (BlockPos sourcePos : sourcePosList) {
					
					FluidState fluidState = this.world.getFluidState(sourcePos);
					
					if (fluidState.isEmpty() ? false : fluidState.isSource() && fluidInTank.isEquivalentTo(fluidState.getFluid())) return sourcePos;
					
				}
				
			}
			
			sourcePosList.clear();
			
		}
		
		return null;
		
	}
	
	private int scannDepth;
	@SuppressWarnings("deprecation")
	public void scannForTankBlocks(List<BlockPos> scannList, BlockPos scannPos) {
		
		BlockState blockState = this.world.getBlockState(scannPos);
		
		if (blockState.isAir() || ModFluids.isFluidBlock(blockState.getBlock())) {
			
			scannList.add(scannPos);
			this.scannDepth++;
			
			int distX = Math.max(this.beginPos.getX(), scannPos.getX()) - Math.min(this.beginPos.getX(), scannPos.getX());
			int distZ = Math.max(this.beginPos.getZ(), scannPos.getZ()) - Math.min(this.beginPos.getZ(), scannPos.getZ());
			boolean isInRange = distX + distZ < MAX_TANK_RANGE;
			
			if (scannDepth <= MAX_SCANN_DEPTH && isInRange) {
				
				for (int i = 0; i < 4; i++) {
					
					Direction d = Direction.byHorizontalIndex(i);
					
					if (!scannList.contains(scannPos.offset(d))) scannForTankBlocks(scannList, scannPos.offset(d));
					
				}
												
			}
			
		}
		
	}
	
}
