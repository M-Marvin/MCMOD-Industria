package de.m_marvin.industria.core.electrics.engine;

import de.m_marvin.industria.core.electrics.engine.network.CUpdateJunctionLanes;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerElectricPackageHandler {
	
	public static void handleUpdateJunctionLanes(CUpdateJunctionLanes msg, Context ctx) {
		
		BlockPos blockPos = msg.getBlockPos();
		Level level = ctx.getSender().level();
		BlockEntity blockEntity = level.getBlockEntity(blockPos);
		if (blockEntity instanceof IJunctionEdit junctionEditEntity) {
			if (msg.isInternalNode()) {
				junctionEditEntity.setInternalWireLabels(msg.getInternalNode(), msg.getLaneLabels());
			} else {
				junctionEditEntity.setCableWireLabels(msg.getCableNode(), msg.getLaneLabels());
			}
		}
		
	}
	
}
