package de.m_marvin.industria.core.electrics.engine;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.network.CUpdateJunctionLanes;
import de.m_marvin.industria.core.electrics.types.blockentities.AbstractJunctionBoxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerElectricPackageHandler {
	
	public static void handleUpdateJunctionLanes(CUpdateJunctionLanes msg, Context ctx) {
		
		NodePos nodePos = msg.getCableNode();
		BlockPos blockPos = nodePos.getBlock();
		Level level = ctx.getSender().getLevel();
		BlockEntity blockEntity = level.getBlockEntity(blockPos);
		if (blockEntity instanceof AbstractJunctionBoxBlockEntity junctionBlockEntity) {
			junctionBlockEntity.setCableWireLabels(nodePos, msg.getLaneLabels());
		}
		
	}
	
}
