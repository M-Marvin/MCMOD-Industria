package de.m_marvin.industria.content.magnetism;

import de.m_marvin.industria.content.magnetism.engine.MagnetismHandlerCapability;
import de.m_marvin.industria.content.magnetism.types.MagneticFieldInfluence;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MagnetismUtility {
	
	public static void removeFieldInfluence(Level level, BlockPos pos) {
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, de.m_marvin.industria.content.magnetism.Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		handler.removeFieldInfluence(pos);
	}

	public static void setFieldInfluence(Level level, MagneticFieldInfluence influence) {
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, de.m_marvin.industria.content.magnetism.Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		handler.setFieldInfluence(influence);
	}
	
}
