package de.m_marvin.industria.core.magnetism.types.items;

import java.util.List;

import de.m_marvin.industria.core.parametrics.BlockParametrics;
import de.m_marvin.industria.core.parametrics.BlockParametricsManager;
import de.m_marvin.industria.core.util.Formatter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class MagneticBlockItem extends BlockItem {

	public MagneticBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}
	
	// TODO add tooltip to all blocks with magnetic tag
	
	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(this.getBlock());
		pTooltip.add(Formatter.build().appand(Component.translatable("industriacore.tooltip.magneticblock.fieldstrength", parametrics.getMagneticVector().length())).withStyle(ChatFormatting.GRAY).component());
		pTooltip.add(Formatter.build().appand(Component.translatable("industriacore.tooltip.electricblock.coefficient", parametrics.getMagneticCoefficient())).withStyle(ChatFormatting.GRAY).component());
		super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
	}
	
}
