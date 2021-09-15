package de.industria.tileentity;

import java.util.ArrayList;

import de.industria.blocks.BlockMFluidInput;
import de.industria.entity.EntityFallingFluid;
import de.industria.fluids.util.BlockGasFluid;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.ITEFluidConnective;
import de.industria.util.handler.FluidStackStateTagHelper;
import de.industria.util.handler.FluidTankHelper;
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

public class TileEntityMFluidOutput extends TileEntity implements ITickableTileEntity, ITEFluidConnective {
	
	private final int maxFluid;
	private FluidStack fluid;
	
	public TileEntityMFluidOutput() {
		super(ModTileEntityType.FLUID_OUTPUT);
		this.maxFluid = 1500;
		this.fluid = FluidStack.EMPTY;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if (!this.fluid.isEmpty()) compound.put("Fluid", this.fluid.writeToNBT(new CompoundNBT()));
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compund) {
		this.fluid = FluidStack.loadFluidStackFromNBT(compund.getCompound("Fluid"));
		super.load(state, compund);
	}
	
	@Override
	public FluidStack getFluid(int amount) {
		return FluidStack.EMPTY;
	}
	
	@Override
	public FluidStack insertFluid(FluidStack fluid) {
		
		if (!fluid.isEmpty()) {
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
		}
		
		return fluid;
	}
	
	@Override
	public Fluid getFluidType() {
		return this.fluid.getFluid();
	}
	
	@Override
	public boolean canConnect(Direction side) {
		return side != this.getBlockState().getValue(BlockMFluidInput.FACING);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick() {
		
		if (!this.level.isClientSide()) {
			
			if (this.fluid.getAmount() >= 1000) {
				
				BlockPos tankBeginPos = this.worldPosition.relative(this.getBlockState().getValue(BlockMFluidInput.FACING));
				BlockPos outputPos = new FluidTankHelper(this.level, tankBeginPos).insertFluidInTank(this.fluid.getFluid());
				
				if (!this.fluid.getFluid().getAttributes().isGaseous()) {
					
					if (outputPos != null) {

						EntityFallingFluid fallingFluid = new EntityFallingFluid(this.level, tankBeginPos.getX() + 0.5F, tankBeginPos.getY(), tankBeginPos.getZ() + 0.5F, FluidStackStateTagHelper.makeStateFromStack(this.fluid));
						fallingFluid.setAirSpawned();
						this.level.addFreshEntity(fallingFluid);
						this.fluid.shrink(1000);
						
					}
					
				} else {
					
					if (outputPos != null) {
						
						FluidState fluidState = FluidStackStateTagHelper.makeStateFromStack(this.fluid);
						
						this.level.setBlockAndUpdate(outputPos, fluidState.createLegacyBlock());
						this.fluid.shrink(1000);
						
					} else {
						
						BlockState replaceState = this.level.getBlockState(tankBeginPos);
						
						if (replaceState.getBlock() instanceof BlockGasFluid) {
							
							((BlockGasFluid) replaceState.getBlock()).pushFluid(new ArrayList<BlockPos>(), replaceState, (ServerWorld) this.level, tankBeginPos, level.random);
							replaceState = this.level.getBlockState(tankBeginPos);
							
							if (replaceState.isAir()) {
								
								FluidState fluidState = FluidStackStateTagHelper.makeStateFromStack(this.fluid);
								
								this.level.setBlockAndUpdate(tankBeginPos, fluidState.createLegacyBlock());
								this.fluid.shrink(1000);
								
							}
							
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

	@Override
	public void setStorage(FluidStack storage) {
		this.fluid = storage;
	}

}
