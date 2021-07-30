package de.industria.tileentity;

import de.industria.blocks.BlockFluidPipe;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.ITEFluidConnective;
import de.industria.util.blockfeatures.ITEFluidWiring;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityFluidPipe extends TileEntity implements ITEFluidWiring, ITickableTileEntity {
	
	protected int maxFluid;
	protected FluidStack fluid;
	
	public TileEntityFluidPipe() {
		this(ModTileEntityType.FLUID_PIPE);
	}
	
	public TileEntityFluidPipe(TileEntityType<?> type) {
		super(type);
		this.fluid = FluidStack.EMPTY;
		this.maxFluid = 100;
	}
	
	@Override
	public FluidStack getFluid(int amount) {
		if (!this.fluid.isEmpty()) {
			int transfer = Math.min(this.fluid.getAmount(), amount);
			FluidStack fluidOut = new FluidStack(this.fluid.getFluid(), transfer);
			fluidOut.setTag(this.fluid.getTag());
			this.fluid.shrink(transfer);
			return fluidOut;
		}
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
				if (this.fluid.getAmount() == this.maxFluid && !rest.isEmpty()) rest = pushFluidThrougPipes(rest, null, this.level, this.worldPosition);
				return rest;
			} else if (this.fluid.getFluid() == fluid.getFluid()) {
				int capacity = this.maxFluid - this.fluid.getAmount();
				int transfer = Math.min(capacity, fluid.getAmount());
				this.fluid.grow(transfer);
				this.fluid.setTag(fluid.getTag());
				FluidStack rest = fluid.copy();
				rest.shrink(transfer);
				if (this.fluid.getAmount() == this.maxFluid && !rest.isEmpty()) rest = pushFluidThrougPipes(rest, null, this.level, this.worldPosition);
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
	public CompoundNBT save(CompoundNBT compound) {
		if (!this.fluid.isEmpty()) compound.put("Fluid", this.fluid.writeToNBT(new CompoundNBT()));
		compound.putInt("MaxFlow", this.maxFluid);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.fluid = FluidStack.loadFluidStackFromNBT(compound.getCompound("Fluid"));
		this.maxFluid = compound.getInt("MaxFlow");
		if (this.maxFluid == 0) this.maxFluid = state.getBlock() instanceof BlockFluidPipe ? ((BlockFluidPipe) state.getBlock()).getMaxFlow() : 0;
		super.load(state, compound);
	}
	
	@Override
	public void tick() {
		
		if (!this.level.isClientSide && !this.fluid.isEmpty()) {
			
			int inputs = 0;
			for (Direction d : Direction.values()) {
				TileEntity te = this.level.getBlockEntity(worldPosition.relative(d));
				if (te instanceof ITEFluidWiring) {
					
					ITEFluidWiring pipe = (ITEFluidWiring) te;
					if (pipe.getFluidType() == this.getFluidType() || pipe.getStorage().isEmpty()) inputs++;
					
				}
			}
			
			if (inputs > 0) {
				
				for (Direction d : Direction.values()) {
					TileEntity te = this.level.getBlockEntity(worldPosition.relative(d));
					if (te instanceof ITEFluidWiring) {

						ITEFluidWiring pipe = (ITEFluidWiring) te;
						
						if (pipe.getStorage().isEmpty() || this.getFluidType() == pipe.getFluidType()) {
							
							int differenz = this.fluid.getAmount() - pipe.getStorage().getAmount();
							
							if (differenz > 0) {
								
								int transfer = differenz / 2;
								
								transfer = Math.min(this.fluid.getAmount() / inputs, transfer);
								int maxFlow = pipe.maxFlow();
								transfer = transfer < 0 ? Math.max(transfer, -maxFlow) : Math.min(transfer, maxFlow);
								
								this.fluid.shrink(transfer);
								
								if (pipe.getStorage().isEmpty()) {
									pipe.setStorage(new FluidStack(this.getFluidType(), transfer));
								} else {
									pipe.getStorage().grow(transfer);
								}
								pipe.getStorage().setTag(this.fluid.getTag());
								
							}
							
						}
						
					}
					
				}
				
			}
			
			if (!this.fluid.isEmpty()) {
				
				for (Direction d : Direction.values()) {
					
					TileEntity te = this.level.getBlockEntity(worldPosition.relative(d));
					
					if (te instanceof ITEFluidConnective && !(te instanceof TileEntityFluidPipe)) {
						
						ITEFluidConnective device = (ITEFluidConnective) te;
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

	public void clear() {
		this.fluid = FluidStack.EMPTY;
	}

	@Override
	public FluidStack getStorage() {
		return this.fluid;
	}

	@Override
	public void setStorage(FluidStack storage) {
		this.fluid = storage;
	}

	@Override
	public int maxFlow() {
		return this.maxFluid;
	}
	
}
