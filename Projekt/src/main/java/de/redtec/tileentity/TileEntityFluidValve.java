package de.redtec.tileentity;

import java.util.ArrayList;

import de.redtec.blocks.BlockFluidValve;
import de.redtec.registys.ModTileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityFluidValve extends TileEntityFluidPipe {
	
	public int maxFlow;
	
	public TileEntityFluidValve() {
		super(ModTileEntityType.FLUID_VALVE);
		this.fluid = FluidStack.EMPTY;
	}
	
	@Override
	public FluidStack insertFluid(FluidStack fluid) {
		if (this.fluid.isEmpty()) {
			int transfer = Math.min(this.maxFlow, fluid.getAmount());
			this.fluid = new FluidStack(fluid.getFluid(), transfer);
			this.fluid.setTag(fluid.getTag());
			FluidStack rest = fluid.copy();
			rest.shrink(transfer);
			if (this.fluid.getAmount() == this.maxFluid && !rest.isEmpty()) rest = pushFluid(rest, null);
			return rest;
		} else if (this.fluid.getFluid() == fluid.getFluid()) {
			int capacity = this.maxFluid - this.fluid.getAmount();
			int transfer = Math.min(Math.min(capacity, fluid.getAmount()), this.maxFlow);
			this.fluid.grow(transfer);
			this.fluid.setTag(fluid.getTag());
			FluidStack rest = fluid.copy();
			rest.shrink(transfer);
			if (this.fluid.getAmount() == this.maxFluid && !rest.isEmpty()) rest = pushFluid(rest, null);
			return rest;
		}
		return fluid;
	}
	
	public void updateFlowRate() {
		
		int power = world.getRedstonePowerFromNeighbors(pos);
		boolean opened = this.getBlockState().get(BlockFluidValve.OPEN);
		this.maxFlow = opened ? this.maxFluid : (int) ((float) (power / 15F) * this.maxFluid);
		
	}
	
	public FluidStack pushFluid(FluidStack fluidIn, Direction callDirection) {
		
		return this.pushFluid0(fluidIn, callDirection, new ArrayList<BlockPos>(), 0, this.maxFlow);
		
	}
	
	@Override
	public boolean canConnect(Direction side) {
		return super.canConnect(side) && side != Direction.UP;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("flowRate", this.maxFlow);
		return super.write(compound);
	}
	
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compound) {
		this.maxFlow = (int) compound.getFloat("flowRate");
		super.func_230337_a_(state, compound);
	}
	
}
