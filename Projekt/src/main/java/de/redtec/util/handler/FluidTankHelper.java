package de.redtec.util.handler;

import java.util.ArrayList;
import java.util.List;

import de.redtec.typeregistys.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FluidTankHelper {
	
	public static final int MAX_TANK_RANGE = 64;
	public static final int MAX_SCANN_DEPTH = 150;
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
			scannForTankBlocks(outletPosList, scannPos, true, fluid);
			
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
	
	public BlockPos extractFluidFromTank(Fluid fluidInTank) {
		
		List<BlockPos> scannList = new ArrayList<BlockPos>();
		scannForTankBlocks(scannList, this.beginPos, false, fluidInTank);
		
		if (scannList.size() > 0) {
			
			for (BlockPos sourcePos : scannList) {
				
				FluidState fluidState = this.world.getFluidState(sourcePos);
				
				if (fluidState.isEmpty() ? false : fluidState.isSource() && fluidInTank.isEquivalentTo(fluidState.getFluid())) return sourcePos;
				
			}
			
		}
		
		return null;
		
	}
	
	private int scannDepth;
	@SuppressWarnings("deprecation")
	public void scannForTankBlocks(List<BlockPos> scannList, BlockPos scannPos, boolean checkAir, Fluid fluid) {
		
		if (checkAir) {
			
			BlockState blockState = this.world.getBlockState(scannPos);
			BlockState groundState = this.world.getBlockState(scannPos.down());
			FluidState groundFluid = groundState.getFluidState();
			
			if ((blockState.isAir() || ModFluids.isFluidBlock(blockState.getBlock())) && ((!groundState.isAir() && !ModFluids.isFluidBlock(groundState.getBlock())) || (groundFluid.isEmpty() ? false : groundFluid.isSource()) || fluid.getAttributes().isGaseous())) {
				
				scannList.add(scannPos);
				this.scannDepth++;
				
				int distX = Math.max(this.beginPos.getX(), scannPos.getX()) - Math.min(this.beginPos.getX(), scannPos.getX());
				int distZ = Math.max(this.beginPos.getZ(), scannPos.getZ()) - Math.min(this.beginPos.getZ(), scannPos.getZ());
				boolean isInRange = distX + distZ < MAX_TANK_RANGE;
				
				if (scannDepth <= MAX_SCANN_DEPTH && isInRange) {
					
					for (int i = 0; i < 4; i++) {
						
						Direction d = Direction.byHorizontalIndex(i);
						
						if (!scannList.contains(scannPos.offset(d))) scannForTankBlocks(scannList, scannPos.offset(d), checkAir, fluid);
						
					}
													
				}
				
			}
			
		} else {

			BlockState blockState = this.world.getBlockState(scannPos);
			
			if (ModFluids.isFluidBlock(blockState.getBlock())) {
				
				scannList.add(scannPos);
				this.scannDepth++;
				
				int distX = Math.max(this.beginPos.getX(), scannPos.getX()) - Math.min(this.beginPos.getX(), scannPos.getX());
				int distZ = Math.max(this.beginPos.getZ(), scannPos.getZ()) - Math.min(this.beginPos.getZ(), scannPos.getZ());
				boolean isInRange = distX + distZ < MAX_TANK_RANGE;
				
				if (scannDepth <= MAX_SCANN_DEPTH && isInRange) {
					
					for (Direction d : Direction.values()) {
						
						if (d != Direction.DOWN) {
							
							if (!scannList.contains(scannPos.offset(d))) scannForTankBlocks(scannList, scannPos.offset(d), checkAir, fluid);
							
						}
						
					}
													
				}
				
			}
			
		}
		
	}
	
}
