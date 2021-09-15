package de.industria.tileentity;

import de.industria.blocks.BlockMFluidInput;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.ITEFluidConnective;
import de.industria.util.blockfeatures.ITESimpleMachineSound;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.FluidStackStateTagHelper;
import de.industria.util.handler.FluidTankHelper;
import de.industria.util.handler.MachineSoundHelper;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMFluidInput extends TileEntity implements ITEFluidConnective, ITickableTileEntity, ITESimpleMachineSound {
	
	private int progress;
	private final int maxFluid;
	private FluidStack fluid;
	private Fluid filterFluid;
	
	public TileEntityMFluidInput() {
		super(ModTileEntityType.FLUID_INPUT);
		this.maxFluid = 1500;
		this.fluid = FluidStack.EMPTY;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if (!this.fluid.isEmpty()) compound.put("Fluid", this.fluid.writeToNBT(new CompoundNBT()));
		compound.putInt("progress", this.progress);
		if (this.filterFluid != null) compound.putString("FluidFilter", this.filterFluid.getRegistryName().toString());
		return super.save(compound);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void load(BlockState state, CompoundNBT compund) {
		this.fluid = FluidStack.loadFluidStackFromNBT(compund.getCompound("Fluid"));
		this.progress = compund.getInt("progress");
		if (compund.contains("FluidFilter")) this.filterFluid = Registry.FLUID.get(new ResourceLocation(compund.getString("FluidFilter")));
		if (this.filterFluid == Fluids.EMPTY) this.filterFluid = null;
		super.load(state, compund);
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
		return fluid;
	}
	
	@Override
	public Fluid getFluidType() {
		return this.fluid.getFluid();
	}
	
	public boolean setFilter(ItemStack bucketStack) {
		
		Item item = bucketStack.getItem();
		
		if (item instanceof BucketItem) {
			
			Fluid fluid = ((BucketItem) item).getFluid();
			
			if (fluid == Fluids.EMPTY) {
				this.filterFluid = null;
			} else {
				this.filterFluid = fluid;
			}
			
			return true;
			
		}
		
		return false;
		
	}
	
	@Override
	public void tick() {
		
		if (!this.level.isClientSide) {
			
			ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(level);
			handler.updateNetwork(level, worldPosition);
			ElectricityNetwork network = handler.getNetwork(worldPosition);
			
			if (network.canMachinesRun() == Voltage.NormalVoltage && canSourceFluid() && this.progress++ >= 5) {
				
				this.progress = 0;
				
				BlockPos tankBeginPos = this.worldPosition.relative(this.getBlockState().getValue(BlockMFluidInput.FACING));
				BlockPos sourcePos = new FluidTankHelper(this.level, tankBeginPos).extractFluidFromTank(this.filterFluid == null ? this.level.getFluidState(tankBeginPos).getType() : this.filterFluid);
				FluidState sourceFluid = this.level.getFluidState(sourcePos);
				FluidStack fluid = FluidStackStateTagHelper.makeStackFromState(sourceFluid);
				
				this.level.setBlockAndUpdate(sourcePos, Blocks.AIR.defaultBlockState());
				
				if (this.fluid.isEmpty()) {
					this.fluid = fluid;
				} else {
					this.fluid.grow(fluid.getAmount());
				}
				
				this.fluid.setTag(fluid.getTag());
				
			}
			
			if (!this.fluid.isEmpty()) {
				
				this.fluid = this.pushFluid(this.fluid, level, worldPosition);
				
			}
			
		} else {
			
			if (this.isSoundRunning()) {
				
				MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.PUMP_LOOP);
				
			}
			
			
		}
		
	}
	
	@Override
	public boolean isSoundRunning() {

		ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(level);
		ElectricityNetwork network = handler.getNetwork(worldPosition);
		
		return network.canMachinesRun() == Voltage.NormalVoltage && canSourceFluid();
		
	}
	
	public boolean canSourceFluid() {
		
		BlockPos tankBeginPos = this.worldPosition.relative(this.getBlockState().getValue(BlockMFluidInput.FACING));
		BlockPos sourceBlock = new FluidTankHelper(this.level, tankBeginPos).extractFluidFromTank(this.filterFluid == null ? this.level.getFluidState(tankBeginPos).getType() : this.filterFluid);
		
		if (sourceBlock != null) {
			
			FluidState sourceFluid = this.level.getFluidState(sourceBlock);
			FluidStack fluid = new FluidStack(sourceFluid.getType(), 1000);
			
			return this.fluid.isEmpty() || (this.maxFluid - this.fluid.getAmount() >= 1000 && this.fluid.getFluid().isSame(fluid.getFluid()));
			
		}
		
		return false;
		
	}

	@Override
	public boolean canConnect(Direction side) {
		return side != this.getBlockState().getValue(BlockMFluidInput.FACING);
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
