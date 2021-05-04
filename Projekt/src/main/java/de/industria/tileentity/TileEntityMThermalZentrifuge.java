package de.industria.tileentity;

import java.util.Optional;

import de.industria.blocks.BlockMThermalZentrifuge;
import de.industria.dynamicsounds.ISimpleMachineSound;
import de.industria.gui.ContainerMThermalZentrifuge;
import de.industria.recipetypes.ThermalZentrifugeRecipe;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IElectricConnectiveBlock.Voltage;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.handler.ItemStackHelper;
import de.industria.util.handler.MachineSoundHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityMThermalZentrifuge extends TileEntityInventoryBase implements ITickableTileEntity, ISidedInventory, ISimpleMachineSound, INamedContainerProvider {
	
	public int progress;
	public int progressTotal;
	public float temp;
	
	public boolean isWorking;
	public boolean hasPower;
	
	public TileEntityMThermalZentrifuge() {
		super(ModTileEntityType.THERMAL_ZENTRIFUGE, 4);
	}
	
	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			ElectricityNetworkHandler.getHandlerForWorld(world).updateNetwork(world, pos);
			
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(world).getNetwork(pos);
			this.hasPower = network.canMachinesRun() == Voltage.HightVoltage;
			this.isWorking = canWork() && this.hasPower;

			if (getBlockState().get(BlockMThermalZentrifuge.ACTIVE) != this.isWorking) this.world.setBlockState(pos, getBlockState().with(BlockMThermalZentrifuge.ACTIVE, this.isWorking));
			
			if (this.isWorking) {
				
				ThermalZentrifugeRecipe recipe = findRecipe();
				if (recipe != null) {
					
					if (ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(1), recipe.getRecipeOutput()) &&
						ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(2), recipe.getRecipeOutput2()) &&
						ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(3), recipe.getRecipeOutput3())) {
						
						this.progressTotal = recipe.getRifiningTime();
						
						if (this.temp == this.progress / (float) this.progressTotal) {
							
							this.progress++;
							this.temp = this.progress / (float) this.progressTotal;

							if (this.progress >= this.progressTotal) {
								
								if (this.getStackInSlot(1).isEmpty()) {
									this.setInventorySlotContents(1, recipe.getRecipeOutput());
								} else {
									this.getStackInSlot(1).grow(recipe.getRecipeOutput().getCount());
								}

								if (this.getStackInSlot(2).isEmpty()) {
									this.setInventorySlotContents(2, recipe.getRecipeOutput2());
								} else {
									this.getStackInSlot(2).grow(recipe.getRecipeOutput2().getCount());
								}

								if (this.getStackInSlot(3).isEmpty()) {
									this.setInventorySlotContents(3, recipe.getRecipeOutput3());
								} else {
									this.getStackInSlot(3).grow(recipe.getRecipeOutput3().getCount());
								}
								
								this.getStackInSlot(0).shrink(recipe.getIngredient().getCount());
								
								this.progress = 0;
								
							}
							
							return;
							
						}
						
					}
					
				}						
				
			}
			
			this.progress = 0;
			if (this.temp > 0) this.temp -= 0.004F;
			if (this.temp < 0) this.temp = 0F;
			
		} else {
			
			if (this.isWorking && this.progress > 0) {
				
				MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.THERMAL_ZENTRIFUGE_LOOP);
				
			}
			
		}
		
	}
	
	@Override
	public boolean isSoundRunning() {
		return this.isWorking && this.progress > 0;
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1, 2, 3};
	}
	
	public boolean canWork() {
		return findRecipe() != null;
	}
	
	public ThermalZentrifugeRecipe findRecipe() {
		Optional<ThermalZentrifugeRecipe> recipe = this.world.getRecipeManager().getRecipe(ModRecipeTypes.THERMAL_ZENTRIFUGE, this, this.world);
		if (recipe.isPresent()) {
			return recipe.get();
		} else {
			return null;
		}
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return index == 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return index > 0;
	}

	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMThermalZentrifuge(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.thermal_zentrifuge");
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("progress", this.progress);
		compound.putInt("progressTotal", this.progressTotal);
		compound.putBoolean("isWorking", this.isWorking);
		compound.putBoolean("hasPower", this.hasPower);
		compound.putFloat("temp", this.temp);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.progress = compound.getInt("progress");
		this.progressTotal = compound.getInt("progressTotal");
		this.hasPower = compound.getBoolean("hasPower");
		this.isWorking = compound.getBoolean("isWorking");
		this.temp = compound.getFloat("temp");
		super.read(state, compound);
	}
	
}
