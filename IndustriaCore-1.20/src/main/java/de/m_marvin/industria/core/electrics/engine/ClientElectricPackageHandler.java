package de.m_marvin.industria.core.electrics.engine;

import java.util.Optional;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.engine.ElectricHandlerCapability.ElectricComponent;
import de.m_marvin.industria.core.electrics.engine.network.SSyncCircuitTemplatesPackage;
import de.m_marvin.industria.core.electrics.engine.network.SSyncElectricComponentsPackage;
import de.m_marvin.industria.core.electrics.engine.network.SUpdateElectricNetworkPackage;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientElectricPackageHandler {

	/* Handle SSyncComponentsPackage package */
	
	@SuppressWarnings("resource")
	public static void handleSyncComponentsServer(SSyncElectricComponentsPackage msg, NetworkEvent.Context ctx) {
//		Level level = Minecraft.getInstance().level;
//		ElectricHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_HANDLER_CAPABILITY);
//		
//		if (msg.request == SyncRequestType.ADDED) {
//			Object position = null;
//			for (ElectricComponent<?, ?, ?> component : msg.components) {
//				if (component.instance(null) == null) continue;
//				if (!handler.isInNetwork(component)) {
//					handler.addToNetwork(component);
//					if (position == null) position = component.pos();
//				}
//			}
//		} else {
//			for (ElectricComponent<?, ?, ?> component : msg.components) {
//				handler.removeFromNetwork(component.pos);
//			}
//		}
	}

	/* Handle SUpdateNetworkPackage */
	
	@SuppressWarnings("resource")
	public static void handleUpdateNetwork(SUpdateElectricNetworkPackage msg, Context context) {
		
//		Level level = Minecraft.getInstance().level;
//		ElectricHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_HANDLER_CAPABILITY);
//
//		Optional<ElectricComponent<?, ?, ?>> c = msg.getComponents().stream().findAny();
//
//		handler.injectNodeVoltages(msg.getComponents(), msg.getDataList());
//		if (c.isPresent())
//			handler.updateNetworkState(c.get().pos(), msg.getState());
		
	}
	
	/* Handle SSyncCircuitTemplatesPackage */
	
	public static void handleSyncCircuitTemplates(SSyncCircuitTemplatesPackage msg, Context context) {
		CircuitTemplateManager.updateClientTemplates(msg.getCircuitTemplates());
	}
	
	/* End of package handling */
	
}
