package de.m_marvin.industria.core.client.magnetism;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.magnetism.types.blocks.IMagneticBlock;
import de.m_marvin.industria.core.parametrics.BlockParametrics;
import de.m_marvin.industria.core.parametrics.BlockParametricsManager;
import de.m_marvin.industria.core.registries.IndustriaTags;
import de.m_marvin.industria.core.util.Formatter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class MagnetismItemTooltips {
	
	@SubscribeEvent
	public static void onTooltip(ItemTooltipEvent event) {
		
		Item item = event.getItemStack().getItem();
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			
			if (block.defaultBlockState().is(IndustriaTags.Blocks.MAGNETIC)) {
				
				if (block instanceof IMagneticBlock magnetic) {
					event.getToolTip().add(Formatter.build().appand(Component.translatable("industriacore.tooltip.magneticblock.fieldstrength", magnetic.getFieldVector(event.getEntity().level(), block.defaultBlockState(), BlockPos.ZERO).length())).withStyle(ChatFormatting.GRAY).component());
					event.getToolTip().add(Formatter.build().appand(Component.translatable("industriacore.tooltip.magneticblock.coefficient", magnetic.getCoefficient(event.getEntity().level(), block.defaultBlockState(), BlockPos.ZERO))).withStyle(ChatFormatting.GRAY).component());
				} else {
					BlockParametrics parametrics = BlockParametricsManager.getInstance().getParametrics(block);	
					event.getToolTip().add(Formatter.build().appand(Component.translatable("industriacore.tooltip.electricblock.coefficient", parametrics.getMagneticCoefficient())).withStyle(ChatFormatting.GRAY).component());
				}
				
			}
		}
		
	}
	
}
