package de.redtec.tileentity;

import de.redtec.gui.ContainerReciver;
import de.redtec.registys.ModTileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityRedstoneReciver extends TileEntity implements INamedContainerProvider, IInventory {
	
	private ItemStack chanelItem;
	
	public TileEntityRedstoneReciver() {
		super(ModTileEntityType.REMOTE_CONTROLER);
		this.chanelItem = ItemStack.EMPTY;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		if (this.chanelItem != null) compound.put("ChanelItem", this.chanelItem.write(new CompoundNBT()));
		return super.write(compound);
	}
	
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compound) {
		this.chanelItem = ItemStack.read(compound.getCompound("ChanelItem"));
		if (this.chanelItem.isEmpty()) this.chanelItem = ItemStack.EMPTY;
		super.func_230337_a_(state, compound);
	}
//	
//	public void setChanelItem(ItemStack chanelItem) {
//		this.chanelItem = chanelItem;
//	}
//	
	public ItemStack getChanelItem() {
		return chanelItem;
	}

	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity lpayer) {
		return new ContainerReciver(id, playerInv, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.redtec.redstone_reciver");
	}

	@Override
	public void clear() {
		this.chanelItem = ItemStack.EMPTY;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return this.chanelItem.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return this.chanelItem;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack = this.chanelItem.copy();
		this.chanelItem.shrink(count);
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = this.chanelItem.copy();
		this.chanelItem = ItemStack.EMPTY;
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.chanelItem = stack;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}
	
}
