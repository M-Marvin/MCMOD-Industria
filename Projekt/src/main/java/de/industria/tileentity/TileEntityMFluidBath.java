package de.industria.tileentity;

import java.util.Optional;

import de.industria.blocks.BlockMFluidBath;
import de.industria.blocks.BlockMultiPart;
import de.industria.dynamicsounds.ISimpleMachineSound;
import de.industria.gui.ContainerMFluidBath;
import de.industria.recipetypes.FluidBathRecipe;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IElectricConnectiveBlock.Voltage;
import de.industria.util.blockfeatures.IFluidConnective;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.handler.FluidBucketHelper;
import de.industria.util.handler.ItemStackHelper;
import de.industria.util.handler.MachineSoundHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMFluidBath extends TileEntityInventoryBase implements IFluidConnective, ISidedInventory, INamedContainerProvider, ISimpleMachineSound, ITickableTileEntity {
	
	public FluidStack fluidIn;
	public FluidStack fluidOut;
	public float fluidBufferState;
	public int progress;
	public int progressTotal;
	public boolean hasPower;
	public boolean isWorking;
	public FluidBathRecipe lastRecipe;
	
	public final int maxFluidStorage;
	
	public TileEntityMFluidBath() {
		super(ModTileEntityType.FLUID_BATH, 6);
		this.fluidIn = FluidStack.EMPTY;
		this.fluidOut = FluidStack.EMPTY;
		this.maxFluidStorage = 3000;
	}

	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			if (BlockMultiPart.getInternPartPos(getBlockState()).equals(BlockPos.ZERO)) {

				ElectricityNetworkHandler.getHandlerForWorld(world).updateNetwork(world, pos);
				ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(world).getNetwork(pos);
				this.hasPower = network.canMachinesRun() == Voltage.NormalVoltage;
				this.isWorking = this.canWork() && this.hasPower;

				this.fluidIn = FluidBucketHelper.transferBuckets(this, 2, this.fluidIn, this.maxFluidStorage);
				this.fluidOut = FluidBucketHelper.transferBuckets(this, 4, this.fluidOut, this.maxFluidStorage);
				
				this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
				
				if (this.isWorking) {
					
					FluidBathRecipe recipe = this.findRecipe();
					
					if (recipe != null) {
						if (this.lastRecipe == null) {
							this.lastRecipe = recipe;
						} else if (recipe.getFluidIn().getFluid() != this.lastRecipe.getFluidIn().getFluid()) {
							this.lastRecipe = recipe;
							this.fluidBufferState = 0;
						}
					}
					
					if (this.fluidBufferState < 0.01F) {
						this.fluidBufferState = 1;
						this.fluidIn.shrink(1500);
					}
					
					if (lastRecipe != null && this.fluidBufferState > 0.01F) {
						
						this.progressTotal = lastRecipe.getProcessTime();
						
						if (	ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(1), lastRecipe.getRecipeOutput()) &&
								ItemStackHelper.canMergeRecipeFluidStacks(this.fluidOut, lastRecipe.getFluidOut(), this.maxFluidStorage)) {
							
							this.progress++;
							this.fluidBufferState -= this.lastRecipe.getFluidIn().getAmount() / 1500F / (float) this.lastRecipe.getProcessTime();
							
							if (this.progress >= this.progressTotal) {
								
								this.progress = 0;
								
								this.getStackInSlot(0).shrink(lastRecipe.getItemIn().getCount());
								
								if (this.getStackInSlot(1).isEmpty()) {
									this.setInventorySlotContents(1, lastRecipe.getRecipeOutput());
								} else {
									this.getStackInSlot(1).grow(lastRecipe.getRecipeOutput().getCount());
								}
								
								if (this.fluidOut.isEmpty()) {
									this.fluidOut = lastRecipe.getFluidOut().copy();
								} else {
									this.fluidOut.grow(lastRecipe.getFluidOut().getAmount());
								}
								
							}
							
						}
						
						return;
						
					}
					
				}
				
				if (this.progress > 0) this.progress--;
				
			} else if (BlockMultiPart.getInternPartPos(this.getBlockState()).equals(new BlockPos(0, 1, 0))) {
				TileEntityMFluidBath tileEntity = (TileEntityMFluidBath) BlockMultiPart.getSCenterTE(pos, getBlockState(), world);
				if (tileEntity != null) {
					FluidStack rest = pushFluid(tileEntity.fluidOut, world, pos);
					if (rest != tileEntity.fluidOut) tileEntity.fluidOut = rest;
				}
			}
			
		} else {
			
			if (this.isWorking) {
				
				MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.RAFFINERY_LOOP);
				
			}
			
		}
		
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put("fluidIn", this.fluidIn.writeToNBT(new CompoundNBT()));
		compound.put("fluidOut", this.fluidOut.writeToNBT(new CompoundNBT()));
		compound.putInt("process", this.progress);
		compound.putInt("progressTotal", this.progressTotal);
		compound.putFloat("fluidBuffer", this.fluidBufferState);
		compound.putBoolean("hasPower", this.hasPower);
		compound.putBoolean("isWorking", this.isWorking);
		if (this.lastRecipe != null) compound.putString("Recipe", this.lastRecipe.getId().toString());
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.fluidIn = FluidStack.loadFluidStackFromNBT(compound.getCompound("fluidIn"));
		this.fluidOut = FluidStack.loadFluidStackFromNBT(compound.getCompound("fluidOut"));
		this.progress = compound.getInt("process");
		this.progressTotal = compound.getInt("progressTotal");
		this.fluidBufferState = compound.getFloat("fluidBuffer");
		this.hasPower = compound.getBoolean("hasPower");
		this.isWorking = compound.getBoolean("isWorking");
		if (compound.contains("Recipe") && this.world != null) {
			Optional<? extends IRecipe<?>> recipe = this.world.getRecipeManager().getRecipe(new ResourceLocation(compound.getString("Recipe")));
			if (recipe.isPresent()) this.lastRecipe = (FluidBathRecipe) recipe.get();
		}
		super.read(state, compound);
	}
	
	public boolean canWork() {
		return this.findRecipe() != null && (this.fluidIn.getAmount() >= 1500 || this.fluidBufferState > 0);
	}
	
	public FluidBathRecipe findRecipe() {
		Optional<FluidBathRecipe> recipe = this.world.getRecipeManager().getRecipe(ModRecipeTypes.FLUID_BATH, this, this.world);
		return recipe.isPresent() ? recipe.get() : null;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return index == 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return index == 1;
	}
	
	@Override
	public FluidStack getFluid(int amount) {
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMFluidBath.getSCenterTE(ipos, this.getBlockState(), world);
		if (tileEntity instanceof TileEntityMFluidBath) {
			if (ipos.equals(new BlockPos(0, 1, 0))) {
				int transfer = Math.min(amount, ((TileEntityMFluidBath) tileEntity).fluidOut.getAmount());
				if (transfer > 0) {
					FluidStack fluidOut = new FluidStack(((TileEntityMFluidBath) tileEntity).fluidOut.getFluid(), transfer);
					((TileEntityMFluidBath) tileEntity).fluidOut.shrink(transfer);
					return fluidOut;
				}
			}
		}
		return FluidStack.EMPTY;
	}
	
	@Override
	public FluidStack insertFluid(FluidStack fluid) {
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMFluidBath.getSCenterTE(pos, this.getBlockState(), world);
		if (tileEntity instanceof TileEntityMFluidBath) {
			if (ipos.equals(new BlockPos(1, 1, 2))) {
				if (((TileEntityMFluidBath) tileEntity).fluidIn.getFluid().isEquivalentTo(fluid.getFluid()) || ((TileEntityMFluidBath) tileEntity).fluidIn.isEmpty()) {
					int capcaity = this.maxFluidStorage - ((TileEntityMFluidBath) tileEntity).fluidIn.getAmount();
					int transfer = Math.min(capcaity, fluid.getAmount());
					if (transfer > 0) {
						FluidStack fluidRest = fluid.copy();
						fluidRest.shrink(transfer);
						if (((TileEntityMFluidBath) tileEntity).fluidIn.isEmpty()) {
							((TileEntityMFluidBath) tileEntity).fluidIn = new FluidStack(fluid.getFluid(), transfer);
						} else {
							((TileEntityMFluidBath) tileEntity).fluidIn.grow(transfer);
						}
						return fluidRest;
					}
				}
			}
		}
		return fluid;
	}
	
	@Override
	public Fluid getFluidType() {
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMFluidBath.getSCenterTE(ipos, this.getBlockState(), world);
		if (tileEntity instanceof TileEntityMFluidBath) {
			if (ipos.equals(new BlockPos(1, 1, 2))) {
				return ((TileEntityMFluidBath) tileEntity).fluidIn.getFluid();
			} else if (ipos.equals(new BlockPos(0, 1, 0))) {
				return ((TileEntityMFluidBath) tileEntity).fluidOut.getFluid();
			}
		}
		return Fluids.EMPTY;
	}

	@Override
	public FluidStack getStorage() {
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMFluidBath.getSCenterTE(ipos, this.getBlockState(), world);
		if (tileEntity instanceof TileEntityMFluidBath) {
			if (ipos.equals(new BlockPos(1, 1, 2))) {
				return ((TileEntityMFluidBath) tileEntity).fluidIn;
			} else if (ipos.equals(new BlockPos(0, 1, 0))) {
				return ((TileEntityMFluidBath) tileEntity).fluidOut;
			}
		}
		return FluidStack.EMPTY;
	}

	@Override
	public boolean canConnect(Direction side) {
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMFluidBath.getSCenterTE(pos, this.getBlockState(), world);
		if (tileEntity instanceof TileEntityMFluidBath) {
			return (ipos.equals(new BlockPos(1, 1, 2)) || ipos.equals(new BlockPos(0, 1, 0))) && side == Direction.UP;
		}
		return false;
	}

	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMFluidBath(id, playerInv, this);
	}

	@Override
	public boolean isSoundRunning() {
		return this.isWorking;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.fluid_bath");
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-4, -1, -4), pos.add(4, 3, 4));
	}
	
}
