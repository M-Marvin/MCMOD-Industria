package de.m_marvin.industria.core.magnetism;

import de.m_marvin.industria.core.magnetism.engine.MagnetismHandlerCapability;
import de.m_marvin.industria.core.magnetism.types.MagneticFieldInfluence;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MagnetismUtility {
	
	public static void removeFieldInfluence(Level level, BlockPos pos) {
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		handler.removeFieldInfluence(pos);
	}

	public static void setFieldInfluence(Level level, MagneticFieldInfluence influence) {
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		System.out.println("place influence at " + influence.getPos());
		handler.setFieldInfluence(influence);
	}
	
}
