package de.redtec.tileentity;

import java.util.ArrayList;
import java.util.List;

import de.redtec.RedTec;
import de.redtec.blocks.BlockItemDetector;
import de.redtec.typeregistys.ModTileEntityType;
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

public class TileEntityItemDetector extends TileEntity implements ITickableTileEntity {
	
	protected ItemStack itemFilter;
	
	public TileEntityItemDetector() {
		super(ModTileEntityType.ITEM_DETECTOR);
		this.itemFilter = ItemStack.EMPTY;
	}
	
	@Override
	public void tick() {

		boolean active = this.detect();
		boolean isActive = this.getBlockState().get(BlockItemDetector.POWERED);
		
		if (active != isActive) {
			this.world.setBlockState(pos, this.getBlockState().with(BlockItemDetector.POWERED, active));
			this.world.notifyNeighborsOfStateChange(pos.offset(this.getBlockState().get(BlockItemDetector.FACING).getOpposite()), RedTec.item_detector);
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
					ItemStack castedStack = detectingStack.copy();
					castedStack.setCount(1);
					if (ItemStack.areItemStacksEqual(this.itemFilter, castedStack)) return true;
				}
			}
			
		} else {
			
			int size = detectingInventory instanceof TileEntityConveyorBelt ? 1 : detectingInventory.getSizeInventory();
			
			for (int i = 0; i < size; i++) {
				ItemStack detectingStack = detectingInventory.getStackInSlot(i);
				
				if (this.itemFilter.isEmpty()) {
					if (!detectingStack.isEmpty()) return true;
				} else {
					ItemStack castedStack = detectingStack.copy();
					castedStack.setCount(1);
					if (ItemStack.areItemStacksEqual(this.itemFilter, castedStack)) return true;
				}
				
			}
			
		}
		
		return false;
		
	}
	
	public List<ItemStack> getDetectingItems() {
		
		BlockState state = this.getBlockState();
		BlockPos detectingPos = pos.offset(state.get(BlockItemDetector.FACING));
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
		BlockPos detectingPos = pos.offset(state.get(BlockItemDetector.FACING));
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
		if (nbt.contains("ItemFilter")) this.itemFilter = ItemStack.read(nbt.getCompound("FilterItem"));
		this.itemFilter.setCount(1);
		super.read(state, nbt);
	}
	
}
