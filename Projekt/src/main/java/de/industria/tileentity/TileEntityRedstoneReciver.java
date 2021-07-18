package de.industria.tileentity;

import de.industria.gui.ContainerReciver;
import de.industria.typeregistys.ModTileEntityType;
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
	public CompoundNBT save(CompoundNBT compound) {
		if (this.chanelItem != null) compound.put("ChanelItem", this.chanelItem.save(new CompoundNBT()));
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.chanelItem = ItemStack.of(compound.getCompound("ChanelItem"));
		if (this.chanelItem.isEmpty()) this.chanelItem = ItemStack.EMPTY;
		super.load(state, compound);
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
		return new TranslationTextComponent("block.industria.redstone_reciver");
	}

	@Override
	public void clearContent() {
		this.chanelItem = ItemStack.EMPTY;
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return this.chanelItem.isEmpty();
	}

	@Override
	public ItemStack getItem(int index) {
		return this.chanelItem;
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack stack = this.chanelItem.copy();
		this.chanelItem.shrink(count);
		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack stack = this.chanelItem.copy();
		this.chanelItem = ItemStack.EMPTY;
		return stack;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.chanelItem = stack;
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}
	
}
