package de.industria.items;

import java.util.List;

import de.industria.typeregistys.ModTabs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemFuse extends ItemBase {
	
	protected int maxCurrent;
	
	public ItemFuse(String name, int maxCurrent) {
		super(name, ModTabs.MACHINES, 16);
		this.maxCurrent = maxCurrent;
	}
	
	public int getMaxCurrent() {
		return maxCurrent;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new StringTextComponent("\u00A77" + new TranslationTextComponent("industria.item.info.fuseMax", this.maxCurrent).getString()));
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}
	
}
