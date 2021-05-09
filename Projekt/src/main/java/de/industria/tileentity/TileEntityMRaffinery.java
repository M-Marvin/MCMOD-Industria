package de.industria.tileentity;

import java.util.Optional;

import de.industria.blocks.BlockMultiPart;
import de.industria.dynamicsounds.ISimpleMachineSound;
import de.industria.gui.ContainerMRaffinery;
import de.industria.recipetypes.RifiningRecipe;
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
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMRaffinery extends TileEntityInventoryBase implements ITickableTileEntity, ISimpleMachineSound, ISidedInventory, IFluidConnective, INamedContainerProvider {
	
	public final int maxFluidStorage;
	
	public FluidStack fluidIn;
	public FluidStack fluidOut;
	
	public int progress1;
	public int progress2;
	public int progress3;
	public int progress4;
	public int progressTotal;
	public boolean isWorking;
	public boolean hasPower;
	public RifiningRecipe currentRecipe;
	
	public TileEntityMRaffinery() {
		super(ModTileEntityType.RAFFINERY, 7);
		this.maxFluidStorage = 3000;
		this.fluidIn = FluidStack.EMPTY;
		this.fluidOut = FluidStack.EMPTY;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-5, -1, -5), pos.add(5, 6, 5));
	}
	
	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			if (BlockMultiPart.getInternPartPos(this.getBlockState()).equals(BlockPos.ZERO)) {
				
				this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
				ElectricityNetworkHandler.getHandlerForWorld(world).updateNetwork(world, pos);
				
				this.fluidIn = FluidBucketHelper.transferBuckets(this, 3, this.fluidIn, this.maxFluidStorage);
				this.fluidOut = FluidBucketHelper.transferBuckets(this, 5, this.fluidOut, this.maxFluidStorage);
				
				ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(this.world).getNetwork(this.pos);
				
				this.hasPower = network.canMachinesRun() == Voltage.HightVoltage;
				this.isWorking = canWork() && this.hasPower;
				
				if (this.isWorking) {
					
					RifiningRecipe recipe = findRecipe();
					
					if (recipe != null) {
						this.currentRecipe = recipe;
					}
					
					if (this.currentRecipe != null) {
						
						if (	ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(0), currentRecipe.getRecipeOutput()) &&
								ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(1), currentRecipe.getRecipeOutput2()) &&
								ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(2), currentRecipe.getRecipeOutput3()) &&
								(this.fluidOut.getFluid() == currentRecipe.fluidOut.getFluid() || this.fluidOut.isEmpty()) ? this.fluidOut.getAmount() + currentRecipe.fluidOut.getAmount() < this.maxFluidStorage : false) {
							
							this.progressTotal = currentRecipe.getRifiningTime() / 3;
							
							if (this.progress4 > 0) {
								
								this.progress4++;
								
								if (this.progress4 >= this.progressTotal) {
									
									this.progress4 = 0;
									
									if (this.fluidOut.isEmpty()) {
										this.fluidOut = currentRecipe.getRecipeOutputFluid();
									} else {
										this.fluidOut.grow(currentRecipe.getRecipeOutputFluid().getAmount());
									}
									
								}
								
							} else if (this.progress3 > 0) {
								
								this.progress3++;
								
								if (this.progress3 >= this.progressTotal) {
									
									this.progress3 = 0;
									if (!currentRecipe.getRecipeOutputFluid().isEmpty()) this.progress4 = 1;
									
									if (this.getStackInSlot(2).isEmpty()) {
										this.setInventorySlotContents(2, currentRecipe.getRecipeOutput3());
									} else {
										this.getStackInSlot(2).grow(currentRecipe.getRecipeOutput3().getCount());
									}
									
								}
								
							} else if (this.progress2 > 0) {
								
								this.progress2++;
								
								if (this.progress2 >= this.progressTotal) {
									
									this.progress2 = 0;
									if (!currentRecipe.getRecipeOutput3().isEmpty()) {
										this.progress3 = 1;
									} else {
										this.progress4 = 1;
									}
									
									if (this.getStackInSlot(1).isEmpty()) {
										this.setInventorySlotContents(1, currentRecipe.getRecipeOutput2());
									} else {
										this.getStackInSlot(1).grow(currentRecipe.getRecipeOutput2().getCount());
									}
									
								}
								
							} else {
								
								this.progress1++;
								
								if (this.progress1 >= this.progressTotal) {
									
									this.progress1 = 0;
									if (!currentRecipe.getRecipeOutput2().isEmpty()) {
										this.progress2 = 1;
									} else {
										this.progress4 = 1;
									}
									
									if (this.getStackInSlot(0).isEmpty()) {
										this.setInventorySlotContents(0, currentRecipe.getRecipeOutput());
									} else {
										this.getStackInSlot(0).grow(currentRecipe.getRecipeOutput().getCount());
									}
									
									this.fluidIn.shrink(currentRecipe.fluidIn.getAmount());
									
								}
								
							}
							
						}
						
					}
					
				}
				
			} else if (BlockMultiPart.getInternPartPos(this.getBlockState()).equals(new BlockPos(1, 3, 0))) {
				TileEntityMRaffinery tileEntity = (TileEntityMRaffinery) BlockMultiPart.getSCenterTE(pos, getBlockState(), world);
				if (tileEntity != null) {
					FluidStack rest = pushFluid(tileEntity.fluidOut, world, pos);
					if (rest != tileEntity.fluidOut) tileEntity.fluidOut = rest;
				}
			}
			
		} else {
			
			if (this.isWorking) {
				
				IParticleData paricle = ParticleTypes.POOF;
				Direction facing = getBlockState().get(BlockMultiPart.FACING);
				
				int ox = 0;
				int oz = 0;
				
				switch(facing) {
				default:
				case NORTH:
					oz = 0;
					ox = 1;
					break;
				case EAST:
					ox = -1;
					oz = 2;
					break;
				case SOUTH:
					ox = -3;
					oz = 0;
					break;
				case WEST:
					ox = -1;
					oz = -2;
					break;
				}
				;
				float width = 0.4F;
				float height = 0.4F;

				float x = this.pos.getX() + ox + (world.rand.nextFloat() + 3.0F) * width;
				float y = this.pos.getY() + 2 + (world.rand.nextFloat() + 5.5F) * height;
				float z = this.pos.getZ() + oz + (world.rand.nextFloat() + 1.0F) * width;
				this.world.addParticle(paricle, x, y, z, 0, 0, 0);
				
				MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.RAFFINERY_LOOP);
				
			}
		}
		
	}
	
	@Override
	public boolean isSoundRunning() {
		return this.isWorking;
	}
	
	public boolean canWork() {
		return this.findRecipe() != null || isPostProcessing();
	}
	
	public boolean isPostProcessing() {
		return this.progress2 > 0 || this.progress3 > 0 || this.progress4 > 0;
	}
	
	public RifiningRecipe findRecipe() {
		Optional<RifiningRecipe> recipe = this.world.getRecipeManager().getRecipe(ModRecipeTypes.RIFINING, this, this.world);
		return recipe.isPresent() ? recipe.get() : null;
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMRaffinery(id, playerInv, this);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.raffinery");
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1, 2};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return !this.isPostProcessing();
	}
	
	@Override
	public FluidStack getFluid(int amount) {
		BlockPos ipos =  BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMultiPart.getSCenterTE(pos, getBlockState(), world);
		if (tileEntity instanceof TileEntityMRaffinery) {
			if (ipos.equals(new BlockPos(1, 3, 0))) {
				int transfer = Math.min(amount, ((TileEntityMRaffinery) tileEntity).fluidOut.getAmount());
				if (transfer > 0) {
					FluidStack fluidOut = new FluidStack(((TileEntityMRaffinery) tileEntity).fluidOut.getFluid(), transfer);
					((TileEntityMRaffinery) tileEntity).fluidOut.shrink(transfer);
					return fluidOut;
				}
			}
		}
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack insertFluid(FluidStack fluid) {
		BlockPos ipos =  BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMultiPart.getSCenterTE(pos, getBlockState(), world);
		if (tileEntity instanceof TileEntityMRaffinery) {
			if (ipos.equals(new BlockPos(1, 0, 1))) {
				if (((TileEntityMRaffinery) tileEntity).fluidIn.getFluid().isEquivalentTo(fluid.getFluid()) || ((TileEntityMRaffinery) tileEntity).fluidIn.isEmpty()) {
					int capcaity = this.maxFluidStorage - ((TileEntityMRaffinery) tileEntity).fluidIn.getAmount();
					int transfer = Math.min(capcaity, fluid.getAmount());
					if (transfer > 0) {
						FluidStack fluidRest = fluid.copy();
						fluidRest.shrink(transfer);
						if (((TileEntityMRaffinery) tileEntity).fluidIn.isEmpty()) {
							((TileEntityMRaffinery) tileEntity).fluidIn = new FluidStack(fluid.getFluid(), transfer);
						} else {
							((TileEntityMRaffinery) tileEntity).fluidIn.grow(transfer);
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
		BlockPos ipos =  BlockMultiPart.getInternPartPos(this.getBlockState());
		if (ipos.equals(new BlockPos(1, 0, 1))) {
			return this.fluidIn.getFluid();
		} else {
			return this.fluidOut.getFluid();
		}
	}

	@Override
	public FluidStack getStorage() {
		BlockPos ipos =  BlockMultiPart.getInternPartPos(this.getBlockState());
		if (ipos.equals(new BlockPos(1, 0, 1))) {
			return this.fluidIn;
		} else {
			return this.fluidOut;
		}
	}
	
	@Override
	public boolean canConnect(Direction side) {
		BlockPos ipos =  BlockMultiPart.getInternPartPos(this.getBlockState());
		Direction facing = this.getBlockState().get(BlockMultiPart.FACING);
		return	(ipos.equals(new BlockPos(1, 0, 1)) && side == facing.getOpposite()) ||
				(ipos.equals(new BlockPos(1, 3, 0)) && side == facing);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		if (!this.fluidIn.isEmpty()) compound.put("FluidIn", this.fluidIn.writeToNBT(new CompoundNBT()));
		if (!this.fluidOut.isEmpty()) compound.put("FluidOut", this.fluidOut.writeToNBT(new CompoundNBT()));
		compound.putBoolean("hasPower", this.hasPower);
		compound.putBoolean("isWorking", this.isWorking);
		compound.putInt("progress1", this.progress1);
		compound.putInt("progress2", this.progress2);
		compound.putInt("progress3", this.progress3);
		compound.putInt("progress4", this.progress4);
		compound.putInt("progressTotal", this.progressTotal);
		if (this.currentRecipe != null) compound.putString("Recipe", this.currentRecipe.getId().toString());
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.fluidIn = FluidStack.EMPTY;
		this.fluidOut = FluidStack.EMPTY;
		this.currentRecipe = null;
		if (compound.contains("FluidIn")) this.fluidIn = FluidStack.loadFluidStackFromNBT(compound.getCompound("FluidIn"));
		if (compound.contains("FluidOut")) this.fluidOut = FluidStack.loadFluidStackFromNBT(compound.getCompound("FluidOut"));
		this.hasPower = compound.getBoolean("hasPower");
		this.isWorking = compound.getBoolean("isWorking");
		this.progress1 = compound.getInt("progress1");
		this.progress2 = compound.getInt("progress2");
		this.progress3 = compound.getInt("progress3");
		this.progress4 = compound.getInt("progress4");
		this.progressTotal = compound.getInt("progressTotal");
		if (compound.contains("Recipe") && this.world != null) {
			Optional<? extends IRecipe<?>> recipe = this.world.getRecipeManager().getRecipe(new ResourceLocation(compound.getString("Recipe")));
			if (recipe.isPresent()) this.currentRecipe = (RifiningRecipe) recipe.get();
		}
		super.read(state, compound);
	}
	
}
