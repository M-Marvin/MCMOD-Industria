package de.industria.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.industria.ModItems;
import de.industria.blocks.BlockRHoverControler;
import de.industria.blocks.BlockRHoverExtension;
import de.industria.gui.ContainerRHoverControler;
import de.industria.typeregistys.ModTileEntityType;
import de.industria.util.blockfeatures.IBElectricConnectiveBlock.Voltage;
import de.industria.util.handler.ElectricityNetworkHandler;
import de.industria.util.handler.ItemStackHelper;
import de.industria.util.handler.ElectricityNetworkHandler.ElectricityNetwork;
import de.industria.util.types.AdvancedPistonBlockStructureHelper;
import de.industria.util.types.AdvancedStrukture;
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

public class TileEntityRHoverControler extends TileEntity implements IInventory, INamedContainerProvider, ITickableTileEntity {
	
	private int energyTimer;
	private int moveTimer;
	
	private final int maxScannBlocks = 10000;
	private ItemStack actionItemForward;
	private ItemStack actionItemBackward;
	private ItemStack actionItemLeft;
	private ItemStack actionItemRight;
	private ItemStack actionItemUp;
	private ItemStack actionItemDown;
	private ItemStack actionItemRotate;
	private HashMap<String, Boolean> chanelState;
	
	public TileEntityRHoverControler() {
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
		
		ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(level);
		handler.updateNetwork(level, worldPosition);
		ElectricityNetwork network = handler.getNetwork(worldPosition);
		boolean hasEnergy = network.canMachinesRun() == Voltage.NormalVoltage && this.energyTimer > 0;
		
		if (!wasLastTickPowered(actionItem) && state) {
			
			setWasPowered(actionItem, true);
			
			if (hasEnergy && this.moveTimer <= 0) {
				
				if (!ItemStackHelper.isItemStackItemEqual(actionItem, actionItemRotate, false)) {
					
					if (wasLastTickPowered(this.actionItemRotate)) {
						
						boolean left = ItemStackHelper.isItemStackItemEqual(actionItem, actionItemLeft, false);
						boolean right = ItemStackHelper.isItemStackItemEqual(actionItem, actionItemRight, false);
						
						if (left || right) {
							
							this.moveTimer = 10;
							doRotate(right);
							
						}
						
					} else {
						
						Direction direction = null;
						Direction blockFacing = getBlockState().getValue(BlockRHoverControler.FACING);
						if (ItemStackHelper.isItemStackItemEqual(actionItem, actionItemForward, false)) direction = blockFacing;
						else if (ItemStackHelper.isItemStackItemEqual(actionItem, actionItemBackward, false)) direction = blockFacing.getOpposite();
						else if (ItemStackHelper.isItemStackItemEqual(actionItem, actionItemLeft, false)) direction = blockFacing.getCounterClockWise();
						else if (ItemStackHelper.isItemStackItemEqual(actionItem, actionItemRight, false)) direction = blockFacing.getClockWise();
						else if (ItemStackHelper.isItemStackItemEqual(actionItem, actionItemUp, false)) direction = Direction.UP;
						else if (ItemStackHelper.isItemStackItemEqual(actionItem, actionItemDown, false)) direction = Direction.DOWN;
						
						if (direction != null) {
							
							this.moveTimer = 10;
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
		return this.chanelState.getOrDefault(chanelItem.getItem().getRegistryName() + "{" + chanelItem.getHoverName().getContents() + "}", false);
	}
	
	public void setWasPowered(ItemStack chanelItem, boolean state) {
		for (int i = 0; i < this.getContainerSize(); i++) {
			if (ItemStackHelper.isItemStackItemEqual(this.getItem(i), chanelItem, false)) {
				
				this.chanelState.put(chanelItem.getItem().getRegistryName() + "{" + chanelItem.getHoverName().getContents() + "}", state);
				return;
				
			}
		}
	}
	
	public boolean doMove(Direction direction) {
		
		int hoverForce = getHoverForce();
		AdvancedPistonBlockStructureHelper pistonStrukture = new AdvancedPistonBlockStructureHelper(this.level, this.worldPosition, direction, maxScannBlocks);
		
		if (pistonStrukture.canMove()) {
			
			AdvancedStrukture strukture = new AdvancedStrukture(pistonStrukture);
			
			if (strukture.getBlocks().size() <= hoverForce && strukture.getBlocks().size() > 0) {
				
				return strukture.doMove(this.level, this.getMoveDistance(strukture));
				
			}
			
		}
		
		return false;
		
	}
	
	public boolean doRotate(boolean right) {
		
		int hoverForce = getHoverForce();
		Direction blockFacing = getBlockState().getValue(BlockRHoverControler.FACING);
		AdvancedPistonBlockStructureHelper pistonStrukture = new AdvancedPistonBlockStructureHelper(this.level, this.worldPosition, blockFacing, maxScannBlocks);
		
		if (pistonStrukture.canMove()) {
			
			AdvancedStrukture strukture = new AdvancedStrukture(pistonStrukture);
			
			if (strukture.getBlocks().size() <= hoverForce && strukture.getBlocks().size() > 0) {
				
				return strukture.doRotate(this.level, right);
				
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
			
			BlockState state = this.level.getBlockState(pos);
			boolean speedBoost = state.getBlock() == ModItems.hover_extension ? state.getValue(BlockRHoverExtension.ACTIVATED) : false;
			
			if (speedBoost && !checkedBlocks.contains(pos)) moveDistance += 1;
			checkedBlocks.add(pos);
			
		}
		
		return moveDistance;
		
	}
	
	
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if (!this.actionItemForward.isEmpty()) compound.put("actionItemForward", this.actionItemForward.save(new CompoundNBT()));
		if (!this.actionItemBackward.isEmpty()) compound.put("actionItemBackward", this.actionItemBackward.save(new CompoundNBT()));
		if (!this.actionItemLeft.isEmpty()) compound.put("actionItemLeft", this.actionItemLeft.save(new CompoundNBT()));
		if (!this.actionItemRight.isEmpty()) compound.put("actionItemRight", this.actionItemRight.save(new CompoundNBT()));
		if (!this.actionItemUp.isEmpty()) compound.put("actionItemUp", this.actionItemUp.save(new CompoundNBT()));
		if (!this.actionItemDown.isEmpty()) compound.put("actionItemDown", this.actionItemDown.save(new CompoundNBT()));
		if (!this.actionItemRotate.isEmpty()) compound.put("actionItemRotate", this.actionItemRotate.save(new CompoundNBT()));
		CompoundNBT chanelStates = new CompoundNBT();
		for (Entry<String, Boolean> entry : this.chanelState.entrySet()) {
			chanelStates.putBoolean(entry.getKey(), entry.getValue());
		}
		compound.put("chanelStates", chanelStates);
		compound.putInt("energyTimer", this.energyTimer);
		compound.putInt("moveTimer", this.moveTimer);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.actionItemForward = ItemStack.of(compound.getCompound("actionItemForward"));
		this.actionItemBackward = ItemStack.of(compound.getCompound("actionItemBackward"));
		this.actionItemLeft = ItemStack.of(compound.getCompound("actionItemLeft"));
		this.actionItemRight = ItemStack.of(compound.getCompound("actionItemRight"));
		this.actionItemUp = ItemStack.of(compound.getCompound("actionItemUp"));
		this.actionItemDown = ItemStack.of(compound.getCompound("actionItemDown"));
		this.actionItemRotate = ItemStack.of(compound.getCompound("actionItemRotate"));
		CompoundNBT chanelStates = compound.getCompound("chanelStates");
		this.chanelState.clear();
		for (String chanel : chanelStates.getAllKeys()) {
			this.chanelState.put(chanel, chanelStates.getBoolean(chanel));
		}
		this.energyTimer = compound.getInt("energyTimer");
		this.moveTimer = compound.getInt("moveTimer");
		super.load(state, compound);
	}
	
	@Override
	public boolean isEmpty() {
		for (int i = 0; i < 7; i++) {
			if (!this.getItem(i).isEmpty()) return false;
		}
		return true;
	}
	
	@Override
	public ItemStack getItem(int index) {
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
	public ItemStack removeItem(int index, int count) {
		ItemStack stack2 = this.getItem(index).copy();
		if (!stack2.isEmpty()) {
			ItemStack stack = this.getItem(index);
			stack.shrink(count);
			this.setItem(index, stack);
		}
		return stack2;
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack stack = this.getItem(index);
		this.setItem(index, ItemStack.EMPTY);
		return stack;
	}
	
	@Override
	public void setItem(int index, ItemStack stack) {
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
	public boolean stillValid(PlayerEntity player) {
		return true;
	}
	
	@Override
	public void clearContent() {
		for (int i = 0; i < 7; i++) {
			this.setItem(i, ItemStack.EMPTY);
		}
	}

	@Override
	public int getContainerSize() {
		return 7;
	}
	
	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerRHoverControler(id, playerInv, this);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.hover_controler");
	}

	@Override
	public void tick() {
		if (!this.level.isClientSide()) {
			if (this.energyTimer > 0) this.energyTimer--;
			if (this.moveTimer > 0) this.moveTimer--;
		}
	}
	
	public boolean needEnergy() {
		return this.energyTimer > 0;
	}
	
}
