package de.m_marvin.industria.content;

import de.m_marvin.industria.content.items.ScrewDriverItem;
import de.m_marvin.industria.content.network.CScrewDriverAdjustmentPackage;
import de.m_marvin.industria.content.registries.ModItems;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.engine.network.CBreakConduitPackage;
import de.m_marvin.industria.core.conduits.engine.network.CChangeNodesPerBlockPackage;
import de.m_marvin.industria.core.conduits.types.items.AbstractConduitItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerPackageHandler {
	
	/* Handle CScrewDriverAdjustment package */
	
	public static void handleScrewDriverAdjustment(CScrewDriverAdjustmentPackage msg, Context ctx) {
		ServerPlayer player = ctx.getSender();
		ItemStack screwDriverStack = player.getItemInHand(msg.getHand());
		if (screwDriverStack.getItem() == ModItems.SCREW_DRIVER.get()) {
			
			UseOnContext context = new UseOnContext(player, msg.getHand(), msg.getHitResult());
			ServerLevel level = ctx.getSender().getLevel();
			BlockState targetedBlock = level.getBlockState(context.getClickedPos());
			
			((ScrewDriverItem) screwDriverStack.getItem()).adjustTargetedBlock(targetedBlock, context, msg.getScrollDelta());
		}	
	}
	
	/* End of package handling */
	
}
