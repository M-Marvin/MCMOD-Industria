package de.redtec.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class ItemBlockAdvancedInfo extends BlockItem {
	
	private List<ITextComponent> info;
	
	public ItemBlockAdvancedInfo(Block blockIn, Properties builder, List<ITextComponent> info) {
		super(blockIn, builder);
		this.info = info;
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		for (ITextComponent line : info) {
			tooltip.add(line);
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
	}
	
}
