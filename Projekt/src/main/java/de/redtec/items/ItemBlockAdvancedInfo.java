package de.redtec.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class ItemBlockAdvancedInfo extends BlockItem {
	
	private IBlockToolType info;
	private int burnTime;
	
	public ItemBlockAdvancedInfo(Block blockIn, Properties builder, IBlockToolType info, int burnTime) {
		super(blockIn, builder);
		this.info = info;
		this.burnTime = burnTime;
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		List<ITextComponent> infoLines = new ArrayList<ITextComponent>();
		if (this.info != null) this.info.addInformation(stack, infoLines);
		for (ITextComponent line : infoLines) {
			tooltip.add(new StringTextComponent("\u00A77" + line.getString()));
		}
		super.addInformation(stack, worldIn, tooltip, flagIn);
		
	}
	
	@Override
	public int getBurnTime(ItemStack itemStack) {
		return this.burnTime;
	}
	
	@FunctionalInterface
	public static interface IBlockToolType {
		public void addInformation(ItemStack stack, List<ITextComponent> info);
	}
	
}
