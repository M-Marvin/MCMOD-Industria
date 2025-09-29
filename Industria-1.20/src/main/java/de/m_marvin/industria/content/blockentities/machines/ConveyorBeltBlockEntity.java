package de.m_marvin.industria.content.blockentities.machines;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import de.m_marvin.industria.content.blockentities.kinetics.BaseBeltBlockEntity;
import de.m_marvin.industria.content.blocks.machines.ConveyorBeltBlock;
import de.m_marvin.industria.content.registries.ModBlockEntityTypes;
import de.m_marvin.industria.content.registries.ModTags;
import de.m_marvin.industria.core.compound.types.blocks.CompoundBlock;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.NBTUtility;
import de.m_marvin.industria.core.util.types.DiagonalPlanarDirection;
import de.m_marvin.univec.impl.Vec3f;
import de.m_marvin.univec.impl.Vec3i;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ConveyorBeltBlockEntity extends BaseBeltBlockEntity implements Container {
	
	public static class ItemOnBelt {
		public final ItemStack stack;
		public float position;
		public Vec3f insertedFrom;
		
		public ItemOnBelt(ItemStack item) {
			this.stack = item;
			this.position = 0F;
			this.insertedFrom = new Vec3f();
		}
		
		@Override
		public String toString() {
			return "[" + this.stack.toString() + " @ " + this.position + "]";
		}
		
	}
	
	protected final int itemsPerSection = 3;
	protected final List<ItemOnBelt> items = new ArrayList<ConveyorBeltBlockEntity.ItemOnBelt>();
	
	public ConveyorBeltBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.CONVEYOR_BELT.get(), pPos, pBlockState);
	}
	
	public Vec3i getVisualBeltDirection() {
		Axis axis = getBlockState().getValue(ConveyorBeltBlock.AXIS);
		DiagonalPlanarDirection orientation = getBlockState().getValue(ConveyorBeltBlock.ORIENTATION);
		boolean isHorizontal = orientation.getNormal().y == 0;
		boolean isUpwards = !isHorizontal && (orientation.getNormal().x == orientation.getNormal().y ^ axis == Axis.Z);
		
		Vec3i beltDir = new Vec3i(0, 0, 0);
		if (axis == Axis.X)
			beltDir = new Vec3i(0, isUpwards ? 1 : 0, 1);
		else
			beltDir = new Vec3i(-1, isUpwards ? 1 : 0, 0);
		if (getRPM(0) < 0) beltDir.mulI(-1);
		
		return beltDir;
	}
	
	public static void moveItemsTick(Level pLevel, BlockPos pPos, BlockState pState, ConveyorBeltBlockEntity pBlockEntity) {
		
		float motionSpeed = (float) Math.abs(pBlockEntity.getRPM(0)) * 0.0006F;
		boolean itemToPush = false;

		Iterator<ItemOnBelt> itemIter = pBlockEntity.items.iterator();
		while (itemIter.hasNext()) {
			ItemOnBelt item = itemIter.next();
			if (item.stack.isEmpty()) itemIter.remove();
			if (item.position < 1.0F)
				item.position += motionSpeed;
			else
				itemToPush = true;
		}
		
		if (itemToPush)
			tryPushItemOut(pLevel, pPos, pState, pBlockEntity);
		
	}
	
	protected static void tryPushItemOut(Level pLevel, BlockPos pPos, BlockState pState, ConveyorBeltBlockEntity pBlockEntity) {
		
		Axis axis = pState.getValue(ConveyorBeltBlock.AXIS);
		DiagonalPlanarDirection orientation = pState.getValue(ConveyorBeltBlock.ORIENTATION);
		boolean isEnd = pState.getValue(ConveyorBeltBlock.IS_END);
		
		Direction handingDirection = null;
		if (axis == Axis.Z)
			handingDirection = pBlockEntity.rpm > 0 ? Direction.WEST : Direction.EAST;
		else
			handingDirection = pBlockEntity.rpm > 0 ? Direction.SOUTH : Direction.NORTH;
		BlockPos handTo = pPos.relative(handingDirection);
		
		boolean isHorizontal = orientation.getNormal().y == 0;
		boolean isUpwards = orientation.getNormal().x == orientation.getNormal().y ^ pBlockEntity.rpm > 0;
		boolean isEndHorizontal = isEnd && orientation.getNormal().y < 0 == pBlockEntity.rpm > 0;
		
		handTo = handTo.offset(0, (isHorizontal || isEndHorizontal) ? 0 : isUpwards ? 1 : -1, 0);
		
		BlockState targetState = pLevel.getBlockState(handTo);
		boolean handsToBelt = CompoundBlock.performOnAllAndCombine(pLevel, handTo, 
				() -> targetState.is(ModTags.Blocks.CONVEYOR_BELTS),
				(compound, p) -> p.getState().is(ModTags.Blocks.CONVEYOR_BELTS),
				Boolean::logicalOr);
		
		if (!handsToBelt) {
			if (tryHandItemTo(pLevel, pBlockEntity, handTo.above(), handingDirection))
				return;
		}
		if (tryHandItemTo(pLevel, pBlockEntity, handTo, handingDirection))
			return;
		
		if (!targetState.isFaceSturdy(pLevel, pPos, handingDirection.getOpposite())) {
			
			if (pBlockEntity.items.isEmpty()) return;
			ItemOnBelt toDrop = pBlockEntity.items.get(0);
			if (toDrop.stack.isEmpty()) return;
			
			GameUtility.dropItem(pLevel, toDrop.stack, Vec3f.fromVec(handTo).add(0.5F, 1.5F, 0.5F), 0.3F, 0.4F);
			pBlockEntity.items.remove(toDrop);
			
		}
		
	}
	
	protected ItemOnBelt getLastInserted() {
		return this.items.size() == 0 ? null : this.items.get(this.items.size() - 1);
	}
	
	protected static boolean tryHandItemTo(Level pLevel, ConveyorBeltBlockEntity pBlockEntity, BlockPos pTarget, Direction direction) {
		
		BlockEntity blockEntity = CompoundBlock.getBlockEntityMaybeInCompound(pLevel, pTarget, ConveyorBeltBlockEntity.class);
		if (blockEntity == null)
			blockEntity = pLevel.getBlockEntity(pTarget);
		BlockState targetState = blockEntity != null ? blockEntity.getBlockState() : pLevel.getBlockState(pTarget);
		
		if (targetState.getBlock() instanceof WorldlyContainerHolder containerHolder) {
			WorldlyContainer container = containerHolder.getContainer(targetState, pLevel, pTarget);
			if (container != null && tryTransferItemTo(pBlockEntity, container, direction))
				return true;	
		} else if (blockEntity instanceof Container container) {
			if (tryTransferItemTo(pBlockEntity, container, direction)) {
		
				if (blockEntity instanceof ConveyorBeltBlockEntity conveyor) {
					ItemOnBelt item = conveyor.getLastInserted();
					if (item != null) {
						item.insertedFrom = Vec3f.fromVec(conveyor.getBlockPos()).sub(Vec3f.fromVec(pBlockEntity.getBlockPos()));
						item.position = -1F;
						conveyor.setChanged();
					}
				}
			}
		}
		
		return false;
		
	}
	
	protected static boolean tryTransferItemTo(ConveyorBeltBlockEntity pBlockEntity, Container container, Direction direction) {
		
		if (pBlockEntity.items.isEmpty()) return true;
		ItemOnBelt toTransfer = pBlockEntity.items.get(0);
		if (toTransfer.position < 1.0F && toTransfer.position > -1.0F) return true;
		
		if (container instanceof WorldlyContainer worldlyContainer) {
			for (int slot : worldlyContainer.getSlotsForFace(direction.getOpposite())) {
				if (worldlyContainer.canPlaceItemThroughFace(slot, toTransfer.stack, direction.getOpposite()))
					if (mergeItemsTo(pBlockEntity, toTransfer, worldlyContainer, slot)) return true;
			}
		} else {
			for (int slot = 0; slot < container.getContainerSize(); slot++) {
				if (mergeItemsTo(pBlockEntity, toTransfer, container, slot)) return true;
			}
		}

		return true; // The container does not accept items, but its still an container the belt connects to
		
	}
	
	protected static boolean mergeItemsTo(ConveyorBeltBlockEntity pBlockEntity, ItemOnBelt toTransfer, Container container, int slot) {
		
		ItemStack inTarget = container.getItem(slot);
		if (inTarget.isEmpty()) {
			container.setItem(slot, toTransfer.stack);
			pBlockEntity.items.remove(toTransfer);
			return true;
		} else if (inTarget.getItem() == toTransfer.stack.getItem() && Objects.equals(inTarget.getTag(), toTransfer.stack.getTag())) {
			int transferCount = Math.min(inTarget.getMaxStackSize() - inTarget.getCount(), toTransfer.stack.getCount());
			inTarget.grow(transferCount);
			toTransfer.stack.shrink(transferCount);
			if (toTransfer.stack.isEmpty()) {
				pBlockEntity.items.remove(toTransfer);
				return true;
			}
		}
		
		return false;
		
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
			item.insertedFrom = NBTUtility.loadVector3f(itemTag.getCompound("InsertedFrom"));
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
			itemTag.put("InsertedFrom", NBTUtility.writeVector3f(this.items.get(i).insertedFrom));
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
			itemTag.put("InsertedFrom", NBTUtility.writeVector3f(this.items.get(i).insertedFrom));
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
