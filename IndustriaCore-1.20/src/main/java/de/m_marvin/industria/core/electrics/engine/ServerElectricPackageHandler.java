package de.m_marvin.industria.core.electrics.engine;

import java.util.Optional;

import de.m_marvin.industria.core.electrics.engine.network.CEditPowerSourcePackage;
import de.m_marvin.industria.core.electrics.engine.network.CUpdateJunctionLanesPackage;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.blockentities.PowerSourceBlockEntity;
import de.m_marvin.industria.core.registries.BlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerElectricPackageHandler {
	
	public static void handleUpdateJunctionLanes(CUpdateJunctionLanesPackage msg, Context ctx) {
		
		BlockPos blockPos = msg.getCableNode().getBlock();
		Level level = ctx.getSender().level();
		BlockEntity blockEntity = level.getBlockEntity(blockPos);
		if (blockEntity instanceof IJunctionEdit junctionEditEntity) {
			if (msg.isInternalNode()) {
				junctionEditEntity.setInternalWireLabels(msg.getCableNode(), msg.getLaneLabels());
			} else {
				junctionEditEntity.setCableWireLabels(msg.getCableNode(), msg.getLaneLabels());
			}
		}
		
	}

	public static void handleEditPowerSource(CEditPowerSourcePackage msg, Context context) {
		
		Optional<PowerSourceBlockEntity> powerSource = context.getSender().level().getBlockEntity(msg.getPos(), BlockEntityTypes.POWER_SOURCE.get());
		if (powerSource.isPresent()) {
			powerSource.get().setVoltageAndPower(msg.getVoltage(), msg.getPower());
		}
		
	}
	
}
