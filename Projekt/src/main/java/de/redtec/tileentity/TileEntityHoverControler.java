package de.redtec.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.redtec.RedTec;
import de.redtec.blocks.BlockHoverControler;
import de.redtec.blocks.BlockHoverExtension;
import de.redtec.gui.ContainerHoverControler;
import de.redtec.typeregistys.ModTileEntityType;
import de.redtec.util.AdvancedPistonBlockStructureHelper;
import de.redtec.util.AdvancedStrukture;
import de.redtec.util.ElectricityNetworkHandler;
import de.redtec.util.ElectricityNetworkHandler.ElectricityNetwork;
import de.redtec.util.IElectricConnective.Voltage;
import de.redtec.util.ItemStackHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityHoverControler extends TileEntity implements IInventory, INamedContainerProvider, ITickableTileEntity {
	
	/**
	 * TODO
	 * 
	 * - Antriebs-Elemente (eventuell Strom betrieben), zum bestimmen der max. Block Anzahl getHoverForce()
	 */
	
	private int energyTimer;
	
	private final int maxScannBlocks = 10000;
	private ItemStack actionItemForward;
	private ItemStack actionItemBackward;
	private ItemStack actionItemLeft;
	private ItemStack actionItemRight;
	private ItemStack actionItemUp;
	private ItemStack actionItemDown;
	private ItemStack actionItemRotate;
	private HashMap<String, Boolean> chanelState;
	
	public TileEntityHoverControler() {
		super(ModTileEntityType.HOVER_CONTROLER);
		this.actionItemForward = ItemStack.EMPTY;
		this.actionItemBackward = ItemStack.EMPTY;
		this.actionItemLeft = ItemStack.EMPTY;
		this.actionItemRight = ItemStack.EMPTY;
		this.actionItemUp = ItemStack.EMPTY;
		this.actionItemDown = ItemStack.EMPTY;
		this.actionItemRotate = ItemStack.EMPTY;
		this.chanelState = new HashMap<String, Boolean>();
	}
	
	public void onControll(ItemStack actionItem, boolean state) {
		
		ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(world);
		handler.updateNetwork(world, pos);
		ElectricityNetwork network = handler.getNetwork(pos);
		boolean hasEnergy = network.canMachinesRun() == Voltage.NormalVoltage && this.energyTimer > 0;
		
		if (!wasLastTickPowered(actionItem) && state) {
			
			setWasPowered(actionItem, true);
			
			if (hasEnergy) {
				
				if (!ItemStackHelper.isItemStackItemEqual(actionItem, actionItemRotate, false)) {

					if (pos.getY() == 74) System.out.println("TSET222");
					
					if (wasLastTickPowered(this.actionItemRotate)) {
						
						boolean left = ItemStackHelper.isItemStackItemEqual(actionItem, actionItemLeft, false);
						boolean right = ItemStackHelper.isItemStackItemEqual(actionItem, actionItemRight, false);
						
						if (left || right) {
							
							doRotate(right);
							
						}
						
					} else {
						
						Direction direction = null;
						Direction blockFacing = getBlockState().get(BlockHoverControler.FACING);
						if (ItemStackHelper.isItemStackItemEqual(actionItem, actionItemForward, false)) direction = blockFacing;
						else if (ItemStackHelper.isItemStackItemEqual(actionItem, actionItemBackward, false)) direction = blockFacing.getOpposite();
						else if (ItemStackHelper.isItemStackItemEqual(actionItem, actionItemLeft, false)) direction = blockFacing.rotateYCCW();
						else if (ItemStackHelper.isItemStackItemEqual(actionItem, actionItemRight, false)) direction = blockFacing.rotateY();
						else if (ItemStackHelper.isItemStackItemEqual(actionItem, actionItemUp, false)) direction = Direction.UP;
						else if (ItemStackHelper.isItemStackItemEqual(actionItem, actionItemDown, false)) direction = Direction.DOWN;
						
						if (direction != null) {
							
							this.doMove(direction);
							
						}
						
					}
					
				}
				
			}
			
		} else if (wasLastTickPowered(actionItem) && !state) {
			
			setWasPowered(actionItem, false);
			this.energyTimer = 100;
			
		}
		
	}
	
	public boolean wasLastTickPowered(ItemStack chanelItem) {
		return this.chanelState.getOrDefault(chanelItem.getItem().getRegistryName() + "{" + chanelItem.getDisplayName().getUnformattedComponentText() + "}", false);
	}
	
	public void setWasPowered(ItemStack chanelItem, boolean state) {
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (ItemStackHelper.isItemStackItemEqual(this.getStackInSlot(i), chanelItem, false)) {
				
				this.chanelState.put(chanelItem.getItem().getRegistryName() + "{" + chanelItem.getDisplayName().getUnformattedComponentText() + "}", state);
				return;
				
			}
		}
	}
	
	public boolean doMove(Direction direction) {
		
		int hoverForce = getHoverForce();
		AdvancedPistonBlockStructureHelper pistonStrukture = new AdvancedPistonBlockStructureHelper(this.world, this.pos, direction, maxScannBlocks);
		
		if (pistonStrukture.canMove()) {
			
			AdvancedStrukture strukture = new AdvancedStrukture(pistonStrukture);
			
			if (strukture.getBlocks().size() <= hoverForce && strukture.getBlocks().size() > 0) {
				
				return strukture.doMove(this.world, this.getMoveDistance(strukture));
				
			}
			
		}
		
		return false;
		
	}
	
	public boolean doRotate(boolean right) {
		
		int hoverForce = getHoverForce();
		Direction blockFacing = getBlockState().get(BlockHoverControler.FACING);
		AdvancedPistonBlockStructureHelper pistonStrukture = new AdvancedPistonBlockStructureHelper(this.world, this.pos, blockFacing, maxScannBlocks);
		
		if (pistonStrukture.canMove()) {
			
			AdvancedStrukture strukture = new AdvancedStrukture(pistonStrukture);
			
			if (strukture.getBlocks().size() <= hoverForce && strukture.getBlocks().size() > 0) {
				
				return strukture.doRotate(this.world, right);
				
			}
			
		}
		
		return false;
		
	}
	
	public int getHoverForce() {
		return 20000;
	}
	
	public int getMoveDistance(AdvancedStrukture structure) {
		
		int moveDistance = 1;
		
		List<BlockPos> checkedBlocks = new ArrayList<BlockPos>();
		for (BlockPos pos : structure.getBlocks()) {
			
			BlockState state = this.world.getBlockState(pos);
			boolean speedBoost = state.getBlock() == RedTec.hover_extension ? state.get(BlockHoverExtension.ACTIVATED) : false;
			
			if (speedBoost && !checkedBlocks.contains(pos)) moveDistance += 1;
			checkedBlocks.add(pos);
			
		}
		
		return moveDistance;
		
	}
	
	
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		if (!this.actionItemForward.isEmpty()) compound.put("actionItemForward", this.actionItemForward.write(new CompoundNBT()));
		if (!this.actionItemBackward.isEmpty()) compound.put("actionItemBackward", this.actionItemBackward.write(new CompoundNBT()));
		if (!this.actionItemLeft.isEmpty()) compound.put("actionItemLeft", this.actionItemLeft.write(new CompoundNBT()));
		if (!this.actionItemRight.isEmpty()) compound.put("actionItemRight", this.actionItemRight.write(new CompoundNBT()));
		if (!this.actionItemUp.isEmpty()) compound.put("actionItemUp", this.actionItemUp.write(new CompoundNBT()));
		if (!this.actionItemDown.isEmpty()) compound.put("actionItemDown", this.actionItemDown.write(new CompoundNBT()));
		if (!this.actionItemRotate.isEmpty()) compound.put("actionItemRotate", this.actionItemRotate.write(new CompoundNBT()));
		CompoundNBT chanelStates = new CompoundNBT();
		for (Entry<String, Boolean> entry : this.chanelState.entrySet()) {
			chanelStates.putBoolean(entry.getKey(), entry.getValue());
		}
		compound.put("chanelStates", chanelStates);
		compound.putInt("energyTimer", this.energyTimer);
		return super.write(compound);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT compound) {
		this.actionItemForward = ItemStack.read(compound.getCompound("actionItemForward"));
		this.actionItemBackward = ItemStack.read(compound.getCompound("actionItemBackward"));
		this.actionItemLeft = ItemStack.read(compound.getCompound("actionItemLeft"));
		this.actionItemRight = ItemStack.read(compound.getCompound("actionItemRight"));
		this.actionItemUp = ItemStack.read(compound.getCompound("actionItemUp"));
		this.actionItemDown = ItemStack.read(compound.getCompound("actionItemDown"));
		this.actionItemRotate = ItemStack.read(compound.getCompound("actionItemRotate"));
		CompoundNBT chanelStates = compound.getCompound("chanelStates");
		this.chanelState.clear();
		for (String chanel : chanelStates.keySet()) {
			this.chanelState.put(chanel, chanelStates.getBoolean(chanel));
		}
		this.energyTimer = compound.getInt("energyTimer");
		super.read(state, compound);
	}
	
	@Override
	public boolean isEmpty() {
		for (int i = 0; i < 7; i++) {
			if (!this.getStackInSlot(i).isEmpty()) return false;
		}
		return true;
	}
	
	@Override
	public ItemStack getStackInSlot(int index) {
		switch (index) {
		case 0: return actionItemForward;
		case 1: return actionItemBackward;
		case 2: return actionItemLeft;
		case 3: return actionItemRight;
		case 4: return actionItemUp;
		case 5: return actionItemDown;
		case 6: return actionItemRotate;
		default: throw new ArrayIndexOutOfBoundsException(index);
		}
	}
	
	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack2 = this.getStackInSlot(index).copy();
		if (!stack2.isEmpty()) {
			ItemStack stack = this.getStackInSlot(index);
			stack.shrink(count);
			this.setInventorySlotContents(index, stack);
		}
		return stack2;
	}
	
	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack = this.getStackInSlot(index);
		this.setInventorySlotContents(index, ItemStack.EMPTY);
		return stack;
	}
	
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		switch (index) {
		case 0: this.actionItemForward = stack; break;
		case 1: this.actionItemBackward = stack; break;
		case 2: this.actionItemLeft = stack; break;
		case 3: this.actionItemRight = stack; break;
		case 4: this.actionItemUp = stack; break;
		case 5: this.actionItemDown = stack; break;
		case 6: this.actionItemRotate = stack; break;
		default: throw new ArrayIndexOutOfBoundsException(index);
		}
	}
	
	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return true;
	}
	
	@Override
	public void clear() {
		for (int i = 0; i < 7; i++) {
			this.setInventorySlotContents(i, ItemStack.EMPTY);
		}
	}

	@Override
	public int getSizeInventory() {
		return 7;
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerHoverControler(id, playerInv, this);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.redtec.hover_controler");
	}

	@Override
	public void tick() {
		if (!this.world.isRemote()) {
			if (this.energyTimer > 0) this.energyTimer--;
		}
	}
	
	public boolean needEnergy() {
		return this.energyTimer > 0;
	}
	
}
