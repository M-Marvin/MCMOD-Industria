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
	protected final int slots;
	
	public TileEntityInventoryBase(TileEntityType<?> tileEntityTypeIn, int slots) {
		super(tileEntityTypeIn);
		this.slots = slots;
		this.itemstacks = NonNullList.withSize(this.slots, ItemStack.EMPTY);
	}
	
	@Override
	public double getViewDistance() {
		return 300F;
	}
	
	@Override
	public void clearContent() {
		for (int i = 0; i < this.slots; i++) {
			this.itemstacks.set(i, ItemStack.EMPTY);
		}
	}

	@Override
	public int getContainerSize() {
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
	public ItemStack getItem(int index) {
		return this.itemstacks.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack stack = this.itemstacks.get(index).copy();
		this.itemstacks.get(index).shrink(count);
		stack.setCount(count);
		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack stack = this.itemstacks.get(index);
		this.itemstacks.set(index, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.itemstacks.set(index, stack);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		net.minecraft.inventory.ItemStackHelper.saveAllItems(compound, this.itemstacks);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.itemstacks.clear();
		net.minecraft.inventory.ItemStackHelper.loadAllItems(compound, this.itemstacks);
		super.load(state, compound);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = this.serializeNBT();
		return new SUpdateTileEntityPacket(worldPosition, 0, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getTag());
	}
	
}
