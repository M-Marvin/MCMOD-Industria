package de.m_marvin.industria.core.electrics.types.item;

import java.util.List;

import de.m_marvin.industria.core.electrics.types.blocks.IElectricInfoProvider;
import de.m_marvin.industria.core.parametrics.BlockParametrics;
import de.m_marvin.industria.core.util.Formatter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ElectricBlockItem extends BlockItem {

	public ElectricBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		if (this.getBlock() instanceof IElectricInfoProvider provider) {
			BlockParametrics parametrics = provider.getParametrics(this.getBlock().defaultBlockState(), null, BlockPos.ZERO);
			pTooltip.add(Formatter.build().appand(Component.translatable("industriacore.tooltip.electricblock.voltage", parametrics.getNominalVoltage())).withStyle(ChatFormatting.GRAY).component());
			pTooltip.add(Formatter.build().appand(Component.translatable("industriacore.tooltip.electricblock.power", parametrics.getNominalPower())).withStyle(ChatFormatting.GRAY).component());
		}
		super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
	}
	
}
