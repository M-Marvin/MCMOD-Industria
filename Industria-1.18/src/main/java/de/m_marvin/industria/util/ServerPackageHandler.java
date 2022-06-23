package de.m_marvin.industria.util;

import de.m_marvin.industria.items.AbstractConduitItem;
import de.m_marvin.industria.network.CBreakConduitPackage;
import de.m_marvin.industria.network.CChangeNodesPerBlockPackage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerPackageHandler {

	/* Handle CChangeNodesPerBlock package */
	
	public static void handleChangeNodesPerBlock(CChangeNodesPerBlockPackage msg, Context ctx) {
		ServerPlayer player = ctx.getSender();
		ItemStack heldStack = player.getMainHandItem();
		if (heldStack.getItem() instanceof AbstractConduitItem) {
			((AbstractConduitItem) heldStack.getItem()).onChangeNodesPerBlock(heldStack, msg.getNodesPerBlock());
		}
	}
	
	/* Handle CBreakConduit package */
	
	public static void handleBreakConduit(CBreakConduitPackage msg, Context ctx) {
		ServerPlayer player = ctx.getSender();
		ServerLevel level = player.getLevel();
		UtilityHelper.removeConduit(level, msg.getConduitPos(), msg.isDropItems());
		
		ItemStack toolItem = player.getMainHandItem();
		if (!toolItem.isEmpty() && toolItem.getMaxDamage() != 0) {
			toolItem.hurtAndBreak(1, player, (p) -> {});
		}
	}

	/* End of package handling */
	
}
