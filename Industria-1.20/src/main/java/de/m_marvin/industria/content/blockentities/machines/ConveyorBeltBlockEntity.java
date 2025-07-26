package de.m_marvin.industria.content.blockentities.machines;

import java.util.ArrayList;
import java.util.List;

import de.m_marvin.industria.content.blockentities.kinetics.BaseBeltBlockEntity;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ConveyorBeltBlockEntity extends BaseBeltBlockEntity implements Container {
	
	public static class ItemOnBelt {
		public final ItemStack stack;
		public float position;
		
		public ItemOnBelt(ItemStack item) {
			this.stack = item;
			this.position = 0F;
		}
		
	}
	
	protected final int itemsPerSection = 3;
	protected final List<ItemOnBelt> items = new ArrayList<ConveyorBeltBlockEntity.ItemOnBelt>();
	
	public ConveyorBeltBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.CONVEYOR_BELT.get(), pPos, pBlockState);
	}
	
	public static void moveItemsTick(Level pLevel, BlockPos pPos, BlockState pState, ConveyorBeltBlockEntity pBlockEntity) {
		
		float motionSpeed = (float) pBlockEntity.getRPM(0) * 0.0006F;
		
		for (var item : pBlockEntity.items) {
			item.position += motionSpeed;
			item.position = item.position % 1.0F;
		}
		
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		
		ListTag itemsTag = pTag.getList("ItemsOnBelt", 10);
		this.items.clear();
		for (int i = 0; i < itemsTag.size(); i++) {
			CompoundTag itemTag = itemsTag.getCompound(i);
			ItemOnBelt item = new ItemOnBelt(ItemStack.of(itemTag.getCompound("Item")));
			item.position = itemTag.getFloat("Position");
			this.items.add(item);
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		
		ListTag itemsTag = new ListTag();
		for (int i = 0; i < this.items.size(); i++) {
			CompoundTag itemTag = new CompoundTag();
			itemTag.putFloat("Position", this.items.get(i).position);
			itemTag.put("Item", this.items.get(i).stack.save(new CompoundTag()));
			itemsTag.add(itemTag);
		}
		pTag.put("ItemsOnBelt", itemsTag);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		
		ListTag itemsTag = new ListTag();
		for (int i = 0; i < this.items.size(); i++) {
			CompoundTag itemTag = new CompoundTag();
			itemTag.putFloat("Position", this.items.get(i).position);
			itemTag.put("Item", this.items.get(i).stack.save(new CompoundTag()));
			itemsTag.add(itemTag);
		}
		tag.put("ItemsOnBelt", itemsTag);
		return tag;
	}
	
	public List<ItemOnBelt> getItems() {
		return items;
	}
	
	@Override
	public void clearContent() {
		this.items.clear();
	}

	@Override
	public int getContainerSize() {
		return this.itemsPerSection;
	}

	@Override
	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	@Override
	public ItemStack getItem(int pSlot) {
		return this.items.size() > pSlot ? this.items.get(pSlot).stack : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int pSlot, int pAmount) {
		ItemStack stack = getItem(pSlot);
		if (stack.getCount() <= pAmount) {
			int c = stack.getCount();
			ItemStack s = new ItemStack(stack.getItem(), c);
			stack.shrink(c);
			setChanged();
			GameUtility.triggerClientSync(level, worldPosition);
			return s;
		} else {
			stack.shrink(pAmount);
			setChanged();
			GameUtility.triggerClientSync(level, worldPosition);
			return new ItemStack(stack.getItem(), pAmount);
		}
	}

	@Override
	public ItemStack removeItemNoUpdate(int pSlot) {
		ItemStack s = this.items.size() > pSlot ? this.items.remove(pSlot).stack : null;
		if (s != null) {
			setChanged();
			GameUtility.triggerClientSync(level, worldPosition);
		}
		return s;
	}

	@Override
	public void setItem(int pSlot, ItemStack pStack) {
		if (pSlot > this.items.size() || pSlot >= this.itemsPerSection)
			return;
		if (pSlot < this.items.size()) {
			ItemOnBelt onBelt = this.items.set(pSlot, new ItemOnBelt(pStack));
			this.items.get(pSlot).position = onBelt.position;
		} else {
			this.items.add(new ItemOnBelt(pStack));
		}
		setChanged();
		GameUtility.triggerClientSync(level, worldPosition);
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return false;
	}
	
}
