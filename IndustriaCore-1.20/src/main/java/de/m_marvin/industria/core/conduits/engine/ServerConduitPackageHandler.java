package de.m_marvin.industria.core.conduits.engine;

import de.m_marvin.industria.core.conduits.engine.network.CChangeConduitPlacementLengthPackage;
import de.m_marvin.industria.core.conduits.types.items.IAdjustableConduitItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerConduitPackageHandler {

	/* Handle CChangeNodesPerBlock package */
	
	public static void handleChangePlacementLength(CChangeConduitPlacementLengthPackage msg, Context ctx) {
		ServerPlayer player = ctx.getSender();
		ItemStack heldStack = player.getMainHandItem();
		if (heldStack.getItem() instanceof IAdjustableConduitItem adjustableItem) {
			adjustableItem.onChangePlacementLength(heldStack, msg.getPlacementLength());
		}
	}
	
}
