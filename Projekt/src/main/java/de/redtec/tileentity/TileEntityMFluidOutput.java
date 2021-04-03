package de.redtec.tileentity;

import java.util.ArrayList;

import de.redtec.blocks.BlockMFluidInput;
import de.redtec.fluids.util.BlockGasFluid;
import de.redtec.typeregistys.ModTileEntityType;
import de.redtec.util.blockfeatures.IFluidConnective;
import de.redtec.util.handler.FluidStackStateTagHelper;
import de.redtec.util.handler.FluidTankHelper;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMFluidOutput extends TileEntity implements ITickableTileEntity, IFluidConnective {
	
	private final int maxFluid;
	private FluidStack fluid;
	
	public TileEntityMFluidOutput() {
		super(ModTileEntityType.FLUID_OUTPUT);
		this.maxFluid = 1500;
		this.fluid = FluidStack.EMPTY;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		if (!this.fluid.isEmpty()) compound.put("Fluid", this.fluid.writeToNBT(new CompoundNBT()));
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compund) {
		this.fluid = FluidStack.loadFluidStackFromNBT(compund.getCompound("Fluid"));
		super.read(state, compund);
	}
	
	@Override
	public FluidStack getFluid(int amount) {
		return FluidStack.EMPTY;
	}
	
	@Override
	public FluidStack insertFluid(FluidStack fluid) {
		
		if (this.fluid.isEmpty()) {
			int transfer = Math.min(this.maxFluid, fluid.getAmount());
			this.fluid = new FluidStack(fluid.getFluid(), transfer);
			this.fluid.setTag(fluid.getTag());
			FluidStack rest = fluid.copy();
			rest.shrink(transfer);
			return rest;
		} else if (this.fluid.getFluid() == fluid.getFluid()) {
			int capacity = this.maxFluid - this.fluid.getAmount();
			int transfer = Math.min(capacity, fluid.getAmount());
			this.fluid.grow(transfer);
			this.fluid.setTag(fluid.getTag());
			FluidStack rest = fluid.copy();
			rest.shrink(transfer);
			return rest;
		}
		
		return fluid;
	}
	
	@Override
	public Fluid getFluidType() {
		return this.fluid.getFluid();
	}
	
	@Override
	public boolean canConnect(Direction side) {
		return side == this.getBlockState().get(BlockMFluidInput.FACING).getOpposite();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			if (this.fluid.getAmount() >= 1000) {
				
				BlockPos tankBeginPos = this.pos.offset(this.getBlockState().get(BlockMFluidInput.FACING));
				BlockPos outputPos = new FluidTankHelper(this.world, tankBeginPos).insertFluidInTank(this.fluid.getFluid());
				
				if (outputPos != null) {
					
					FluidState fluidState = FluidStackStateTagHelper.makeStateFromStack(this.fluid);
					
					this.world.setBlockState(outputPos, fluidState.getBlockState());
					this.fluid.shrink(1000);
					
				} else {
					
					BlockState replaceState = this.world.getBlockState(tankBeginPos);
					
					if (replaceState.getBlock() instanceof BlockGasFluid) {
						
						((BlockGasFluid) replaceState.getBlock()).pushFluid(new ArrayList<BlockPos>(), replaceState, (ServerWorld) this.world, tankBeginPos, world.rand);
						replaceState = this.world.getBlockState(tankBeginPos);
						
						if (replaceState.isAir()) {
							
							FluidState fluidState = FluidStackStateTagHelper.makeStateFromStack(this.fluid);
							
							this.world.setBlockState(tankBeginPos, fluidState.getBlockState());
							this.fluid.shrink(1000);
							
						}
						
					}
					
				}
				
			}
			
		}
		
	}

	@Override
	public FluidStack getStorage() {
		return this.fluid;
	}

}
