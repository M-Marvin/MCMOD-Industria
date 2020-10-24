package de.redtec.items.panelitems;

import de.redtec.tileentity.TileEntityControllPanel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;

public class ItemLampElement extends ItemPanelElement {

	public ItemLampElement() {
		super("lamp_element");
	}

	@Override
	public AxisAlignedBB getCollisionBounds() {
		return new AxisAlignedBB(0, 0, 0, 3, 2, 1);
	}

	@Override
	public void onActivated(TileEntityControllPanel panel, ItemStack elementStack) {}

	@Override
	public void onPowerStateChange(TileEntityControllPanel panel, ItemStack elementStack, boolean powered) {
		
		if (elementStack.hasTag()) {
			
			CompoundNBT tag = elementStack.getTag();
			tag.putInt("CustomModelData", powered ? 1 : 0);
			elementStack.setTag(tag);
			
		}
		
	}
	
}
