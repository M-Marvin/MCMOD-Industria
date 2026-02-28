package de.m_marvin.industria.core.electrics.engine;

import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.network.CSwitchNetworkStatePackage;
import de.m_marvin.industria.core.electrics.engine.network.CUpdateJunctionLanesPackage;
import de.m_marvin.industria.core.electrics.types.IElectric.ElectricReference;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.electrics.types.blocks.IElectricBlock;
import de.m_marvin.industria.core.util.container.AbstractBlockContainerMenu;
import de.m_marvin.industria.core.util.types.PowerNetState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class ServerElectricPackageHandler {
	
	public static void handlePlayerSwitchNetwork(CSwitchNetworkStatePackage msg, Context ctx) {
		
		if (ctx.getSender().containerMenu instanceof AbstractBlockContainerMenu menu && menu.containerId == msg.getContainerId()) {
			if (menu.getBlockState().getBlock() instanceof IElectricBlock electric) {
				BlockPos masterPos = electric.getConnectorMasterPos(menu.getLevel(), menu.getBlockPos(), menu.getBlockState());
				ElectricUtility.setNetworkState(menu.getLevel(), ElectricReference.block(masterPos), msg.getState() ? PowerNetState.ACTIVE : PowerNetState.INACTIVE);
			}
		}
		
	}
	
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
	
}
