package de.m_marvin.industria.core.conduits.engine;

import java.util.Optional;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.engine.network.SSyncAddedConduits;
import de.m_marvin.industria.core.conduits.engine.network.SSyncRemovedConduits;
import de.m_marvin.industria.core.conduits.engine.network.SUpdateConduitEntity;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.conduits.ConduitEntity;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.ConditionalExecutor;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientConduitPackageHandler {
	
	/* Handle SSyncAddedConduits package */
	
	public static void handleSyncAddedConduitsFromServer(SSyncAddedConduits msg, NetworkEvent.Context ctx) {
		
		ConditionalExecutor.CLIENT_TICK_EXECUTOR.executeAsSoonAs(() -> {

			Level level = Minecraft.getInstance().level;
			ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
			
			for (ConduitEntity conduitEntity : msg.conduits) {
				handler.addConduit(conduitEntity);
			}
			
		}, () -> Minecraft.getInstance().level.isLoaded(msg.getChunkPos().getWorldPosition()));
		
	}

	/* Handle SSyncRemovedConduits package */
	
	public static void handleSyncRemovedConduitsFromServer(SSyncRemovedConduits msg, NetworkEvent.Context ctx) {
		ConditionalExecutor.CLIENT_TICK_EXECUTOR.execute(() -> {

			Level level = Minecraft.getInstance().level;
			ConduitHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.CONDUIT_HANDLER_CAPABILITY);
			
			for (ConduitPos conduit : msg.conduits)
				handler.removeConduit(conduit);
			
		});
	}

	/* Handle SUpdateConduitEntity package */
	
	public static void handleUpdateConduitEntityFromServer(SUpdateConduitEntity msg, Context context) {
		ConditionalExecutor.CLIENT_TICK_EXECUTOR.execute(() -> {

			Level level = Minecraft.getInstance().level;
			Optional<ConduitEntity> conduitEntity = ConduitUtility.getConduit(level, msg.getPosition());
			
			if (conduitEntity.isPresent() && conduitEntity.get().getConduit() == msg.getConduit()) {
				conduitEntity.get().readUpdateTag(msg.getUpdateTag());
			}
			
		});
	}
	
}
