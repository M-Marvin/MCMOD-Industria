package de.redtec.tileentity;

import java.util.ArrayList;
import java.util.List;

import de.redtec.registys.ModTileEntityType;
import de.redtec.util.IFluidConnective;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityFluidPipe extends TileEntity implements IFluidConnective, ITickableTileEntity {
	
	public static final int MAX_PUSH_DEPTH = 5000;
	
	protected final int maxFluid;
	protected FluidStack fluid;
	
	public TileEntityFluidPipe() {
		super(ModTileEntityType.FLUID_PIPE);
		this.fluid = FluidStack.EMPTY;
		this.maxFluid = 2000;
	}
	
	@Override
	public FluidStack getFluid(int amount) {
		if (!this.fluid.isEmpty()) {
			int transfer = Math.min(this.fluid.getAmount(), amount);
			this.fluid.shrink(transfer);
			return new FluidStack(this.fluid.getFluid(), transfer);
		}
		return FluidStack.EMPTY;
	}
	
	@Override
	public FluidStack insertFluid(FluidStack fluid) {
		if (this.fluid.isEmpty()) {
			int transfer = Math.min(this.maxFluid, fluid.getAmount());
			this.fluid = new FluidStack(fluid.getFluid(), transfer);
			FluidStack rest = fluid.copy();
			rest.shrink(transfer);
			if (this.fluid.getAmount() == this.maxFluid && !rest.isEmpty()) rest = pushFluid(rest, null);
			return rest;
		} else if (this.fluid.getFluid() == fluid.getFluid()) {
			int capacity = this.maxFluid - this.fluid.getAmount();
			int transfer = Math.min(capacity, fluid.getAmount());
			this.fluid.grow(transfer);
			FluidStack rest = fluid.copy();
			rest.shrink(transfer);
			if (this.fluid.getAmount() == this.maxFluid && !rest.isEmpty()) rest = pushFluid(rest, null);
			return rest;
		}
		return fluid;
	}
	
	@Override
	public Fluid getFluidType() {
		return this.fluid.getFluid();
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		if (!this.fluid.isEmpty()) compound.put("Fluid", this.fluid.writeToNBT(new CompoundNBT()));
		return super.write(compound);
	}
	
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compound) {
		this.fluid = FluidStack.loadFluidStackFromNBT(compound.getCompound("Fluid"));
		super.func_230337_a_(state, compound);
	}
	
	public FluidStack pushFluid(FluidStack fluidIn, Direction callDirection) {
		
		return pushFluid0(fluidIn, callDirection, new ArrayList<BlockPos>(), 0);
		
	}

	private FluidStack pushFluid0(FluidStack fluidIn, Direction callDirection, List<BlockPos> scannList, int scannDepth) {
		
		if (!fluidIn.isEmpty()) {
			
			for (Direction d : Direction.values()) {
				
				TileEntity te = this.world.getTileEntity(pos.offset(d));
				
				if (te instanceof TileEntityFluidPipe && (callDirection != null ? callDirection.getOpposite() != d : true)) {
					
					TileEntityFluidPipe pipe = (TileEntityFluidPipe) te;
					
					if (pipe.fluid.isEmpty() || (this.getFluidType() == pipe.getFluidType() && pipe.fluid.getAmount() < pipe.maxFluid)) {
						
						int transfer = Math.min(fluidIn.getAmount(), pipe.maxFluid - pipe.fluid.getAmount());
						if (pipe.fluid.isEmpty()) {
							pipe.fluid = new FluidStack(fluidIn.getFluid(), transfer);
						} else {
							pipe.fluid.grow(transfer);
						}
						fluidIn.shrink(transfer);
						
						if (fluidIn.isEmpty()) return FluidStack.EMPTY;
						
					}
					
				}
				
			}
			
			if (!scannList.contains(pos) && scannDepth <= MAX_PUSH_DEPTH) {
				
				scannList.add(pos);
				
				for (Direction d : Direction.values()) {
					
					TileEntity te = this.world.getTileEntity(pos.offset(d));
					
					if (te instanceof TileEntityFluidPipe && (callDirection != null ? callDirection.getOpposite() != d : true)) {
						
						TileEntityFluidPipe pipe = (TileEntityFluidPipe) te;
						
						fluidIn = pipe.pushFluid0(fluidIn, d, scannList, scannDepth++);
						
						if (fluidIn.isEmpty()) return FluidStack.EMPTY;
						
					}
					
				}
				
			}
			
		}
		
		return fluidIn;
		
	}
	
	@Override
	public void tick() {
		
		if (!this.world.isRemote && !this.fluid.isEmpty()) {
			
			int inputs = 0;
			for (Direction d : Direction.values()) {
				TileEntity te = this.world.getTileEntity(pos.offset(d));
				if (te instanceof TileEntityFluidPipe) {
					
					TileEntityFluidPipe pipe = (TileEntityFluidPipe) te;
					if (pipe.getFluidType() == this.getFluidType() || pipe.fluid.isEmpty()) inputs++;
					
				}
			}
			
			if (inputs > 0) {
				
				for (Direction d : Direction.values()) {
					TileEntity te = this.world.getTileEntity(pos.offset(d));
					if (te instanceof TileEntityFluidPipe) {

						TileEntityFluidPipe pipe = (TileEntityFluidPipe) te;
						
						if (pipe.fluid.isEmpty() || this.getFluidType() == pipe.getFluidType()) {
							
							int differenz = this.fluid.getAmount() - pipe.fluid.getAmount();
							
							if (differenz > 0) {
								
								int transfer = differenz / 2;
								
								transfer = Math.min(this.fluid.getAmount() / inputs, transfer);
								
								this.fluid.shrink(transfer);
								
								if (pipe.fluid.isEmpty()) {
									pipe.fluid = new FluidStack(this.getFluidType(), transfer);
								} else {
									pipe.fluid.grow(transfer);
								}
								
							}
							
						}
						
					}
					
				}
				
			}
			
			if (!this.fluid.isEmpty()) {
				
				for (Direction d : Direction.values()) {
					
					TileEntity te = this.world.getTileEntity(pos.offset(d));
					
					if (te instanceof IFluidConnective && !(te instanceof TileEntityFluidPipe)) {
						
						IFluidConnective device = (IFluidConnective) te;
						FluidStack fluidRemaining = device.insertFluid(this.fluid);
						if (fluidRemaining == this.fluid) continue;
						this.fluid = fluidRemaining;
						
					}
					
				}
				
			}
			
		}
		
		
		
	}

	@Override
	public boolean canConnect(Direction side) {
		return true;
	}
	
}
