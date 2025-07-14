package de.m_marvin.industria.core.electrics.engine;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.engine.network.SSyncCircuitTemplatesPackage;
import de.m_marvin.industria.core.electrics.engine.network.SSyncElectricComponentsPackage;
import de.m_marvin.industria.core.electrics.engine.network.SUpdateElectricNetworkPackage;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import de.m_marvin.industria.core.util.ufns.SynchronizedFunctionalNetworkSpace.UpdateType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientElectricPackageHandler {

	public static void handleSyncComponentsServer(SSyncElectricComponentsPackage msg, NetworkEvent.Context ctx) {
		Level level = Minecraft.getInstance().level;
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		
		for (var c : msg.getComponents()) {
			networkSpace.updateTicket(c.reference(), msg.request == SyncRequestType.ADDED ? UpdateType.COMPONENT_PUT : UpdateType.COMPONENT_REMOVE);
		}
	}

	public static void handleUpdateNetwork(SUpdateElectricNetworkPackage msg, Context context) {
		Level level = Minecraft.getInstance().level;
		ElectricNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_SPACE_CAPABILITY);
		
		// Add components to create network
		CompletableFuture.allOf(
				msg.getComponents().stream()
				.map(c -> networkSpace.updateTicketCompletable(c.reference(), UpdateType.COMPONENT_PUT))
				.toArray(CompletableFuture[]::new)
			).thenAccept(v -> {

				// On the client the network might be split because of unloaded chunks/components
				Collection<ElectricNetwork> networks = msg.getComponents().stream().map(networkSpace::findNetworkAt).distinct().toList();
				for (var n : networks) {
					n.parseDataList(msg.getDataList());
					n.setMaxPower(msg.getMaxPower());
					n.setCurrentProduction(msg.getCurrentProduction());
					n.setCurrentConsumtion(msg.getCurrentConsumtion());
					n.setState(msg.getState());
				}
				
			});
		
	}
	
	public static void handleSyncCircuitTemplates(SSyncCircuitTemplatesPackage msg, Context context) {
		CircuitTemplateManager.updateClientTemplates(msg.getCircuitTemplates());
	}
	
}
