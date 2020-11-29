package de.redtec.tileentity;

import javax.annotation.Nullable;

import de.redtec.blocks.BlockMGenerator;
import de.redtec.dynamicsounds.ISimpleMachineSound;
import de.redtec.dynamicsounds.SoundMachine;
import de.redtec.gui.ContainerMGenerator;
import de.redtec.registys.ModSoundEvents;
import de.redtec.registys.ModTileEntityType;
import de.redtec.util.ElectricityNetworkHandler;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;

public class TileEntityMGenerator extends TileEntityInventoryBase implements INamedContainerProvider, ISidedInventory, ITickableTileEntity, ISimpleMachineSound {
	
	public int fuelTime;
	public float burnTime;
	public float producingPower;
	
	public TileEntityMGenerator() {
		super(ModTileEntityType.GENERATOR, 1);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putFloat("burnTime", this.burnTime);
		compound.putFloat("producingPower", this.producingPower);
		compound.putInt("fuelTime", this.fuelTime);
		return super.write(compound);
	}
	
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compound) {
		this.burnTime = compound.getFloat("burnTime");
		this.producingPower = compound.getFloat("producingPower");
		this.fuelTime = compound.getInt("fuelTime");
		super.func_230337_a_(state, compound);
	}

	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerMGenerator(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.redtec.generator");
	}
	
	net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
           net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.NORTH);
	
	@Override
	public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
		if (!this.removed && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handlers[0].cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0};
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return true;
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return false;
	}

	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			
			ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(this.world);
			handler.updateNetwork(world, pos);
			ElectricityNetwork network = handler.getNetwork(pos);
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
			boolean activeState = this.getBlockState().get(BlockMGenerator.ACTIVE);
			if (active != activeState) this.world.setBlockState(pos, this.getBlockState().with(BlockMGenerator.ACTIVE, active));
			
		} else {
			
			if (this.isSoundRunning()) {
				
				SoundHandler soundHandler = Minecraft.getInstance().getSoundHandler();
				
				if (this.sound == null ? true : !soundHandler.isPlaying(sound)) {
					
					this.sound = new SoundMachine(this, ModSoundEvents.GENERATOR_LOOP);
					soundHandler.play(this.sound);
					
				}
				
			}
			
			
		}
		
	}
	
	private SoundMachine sound;
	
	@Override
	public boolean isSoundRunning() {
		return this.getBlockState().get(BlockMGenerator.ACTIVE);
	}
	
	public boolean canWork() {
		return this.burnTime > 0 || this.hasFuelItems();
	}
	
	public boolean hasFuelItems() {
		return this.itemstacks.get(0).isEmpty() ? false : ForgeHooks.getBurnTime(this.itemstacks.get(0)) > 0;
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 0, this.serializeNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getNbtCompound());
	}
	
}
