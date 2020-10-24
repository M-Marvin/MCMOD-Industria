package de.redtec.tileentity;

import java.util.HashMap;
import java.util.Map.Entry;

import de.redtec.RedTec;
import de.redtec.blocks.BlockControllPanel;
import de.redtec.items.panelitems.ItemPanelElement;
import de.redtec.util.ModTileEntityType;
import de.redtec.util.RedstoneControlSignal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityControllPanel extends TileEntity implements ITickableTileEntity {
	
	private HashMap<Pos, ItemStack> panelElements;
	private HashMap<ItemStack, Integer> sheduleTicks;
	
	public TileEntityControllPanel() {
		super(ModTileEntityType.CONTROLL_PANEL);
		this.panelElements = new HashMap<TileEntityControllPanel.Pos, ItemStack>();
		this.sheduleTicks = new HashMap<ItemStack, Integer>();
	}
	
	public HashMap<Pos, ItemStack> getPanelElements() {
		return panelElements;
	}
	
	public void onSignal(RedstoneControlSignal signal) {
		
		for (Entry<Pos, ItemStack> element : this.panelElements.entrySet()) {
			
			ItemStack stack = element.getValue();
			if (stack.getDisplayName().getUnformattedComponentText().equals(signal.getChanelItem().getDisplayName().getUnformattedComponentText()) && signal.getChanelItem().getItem() == Items.REDSTONE_TORCH) {
				
				((ItemPanelElement) stack.getItem()).onPowerStateChange(this, stack, signal.isPowered());
				
			}
			
		}
		
	}
	
	public void sendSignal(RedstoneControlSignal signal) {
		
		BlockState state = getBlockState();
		if (state.getBlock() == RedTec.controll_panel) {
			
			((BlockControllPanel) state.getBlock()).sendSignal(this.world, this.pos, signal);
			
		}
		
	}
	
	public ActionResultType onClickOnPanel(int x, int y, boolean doEdit, ItemStack editStack) {
		
		if (doEdit) {
			
			ItemStack removedItem = this.removeElement(x, y);
			
			if (removedItem != null) {
				
				Direction facing = this.getBlockState().get(BlockControllPanel.FACING);
				
				this.world.addEntity(new ItemEntity(
						this.world, 
						this.pos.getX() + facing.getXOffset() * 0.5F + 0.5F, 
						this.pos.getY() + facing.getYOffset() * 0.5F + 0.5F, 
						this.pos.getZ() + facing.getZOffset() * 0.5F + 0.5F, 
						removedItem));
				return ActionResultType.CONSUME;
				
			}
			
		} else if (editStack.getItem() instanceof ItemPanelElement) {
			
			ItemStack newElement = editStack.copy();
			newElement.setCount(1);
			boolean flag = this.addElement(newElement, x, y);
			
			if (flag) {
				
				editStack.shrink(1);
				this.world.playSound(null, this.pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1, 1);
				return ActionResultType.CONSUME;
				
			}
			
		}

		Entry<Pos, ItemStack> element = getElementAt(x, y);
		if (element != null) ((ItemPanelElement) element.getValue().getItem()).onActivated(this, element.getValue());
		this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 0);
		
		return ActionResultType.CONSUME;
				
	}
	
	public Entry<Pos, ItemStack> getElementAt(int x, int y) {

		for (Entry<Pos, ItemStack> element : this.panelElements.entrySet()) {
			
			AxisAlignedBB bounds = ((ItemPanelElement) element.getValue().getItem()).getCollisionBounds();
			Pos pos = element.getKey();
			AxisAlignedBB collision = bounds.offset(pos.getX(), pos.getY(), 0);
			
			if (collision.contains(x, y, 0)) {
				
				return element;
				
			}
			
		}
		
		return null;
		
	}
	
	public boolean addElement(ItemStack elementStack, int x, int y) {
		
		if (elementStack.getItem() instanceof ItemPanelElement) {
			
			AxisAlignedBB bounds = ((ItemPanelElement) elementStack.getItem()).getCollisionBounds();
			AxisAlignedBB collision = bounds.offset(x, y, 0);
			
			if (collision.maxX > 16 || collision.maxY > 16 || collision.maxZ > 16) return false;
			
			for (Entry<Pos, ItemStack> element : this.panelElements.entrySet()) {
				
				AxisAlignedBB bounds2 = ((ItemPanelElement) element.getValue().getItem()).getCollisionBounds();
				Pos pos2 = element.getKey();
				AxisAlignedBB collision2 = bounds2.offset(pos2.getX(), pos2.getY(), 0);
				
				if (collision.intersects(collision2)) return false;
				
			}
			
			this.panelElements.put(new Pos(x, y), elementStack);
			return true;
			
		}
		
		return false;
		
	}
	
	public ItemStack removeElement(int x, int y) {
		
		Pos elementToRemove = null;
		
		for (Entry<Pos, ItemStack> element : this.panelElements.entrySet()) {
			
			AxisAlignedBB bounds2 = ((ItemPanelElement) element.getValue().getItem()).getCollisionBounds();
			Pos pos2 = element.getKey();
			AxisAlignedBB collision2 = bounds2.offset(pos2.getX(), pos2.getY(), 0);
			
			if (collision2.contains(x, y, 0)) {
				
				elementToRemove = element.getKey();
				break;
				
			}
			
		}
		
		if (elementToRemove != null) {
			
			return this.panelElements.remove(elementToRemove);
			
		}
		
		return null;
		
	}
	
	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 0, this.write(new CompoundNBT()));
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.func_230337_a_(this.getBlockState(), pkt.getNbtCompound());
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		saveToNBT(compound);
		return super.write(compound);
	}
	
	@Override
	public void func_230337_a_(BlockState state, CompoundNBT compound) {
		ListNBT list = compound.getList("PanelElements", 10);
		this.panelElements.clear();
		for (int i = 0; i < list.size(); i++) {
			CompoundNBT stackNBT = list.getCompound(i);
			ItemStack stack = ItemStack.read(stackNBT);
			int[] posArr = stackNBT.getIntArray("Pos");
			Pos pos = new Pos(posArr[0], posArr[1]);
			if (stackNBT.contains("SheduleTick") && !this.world.isRemote()) {
				int tick = stackNBT.getInt("SheduleTick");
				this.sheduleTicks.put(stack, tick);
			}
			this.panelElements.put(pos, stack);
		}
		super.func_230337_a_(state, compound);
	}
	
	public static final class Pos {
		
		private int x;
		private int y;
		
		public Pos(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
	}

	public CompoundNBT saveToNBT(CompoundNBT compound) {
		ListNBT list = new ListNBT();
		for (Entry<Pos, ItemStack> element : panelElements.entrySet()) {
			if (element.getValue().getItem() instanceof ItemPanelElement) {
				CompoundNBT stack = element.getValue().write(new CompoundNBT());
				stack.putIntArray("Pos", new int[] {element.getKey().getX(), element.getKey().getY()});
				if (this.sheduleTicks.get(element.getValue()) != null) {
					stack.putInt("SheduleTick", this.sheduleTicks.get(element.getValue()));
				}
				list.add(stack);
			}
		}
		compound.put("PanelElements", list);
		return compound;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void tick() {
		
		if (!this.world.isRemote) {
			
			for (Entry<ItemStack, Integer> entry : ((HashMap<ItemStack, Integer>) this.sheduleTicks.clone()).entrySet()) {
				
				int ticksRemaining = entry.getValue() - 1;
				
				if (ticksRemaining <= 0) {
					
					this.sheduleTicks.remove(entry.getKey());
					
					if (entry.getKey().getItem() instanceof ItemPanelElement) {
						
						((ItemPanelElement) entry.getKey().getItem()).onSheduleTick(this, entry.getKey());
						
					}
					
				} else {
					
					this.sheduleTicks.put(entry.getKey(), ticksRemaining);
					
				}
				
			}
			
			this.world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 0);
			
		}
		
	}

	public void addSheduleTick(ItemStack elementStack, int i) {
		
		this.sheduleTicks.put(elementStack, i);
		
	}
	
}
