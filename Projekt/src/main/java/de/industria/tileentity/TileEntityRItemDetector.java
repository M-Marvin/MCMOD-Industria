package de.industria.tileentity;

import java.util.ArrayList;
import java.util.List;

import de.industria.ModItems;
import de.industria.blocks.BlockRItemDetector;
import de.industria.typeregistys.ModTileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityRItemDetector extends TileEntity implements ITickableTileEntity {
	
	protected ItemStack itemFilter;
	
	public TileEntityRItemDetector() {
		super(ModTileEntityType.ITEM_DETECTOR);
		this.itemFilter = ItemStack.EMPTY;
	}
	
	@Override
	public void tick() {

		boolean active = this.detect();
		boolean isActive = this.getBlockState().get(BlockRItemDetector.POWERED);
		
		if (active != isActive) {
			this.world.setBlockState(pos, this.getBlockState().with(BlockRItemDetector.POWERED, active));
			this.world.notifyNeighborsOfStateChange(pos.offset(this.getBlockState().get(BlockRItemDetector.FACING).getOpposite()), ModItems.item_detector);
		}
		
	}
	
	public boolean detect() {

		IInventory detectingInventory = getDetectingInventory();
		
		if (detectingInventory == null) {
			
			List<ItemStack> items = getDetectingItems();
			
			for (ItemStack detectingStack : items) {
				if (this.itemFilter.isEmpty()) {
					if (!detectingStack.isEmpty()) return true;
				} else {
					if (matchItem(detectingStack)) return true;
				}
			}
			
		} else {
			
			boolean isSpliter = false;
			if (detectingInventory instanceof TileEntityConveyorBelt) {
				if (((TileEntityConveyorBelt) detectingInventory).getBlockState().getBlock() == ModItems.conveyor_spliter) isSpliter = true;
			}
			
			int size = isSpliter ? 1 : detectingInventory.getSizeInventory();
			
			for (int i = 0; i < size; i++) {
				ItemStack detectingStack = detectingInventory.getStackInSlot(i);
				
				if (this.itemFilter.isEmpty()) {
					if (!detectingStack.isEmpty()) return true;
				} else {
					if (matchItem(detectingStack)) return true;
				}
				
			}
			
		}
		
		return false;
		
	}
	
	public boolean matchItem(ItemStack stack) {
		ItemStack castedStack = stack.copy();
		castedStack.setCount(1);
		castedStack.setDamage(0);
		this.itemFilter.setDamage(0);
		return ItemStack.areItemStacksEqual(this.itemFilter, castedStack);
	}
	
	public List<ItemStack> getDetectingItems() {
		
		BlockState state = this.getBlockState();
		BlockPos detectingPos = pos.offset(state.get(BlockRItemDetector.FACING));
		AxisAlignedBB aaBounds = new AxisAlignedBB(detectingPos, detectingPos.add(1, 1, 1));
		
		List<Entity> entitys = this.world.getEntitiesInAABBexcluding(null, aaBounds, null);
		List<ItemStack> items = new ArrayList<ItemStack>();
		
		entitys.forEach((itemEntity) -> {
			if (itemEntity instanceof ItemEntity) {
				items.add(((ItemEntity) itemEntity).getItem());
			} else if (itemEntity instanceof IInventory) {
				int size = ((IInventory) itemEntity).getSizeInventory();
				for (int i = 0; i < size; i++) {
					ItemStack stack = ((IInventory) itemEntity).getStackInSlot(i);
					if (!stack.isEmpty()) items.add(stack);
				}
			}
		});
		
		return items;
		
	}
	
	public IInventory getDetectingInventory() {
		
		BlockState state = this.getBlockState();
		BlockPos detectingPos = pos.offset(state.get(BlockRItemDetector.FACING));
		BlockState detectingState = world.getBlockState(detectingPos);
		
		if (detectingState.hasTileEntity()) {
			
			TileEntity tileEntity = world.getTileEntity(detectingPos);
			
			if (tileEntity instanceof IInventory) {
				
				return (IInventory) tileEntity;
				
			}
			
		}
		
		return null;
		
	}
	
	public void setItemFilter(ItemStack itemFilter) {
		this.itemFilter = itemFilter;
	}
	
	public ItemStack getItemFilter() {
		return itemFilter;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		if (!this.itemFilter.isEmpty()) compound.put("ItemFilter", this.itemFilter.write(new CompoundNBT()));
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		this.itemFilter = ItemStack.EMPTY;
		if (nbt.contains("ItemFilter")) this.itemFilter = ItemStack.read(nbt.getCompound("ItemFilter"));
		this.itemFilter.setCount(1);
		super.read(state, nbt);
	}
	
}
