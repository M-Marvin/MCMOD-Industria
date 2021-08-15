package de.industria.tileentity;

import java.util.ArrayList;
import java.util.List;

import de.industria.blocks.BlockRItemDetector;
import de.industria.typeregistys.ModItems;
import de.industria.typeregistys.ModTileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
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
		boolean isActive = this.getBlockState().getValue(BlockRItemDetector.POWERED);
		
		if (active != isActive) {
			this.level.setBlockAndUpdate(worldPosition, this.getBlockState().setValue(BlockRItemDetector.POWERED, active));
			this.level.updateNeighborsAt(worldPosition.relative(this.getBlockState().getValue(BlockRItemDetector.FACING).getOpposite()), ModItems.item_detector);
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
			
			int size = isSpliter ? 1 : detectingInventory.getContainerSize();
			
			for (int i = 0; i < size; i++) {
				ItemStack detectingStack = detectingInventory.getItem(i);
				
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
		castedStack.setDamageValue(0);
		this.itemFilter.setDamageValue(0);
		return ItemStack.matches(this.itemFilter, castedStack);
	}
	
	public List<ItemStack> getDetectingItems() {
		
		BlockState state = this.getBlockState();
		BlockPos detectingPos = worldPosition.relative(state.getValue(BlockRItemDetector.FACING));
		AxisAlignedBB aaBounds = new AxisAlignedBB(detectingPos, detectingPos.offset(1, 1, 1));
		
		List<Entity> entitys = this.level.getEntities(null, aaBounds);
		List<ItemStack> items = new ArrayList<ItemStack>();
		
		entitys.forEach((itemEntity) -> {
			if (itemEntity instanceof ItemEntity) {
				items.add(((ItemEntity) itemEntity).getItem());
			} else if (itemEntity instanceof IInventory) {
				int size = ((IInventory) itemEntity).getContainerSize();
				for (int i = 0; i < size; i++) {
					ItemStack stack = ((IInventory) itemEntity).getItem(i);
					if (!stack.isEmpty()) items.add(stack);
				}
			} else if (itemEntity instanceof LivingEntity) {
				items.add(((LivingEntity) itemEntity).getItemInHand(Hand.MAIN_HAND));
				items.add(((LivingEntity) itemEntity).getItemInHand(Hand.OFF_HAND));
			}
		});
		
		return items;
		
	}
	
	public IInventory getDetectingInventory() {
		
		BlockState state = this.getBlockState();
		BlockPos detectingPos = worldPosition.relative(state.getValue(BlockRItemDetector.FACING));
		BlockState detectingState = level.getBlockState(detectingPos);
		
		if (detectingState.hasTileEntity()) {
			
			TileEntity tileEntity = level.getBlockEntity(detectingPos);
			
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
	public CompoundNBT save(CompoundNBT compound) {
		if (!this.itemFilter.isEmpty()) compound.put("ItemFilter", this.itemFilter.save(new CompoundNBT()));
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		this.itemFilter = ItemStack.EMPTY;
		if (nbt.contains("ItemFilter")) this.itemFilter = ItemStack.of(nbt.getCompound("ItemFilter"));
		this.itemFilter.setCount(1);
		super.load(state, nbt);
	}
	
}
