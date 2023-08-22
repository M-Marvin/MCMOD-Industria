package de.m_marvin.industria.core.electrics.engine;

import java.util.Map.Entry;

import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability.Component;
import de.m_marvin.industria.core.electrics.engine.network.SSyncComponentsPackage;
import de.m_marvin.industria.core.electrics.types.IElectric;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.SyncRequestType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientElectricPackageHandler {

	@SuppressWarnings({ "resource", "unchecked", "rawtypes" })
	public static void handleSyncComponentsServer(SSyncComponentsPackage msg, Context context) {
		
		Level level = Minecraft.getInstance().level;
		ElectricNetworkHandlerCapability handler = GameUtility.getCapability(level, Capabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		if (msg.request == SyncRequestType.ADDED) {
			for (Entry<Object, IElectric<?, ?, ?>> component : msg.components.entrySet()) {
				handler.addToNetwork(new Component(level, component.getKey(), component.getValue()));
			}
		} else {
			for (Entry<Object, IElectric<?, ?, ?>> component : msg.components.entrySet()) {
				handler.removeFromNetwork(component.getKey());
			}
		}
		
	}
	
}
