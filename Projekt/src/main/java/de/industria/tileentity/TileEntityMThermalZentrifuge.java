package de.industria.tileentity;

import java.util.Optional;

import de.industria.gui.ContainerMThermalZentrifuge;
import de.industria.recipetypes.ThermalZentrifugeRecipe;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import de.industria.util.blockfeatures.ITESimpleMachineSound;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.types.MultipartBuild.MultipartBuildLocation;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityMThermalZentrifuge extends TileEntityInventoryBase implements ITickableTileEntity, ISidedInventory, ITESimpleMachineSound, INamedContainerProvider {
	
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
		
		if (!this.level.isClientSide()) {
			
			this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			ElectricityNetworkHandler.getHandlerForWorld(level).updateNetwork(level, worldPosition);
			
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(level).getNetwork(worldPosition);
			this.hasPower = network.canMachinesRun() == Voltage.HightVoltage;
			this.isWorking = canWork() && this.hasPower;
			
			if (this.isWorking) {
				
				ThermalZentrifugeRecipe recipe = findRecipe();
				if (recipe != null) {
					
					if (ItemStackHelper.canMergeRecipeStacks(this.getItem(1), recipe.getResultItem()) &&
						ItemStackHelper.canMergeRecipeStacks(this.getItem(2), recipe.getResultItem2()) &&
						ItemStackHelper.canMergeRecipeStacks(this.getItem(3), recipe.getResultItem3())) {
						
						this.progressTotal = recipe.getRifiningTime();
						
						if (this.temp == this.progress / (float) this.progressTotal) {
							
							this.progress++;
							this.temp = this.progress / (float) this.progressTotal;
							
							if (this.progress >= this.progressTotal) {
								
								if (this.getItem(1).isEmpty()) {
									this.setItem(1, recipe.getResultItem());
								} else {
									this.getItem(1).grow(recipe.getResultItem().getCount());
								}

								if (this.getItem(2).isEmpty()) {
									this.setItem(2, recipe.getResultItem2());
								} else {
									this.getItem(2).grow(recipe.getResultItem2().getCount());
								}

								if (this.getItem(3).isEmpty()) {
									this.setItem(3, recipe.getResultItem3());
								} else {
									this.getItem(3).grow(recipe.getResultItem3().getCount());
								}
								
								this.getItem(0).shrink(recipe.getIngredient().getCount());
								
								this.progress = 0;
								
							}
							
							return;
							
						}
						
					}
					
				}						
				
			}
			
			this.progress = 0;
			if (this.temp > 0) this.temp -= 0.02F;
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
		Optional<ThermalZentrifugeRecipe> recipe = this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.THERMAL_ZENTRIFUGE, this, this.level);
		if (recipe.isPresent()) {
			return recipe.get();
		} else {
			return null;
		}
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return index == 0;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
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
	public CompoundNBT save(CompoundNBT compound) {
		compound.putInt("Progress", this.progress);
		compound.putInt("progressTotal", this.progressTotal);
		compound.putBoolean("isWorking", this.isWorking);
		compound.putBoolean("hasPower", this.hasPower);
		compound.putFloat("Temp", this.temp);
		compound.put("BuildData", this.buildData.writeNBT(new CompoundNBT()));
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.progress = compound.getInt("Progress");
		this.progressTotal = compound.getInt("progressTotal");
		this.hasPower = compound.getBoolean("hasPower");
		this.isWorking = compound.getBoolean("isWorking");
		this.temp = compound.getFloat("Temp");
		this.buildData = MultipartBuildLocation.loadNBT(compound.getCompound("BuildData"));
		super.load(state, compound);
	}

	public MultipartBuildLocation buildData = MultipartBuildLocation.EMPTY;
	public void storeBuildData(MultipartBuildLocation buildData) {
		this.buildData = buildData;
	}

	public MultipartBuildLocation getBuildData() {
		return this.buildData;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(worldPosition.offset(-2, -1, -2), worldPosition.offset(2, 3, 2));
	}
	
}
