package de.m_marvin.industria.core.conduits.engine;

import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.engine.network.CChangeConduitPlacementLengthPackage;
import de.m_marvin.industria.core.conduits.engine.network.SCConduitPackage.SCBreakConduitPackage;
import de.m_marvin.industria.core.conduits.engine.network.SCConduitPackage.SCPlaceConduitPackage;
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
	
	public static void handlePlaceConduit(SCPlaceConduitPackage msg, Context ctx) {
		ConduitUtility.setConduit(ctx.getSender().level(), msg.getPosition(), msg.getConduit(), msg.getLength());
	}

	public static void handleRemoveConduit(SCBreakConduitPackage msg, Context ctx) {
		ConduitUtility.removeConduit(ctx.getSender().level(), msg.getPosition(), msg.dropItems());
	}
	
}
