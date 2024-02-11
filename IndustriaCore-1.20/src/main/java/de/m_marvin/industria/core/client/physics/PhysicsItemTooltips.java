package de.m_marvin.industria.core.client.physics;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.physics.PhysicUtility;
import de.m_marvin.industria.core.util.Formatter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class PhysicsItemTooltips {
	
	@SubscribeEvent
	public static void onTooltip(ItemTooltipEvent event) {
		
		Item item = event.getItemStack().getItem();
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			event.getToolTip().add(Formatter.build().appand(Component.translatable("industriacore.tooltip.physics.mass", PhysicUtility.getBlockMass(block.defaultBlockState()))).withStyle(ChatFormatting.GRAY).component());
		}
		
	}
	
}
