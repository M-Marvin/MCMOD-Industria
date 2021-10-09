package de.industria.tileentity;

import javax.annotation.Nullable;

import de.industria.blocks.BlockMGenerator;
import de.industria.gui.ContainerMGenerator;
import de.industria.typeregistys.ModSoundEvents;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.DataWatcher;
import de.industria.util.blockfeatures.ITESimpleMachineSound;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
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
import net.minecraftforge.common.ForgeHooks;

public class TileEntityMGenerator extends TileEntityInventoryBase implements INamedContainerProvider, ISidedInventory, ITickableTileEntity, ITESimpleMachineSound {
	
	public int fuelTime;
	public float burnTime;
	public float producingPower;
	
	public TileEntityMGenerator() {
		super(ModTileEntityType.GENERATOR, 1);
		DataWatcher.registerBlockEntity(this, (tileEntity, data) -> {
			if (data[0] != null) ((TileEntityMGenerator) tileEntity).fuelTime = (int) data[0];
			if (data[1] != null) ((TileEntityMGenerator) tileEntity).burnTime = (float) data[1];
			if (data[2] != null) ((TileEntityMGenerator) tileEntity).producingPower = (float) data[2];
		}, () -> fuelTime, () -> burnTime, () -> producingPower);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putFloat("burnTime", this.burnTime);
		compound.putFloat("producingPower", this.producingPower);
		compound.putInt("fuelTime", this.fuelTime);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.burnTime = compound.getFloat("burnTime");
		this.producingPower = compound.getFloat("producingPower");
		this.fuelTime = compound.getInt("fuelTime");
		super.load(state, compound);
	}

	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMGenerator(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.generator");
	}
	
	net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
           net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.NORTH);
	
	@Override
	public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handlers[0].cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0};
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return true;
	}
	
	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void tick() {
				
		if (!this.level.isClientSide()) {
			
			ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(this.level);
			handler.updateNetwork(level, worldPosition);
			ElectricityNetwork network = handler.getNetwork(worldPosition);
			float f = network.getCurrent() > 0 ? network.getCurrent() / (float) network.getCapacity() : 0;
			
			if (f > 0) {
				
				if (this.burnTime > 0) {
					this.burnTime -= f;
					this.producingPower = 8 * f;
				} else if (hasFuelItems()) {
					this.burnTime = ForgeHooks.getBurnTime(this.itemstacks.get(0));
					this.fuelTime = (int) this.burnTime;
					this.itemstacks.get(0).shrink(1);
				} else {
					this.producingPower = 0;
				}
				
			} else {
				this.producingPower = 0;
			}
			
			boolean active = this.producingPower > 0;
			boolean activeState = this.getBlockState().getValue(BlockMGenerator.ACTIVE);
			if (active != activeState) this.level.setBlockAndUpdate(worldPosition, this.getBlockState().setValue(BlockMGenerator.ACTIVE, active));
			
		} else {
			
			MachineSoundHelper.startSoundIfNotRunning(this, ModSoundEvents.GENERATOR_LOOP);
			
		}
	}
	
	@Override
	public boolean isSoundRunning() {
		return this.producingPower > 0;
	}
	
	public boolean canWork() {
		return this.burnTime > 0 || this.hasFuelItems();
	}
	
	@SuppressWarnings("deprecation")
	public boolean hasFuelItems() {
		return this.itemstacks.get(0).isEmpty() ? false : ForgeHooks.getBurnTime(this.itemstacks.get(0)) > 0;
	}
	
}
