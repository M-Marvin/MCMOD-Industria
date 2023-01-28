package de.m_marvin.industria.core.electrics;

import de.m_marvin.industria.core.electrics.engine.ElectricNetworkHandlerCapability;
import de.m_marvin.industria.core.registries.ModCapabilities;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ElectricUtility {

	public static void updateElectricNetwork(Level level, BlockPos worldPosition) {
		ElectricNetworkHandlerCapability handler = GameUtility.getCapability(level, ModCapabilities.ELECTRIC_NETWORK_HANDLER_CAPABILITY);
		handler.updateNetwork(worldPosition);
	}
	
}
