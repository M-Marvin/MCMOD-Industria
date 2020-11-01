package de.redtec.tileentity;

import de.redtec.blocks.BlockFluidInput;
import de.redtec.util.ElectricityNetworkHandler;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import de.redtec.util.IFluidConnective;
import de.redtec.util.ModSoundEvents;
import de.redtec.util.ModTileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityFluidInput extends TileEntity implements IFluidConnective, ITickableTileEntity {
	
	private int progress;
	private final int maxFluid;
	private FluidStack fluid;
	
	public TileEntityFluidInput() {
		super(ModTileEntityType.FLUID_INPUT);
		this.maxFluid = 1500;
		this.fluid = FluidStack.EMPTY;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		if (!this.fluid.isEmpty()) compound.put("Fluid", this.fluid.writeToNBT(new CompoundNBT()));
		compound.putInt("progress", this.progress);
		return super.write(compound);
	}
	
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compund) {
		this.fluid = FluidStack.loadFluidStackFromNBT(compund.getCompound("Fluid"));
		this.progress = compund.getInt("progress");
		super.func_230337_a_(state, compund);
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
			return rest;
		} else if (this.fluid.getFluid() == fluid.getFluid()) {
			int capacity = this.maxFluid - this.fluid.getAmount();
			int transfer = Math.min(capacity, fluid.getAmount());
			this.fluid.grow(transfer);
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
	public void tick() {
		if (!this.world.isRemote) {
			
			ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(world);
			handler.updateNetwork(world, pos);
			ElectricityNetwork network = handler.getNetwork(pos);
			
			if (network.canMachinesRun() && canSourceFluid()) {
				
				world.playSound(null, pos, ModSoundEvents.PUMP_LOOP, SoundCategory.BLOCKS, 1, 1);
				
				if (this.progress++ >= 40) {
					
					Direction facing = this.getBlockState().get(BlockFluidInput.FACING);
					FluidState fluidState = this.world.getFluidState(pos.offset(facing));
					
					if (this.fluid.isEmpty()) {
						this.fluid = new FluidStack(fluidState.getFluid(), 1000);
					} else {
						this.fluid.grow(1000);
					}
					
					this.world.setBlockState(pos.offset(facing), Blocks.AIR.getDefaultState());
					
					this.progress = 0;
					
				}
				
			}
			
			if (!this.fluid.isEmpty()) {
				
				this.fluid = this.pushFluid(this.fluid, world, pos);
				
			}
			
		}
	}

	public boolean canSourceFluid() {
		
		Direction direction = this.getBlockState().get(BlockFluidInput.FACING);
		FluidState fluidState = this.world.getFluidState(pos.offset(direction));
		
		if (!fluidState.isEmpty()) {
			
			Fluid fluid = fluidState.getFluid();
			if (fluid == this.fluid.getFluid()|| this.fluid.isEmpty()) {
				
				if (fluidState.isSource()) return this.fluid.getAmount() <= 500;
				
			}
			
		}
		
		return false;
		
	}

	@Override
	public boolean canConnect(Direction side) {
		return side == this.getBlockState().get(BlockFluidInput.FACING).getOpposite();
	}

}
