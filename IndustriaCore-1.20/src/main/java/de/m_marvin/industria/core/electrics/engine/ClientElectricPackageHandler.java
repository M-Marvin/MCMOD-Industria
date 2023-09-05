package de.m_marvin.industria.core.electrics.engine;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.engine.network.SSyncComponentsPackage;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientElectricPackageHandler {
	
	/* Handle SSyncComponentsPackage package */
	
	@SuppressWarnings("resource")
	public static void handleSyncComponentsServer(SSyncComponentsPackage msg, NetworkEvent.Context ctx) {
		Level level = Minecraft.getInstance().level;
		ElectricNetworkHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		
		if (msg.request == SyncRequestType.ADDED) {
			Object position = null;
			for (Component<?, ?, ?> component : msg.components) {
				if (component.instance(null) == null) continue;
				if (!handler.isInNetwork(component)) {
					handler.addToNetwork(component);
					if (position == null) position = component.pos();
				}
			}
		} else {
			for (Component<?, ?, ?> component : msg.components) {
				handler.removeFromNetwork(component.pos());
			}
		}
	}
	
	/* End of package handling */
	
}
