package de.redtec.items;

import java.util.List;

import de.redtec.RedTec;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemFuse extends ItemBase {
	
	protected int maxCurrent;
	
	public ItemFuse(String name, int maxCurrent) {
		super(name, RedTec.MACHINES, 16);
		this.maxCurrent = maxCurrent;
	}
	
	public int getMaxCurrent() {
		return maxCurrent;
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("redtec.item.info.fuseMax", this.maxCurrent));
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
}
