package de.industria.tileentity;

import java.util.function.Supplier;
import java.util.stream.IntStream;

import de.industria.Industria;
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
	public void clear() {
		this.beltMoveStateIn = this.beltMoveStateOut = 0;
		super.clear();
	}
	
	// Called from other belts, when inserting item, do set beltMoveStateIn and beltInsertSide
	public void onItemInserted(Direction side) {
		
		this.beltMoveStateIn = 0;
		
		BlockState state = this.getBlockState();
		Direction facing = state.get(BlockConveyorBelt.FACING);
		
		if (facing == side.getOpposite()) {
			this.beltInsertSide = 0;
		} else if (facing.rotateY() == side.getOpposite()) {
			this.beltInsertSide = 1;
		} else if (facing.rotateYCCW() == side.getOpposite()) {
			this.beltInsertSide = -1;
		}
		
	}
	
	@Override
	public void tick() {
		
		if (!this.world.isRemote()) {
			
			this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
			
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
				if (!this.getStackInSlot(1).isEmpty()) dropItem();
			}
			
			if (!this.getStackInSlot(2).isEmpty()) {
				if (canMoveOutSecondary() && beltMoveStateOutSecondary == 8) {
					moveItemOutSecondary();
				}
			}
			
			if (hasSwitchedSecondary() && this.beltMoveStateIn >= 8 && this.getStackInSlot(2).isEmpty()) {
				this.setInventorySlotContents(2, this.getStackInSlot(0));
				this.setInventorySlotContents(0, ItemStack.EMPTY);
				this.beltMoveStateOutSecondary = 0;
			} else if (this.beltMoveStateIn >= 8 && this.getStackInSlot(1).isEmpty()) {
				this.setInventorySlotContents(1, this.getStackInSlot(0));
				this.setInventorySlotContents(0, ItemStack.EMPTY);
				this.beltMoveStateOut = 0;
			}
			
			if (this.beltMoveStateIn < (this.getStackInSlot(1).isEmpty() ? 8 : this.beltMoveStateOut) && isEnabled()) {
				if (this.moveTimer == 0) this.beltMoveStateIn++;
			}
			
			if (this.beltMoveStateOut < (canMoveOut() ? 8 : 4) && isEnabled()) {
				if (this.moveTimer == 0) this.beltMoveStateOut++;
			}
			
			if (!this.getStackInSlot(2).isEmpty()) {
				if (this.beltMoveStateOutSecondary < (canMoveOutSecondary() ? 8 : 4)) {
					if (this.moveTimer == 0) this.beltMoveStateOutSecondary++;
				}
			}
			
		}
		
	}
	
	public boolean hasSwitchedSecondary() {
		BlockState state = this.getBlockState();
		if (state.getBlock() == Industria.conveyor_spliter) {
			return state.get(BlockConveyorSpliter.ACTIVE);
		}
		return false;
	}
	
	public boolean isEnabled() {
		BlockState state = this.getBlockState();
		if (state.getBlock() == Industria.conveyor_switch) {
			return state.get(BlockConveyorSwitch.ENABLED);
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public boolean isNotConnected() {
		
		BlockState state = getBlockState();
		Direction facing = state.get(BlockConveyorBelt.FACING);
		BlockPos connectPos = this.pos.offset(facing);
		return this.world.getBlockState(connectPos).isAir();
		
	}

	@SuppressWarnings("deprecation")
	public boolean isNotConnectedSecondary() {
		
		BlockState state = getBlockState();
		Direction facing = getSecondary(state);
		BlockPos connectPos = this.pos.offset(facing);
		return this.world.getBlockState(connectPos).isAir();
		
	}
	
	public void dropItem() {
		Direction motion = getBlockState().get(BlockConveyorBelt.FACING);
		Vector3f motionVec = new Vector3f(motion.getXOffset() * 0.2F, 0.0F, motion.getZOffset() * 0.2F);
		ItemEntity drop = new ItemEntity(this.world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, this.getStackInSlot(1));
		drop.setMotion(motionVec.getX(), motionVec.getY(), motionVec.getZ());
		this.world.addEntity(drop);
		this.setInventorySlotContents(1, ItemStack.EMPTY);
	}
	
	public boolean moveItemIn() {
		
		if (getStackInSlot(0).isEmpty()) {
			
			BlockState state = getBlockState();
			Direction facing = state.get(BlockConveyorBelt.FACING);
			BlockPos sourcePos = this.pos.offset(facing.getOpposite());
			
			IInventory inventory = HopperTileEntity.getInventoryAtPosition(this.world, sourcePos);
			Supplier<IntStream> slots = () -> getAviableInventorySlots(inventory, facing.getOpposite());
			
			boolean isEmpty = slots.get().allMatch((slot) -> {
				ItemStack stack = inventory.getStackInSlot(slot);
				return stack.isEmpty();
			});
			
			if (!isEmpty) {
				
				for (int slot : slots.get().toArray()) {
					ItemStack beltItem = tryExtractItem(inventory, slot);
					if (!beltItem.isEmpty()) {
						this.setInventorySlotContents(0, beltItem);
						return true;
					}
				}
				
			}
			
		}
		
		return false;
		
	}
	
	public boolean canMoveIn() {

		BlockState state = getBlockState();
		Direction facing = state.get(BlockConveyorBelt.FACING);
		BlockPos sourcePos = this.pos.offset(facing.getOpposite());
		
		IInventory inventory = HopperTileEntity.getInventoryAtPosition(this.world, sourcePos);
		return inventory != null && !(inventory instanceof TileEntityConveyorBelt);
		
	}

	public Direction getSecondary(BlockState state) {
		Direction facing = state.get(BlockConveyorBelt.FACING);
		if (state.get(BlockConveyorBelt.LEFT) == BeltState.OPEN) {
			facing = facing.rotateYCCW();
		} else if (state.get(BlockConveyorBelt.RIGHT) == BeltState.OPEN) {
			facing = facing.rotateY();
		}
		return facing;
	}
	
	public boolean moveItemOut() {
		
		if (!this.getStackInSlot(1).isEmpty()) {
			
			BlockState state = getBlockState();
			Direction facing = state.get(BlockConveyorBelt.FACING);
			BlockPos targetPos = this.pos.offset(facing);
			
			IInventory inventory = HopperTileEntity.getInventoryAtPosition(this.world, targetPos);
			Supplier<IntStream> slots = () -> getAviableInventorySlots(inventory, facing);
			
			boolean isFull = slots.get().allMatch((slot) -> {
				ItemStack stack = inventory.getStackInSlot(slot);
				return stack.isEmpty() ? false : stack.getCount() >= stack.getMaxStackSize();
			});
			
			if (!isFull) {
				
				ItemStack beltItem = getStackInSlot(1);
				for (int slot : slots.get().toArray()) {
					beltItem = tryInsertItem(inventory, slot, beltItem);
					if (beltItem.isEmpty()) {
						setInventorySlotContents(1, beltItem);
						if (inventory instanceof TileEntityConveyorBelt) ((TileEntityConveyorBelt) inventory).onItemInserted(facing.getOpposite());
						return true;
					}
				}
				
			}
			
		}
		
		return false;
		
	}
	
	public boolean moveItemOutSecondary() {
		
		if (!this.getStackInSlot(2).isEmpty()) {
			
			BlockState state = getBlockState();
			Direction facing = getSecondary(state);
			BlockPos targetPos = this.pos.offset(facing);
			
			IInventory inventory = HopperTileEntity.getInventoryAtPosition(this.world, targetPos);
			Supplier<IntStream> slots = () -> getAviableInventorySlots(inventory, facing);
			
			boolean isFull = slots.get().allMatch((slot) -> {
				ItemStack stack = inventory.getStackInSlot(slot);
				return stack.isEmpty() ? false : stack.getCount() >= stack.getMaxStackSize();
			});
			
			if (!isFull) {
				
				ItemStack beltItem = getStackInSlot(2);
				for (int slot : slots.get().toArray()) {
					beltItem = tryInsertItem(inventory, slot, beltItem);
					if (beltItem.isEmpty()) {
						setInventorySlotContents(2, beltItem);
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
		Direction facing = state.get(BlockConveyorBelt.FACING);
		BlockPos targetPos = this.pos.offset(facing);
		
		IInventory inventory = HopperTileEntity.getInventoryAtPosition(this.world, targetPos);
		
		if (inventory != null) {
			
			Supplier<IntStream> slots = () -> getAviableInventorySlots(inventory, facing);
			
			boolean isFull = slots.get().allMatch((slot) -> {
				ItemStack stack = inventory.getStackInSlot(slot);
				return stack.isEmpty() ? false : (inventory instanceof TileEntityConveyorBelt ? true : stack.getCount() >= stack.getMaxStackSize());
			});
			
			return !isFull;
			
		}
		
		return false;
		
	}
	
	public boolean canMoveOutSecondary() {
		
		BlockState state = getBlockState();
		Direction facing = getSecondary(state);
		BlockPos targetPos = this.pos.offset(facing);
		
		IInventory inventory = HopperTileEntity.getInventoryAtPosition(this.world, targetPos);
		
		if (inventory != null) {
			
			Supplier<IntStream> slots = () -> getAviableInventorySlots(inventory, facing);
			
			boolean isFull = slots.get().allMatch((slot) -> {
				ItemStack stack = inventory.getStackInSlot(slot);
				return stack.isEmpty() ? false : (inventory instanceof TileEntityConveyorBelt ? true : stack.getCount() >= stack.getMaxStackSize());
			});
			
			return !isFull;
			
		}
		
		return false;
		
	}
	
	public ItemStack tryExtractItem(IInventory inventory, int slot) {
		
		boolean flag = inventory instanceof ISidedInventory ? ((ISidedInventory) inventory).canExtractItem(slot, inventory.getStackInSlot(slot), Direction.DOWN) : true;
				
		if (flag) {
			ItemStack item = inventory.getStackInSlot(slot);
			if (item.isEmpty()) {
				return ItemStack.EMPTY;
			} else {
				inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
				return item;
			}
		}
		return ItemStack.EMPTY;
		
	}

	public ItemStack tryInsertItem(IInventory inventory, int slot, ItemStack item) {
		
		boolean flag = inventory instanceof ISidedInventory ? ((ISidedInventory) inventory).canInsertItem(slot, item, Direction.NORTH) || ((ISidedInventory) inventory).canInsertItem(slot, item, Direction.UP) : true;
		
		if (flag) {
			ItemStack stack = inventory.getStackInSlot(slot);
			if (stack.isEmpty()) {
				inventory.setInventorySlotContents(slot, item);
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
		return inventory instanceof ISidedInventory ? IntStream.of(((ISidedInventory) inventory).getSlotsForFace(direction.getOpposite())) : IntStream.range(0, inventory.getSizeInventory());
	}
	
	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] {0, 1};
	}
	
	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return index == 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return index == 1;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("conveyorStateIn", this.beltMoveStateIn);
		compound.putInt("conveyorStateOut", this.beltMoveStateOut);
		compound.putInt("conveyorStateOutSecondary", this.beltMoveStateOutSecondary);
		compound.putInt("conveyorInsertSide", this.beltInsertSide);
		compound.putInt("moveTimer", this.moveTimer);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.beltMoveStateIn = compound.getInt("conveyorStateIn");
		this.beltMoveStateOut = compound.getInt("conveyorStateOut");
		this.beltMoveStateOutSecondary = compound.getInt("conveyorStateOutSecondary");
		this.beltInsertSide = compound.getInt("conveyorInsertSide");
		this.moveTimer = compound.getInt("moveTimer");
		super.read(state, compound);
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, 0, this.serializeNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.deserializeNBT(pkt.getNbtCompound());
	}
	
}
