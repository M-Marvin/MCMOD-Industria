package de.m_marvin.industria.core.magnetism;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.magnetism.engine.MagnetismHandlerCapability;
import de.m_marvin.industria.core.magnetism.engine.network.SUpdateMagneticFieldPackage;
import de.m_marvin.industria.core.magnetism.types.MagneticField;
import de.m_marvin.industria.core.magnetism.types.MagneticFieldInfluence;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public class MagnetismUtility {
	
	public static void removeFieldInfluence(Level level, BlockPos pos) {
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		handler.removeFieldInfluence(pos);
	}

	public static void setFieldInfluence(Level level, MagneticFieldInfluence influence) {
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		handler.setFieldInfluence(influence);
	}
	
	public static void updateField(Level level, BlockPos pos) {
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		if (!level.isClientSide()) {
			IndustriaCore.NETWORK.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), new SUpdateMagneticFieldPackage(pos));
		}
		handler.updateField(pos);
	}
	
	public static MagneticField getMagneticFieldAt(Level level, BlockPos pos) {
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		return handler.getFieldAt(pos);
	}
	
	public static MagneticFieldInfluence getMagneticInfluenceOf(Level level, BlockPos pos) {
		MagnetismHandlerCapability handler = GameUtility.getLevelCapability(level, Capabilities.MAGNETISM_HANDLER_CAPABILITY);
		return handler.getInfluenceOf(pos);
	}
	
}
