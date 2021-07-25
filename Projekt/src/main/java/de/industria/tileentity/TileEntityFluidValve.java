package de.industria.tileentity;

import de.industria.blocks.BlockFluidValve;
import de.industria.typeregistys.ModTileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
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
			if (this.fluid.getAmount() == this.maxFluid && !rest.isEmpty()) rest = pushFluidThrougPipes(rest, null, this.level, this.worldPosition);
			return rest;
		} else if (this.fluid.getFluid() == fluid.getFluid()) {
			int capacity = this.maxFluid - this.fluid.getAmount();
			int transfer = Math.min(Math.min(capacity, fluid.getAmount()), this.maxFlow);
			this.fluid.grow(transfer);
			this.fluid.setTag(fluid.getTag());
			FluidStack rest = fluid.copy();
			rest.shrink(transfer);
			if (this.fluid.getAmount() == this.maxFluid && !rest.isEmpty()) rest = pushFluidThrougPipes(rest, null, this.level, this.worldPosition);
			return rest;
		}
		return fluid;
	}
	
	public void updateFlowRate() {
		
		int power = level.getBestNeighborSignal(worldPosition);
		boolean opened = this.getBlockState().getValue(BlockFluidValve.OPEN);
		this.maxFlow = opened ? this.maxFluid : (int) ((float) (power / 15F) * this.maxFluid);
		
	}
	
	@Override
	public boolean canConnect(Direction side) {
		return super.canConnect(side) && side != Direction.UP;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putInt("flowRate", this.maxFlow);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.maxFlow = (int) compound.getFloat("flowRate");
		super.load(state, compound);
	}
	
	@Override
	public int maxFlow() {
		return this.maxFlow;
	}
	
}
