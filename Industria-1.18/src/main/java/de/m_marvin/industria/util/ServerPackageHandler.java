package de.m_marvin.industria.util;

import de.m_marvin.industria.items.FlexibleConduitItem;
import de.m_marvin.industria.network.CChangeNodesPerBlockPackage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerPackageHandler {

	/* Handle SSyncPlacedConduit package */
	
	public static void handleChangeNodesPerBlock(CChangeNodesPerBlockPackage msg, Context ctx) {
		ServerPlayer player = ctx.getSender();
		ItemStack heldStack = player.getMainHandItem();
		if (heldStack.getItem() instanceof FlexibleConduitItem) {
			((FlexibleConduitItem) heldStack.getItem()).onChangeNodesPerBlock(heldStack, msg.getNodesPerBlock());
		}
	}

	/* End of package handling */
	
}
