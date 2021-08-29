package de.industria.tileentity;

import de.industria.multipartbuilds.MultipartBuild.MultipartBuildLocation;
import de.industria.typeregistys.ModFluids;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.ITEFluidConnective;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMGasHeater extends TileEntityMHeaterBase implements ITEFluidConnective {
	
	public FluidStack gasStorage;
	public final int maxFluidStorage;
	
	public TileEntityMGasHeater() {
		super(ModTileEntityType.GAS_HEATER, 0);
		this.maxFluidStorage = 1000;
		this.gasStorage = FluidStack.EMPTY;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {};
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return false;
	}

	@Override
	public void updateWorkState() {
		
		if (this.gasStorage.getAmount() >= 10) {
			this.gasStorage.shrink(10);
			this.isWorking = true;
		} else {
			this.isWorking = false;
		}
		
	}

	@Override
	public boolean canWork() {
		return this.powered;
	}

	public FluidStack getFluid(int amount) {
		return FluidStack.EMPTY;
	}
	
	@Override
	public FluidStack insertFluid(FluidStack fluid) {
		if (this.gasStorage.getFluid().isSame(fluid.getFluid()) || this.gasStorage.isEmpty()) {
			int capcaity = this.maxFluidStorage - this.gasStorage.getAmount();
			int transfer = Math.min(capcaity, fluid.getAmount());
			if (transfer > 0) {
				FluidStack fluidRest = fluid.copy();
				fluidRest.shrink(transfer);
				if (this.gasStorage.isEmpty()) {
					this.gasStorage = new FluidStack(fluid.getFluid(), transfer);
				} else {
					this.gasStorage.grow(transfer);
				}
				return fluidRest;
			}
		}
		return fluid;
	}

	@Override
	public Fluid getFluidType() {
		return ModFluids.FUEL_GAS;
	}

	@Override
	public FluidStack getStorage() {
		return this.gasStorage;
	}
	
	@Override
	public boolean canConnect(Direction side) {
		return side == getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.put("Fuel", this.gasStorage.writeToNBT(new CompoundNBT()));
		compound.put("BuildData", this.buildData.writeNBT(new CompoundNBT()));
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.gasStorage = FluidStack.loadFluidStackFromNBT(compound.getCompound("Fuel"));
		this.buildData = MultipartBuildLocation.loadNBT(compound.getCompound("BuildData"));
		super.load(state, compound);
	}

	@Override
	public void setStorage(FluidStack storage) {
		this.gasStorage = storage;
	}
	
	public MultipartBuildLocation buildData = MultipartBuildLocation.EMPTY;
	public void storeBuildData(MultipartBuildLocation buildData) {
		this.buildData = buildData;
	}
	
}
