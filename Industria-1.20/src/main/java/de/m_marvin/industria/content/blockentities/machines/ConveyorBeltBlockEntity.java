package de.m_marvin.industria.content.blockentities.machines;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ConveyorBeltBlockEntity extends BaseBeltBlockEntity implements Container {
	
	public class ItemOnBelt {
		public final ItemStack stack;
		public float position;
		public float rotation;
		public float offset;
		
		public Vec3f insertedFrom;
		public final long insertedAt;
		public boolean isClogged;
		
		public ItemOnBelt(ItemStack item, long insertedAt, float position, float rotation) {
			this.insertedAt = insertedAt;
			this.stack = item;
			this.position = position;
			this.rotation = rotation;
			this.insertedFrom = new Vec3f();
		}
		
		@Override
		public String toString() {
			return "[" + this.stack.toString() + " @ " + this.offset + "]";
		}
	}

	protected final float itemSpacing = 0.25F;
	protected final int itemsPerSection = (int) Math.floor(1F / this.itemSpacing);
	protected final List<ItemOnBelt> items = new ArrayList<ConveyorBeltBlockEntity.ItemOnBelt>();
	
	public ConveyorBeltBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(ModBlockEntityTypes.CONVEYOR_BELT.get(), pPos, pBlockState);
	}
	
	public Vec3i getVisualBeltDirection() {
		Axis axis = getBlockState().getValue(ConveyorBeltBlock.AXIS);
		DiagonalPlanarDirection orientation = getBlockState().getValue(ConveyorBeltBlock.ORIENTATION);
		boolean isReversed = getRPM(0) < 0;
		boolean isEnd = getBlockState().getValue(ConveyorBeltBlock.IS_END);
		boolean isHorizontal = orientation.getNormal().y == 0 || (isEnd && (axis == Axis.Z ^ orientation.getNormal().x < 0 ^ isReversed));
		boolean isUpwards = !isHorizontal && (orientation.getNormal().x == orientation.getNormal().y ^ axis == Axis.Z);
		boolean isDownwards = !isHorizontal && !isUpwards;
		
		Vec3i beltDir = new Vec3i(0, 0, 0);
		if (axis == Axis.X)
			beltDir = new Vec3i(0, isUpwards ? 1 : isDownwards ? -1 : 0, 1);
		else
			beltDir = new Vec3i(-1, isUpwards ? 1 : isDownwards ? -1 : 0, 0);
		if (isReversed) beltDir.mulI(-1);
		
		return beltDir;
	}
	
	public double getItemTransportSpeed() {
		return getRPM(0) * 0.0014F;
	}
	
	public static void moveItemsTick(Level pLevel, BlockPos pPos, BlockState pState, ConveyorBeltBlockEntity pBlockEntity) {
		float motionSpeed = (float) Math.abs(pBlockEntity.getItemTransportSpeed());
		float conveyorItemDistance = pBlockEntity.itemSpacing * 2; // convert from block to -1 to +1 range
		
		Iterator<ItemOnBelt> itemIter = pBlockEntity.items.iterator();
		ItemOnBelt prevItem = null;
		while (itemIter.hasNext()) {
			ItemOnBelt item = itemIter.next();
			
			if (item.insertedAt != pLevel.getGameTime() && (!item.isClogged || !pBlockEntity.level.isClientSide())) {
				if (prevItem == null) {
					// check if item should be pushed to next container
					if (item.offset + motionSpeed >= 1.0F - conveyorItemDistance) {
						
						// try to move item to the next container
						item.offset += motionSpeed; // additional movement tick to prevent stutter in transition
						if (tryPushItemOut(pLevel, pPos, pState, pBlockEntity, item)) {
							itemIter.remove();
							item.isClogged = false;
							pBlockEntity.setChanged();
							GameUtility.triggerClientSync(pLevel, pPos);
						} else if (!item.isClogged) {
							item.offset = 1.0F - conveyorItemDistance;
							item.isClogged = true;
							pBlockEntity.setChanged();
							GameUtility.triggerClientSync(pLevel, pPos);
						} else {
							item.offset -= motionSpeed; // undo additional tick to prevent stutter when item stopped
						}
					} else {
						// just increment position
						item.offset += motionSpeed;
						item.isClogged = false;
						pBlockEntity.setChanged();
					}
				} else {
					// just increment position
					if (item.offset + motionSpeed < prevItem.offset - conveyorItemDistance) {
						item.offset += motionSpeed;
						if (item.isClogged) {
							item.isClogged = false;
							GameUtility.triggerClientSync(pLevel, pPos);
						}
						pBlockEntity.setChanged();
					} else if (!item.isClogged) {
						item.offset = prevItem.offset - conveyorItemDistance;
						item.isClogged = true;
						pBlockEntity.setChanged();
						GameUtility.triggerClientSync(pLevel, pPos);
					}
				}
			}
			
			prevItem = item;
		}
		
	}
	
	protected static boolean tryPushItemOut(Level pLevel, BlockPos pPos, BlockState pState, ConveyorBeltBlockEntity pBlockEntity, ItemOnBelt itemToPush) {
		
		Axis axis = pState.getValue(ConveyorBeltBlock.AXIS);
		Vec3i beltDir = pBlockEntity.getVisualBeltDirection();
		
		Direction handingDirection = null;
		if (axis == Axis.Z)
			handingDirection = beltDir.x < 0 ? Direction.WEST : Direction.EAST;
		else
			handingDirection = beltDir.z < 0 ? Direction.SOUTH : Direction.NORTH;
		BlockPos handTo = pPos.offset(beltDir.x, beltDir.y, beltDir.z);
		
		BlockState targetState = pLevel.getBlockState(handTo);
		boolean handsToBelt = CompoundBlock.performOnAllAndCombine(pLevel, handTo, 
				() -> targetState.is(ModTags.Blocks.CONVEYOR_BELTS),
				(compound, p) -> p.getState().is(ModTags.Blocks.CONVEYOR_BELTS),
				Boolean::logicalOr);
		
		if (!handsToBelt) {
			int r = tryHandItemTo(pLevel, pBlockEntity, handTo.above(), handingDirection, itemToPush, false);
			if (r != -1) return r == 1;
		}
		int r = tryHandItemTo(pLevel, pBlockEntity, handTo, handingDirection, itemToPush, handsToBelt);
		if (r != -1) return r == 1;
		
		if (!targetState.isFaceSturdy(pLevel, pPos, handingDirection.getOpposite()) && !handsToBelt) {
			
			if (itemToPush.stack.isEmpty()) return true;
			
			Vec3f dropVelocity = new Vec3f(beltDir).mul((float) Math.abs(pBlockEntity.getRPM(0)) * 0.001F);
			Vec3f dropPosition = Vec3f.fromVec(handTo).add(0.5F, 0.8F, 0.5F);
			ItemEntity drop = new ItemEntity(pLevel, dropPosition.x, dropPosition.y, dropPosition.z, itemToPush.stack);
			drop.setDeltaMovement(dropVelocity.writeTo(new Vec3(0, 0, 0)));
			pLevel.addFreshEntity(drop);
			
			return true;
			
		}
		
		return false;
	}
	
	protected ItemOnBelt getLastInserted() {
		return this.items.size() == 0 ? null : this.items.get(this.items.size() - 1);
	}
	
	protected static int tryHandItemTo(Level pLevel, ConveyorBeltBlockEntity pBlockEntity, BlockPos pTarget, Direction direction, ItemOnBelt itemToPush, boolean handsToBelt) {
		
		BlockEntity blockEntity = CompoundBlock.getBlockEntityMaybeInCompound(pLevel, pTarget, ConveyorBeltBlockEntity.class);
		if (blockEntity == null)
			blockEntity = pLevel.getBlockEntity(pTarget);
		BlockState targetState = blockEntity != null ? blockEntity.getBlockState() : pLevel.getBlockState(pTarget);
		
		if (targetState.getBlock() instanceof WorldlyContainerHolder containerHolder) {
			WorldlyContainer container = containerHolder.getContainer(targetState, pLevel, pTarget);
			if (container != null) {
				if (tryTransferItemTo(pBlockEntity, container, direction, itemToPush, !handsToBelt))
					return 1;
				return 0;
			}
		} else if (blockEntity instanceof Container container) {
			if (tryTransferItemTo(pBlockEntity, container, direction, itemToPush, !handsToBelt)) {
				
				if (blockEntity instanceof ConveyorBeltBlockEntity conveyor) {
					ItemOnBelt item = conveyor.getLastInserted();
					if (item != null) {
						item.insertedFrom = Vec3f.fromVec(conveyor.getBlockPos()).sub(Vec3f.fromVec(pBlockEntity.getBlockPos()));
						item.offset = itemToPush.offset - 2F;
						item.position = itemToPush.position;
						item.rotation = itemToPush.rotation;
						conveyor.setChanged();
					}
				}
				
				return 1;
			}
			return 0;
		}
		
		return -1;
		
	}
	
	protected static boolean tryTransferItemTo(ConveyorBeltBlockEntity pBlockEntity, Container container, Direction direction, ItemOnBelt itemToPush, boolean doStack) {
		
		if (pBlockEntity.items.isEmpty()) return true;
		
		if (container instanceof WorldlyContainer worldlyContainer) {
			for (int slot : worldlyContainer.getSlotsForFace(direction.getOpposite())) {
				if (worldlyContainer.canPlaceItemThroughFace(slot, itemToPush.stack, direction.getOpposite()))
					if (mergeItemsTo(pBlockEntity, itemToPush, worldlyContainer, slot, doStack)) return true;
			}
		} else {
			for (int slot = 0; slot < container.getContainerSize(); slot++) {
				if (mergeItemsTo(pBlockEntity, itemToPush, container, slot, doStack)) return true;
			}
		}

		return false;
		
	}
	
	protected static boolean mergeItemsTo(ConveyorBeltBlockEntity pBlockEntity, ItemOnBelt toTransfer, Container container, int slot, boolean doStack) {
		ItemStack inTarget = container.getItem(slot);
		if (inTarget.isEmpty()) {
			container.setItem(slot, toTransfer.stack);
			return true;
		} else if (doStack && ItemStack.isSameItemSameTags(inTarget, toTransfer.stack)) {
			int transferCount = Math.min(inTarget.getMaxStackSize() - inTarget.getCount(), toTransfer.stack.getCount());
			inTarget.grow(transferCount);
			toTransfer.stack.shrink(transferCount);
			return toTransfer.stack.isEmpty();
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
			ItemStack item = ItemStack.of(itemTag.getCompound("Item"));
			float position = itemTag.getFloat("Position");
			float rotation = itemTag.getFloat("Rotation");
			ItemOnBelt itemOnBelt = new ItemOnBelt(item, 0L, position, rotation);
			itemOnBelt.offset = itemTag.getFloat("offset");
			itemOnBelt.isClogged = itemTag.getBoolean("isClogged");
			itemOnBelt.insertedFrom = NBTUtility.loadVector3f(itemTag.getCompound("insertedFrom"));
			this.items.add(itemOnBelt);
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		
		ListTag itemsTag = new ListTag();
		for (ItemOnBelt item : this.items) {
			if (item.stack.isEmpty()) continue;
			CompoundTag itemTag = new CompoundTag();
			itemTag.putFloat("offset", item.offset);
			itemTag.putBoolean("isClogged", item.isClogged);
			itemTag.put("insertedFrom", NBTUtility.writeVector3f(item.insertedFrom));
			itemTag.put("Item", item.stack.save(new CompoundTag()));
			itemTag.putFloat("Position", item.position);
			itemTag.putFloat("Rotation", item.rotation);
			itemsTag.add(itemTag);
		}
		pTag.put("ItemsOnBelt", itemsTag);
	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		
		ListTag itemsTag = new ListTag();
		for (ItemOnBelt item : this.items) {
			if (item.stack.isEmpty()) continue;
			CompoundTag itemTag = new CompoundTag();
			itemTag.putFloat("offset", item.offset);
			itemTag.putBoolean("isClogged", item.isClogged);
			itemTag.put("insertedFrom", NBTUtility.writeVector3f(item.insertedFrom));
			itemTag.put("Item", item.stack.save(new CompoundTag()));
			itemTag.putFloat("Position", item.position);
			itemTag.putFloat("Rotation", item.rotation);
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
//		if (this.items.size() > pSlot)
//			return this.items.get(pSlot).stack.isEmpty()
		return this.items.size() > pSlot ? this.items.get(pSlot).stack : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int pSlot, int pAmount) {
		ItemStack stack = getItem(pSlot);
		if (stack.getCount() <= pAmount) {
			this.items.remove(pSlot);
			setChanged();
			GameUtility.triggerClientSync(level, worldPosition);
			return stack;
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
		float position = this.level.random.nextFloat() * 0.5F - 0.25F;
		float rotation = (float) (this.level.random.nextFloat() * 360 - 180);
		if (pSlot < this.items.size()) {
			ItemOnBelt onBelt = this.items.set(pSlot, new ItemOnBelt(pStack, this.level.getGameTime(), position, rotation));
			this.items.get(pSlot).offset = onBelt.offset;
		} else {
			this.items.add(new ItemOnBelt(pStack, this.level.getGameTime(), position, rotation));
		}
		setChanged();
		GameUtility.triggerClientSync(level, worldPosition);
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return false;
	}
	
}
