package de.industria.tileentity;

import java.util.Optional;

import de.industria.dynamicsounds.ISimpleMachineSound;
import de.industria.gui.ContainerMMetalFormer;
import de.industria.recipetypes.MetalFormRecipe;
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
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityMMetalFormer extends TileEntityInventoryBase implements ISidedInventory, ITickableTileEntity, ISimpleMachineSound, INamedContainerProvider {
	
	// Client only
	public float rotation;
	public float lastPartial;
	
	public int processTimeTotal;
	public int processTime;
	public boolean hasPower;
	public boolean isWorking;
	
	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			ElectricityNetworkHandler.getHandlerForWorld(world).updateNetwork(world, pos);
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(world).getNetwork(pos);
			this.hasPower = network.canMachinesRun() == Voltage.NormalVoltage;
			this.isWorking = this.hasPower && canWork();
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			
			if (this.isWorking) {
				
				MetalFormRecipe recipe = findRecipe();
				
				if (recipe != null) {
					
					this.processTimeTotal = recipe.getProcessTime();
					ItemStack result = recipe.getCraftingResult(this);
					
					if (ItemStackHelper.canMergeRecipeStacks(this.getStackInSlot(1), result)) {
						
						this.processTime++;
						
						if (this.processTime >= this.processTimeTotal) {
							
							if (this.getStackInSlot(1).isEmpty()) {
								this.setInventorySlotContents(1, result.copy());
							} else {
								this.getStackInSlot(1).grow(result.getCount());
							}
							this.getStackInSlot(0).shrink(recipe.getItemIn().getCount());
							
							this.processTime = 0;
							
						}
						
					}
					
				}
				
			} else {
				this.processTime = 0;
			}
			
		} else {
			
			MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.SCHREDDER_LOOP);
			
		}
		
	}
	
	@Override
	public boolean isSoundRunning() {
		return this.isWorking;
	}
	
	public TileEntityMMetalFormer() {
		super(ModTileEntityType.METAL_FORMER, 2);
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
	
	public boolean canWork() {
		return findRecipe() != null;
	}
	
	public MetalFormRecipe findRecipe() {
		Optional<MetalFormRecipe> recipe = this.world.getRecipeManager().getRecipe(ModRecipeTypes.METAL_FORM, this, this.world);
		return recipe.isPresent() ? recipe.get() : null;
	}

	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMMetalFormer(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.metal_former");
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-1, -1, -1), pos.add(2, 2, 2));
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
	public CompoundNBT write(CompoundNBT compound) {
		compound.putBoolean("hasPower", hasPower);
		compound.putBoolean("isWorking", isWorking);
		compound.putInt("Progress", processTime);
		compound.putInt("progressTotal", processTimeTotal);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.hasPower = compound.getBoolean("hasPower");
		this.isWorking = compound.getBoolean("isWorking");
		this.processTime = compound.getInt("Progress");
		this.processTimeTotal = compound.getInt("progressTotal");
		super.read(state, compound);
	}
	
}
