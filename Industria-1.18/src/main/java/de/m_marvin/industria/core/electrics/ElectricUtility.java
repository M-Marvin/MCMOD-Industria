package de.m_marvin.industria.core.electrics;

import de.m_marvin.industria.content.registries.ModCapabilities;
import de.m_marvin.industria.core.GameUtility;
import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ElectricUtility {

	public static void updateElectricNetwork(Level level, BlockPos worldPosition) {
		ElectricNetworkHandlerCapability handler = GameUtility.getCapability(level, ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		handler.updateNetwork(worldPosition);
	}
	
}
