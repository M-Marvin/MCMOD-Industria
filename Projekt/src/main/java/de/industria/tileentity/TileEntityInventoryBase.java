package de.industria.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;

public class TileEntityInventoryBase extends TileEntity implements IInventory {

	protected NonNullList<ItemStack> itemstacks;
	protected int slots;
	
	public TileEntityInventoryBase(TileEntityType<?> tileEntityTypeIn, int slots) {
		super(tileEntityTypeIn);
		this.slots = slots;
		this.itemstacks = NonNullList.withSize(this.slots, ItemStack.EMPTY);
	}
	
	@Override
	public double getMaxRenderDistanceSquared() {
		return 300F;
	}
	
	@Override
	public void clear() {
		for (int i = 0; i < this.slots; i++) {
			this.itemstacks.set(i, ItemStack.EMPTY);
		}
	}

	@Override
	public int getSizeInventory() {
		return this.slots;
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < this.slots; i++) {
			if (!this.itemstacks.get(i).isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return this.itemstacks.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack = this.itemstacks.get(index).copy();
		this.itemstacks.get(index).shrink(count);
		stack.setCount(count);
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = this.itemstacks.get(index);
		this.itemstacks.set(index, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.itemstacks.set(index, stack);
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		net.minecraft.inventory.ItemStackHelper.saveAllItems(compound, this.itemstacks);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.itemstacks.clear();
		net.minecraft.inventory.ItemStackHelper.loadAllItems(compound, this.itemstacks);
		super.read(state, compound);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = this.serializeNBT();
		return new SUpdateTileEntityPacket(pos, 0, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getNbtCompound());
	}
	
}
