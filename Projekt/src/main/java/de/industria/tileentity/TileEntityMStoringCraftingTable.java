package de.industria.tileentity;

import java.util.Optional;

import javax.annotation.Nullable;

import de.industria.gui.ContainerMStoredCrafting;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import de.industria.util.gui.ContainerDummy;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
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
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityMStoringCraftingTable extends LockableTileEntity implements INamedContainerProvider, ITickableTileEntity, ISidedInventory {
	
	public NonNullList<ItemStack> inventory;
	public NonNullList<ItemStack> remainingItems;
	
	public int progress;
	public boolean canWork;
	public boolean hasPower;
	public boolean isWorking;
		
	public TileEntityMStoringCraftingTable() {
		super(ModTileEntityType.STORING_CRAFTING_TABLE);
		this.inventory = NonNullList.withSize(11, ItemStack.EMPTY);
		this.remainingItems = NonNullList.withSize(9, ItemStack.EMPTY);
	}
	
	@Override
	public int getContainerSize() {
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
		return new TranslationTextComponent("industria.storing_crafting_table.container");
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return new ContainerMStoredCrafting(id, player, this);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		ListNBT itemsNBT = new ListNBT();
		for (int i = 0; i < 11; i++) {
			ItemStack stack = this.inventory.get(i);
			if (!stack.isEmpty()) {
				CompoundNBT stackNBT = stack.save(new CompoundNBT());
				stackNBT.putShort("Slot", (short) i);
				itemsNBT.add(stackNBT);
			}
		}
		compound.put("Items", itemsNBT);
		ListNBT remainingItemsNBT = new ListNBT();
		for (int i = 0; i < 9; i++) {
			ItemStack stack = this.remainingItems.get(i);
			if (!stack.isEmpty()) {
				CompoundNBT stackNBT = stack.save(new CompoundNBT());
				stackNBT.putShort("Slot", (short) i);
				remainingItemsNBT.add(stackNBT);
			}
		}
		compound.put("RemainingItems", remainingItemsNBT);
		compound.putBoolean("hasPower", this.hasPower);
		compound.putBoolean("isWorking", this.isWorking);
		compound.putInt("Progress", this.progress);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		ListNBT itemsNBT = compound.getList("Items", 10);
		for (int i = 0; i < itemsNBT.size(); i++) {
			CompoundNBT stackNBT = itemsNBT.getCompound(i);
			int index = stackNBT.getShort("Slot");
			ItemStack stack = ItemStack.of(stackNBT);
			this.inventory.set(index, stack);
		}
		ListNBT remainingItemsNBT = compound.getList("RemainingItems", 10);
		for (int i = 0; i < itemsNBT.size(); i++) {
			CompoundNBT stackNBT = remainingItemsNBT.getCompound(i);
			int index = stackNBT.getShort("Slot");
			ItemStack stack = ItemStack.of(stackNBT);
			this.remainingItems.set(index, stack);
		}
		this.hasPower = compound.getBoolean("hasPower");
		this.isWorking = compound.getBoolean("isWorking");
		this.progress = compound.getInt("Progress");
		super.load(state, compound);
	}
	
	public CraftingInventory makeCraftMatrix() {

		CraftingInventory craftMatrix = new CraftingInventory(new ContainerDummy(), 3, 3);
		for (int i = 0; i < 9; i++) {
			ItemStack stack = this.inventory.get(i);
			if (stack.isEmpty()) {
				return null;
			} else if (stack.getItem() != this.inventory.get(10).getItem()) {
				craftMatrix.setItem(i, this.getItem(i));
			}
		}
		return craftMatrix;
		
	}
	
	public ICraftingRecipe findRecipe(CraftingInventory craftMatrix) {
		
		if (craftMatrix == null) return null;
		Optional<ICraftingRecipe> recipe = this.level.getRecipeManager().getRecipeFor(IRecipeType.CRAFTING, craftMatrix, this.level);
		return recipe.isPresent() ? recipe.get() : null;
		
	}
	
	@Override
	public void tick() {
		
		if (!this.level.isClientSide()) {
			
			this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			
			ElectricityNetworkHandler.getHandlerForWorld(level).updateNetwork(level, worldPosition);
			ElectricityNetwork network = ElectricityNetworkHandler.getHandlerForWorld(level).getNetwork(worldPosition);
			
			CraftingInventory craftMatrix = makeCraftMatrix();
			ICraftingRecipe recipe = findRecipe(craftMatrix);
			
			canWork = false;
			if (recipe != null) {
				
				ItemStack result = recipe.assemble(craftMatrix);
				ItemStack stack = this.getItem(9);
				
				if (stack.isEmpty()) {
					canWork = true;
				} else if (stack.getItem() == result.getItem() && stack.getCount() + result.getCount() <= result.getMaxStackSize()) {
					canWork = true;
				}
			}
			
			this.hasPower = network.canMachinesRun() == Voltage.NormalVoltage;
			this.isWorking = this.canWork && hasPower;
			
			if (this.isWorking) {
				
				if (this.progress >= 100) {

					ItemStack result = recipe.assemble(craftMatrix);
					ItemStack stack = this.getItem(9);
					
					this.remainingItems = recipe.getRemainingItems(craftMatrix);
					
					if (stack.isEmpty()) {
						this.inventory.set(9, result.copy());
					} else if (stack.getItem() == result.getItem() && stack.getCount() + result.getCount() <= result.getMaxStackSize()) {
						stack.grow(result.getCount());
					}
					
					for (int i = 0; i < 9; i++) {
						
						if (this.inventory.get(i).getItem() != this.inventory.get(10).getItem()) this.inventory.get(i).shrink(1);
						
					}
					
					for (int i = 0; i < 9; i++) {
						
						ItemStack remainingItem = remainingItems.get(i);
						if (!remainingItem.isEmpty()) this.inventory.set(i, remainingItem.copy());
						
					}
					
					this.progress = 0;
					
				} else {
					
					this.progress++;
					
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
		boolean flag = remainingItem.getItem() == inventoryItem.getItem();
		if (flag) this.remainingItems.set(index, ItemStack.EMPTY);
		return flag;
	}
	
	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return index == 9 || isRemainingItem(index);
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
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
	public ItemStack getItem(int index) {
		return this.inventory.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack stack2 = this.inventory.get(index).copy();
		if (!stack2.isEmpty()) {
			ItemStack stack = this.inventory.get(index);
			stack.shrink(count);
			this.inventory.set(index, stack);
		}
		return stack2;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return this.inventory.set(index, ItemStack.EMPTY);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.inventory.set(index, stack);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < 11; i++) {
			this.inventory.set(i, ItemStack.EMPTY);
		}
	}
	
	net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
		net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.DOWN, Direction.NORTH);
	
	@Override
	public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
		if (facing == Direction.DOWN)
			return handlers[0].cast();
		else
			return handlers[1].cast();
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = this.serializeNBT();
		nbt.remove("Items");
		nbt.remove("RemainingItems");
		return new SUpdateTileEntityPacket(worldPosition, 0, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getTag());
	}
	
}
