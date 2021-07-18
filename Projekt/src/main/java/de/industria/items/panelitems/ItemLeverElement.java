package de.industria.items.panelitems;

import de.industria.tileentity.TileEntityControllPanel;
import de.industria.util.types.RedstoneControlSignal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;

public class ItemLeverElement extends ItemPanelElement {

	public ItemLeverElement() {
		super("lever_element");
	}

	@Override
	public AxisAlignedBB getCollisionBounds() {
		return new AxisAlignedBB(0, 0, 0, 3, 4, 1);
	}

	@Override
	public void onActivated(TileEntityControllPanel panel, ItemStack elementStack) {
		
		if (elementStack.hasTag()) {
			
			CompoundNBT tag = elementStack.getTag();
			boolean powered = !tag.getBoolean("Powered");
			tag.putBoolean("Powered", powered);
			tag.putInt("CustomModelData", powered ? 1 : 0);
			elementStack.setTag(tag);
			
			ItemStack chanelItem = new ItemStack(Items.REDSTONE_TORCH);
			chanelItem.setHoverName(elementStack.getHoverName());
			RedstoneControlSignal signal = new RedstoneControlSignal(chanelItem, powered);
			panel.sendSignal(signal);
			
			panel.getLevel().playSound(null, panel.getBlockPos(), SoundEvents.LEVER_CLICK, SoundCategory.BLOCKS, 1, powered ? 0.6F : 0.5F);
			
		}
		
	}

	@Override
	public void onPowerStateChange(TileEntityControllPanel panel, ItemStack elementStack, boolean powered) {}
	
}
