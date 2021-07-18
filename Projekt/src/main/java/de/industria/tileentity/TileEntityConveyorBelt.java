package de.industria.tileentity;

import java.util.function.Supplier;
import java.util.stream.IntStream;

import de.industria.ModItems;
import de.industria.blocks.BlockConveyorBelt;
import de.industria.blocks.BlockConveyorSpliter;
import de.industria.blocks.BlockConveyorSwitch;
import de.industria.blocks.BlockConveyorBelt.BeltState;
import de.industria.typeregistys.ModTileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntityConveyorBelt extends TileEntityInventoryBase implements ITickableTileEntity, ISidedInventory {
	
	public int beltMoveStateIn;
	public int beltMoveStateOut;
	public int beltMoveStateOutSecondary;
	public int beltInsertSide;
	public int moveTimer;
	
	public final int conveyorTime;
	
	public TileEntityConveyorBelt() {
		super(ModTileEntityType.CONVEYOR_BELT, 3);
		this.conveyorTime = 2;
	}
	
	@Override
	public void clearContent() {
		this.beltMoveStateIn = this.beltMoveStateOut = 0;
		super.clearContent();
	}
	
	// Called from other belts, when inserting item, do set beltMoveStateIn and beltInsertSide
	public void onItemInserted(Direction side) {
		
		this.beltMoveStateIn = 0;
		
		BlockState state = this.getBlockState();
		Direction facing = state.getValue(BlockConveyorBelt.FACING);
		
		if (facing == side.getOpposite()) {
			this.beltInsertSide = 0;
		} else if (facing.getClockWise() == side.getOpposite()) {
			this.beltInsertSide = 1;
		} else if (facing.getCounterClockWise() == side.getOpposite()) {
			this.beltInsertSide = -1;
		}
		
	}
	
	@Override
	public void tick() {
		
		if (!this.level.isClientSide()) {
			
			this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
			
			this.moveTimer++;
			if (this.moveTimer > conveyorTime) this.moveTimer = 0;
			
			if (canMoveIn()) {
				if (moveItemIn()) {
					this.beltMoveStateIn = 0;
				}
			}
			
			if (canMoveOut() && beltMoveStateOut == 8) {
				moveItemOut();
			} else if (beltMoveStateOut == 4 && isNotConnected()){
				if (!this.getItem(1).isEmpty()) dropItem();
			}
			
			if (!this.getItem(2).isEmpty()) {
				if (canMoveOutSecondary() && beltMoveStateOutSecondary == 8) {
					moveItemOutSecondary();
				}
			}
			
			if (hasSwitchedSecondary() && this.beltMoveStateIn >= 8 && this.getItem(2).isEmpty()) {
				this.setItem(2, this.getItem(0));
				this.setItem(0, ItemStack.EMPTY);
				this.beltMoveStateOutSecondary = 0;
			} else if (this.beltMoveStateIn >= 8 && this.getItem(1).isEmpty()) {
				this.setItem(1, this.getItem(0));
				this.setItem(0, ItemStack.EMPTY);
				this.beltMoveStateOut = 0;
			}
			
			if (this.beltMoveStateIn < (this.getItem(1).isEmpty() ? 8 : this.beltMoveStateOut) && isEnabled()) {
				if (this.moveTimer == 0) this.beltMoveStateIn++;
			}
			
			if (this.beltMoveStateOut < (canMoveOut() ? 8 : 4) && isEnabled()) {
				if (this.moveTimer == 0) this.beltMoveStateOut++;
			}
			
			if (!this.getItem(2).isEmpty()) {
				if (this.beltMoveStateOutSecondary < (canMoveOutSecondary() ? 8 : 4)) {
					if (this.moveTimer == 0) this.beltMoveStateOutSecondary++;
				}
			}
			
		}
		
	}
	
	public boolean hasSwitchedSecondary() {
		BlockState state = this.getBlockState();
		if (state.getBlock() == ModItems.conveyor_spliter) {
			return state.getValue(BlockConveyorSpliter.ACTIVE);
		}
		return false;
	}
	
	public boolean isEnabled() {
		BlockState state = this.getBlockState();
		if (state.getBlock() == ModItems.conveyor_switch) {
			return state.getValue(BlockConveyorSwitch.ENABLED);
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public boolean isNotConnected() {
		
		BlockState state = getBlockState();
		Direction facing = state.getValue(BlockConveyorBelt.FACING);
		BlockPos connectPos = this.worldPosition.relative(facing);
		return this.level.getBlockState(connectPos).isAir();
		
	}

	@SuppressWarnings("deprecation")
	public boolean isNotConnectedSecondary() {
		
		BlockState state = getBlockState();
		Direction facing = getSecondary(state);
		BlockPos connectPos = this.worldPosition.relative(facing);
		return this.level.getBlockState(connectPos).isAir();
		
	}
	
	public void dropItem() {
		Direction motion = getBlockState().getValue(BlockConveyorBelt.FACING);
		Vector3f motionVec = new Vector3f(motion.getStepX() * 0.2F, 0.0F, motion.getStepZ() * 0.2F);
		ItemEntity drop = new ItemEntity(this.level, worldPosition.getX() + 0.5F, worldPosition.getY() + 0.5F, worldPosition.getZ() + 0.5F, this.getItem(1));
		drop.setDeltaMovement(motionVec.x(), motionVec.y(), motionVec.z());
		drop.setPickUpDelay(60);
		this.level.addFreshEntity(drop);
		this.setItem(1, ItemStack.EMPTY);
	}
	
	public boolean moveItemIn() {
		
		if (getItem(0).isEmpty()) {
			
			BlockState state = getBlockState();
			Direction facing = state.getValue(BlockConveyorBelt.FACING);
			BlockPos sourcePos = this.worldPosition.relative(facing.getOpposite());
			
			IInventory inventory = HopperTileEntity.getContainerAt(this.level, sourcePos);
			Supplier<IntStream> slots = () -> getAviableInventorySlots(inventory, facing.getOpposite());
			
			boolean isEmpty = slots.get().allMatch((slot) -> {
				ItemStack stack = inventory.getItem(slot);
				return stack.isEmpty();
			});
			
			if (!isEmpty) {
				
				for (int slot : slots.get().toArray()) {
					ItemStack beltItem = tryExtractItem(inventory, slot);
					if (!beltItem.isEmpty()) {
						this.setItem(0, beltItem);
						return true;
					}
				}
				
			}
			
		}
		
		return false;
		
	}
	
	public boolean canMoveIn() {

		BlockState state = getBlockState();
		Direction facing = state.getValue(BlockConveyorBelt.FACING);
		BlockPos sourcePos = this.worldPosition.relative(facing.getOpposite());
		
		IInventory inventory = HopperTileEntity.getContainerAt(this.level, sourcePos);
		return inventory != null && !(inventory instanceof TileEntityConveyorBelt);
		
	}

	public Direction getSecondary(BlockState state) {
		Direction facing = state.getValue(BlockConveyorBelt.FACING);
		if (state.getValue(BlockConveyorBelt.LEFT) == BeltState.OPEN) {
			facing = facing.getCounterClockWise();
		} else if (state.getValue(BlockConveyorBelt.RIGHT) == BeltState.OPEN) {
			facing = facing.getClockWise();
		}
		return facing;
	}
	
	public boolean moveItemOut() {
		
		if (!this.getItem(1).isEmpty()) {
			
			BlockState state = getBlockState();
			Direction facing = state.getValue(BlockConveyorBelt.FACING);
			BlockPos targetPos = this.worldPosition.relative(facing);
			
			IInventory inventory = HopperTileEntity.getContainerAt(this.level, targetPos);
			Supplier<IntStream> slots = () -> getAviableInventorySlots(inventory, facing);
			
			boolean isFull = slots.get().allMatch((slot) -> {
				ItemStack stack = inventory.getItem(slot);
				return stack.isEmpty() ? false : stack.getCount() >= stack.getMaxStackSize();
			});
			
			if (!isFull) {
				
				ItemStack beltItem = getItem(1);
				for (int slot : slots.get().toArray()) {
					beltItem = tryInsertItem(inventory, slot, beltItem);
					if (beltItem.isEmpty()) {
						setItem(1, beltItem);
						if (inventory instanceof TileEntityConveyorBelt) ((TileEntityConveyorBelt) inventory).onItemInserted(facing.getOpposite());
						return true;
					}
				}
				
			}
			
		}
		
		return false;
		
	}
	
	public boolean moveItemOutSecondary() {
		
		if (!this.getItem(2).isEmpty()) {
			
			BlockState state = getBlockState();
			Direction facing = getSecondary(state);
			BlockPos targetPos = this.worldPosition.relative(facing);
			
			IInventory inventory = HopperTileEntity.getContainerAt(this.level, targetPos);
			Supplier<IntStream> slots = () -> getAviableInventorySlots(inventory, facing);
			
			boolean isFull = slots.get().allMatch((slot) -> {
				ItemStack stack = inventory.getItem(slot);
				return stack.isEmpty() ? false : stack.getCount() >= stack.getMaxStackSize();
			});
			
			if (!isFull) {
				
				ItemStack beltItem = getItem(2);
				for (int slot : slots.get().toArray()) {
					beltItem = tryInsertItem(inventory, slot, beltItem);
					if (beltItem.isEmpty()) {
						setItem(2, beltItem);
						if (inventory instanceof TileEntityConveyorBelt) ((TileEntityConveyorBelt) inventory).onItemInserted(facing.getOpposite());
						return true;
					}
				}
				
			}
			
		}
		
		return false;
		
	}
	
	public boolean canMoveOut() {
		
		BlockState state = getBlockState();
		Direction facing = state.getValue(BlockConveyorBelt.FACING);
		BlockPos targetPos = this.worldPosition.relative(facing);
		
		IInventory inventory = HopperTileEntity.getContainerAt(this.level, targetPos);
		
		if (inventory != null) {
			
			Supplier<IntStream> slots = () -> getAviableInventorySlots(inventory, facing);
			
			boolean isFull = slots.get().allMatch((slot) -> {
				ItemStack stack = inventory.getItem(slot);
				return stack.isEmpty() ? false : (inventory instanceof TileEntityConveyorBelt ? true : stack.getCount() >= stack.getMaxStackSize());
			});
			
			return !isFull;
			
		}
		
		return false;
		
	}
	
	public boolean canMoveOutSecondary() {
		
		BlockState state = getBlockState();
		Direction facing = getSecondary(state);
		BlockPos targetPos = this.worldPosition.relative(facing);
		
		IInventory inventory = HopperTileEntity.getContainerAt(this.level, targetPos);
		
		if (inventory != null) {
			
			Supplier<IntStream> slots = () -> getAviableInventorySlots(inventory, facing);
			
			boolean isFull = slots.get().allMatch((slot) -> {
				ItemStack stack = inventory.getItem(slot);
				return stack.isEmpty() ? false : (inventory instanceof TileEntityConveyorBelt ? true : stack.getCount() >= stack.getMaxStackSize());
			});
			
			return !isFull;
			
		}
		
		return false;
		
	}
	
	public ItemStack tryExtractItem(IInventory inventory, int slot) {
		
		boolean flag = inventory instanceof ISidedInventory ? ((ISidedInventory) inventory).canTakeItemThroughFace(slot, inventory.getItem(slot), Direction.DOWN) : true;
				
		if (flag) {
			ItemStack item = inventory.getItem(slot);
			if (item.isEmpty()) {
				return ItemStack.EMPTY;
			} else {
				inventory.setItem(slot, ItemStack.EMPTY);
				return item;
			}
		}
		return ItemStack.EMPTY;
		
	}

	public ItemStack tryInsertItem(IInventory inventory, int slot, ItemStack item) {
		
		boolean flag = inventory instanceof ISidedInventory ? ((ISidedInventory) inventory).canPlaceItemThroughFace(slot, item, Direction.NORTH) || ((ISidedInventory) inventory).canPlaceItemThroughFace(slot, item, Direction.UP) : true;
		
		if (flag) {
			ItemStack stack = inventory.getItem(slot);
			if (stack.isEmpty()) {
				inventory.setItem(slot, item);
				return ItemStack.EMPTY;
			} else if (stack.getItem() == item.getItem()) {
				int it = Math.min(item.getCount(), stack.getMaxStackSize() - stack.getCount());
				item.shrink(it);
				stack.grow(it);
			}
		}
		return item;
	}
	
	public IntStream getAviableInventorySlots(IInventory inventory, Direction direction) {
		return inventory instanceof ISidedInventory ? IntStream.of(((ISidedInventory) inventory).getSlotsForFace(direction.getOpposite())) : IntStream.range(0, inventory.getContainerSize());
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1};
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, Direction direction) {
		return index == 0;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return index == 1;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putInt("conveyorStateIn", this.beltMoveStateIn);
		compound.putInt("conveyorStateOut", this.beltMoveStateOut);
		compound.putInt("conveyorStateOutSecondary", this.beltMoveStateOutSecondary);
		compound.putInt("conveyorInsertSide", this.beltInsertSide);
		compound.putInt("moveTimer", this.moveTimer);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.beltMoveStateIn = compound.getInt("conveyorStateIn");
		this.beltMoveStateOut = compound.getInt("conveyorStateOut");
		this.beltMoveStateOutSecondary = compound.getInt("conveyorStateOutSecondary");
		this.beltInsertSide = compound.getInt("conveyorInsertSide");
		this.moveTimer = compound.getInt("moveTimer");
		super.load(state, compound);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(worldPosition, 0, this.serializeNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getTag());
	}
	
}
