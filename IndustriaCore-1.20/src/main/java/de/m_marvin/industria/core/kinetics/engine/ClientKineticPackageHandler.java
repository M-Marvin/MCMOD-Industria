package de.m_marvin.industria.core.kinetics.engine;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.kinetics.engine.KineticHandlerCapabillity.KineticComponent;
import de.m_marvin.industria.core.kinetics.engine.network.SSyncKineticComponentsPackage;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import de.m_marvin.industria.core.util.ufns.SynchronizedFunctionalNetworkSpace.UpdateType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

@Mod.EventBusSubscriber(modid=IndustriaCore.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class ClientKineticPackageHandler {

	public static void handleSyncComponentsServer(SSyncKineticComponentsPackage msg, NetworkEvent.Context ctx) {
		Level level = Minecraft.getInstance().level;
		KineticHandlerCapabillity handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_HANDLER_CAPABILITY);
		for (var c : msg.getComponents()) {
			handler.updateTicket(c.reference(), msg.request == SyncRequestType.ADDED ? UpdateType.COMPONENT_PUT : UpdateType.COMPONENT_REMOVE);
		}
	}

//	@SuppressWarnings("resource")
//	public static void handleUpdateNetwork(SUpdateKineticNetworkPackage msg, Context context) {
//		
//		Level level = Minecraft.getInstance().level;
//		KineticHandlerCapabillity handler = GameUtility.getLevelCapability(level, Capabilities.KINETIC_HANDLER_CAPABILITY);
//
//		Optional<Component> c = msg.getComponents().stream().findAny();
//
//		handler.injectNodeVoltages(msg.getComponents(), msg.getDataList());
//		if (c.isPresent())
//			handler.updateNetworkState(c.get().pos(), msg.getState());
//		
//	}
	
}
