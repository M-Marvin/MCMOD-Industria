package de.m_marvin.industria.core.kinetics.engine;

import java.util.Optional;

import de.m_marvin.industria.core.kinetics.KineticUtility;
import de.m_marvin.industria.core.kinetics.engine.network.CEditMotorPackage;
import de.m_marvin.industria.core.kinetics.types.blockentities.MotorBlockEntity;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.KineticReference;
import de.m_marvin.industria.core.registries.BlockEntityTypes;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerKineticPackageHandler {

	public static void handleEditMotor(CEditMotorPackage msg, Context context) {
		
		Optional<MotorBlockEntity> powerSource = context.getSender().level().getBlockEntity(msg.getPos(), BlockEntityTypes.MOTOR.get());
		if (powerSource.isPresent()) {
			powerSource.get().setSourceRPM(msg.getRPM());
			powerSource.get().setSourceTorque(msg.getTorque());
			KineticUtility.recalculateNetwork(powerSource.get().getLevel(), KineticReference.simple(powerSource.get().getBlockPos()));
		}
		
	}
	
}
