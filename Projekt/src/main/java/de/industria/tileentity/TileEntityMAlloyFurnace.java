package de.industria.tileentity;

import de.industria.gui.ContainerMAlloyFurnace;
import de.industria.recipetypes.AlloyRecipe;
import de.industria.typeregistys.ModRecipeTypes;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.DataWatcher;
import de.industria.util.blockfeatures.ITESimpleMachineSound;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
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
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityMAlloyFurnace extends TileEntityInventoryBase implements ITickableTileEntity, ITESimpleMachineSound, ISidedInventory, INamedContainerProvider {
	
	public boolean hasPower;
	public boolean isWorking;
	public int progressTotal;
	public int progress;
	
	public TileEntityMAlloyFurnace() {
		super(ModTileEntityType.ALLOY_FURNACE, 4);
		DataWatcher.registerBlockEntity(this, (tileEntity, data) -> {
			if (data[0] != null) ((TileEntityMAlloyFurnace) tileEntity).hasPower = (boolean) data[0];
			if (data[1] != null) ((TileEntityMAlloyFurnace) tileEntity).isWorking = (boolean) data[1];
			if (data[2] != null) ((TileEntityMAlloyFurnace) tileEntity).progressTotal = (int) data[2];
			if (data[3] != null) ((TileEntityMAlloyFurnace) tileEntity).progress = (int) data[3];
		}, () -> this.hasPower, () -> this.isWorking, () -> this.progressTotal, () -> this.progress);
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMAlloyFurnace(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.alloy_furnace");
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1, 2, 3};
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return index >= 0 && index <= 2;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return index == 3;
	}

	@Override
	public boolean isSoundRunning() {
		return this.isWorking;
	}

	@Override
	public void tick() {
		
		if (!this.level.isClientSide()) {
			
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(level).getNetwork(worldPosition);
			ElectricityNetworkHandler.getHandlerForWorld(level).updateNetwork(level, worldPosition);
			this.hasPower = network.canMachinesRun() == Voltage.HightVoltage;
			this.isWorking = this.hasPower && canWork();
			
			if (getBlockState().getValue(BlockStateProperties.LIT) != this.isWorking) this.level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockStateProperties.LIT, this.isWorking));
			
			if (this.isWorking) {
				
				AlloyRecipe recipe = findRecipe();
				
				if (recipe != null) {
					
					this.progressTotal = recipe.getSmeltingTime();
					
					if (ItemStackHelper.canMergeRecipeStacks(this.getItem(3), recipe.getResultItem())) {
						
						this.progress++;
						
						if (this.progress >= this.progressTotal) {
							
							for (ItemStack item : recipe.getItemsIn()) {
								for (int i = 0; i < 3; i++) {
									if (this.getItem(i).getItem() == item.getItem() && this.getItem(i).getCount() >= item.getCount()) {
										this.removeItem(i, item.getCount());
									}
								}
							}
							
							if (this.getItem(3).isEmpty()) {
								this.setItem(3, recipe.assemble(this));
							} else {
								this.getItem(3).grow(recipe.assemble(this).getCount());
							}
							
							this.progress = 0;
							
						}
						
						return;
						
					}
					
				}
				
			}
			
			if (this.progress > 0) this.progress--;
			
		} else {
			
			MachineSoundHelper.startSoundIfNotRunning(this, SoundEvents.BLASTFURNACE_FIRE_CRACKLE);
			
		}
		
	}
	
	public boolean canWork() {
		return this.findRecipe() != null;
	}
	
	public AlloyRecipe findRecipe() {
		java.util.Optional<AlloyRecipe> recipe = this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.ALLOY, this, this.level);
		return recipe.isPresent() ? recipe.get() : null;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putBoolean("isWorking", this.isWorking);
		compound.putBoolean("hasPower", this.hasPower);
		compound.putInt("progressTotal", this.progressTotal);
		compound.putInt("progress", this.progress);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.isWorking = compound.getBoolean("isWorking");
		this.hasPower = compound.getBoolean("hasPower");
		this.progressTotal = compound.getInt("progressTotal");
		this.progress = compound.getInt("progress");
		super.load(state, compound);
	}
	
}
