package de.industria.tileentity;

import java.util.Optional;

import de.industria.blocks.BlockMultipart;
import de.industria.gui.ContainerMRaffinery;
import de.industria.recipetypes.RifiningRecipe;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import de.industria.util.DataWatcher;
import de.industria.util.blockfeatures.ITEFluidConnective;
import de.industria.util.blockfeatures.ITESimpleMachineSound;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.types.MultipartBuild.MultipartBuildLocation;
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

public class TileEntityMRaffinery extends TileEntityInventoryBase implements ITickableTileEntity, ITESimpleMachineSound, ISidedInventory, ITEFluidConnective, INamedContainerProvider {
	
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
		DataWatcher.registerBlockEntity(this, (tileEntity, data) -> {
			if (data[0] != null) ((TileEntityMRaffinery) tileEntity).fluidIn = (FluidStack) data[0];
			if (data[1] != null) ((TileEntityMRaffinery) tileEntity).fluidOut = (FluidStack) data[1];
			if (data[2] != null) ((TileEntityMRaffinery) tileEntity).progress1 = (int) data[2];
			if (data[3] != null) ((TileEntityMRaffinery) tileEntity).progress2 = (int) data[3];
			if (data[4] != null) ((TileEntityMRaffinery) tileEntity).progress3 = (int) data[4];
			if (data[5] != null) ((TileEntityMRaffinery) tileEntity).progress4 = (int) data[5];
			if (data[6] != null) ((TileEntityMRaffinery) tileEntity).progressTotal = (int) data[6];
			if (data[7] != null) ((TileEntityMRaffinery) tileEntity).isWorking = (boolean) data[7];
			if (data[8] != null) ((TileEntityMRaffinery) tileEntity).hasPower = (boolean) data[8];
		}, () -> fluidIn, () -> fluidOut, () -> progress1, () -> progress2, () -> progress3, () -> progress4, () -> progressTotal, () -> isWorking, () -> hasPower);
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(worldPosition.offset(-5, -1, -5), worldPosition.offset(5, 6, 5));
	}
	
	@Override
	public void tick() {
		
		if (!this.level.isClientSide()) {
			
			if (BlockMultipart.getInternPartPos(this.getBlockState()).equals(BlockPos.ZERO)) {
				
				ElectricityNetworkHandler.getHandlerForWorld(level).updateNetwork(level, worldPosition);
				ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(this.level).getNetwork(this.worldPosition);
				this.hasPower = network.canMachinesRun() == Voltage.HightVoltage;
				this.isWorking = canWork() && this.hasPower;
				
				this.fluidIn = FluidBucketHelper.transferBuckets(this, 3, this.fluidIn, this.maxFluidStorage);
				this.fluidOut = FluidBucketHelper.transferBuckets(this, 5, this.fluidOut, this.maxFluidStorage);
				
				if (this.isWorking) {
					
					RifiningRecipe recipe = findRecipe();
					
					if (recipe != null) {
						this.currentRecipe = recipe;
					}
					
					if (this.currentRecipe != null) {
						
						if (	ItemStackHelper.canMergeRecipeStacks(this.getItem(0), currentRecipe.getResultItem()) &&
								ItemStackHelper.canMergeRecipeStacks(this.getItem(1), currentRecipe.getResultItem2()) &&
								ItemStackHelper.canMergeRecipeStacks(this.getItem(2), currentRecipe.getResultItem3()) &&
								(this.fluidOut.getFluid() == currentRecipe.fluidOut.getFluid() || this.fluidOut.isEmpty()) ? this.fluidOut.getAmount() + currentRecipe.fluidOut.getAmount() < this.maxFluidStorage : false) {
							
							this.progressTotal = currentRecipe.getRifiningTime() / 3;
							
							if (this.progress4 > 0) {
								
								this.progress4++;
								
								if (this.progress4 >= this.progressTotal) {
									
									this.progress4 = 0;
									
									if (this.fluidOut.isEmpty()) {
										this.fluidOut = currentRecipe.getResultItemFluid();
									} else {
										this.fluidOut.grow(currentRecipe.getResultItemFluid().getAmount());
									}
									
								}
								
							} else if (this.progress3 > 0) {
								
								this.progress3++;
								
								if (this.progress3 >= this.progressTotal) {
									
									this.progress3 = 0;
									if (!currentRecipe.getResultItemFluid().isEmpty()) this.progress4 = 1;
									
									if (this.getItem(2).isEmpty()) {
										this.setItem(2, currentRecipe.getResultItem3());
									} else {
										this.getItem(2).grow(currentRecipe.getResultItem3().getCount());
									}
									
								}
								
							} else if (this.progress2 > 0) {
								
								this.progress2++;
								
								if (this.progress2 >= this.progressTotal) {
									
									this.progress2 = 0;
									if (!currentRecipe.getResultItem3().isEmpty()) {
										this.progress3 = 1;
									} else {
										this.progress4 = 1;
									}
									
									if (this.getItem(1).isEmpty()) {
										this.setItem(1, currentRecipe.getResultItem2());
									} else {
										this.getItem(1).grow(currentRecipe.getResultItem2().getCount());
									}
									
								}
								
							} else {
								
								this.progress1++;
								
								if (this.progress1 >= this.progressTotal) {
									
									this.progress1 = 0;
									if (!currentRecipe.getResultItem2().isEmpty()) {
										this.progress2 = 1;
									} else {
										this.progress4 = 1;
									}
									
									if (this.getItem(0).isEmpty()) {
										this.setItem(0, currentRecipe.getResultItem());
									} else {
										this.getItem(0).grow(currentRecipe.getResultItem().getCount());
									}
									
									this.fluidIn.shrink(currentRecipe.fluidIn.getAmount());
									
								}
								
							}
							
						}
						
					}
					
				}
				
			} else if (BlockMultipart.getInternPartPos(this.getBlockState()).equals(new BlockPos(1, 3, 0))) {
				TileEntityMRaffinery tileEntity = (TileEntityMRaffinery) BlockMultipart.getSCenterTE(worldPosition, getBlockState(), level);
				if (tileEntity != null) {
					FluidStack rest = pushFluid(tileEntity.fluidOut, level, worldPosition);
					if (rest != tileEntity.fluidOut) tileEntity.fluidOut = rest;
				}
			}
			
		} else {

			MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.RAFFINERY_LOOP);
			
			if (this.isWorking) {
				
				IParticleData paricle = ParticleTypes.POOF;
				Direction facing = getBlockState().getValue(BlockMultipart.FACING);
				
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

				float x = this.worldPosition.getX() + ox + (level.random.nextFloat() + 3.0F) * width;
				float y = this.worldPosition.getY() + 2 + (level.random.nextFloat() + 5.5F) * height;
				float z = this.worldPosition.getZ() + oz + (level.random.nextFloat() + 1.0F) * width;
				this.level.addParticle(paricle, x, y, z, 0, 0, 0);
				
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
		if (this.currentRecipe == null) return false;
		return this.progress2 > 0 || this.progress3 > 0 || this.progress4 > 0 && (ItemStackHelper.canMergeRecipeStacks(this.getItem(0), currentRecipe.getResultItem()) &&
				ItemStackHelper.canMergeRecipeStacks(this.getItem(1), currentRecipe.getResultItem2()) &&
				ItemStackHelper.canMergeRecipeStacks(this.getItem(2), currentRecipe.getResultItem3())) && 
				(this.fluidOut.getFluid() == currentRecipe.fluidOut.getFluid() || this.fluidOut.isEmpty()) ? this.fluidOut.getAmount() + currentRecipe.fluidOut.getAmount() < this.maxFluidStorage : false;
	}
	
	public RifiningRecipe findRecipe() {
		Optional<RifiningRecipe> recipe = this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.RIFINING, this, this.level);
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
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return !this.isPostProcessing();
	}
	
	@Override
	public FluidStack getFluid(int amount) {
		BlockPos ipos =  BlockMultipart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMultipart.getSCenterTE(worldPosition, getBlockState(), level);
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
		BlockPos ipos =  BlockMultipart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMultipart.getSCenterTE(worldPosition, getBlockState(), level);
		if (tileEntity instanceof TileEntityMRaffinery) {
			if (ipos.equals(new BlockPos(1, 0, 1))) {
				if (((TileEntityMRaffinery) tileEntity).fluidIn.getFluid().isSame(fluid.getFluid()) || ((TileEntityMRaffinery) tileEntity).fluidIn.isEmpty()) {
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
		BlockPos ipos =  BlockMultipart.getInternPartPos(this.getBlockState());
		if (ipos.equals(new BlockPos(1, 0, 1))) {
			return this.fluidIn.getFluid();
		} else {
			return this.fluidOut.getFluid();
		}
	}

	@Override
	public FluidStack getStorage() {
		BlockPos ipos =  BlockMultipart.getInternPartPos(this.getBlockState());
		if (ipos.equals(new BlockPos(1, 0, 1))) {
			return this.fluidIn;
		} else {
			return this.fluidOut;
		}
	}
	
	@Override
	public boolean canConnect(Direction side) {
		BlockPos ipos =  BlockMultipart.getInternPartPos(this.getBlockState());
		Direction facing = this.getBlockState().getValue(BlockMultipart.FACING);
		return	(ipos.equals(new BlockPos(1, 0, 1)) && side == facing.getOpposite()) ||
				(ipos.equals(new BlockPos(1, 3, 0)) && side == facing);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
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
		compound.put("BuildData", this.buildData.writeNBT(new CompoundNBT()));
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
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
		if (compound.contains("Recipe") && this.level != null) {
			Optional<? extends IRecipe<?>> recipe = this.level.getRecipeManager().byKey(new ResourceLocation(compound.getString("Recipe")));
			if (recipe.isPresent()) this.currentRecipe = (RifiningRecipe) recipe.get();
		}
		this.buildData = MultipartBuildLocation.loadNBT(compound.getCompound("BuildData"));
		super.load(state, compound);
	}

	@Override
	public void setStorage(FluidStack storage) {
		BlockPos ipos =  BlockMultipart.getInternPartPos(this.getBlockState());
		if (ipos.equals(new BlockPos(1, 0, 1))) {
			this.fluidIn = storage;
		} else {
			this.fluidOut = storage;
		}
	}

	public MultipartBuildLocation buildData = MultipartBuildLocation.EMPTY;
	public void storeBuildData(MultipartBuildLocation buildData) {
		this.buildData = buildData;
	}

	public MultipartBuildLocation getBuildData() {
		return this.buildData;
	}
	
}
