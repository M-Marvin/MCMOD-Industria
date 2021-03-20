package de.redtec.tileentity;

import de.redtec.dynamicsounds.ISimpleMachineSound;
import de.redtec.gui.ContainerMAlloyFurnace;
import de.redtec.recipetypes.AlloyRecipe;
import de.redtec.typeregistys.ModRecipeTypes;
import de.redtec.typeregistys.ModTileEntityType;
import de.redtec.util.ElectricityNetworkHandler;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import de.redtec.util.IElectricConnective.Voltage;
import de.redtec.util.ItemStackHelper;
import de.redtec.util.MachineSoundHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityMAlloyFurnace extends TileEntityInventoryBase implements ITickableTileEntity, ISimpleMachineSound, ISidedInventory, INamedContainerProvider {
	
	public boolean hasPower;
	public boolean isWorking;
	public int progressTotal;
	public int progress;
	
	public TileEntityMAlloyFurnace() {
		super(ModTileEntityType.ALLOY_FURNACE, 4);
	}

	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMAlloyFurnace(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.redtec.alloy_furnace");
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1, 2, 3};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return index >= 0 && index <= 2;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return index == 3;
	}

	@Override
	public boolean isSoundRunning() {
		return this.isWorking;
	}

	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(world).getNetwork(pos);
			ElectricityNetworkHandler.getHandlerForWorld(world).updateNetwork(world, pos);
			this.hasPower = network.canMachinesRun() == Voltage.HightVoltage;
			this.isWorking = this.hasPower && canWork();
			
			if (getBlockState().get(BlockStateProperties.LIT) != this.isWorking) this.world.setBlockState(pos, getBlockState().with(BlockStateProperties.LIT, this.isWorking));
			
			if (this.isWorking) {
				
				AlloyRecipe recipe = findRecipe();
				
				if (recipe != null) {
					
					this.progressTotal = recipe.getSmeltingTime();
					
					if (ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(3), recipe.getRecipeOutput())) {
						
						this.progress++;
						
						if (this.progress >= this.progressTotal) {
							
							for (ItemStack item : recipe.getItemsIn()) {
								for (int i = 0; i < 3; i++) {
									if (this.getStackInSlot(i).getItem() == item.getItem() && this.getStackInSlot(i).getCount() >= item.getCount()) {
										this.decrStackSize(i, item.getCount());
									}
								}
							}
							
							if (this.getStackInSlot(3).isEmpty()) {
								this.setInventorySlotContents(3, recipe.getCraftingResult(this));
							} else {
								this.getStackInSlot(3).grow(recipe.getCraftingResult(this).getCount());
							}
							
							this.progress = 0;
							
						}
						
						return;
						
					}
					
				}
				
			}
			
			if (this.progress > 0) this.progress--;
			
		} else {

			if (this.isWorking && this.progress > 0) {
				
				MachineSoundHelper.startSoundIfNotRunning(this, SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE);
								
			}
			
		}
		
	}
	
	public boolean canWork() {
		return this.findRecipe() != null;
	}
	
	public AlloyRecipe findRecipe() {
		java.util.Optional<AlloyRecipe> recipe = this.world.getRecipeManager().getRecipe(ModRecipeTypes.ALLOY, this, this.world);
		return recipe.isPresent() ? recipe.get() : null;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putBoolean("isWorking", this.isWorking);
		compound.putBoolean("hasPower", this.hasPower);
		compound.putInt("progressTotal", this.progressTotal);
		compound.putInt("progress", this.progress);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.isWorking = compound.getBoolean("isWorking");
		this.hasPower = compound.getBoolean("hasPower");
		this.progressTotal = compound.getInt("progressTotal");
		this.progress = compound.getInt("progress");
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
	
}
