package de.m_marvin.industria.core.electrics.engine;

import de.m_marvin.industria.content.blockentities.JunctionBoxBlockEntity;
import de.m_marvin.industria.core.electrics.engine.network.CUpdateJunctionLanes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerElectricPackageHandler {

	/* Handle CUpdateJunctionLanes package */
	
	public static void handleUpdateJunctionLanes(CUpdateJunctionLanes msg, Context ctx) {
		
		BlockPos junctionBoxPos = msg.getWireNode().getBlock();
		BlockEntity junctionBoxTileEntity = ctx.getSender().getLevel().getBlockEntity(junctionBoxPos);
		if (junctionBoxTileEntity instanceof JunctionBoxBlockEntity junctionBox) {
			junctionBox.setCableWireLabels(msg.getWireNode(), msg.getLaneLabels());
		}
		
	}
	
}
