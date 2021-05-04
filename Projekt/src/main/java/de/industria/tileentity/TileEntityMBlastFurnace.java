package de.industria.tileentity;

import java.util.Optional;

import de.industria.blocks.BlockMBlastFurnace;
import de.industria.blocks.BlockMultiPart;
import de.industria.dynamicsounds.ISimpleMachineSound;
import de.industria.recipetypes.BlastFurnaceRecipe;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IElectricConnectiveBlock.Voltage;
import de.industria.util.blockfeatures.IFluidConnective;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityMBlastFurnace extends TileEntityInventoryBase implements ITickableTileEntity, ISimpleMachineSound, INamedContainerProvider, IFluidConnective, ISidedInventory {
	
	public int maxFluidStorage;
	public FluidStack fluidStorage;
	
	public boolean hasPower;
	public boolean isWorking;
	public int progress;
	public int progressTotal;

	@Override
	public void tick() {
		
		if (BlockMultiPart.getInternPartPos(this.getBlockState()).equals(BlockPos.ZERO)) {
			
			if (!this.world.isRemote()) {
				
				this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
				ElectricityNetworkHandler.getHandlerForWorld(world).updateNetwork(world, pos);
				
				ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(world).getNetwork(pos);
				this.hasPower = network.canMachinesRun() == Voltage.NormalVoltage;
				this.isWorking = this.hasPower && this.canWork();
				
				if (this.isWorking) {
					
					BlastFurnaceRecipe recipe = findRecipe();
					
					if (recipe != null) {
						
						this.progressTotal = recipe.getSmeltingTime();
						
						if (ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(3), recipe.getRecipeOutput()) &&
							ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(4), recipe.getWasteOut())) {
							
							this.progress++;
							
							if (this.progress >= this.progressTotal) {
								
								this.progress = 0;
								
								if (this.progress > this.progressTotal) {
									
									this.progress = 0;
									
									for (ItemStack item : recipe.getItemsIn()) {
										for (int i = 0; i < 3; i++) {
											if (this.getStackInSlot(i).getItem() == item.getItem() && this.getStackInSlot(i).getCount() >= item.getCount()) {
												this.decrStackSize(i, item.getCount());
											}
										}
									}
									
									this.fluidStorage.shrink(recipe.getConsumtionFluid().getAmount());
									
									if (this.getStackInSlot(3).isEmpty()) {
										this.setInventorySlotContents(3, recipe.getCraftingResult(this));
									} else {
										this.getStackInSlot(3).grow(recipe.getCraftingResult(this).getCount());
									}

									if (this.getStackInSlot(4).isEmpty()) {
										this.setInventorySlotContents(3, recipe.getWasteOut());
									} else {
										this.getStackInSlot(4).grow(recipe.getWasteOut().getCount());
									}
									
								}
								
							}
							
						}
						
					}
					
				} else {
					
					if (this.progress > 0) this.progress -= 2;
					
				}
				
			} else {

				MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.BLENDER_LOOP); // TODO
				
			}
			
		}
		
	}
	
	public TileEntityMBlastFurnace() {
		super(ModTileEntityType.BLAST_FURNACE, 5);
	}
	
	public boolean canWork() {
		return findRecipe() != null;
	}
	
	public BlastFurnaceRecipe findRecipe() {
		Optional<BlastFurnaceRecipe> recipe = this.world.getRecipeManager().getRecipe(ModRecipeTypes.BLAST_FURNACE, this, this.world);
		return recipe.isPresent() ? recipe.get() : null;
	}
	
	@Override
	public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FluidStack getFluid(int amount) {
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMBlastFurnace.getSCenterTE(ipos, this.getBlockState(), world);
		if (tileEntity instanceof TileEntityMBlastFurnace) {
			if (ipos.equals(new BlockPos(2, 1, 0))) {
				int transfer = Math.min(amount, ((TileEntityMBlastFurnace) tileEntity).fluidStorage.getAmount());
				if (transfer > 0) {
					FluidStack fluidOut = new FluidStack(((TileEntityMBlastFurnace) tileEntity).fluidStorage.getFluid(), transfer);
					((TileEntityMBlastFurnace) tileEntity).fluidStorage.shrink(transfer);
					return fluidOut;
				}
			}
		}
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack insertFluid(FluidStack fluid) {
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMBlastFurnace.getSCenterTE(pos, this.getBlockState(), world);
		if (tileEntity instanceof TileEntityMBlastFurnace) {
			if (ipos.equals(new BlockPos(2, 2, 2))) {
				if (((TileEntityMBlastFurnace) tileEntity).fluidStorage.getFluid().isEquivalentTo(fluid.getFluid()) || ((TileEntityMBlastFurnace) tileEntity).fluidStorage.isEmpty()) {
					int capcaity = this.maxFluidStorage - ((TileEntityMBlastFurnace) tileEntity).fluidStorage.getAmount();
					int transfer = Math.min(capcaity, fluid.getAmount());
					if (transfer > 0) {
						FluidStack fluidRest = fluid.copy();
						fluidRest.shrink(transfer);
						if (((TileEntityMBlastFurnace) tileEntity).fluidStorage.isEmpty()) {
							((TileEntityMBlastFurnace) tileEntity).fluidStorage = new FluidStack(fluid.getFluid(), transfer);
						} else {
							((TileEntityMBlastFurnace) tileEntity).fluidStorage.grow(transfer);
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
		TileEntity tileEntity = BlockMBlastFurnace.getSCenterTE(ipos, this.getBlockState(), world);
		if (tileEntity instanceof TileEntityMBlastFurnace) {
			if (ipos.equals(new BlockPos(2, 2, 2))) {
				return ((TileEntityMBlastFurnace) tileEntity).fluidStorage.getFluid();
			}
		}
		return Fluids.EMPTY;
	}
	
	@Override
	public FluidStack getStorage() {
		BlockPos ipos = BlockMultiPart.getInternPartPos(this.getBlockState());
		TileEntity tileEntity = BlockMBlastFurnace.getSCenterTE(ipos, this.getBlockState(), world);
		if (tileEntity instanceof TileEntityMBlastFurnace) {
			if (ipos.equals(new BlockPos(2, 2, 2))) {
				return ((TileEntityMBlastFurnace) tileEntity).fluidStorage;
			}
		}
		return FluidStack.EMPTY;
	}
	
	@Override
	public boolean canConnect(Direction side) {
		BlockPos ipos =  BlockMultiPart.getInternPartPos(this.getBlockState());
		Direction facing = this.getBlockState().get(BlockMultiPart.FACING);
		return ipos.equals(new BlockPos(2, 0, 0)) && side == facing;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.blast_furnace");
	}

	@Override
	public boolean isSoundRunning() {
		return this.isWorking;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		BlockPos internPartPos = BlockMultiPart.getInternPartPos(this.getBlockState());
		if (internPartPos.equals(new BlockPos(1, 4, 1))) return new int[] {0, 1, 2};
		if (internPartPos.equals(new BlockPos(1, 0, 0))) return new int[] {2, 3};
		return new int[] {};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return index >= 0 && index <= 2;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return index >= 3 && index <= 4;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putBoolean("hasPower", this.hasPower);
		compound.putBoolean("isWorking", this.isWorking);
		compound.putInt("progressTotal", this.progressTotal);
		compound.putInt("Progress", this.progress);
		compound.put("Fluid", this.fluidStorage.writeToNBT(new CompoundNBT()));
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.hasPower = compound.getBoolean("hasPower");
		this.isWorking = compound.getBoolean("isWorking");
		this.progressTotal = compound.getInt("progressTotal");
		this.progress = compound.getInt("Progress");
		this.fluidStorage = FluidStack.loadFluidStackFromNBT(compound.getCompound("Fluid"));
		super.read(state, compound);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, this.serializeNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getNbtCompound());
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-3, -1, -3), pos.add(4, 5, 4));
	}
	
}
