package de.redtec.tileentity;

import java.util.Optional;

import javax.annotation.Nullable;

import de.redtec.gui.ContainerStoredCrafting;
import de.redtec.typeregistys.ModTileEntityType;
import de.redtec.util.ContainerDummy;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityStoringCraftingTable extends LockableTileEntity implements INamedContainerProvider, ITickableTileEntity, ISidedInventory {
	
	NonNullList<ItemStack> inventory;
	NonNullList<ItemStack> remainingItems;
	
	public TileEntityStoringCraftingTable() {
		super(ModTileEntityType.STORING_CRAFTING_TABLE);
		this.inventory = NonNullList.withSize(11, ItemStack.EMPTY);
		this.remainingItems = NonNullList.withSize(9, ItemStack.EMPTY);
	}
	
	@Override
	public int getSizeInventory() {
		return 11;
	}
	
	public NonNullList<ItemStack> getItems() {
		return this.inventory;
	}
	
	public void setItems(NonNullList<ItemStack> itemsIn) {
		this.inventory = itemsIn;
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("redtec.storing_crafting_table.container");
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return new ContainerStoredCrafting(id, player, this);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		ListNBT itemsNBT = new ListNBT();
		for (int i = 0; i < 11; i++) {
			ItemStack stack = this.inventory.get(i);
			if (!stack.isEmpty()) {
				CompoundNBT stackNBT = stack.write(new CompoundNBT());
				stackNBT.putShort("Slot", (short) i);
				itemsNBT.add(stackNBT);
			}
		}
		compound.put("Items", itemsNBT);
		ListNBT remainingItemsNBT = new ListNBT();
		for (int i = 0; i < 9; i++) {
			ItemStack stack = this.remainingItems.get(i);
			if (!stack.isEmpty()) {
				CompoundNBT stackNBT = stack.write(new CompoundNBT());
				stackNBT.putShort("Slot", (short) i);
				remainingItemsNBT.add(stackNBT);
			}
		}
		compound.put("RemainingItems", remainingItemsNBT);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		ListNBT itemsNBT = compound.getList("Items", 10);
		for (int i = 0; i < itemsNBT.size(); i++) {
			CompoundNBT stackNBT = itemsNBT.getCompound(i);
			int index = stackNBT.getShort("Slot");
			ItemStack stack = ItemStack.read(stackNBT);
			this.inventory.set(index, stack);
		}
		ListNBT remainingItemsNBT = compound.getList("RemainingItems", 10);
		for (int i = 0; i < itemsNBT.size(); i++) {
			CompoundNBT stackNBT = remainingItemsNBT.getCompound(i);
			int index = stackNBT.getShort("Slot");
			ItemStack stack = ItemStack.read(stackNBT);
			this.remainingItems.set(index, stack);
		}
		super.read(state, compound);
	}

	@Override
	public void tick() {
		
		if (!this.world.isRemote() && !this.isEmpty()) {
			
			CraftingInventory craftMatrix = new CraftingInventory(new ContainerDummy(), 3, 3);
			for (int i = 0; i < 9; i++) {
				ItemStack stack = this.inventory.get(i);
				if (stack.isEmpty()) {
					return;
				} else if (stack.getItem() != this.inventory.get(10).getItem()) {
					craftMatrix.setInventorySlotContents(i, this.getStackInSlot(i));
				}
			}
			Optional<ICraftingRecipe> recipe = this.world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftMatrix, this.world);
			
			if (recipe.isPresent()) {
				
				ItemStack result = recipe.get().getCraftingResult(craftMatrix);
				this.remainingItems = recipe.get().getRemainingItems(craftMatrix);
				
				boolean canCraft = false;
				ItemStack stack = this.getStackInSlot(9);
				
				if (stack.isEmpty()) {
					this.inventory.set(9, result.copy());
					canCraft = true;
				} else if (stack.getItem() == result.getItem() && stack.getCount() + result.getCount() <= result.getMaxStackSize()) {
					stack.grow(result.getCount());
					canCraft = true;
				}
				
				if (canCraft) {
					
					for (int i = 0; i < 9; i++) {
						
						if (this.inventory.get(i).getItem() != this.inventory.get(10).getItem()) this.inventory.get(i).shrink(1);
						
					}
					
					for (int i = 0; i < 9; i++) {
						
						ItemStack remainingItem = remainingItems.get(i);
						if (!remainingItem.isEmpty()) this.inventory.set(i, remainingItem.copy());
						
					}
					
				}
				
			}
			
		}
		
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	}
	
	private boolean isRemainingItem(int index) {
		ItemStack remainingItem = this.remainingItems.get(index);
		ItemStack inventoryItem = this.inventory.get(index);
		return remainingItem.getItem() == inventoryItem.getItem();
	}
	
	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return index == 9 || isRemainingItem(index);
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return index != 9 && this.inventory.get(index).isEmpty();
	}
	
	@Override
	public boolean isEmpty() {
		for (ItemStack item : this.inventory) {
			if (!item.isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return this.inventory.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack2 = this.inventory.get(index).copy();
		if (!stack2.isEmpty()) {
			ItemStack stack = this.inventory.get(index);
			stack.shrink(count);
			this.inventory.set(index, stack);
		}
		return stack2;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return this.inventory.set(index, ItemStack.EMPTY);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.inventory.set(index, stack);
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}

	@Override
	public void clear() {
		for (int i = 0; i < 11; i++) {
			this.inventory.set(i, ItemStack.EMPTY);
		}
	}
	
   net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
           net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.DOWN, Direction.NORTH);

   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
      if (!this.removed && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (facing == Direction.DOWN)
            return handlers[0].cast();
         else
            return handlers[1].cast();
      }
      return super.getCapability(capability, facing);
   }
	
}
