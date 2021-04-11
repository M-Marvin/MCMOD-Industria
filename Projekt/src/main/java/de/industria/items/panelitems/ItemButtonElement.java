package de.industria.items.panelitems;

import de.industria.tileentity.TileEntityControllPanel;
import de.industria.util.types.RedstoneControlSignal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;

public class ItemButtonElement extends ItemPanelElement {

	public ItemButtonElement() {
		super("button_element");
	}

	@Override
	public AxisAlignedBB getCollisionBounds() {
		return new AxisAlignedBB(0, 0, 0, 3, 2, 1);
	}

	@Override
	public void onActivated(TileEntityControllPanel panel, ItemStack elementStack) {
		
		if (elementStack.hasTag()) {
			
			CompoundNBT tag = elementStack.getTag();
			boolean pressed = tag.getBoolean("Pressed");
			
			if (!pressed) {
				
				tag.putBoolean("Pressed", true);
				tag.putInt("CustomModelData", 1);
				elementStack.setTag(tag);
				
				ItemStack chanelStack = new ItemStack(Items.REDSTONE_TORCH, 1);
				chanelStack.setDisplayName(elementStack.getDisplayName());
				RedstoneControlSignal signal = new RedstoneControlSignal(chanelStack, true);
				panel.sendSignal(signal);
				panel.addSheduleTick(elementStack, 20);
				
				panel.getWorld().playSound(null, panel.getPos(), SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1, 0.6F);
				
			}
			
		}
		
	}
	
	@Override
	public void onSheduleTick(TileEntityControllPanel panel, ItemStack elementStack) {
		
		if (elementStack.hasTag()) {
			
			CompoundNBT tag = elementStack.getTag();
			tag.putBoolean("Pressed", false);
			tag.putInt("CustomModelData", 0);
			elementStack.setTag(tag);

			ItemStack chanelStack = new ItemStack(Items.REDSTONE_TORCH, 1);
			chanelStack.setDisplayName(elementStack.getDisplayName());
			RedstoneControlSignal signal = new RedstoneControlSignal(chanelStack, false);
			panel.sendSignal(signal);
			
			panel.getWorld().playSound(null, panel.getPos(), SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 1, 0.5F);
			
		}
		
	}
	
	@Override
	public void onPowerStateChange(TileEntityControllPanel panel, ItemStack elementStack, boolean powered) {}
	
}
