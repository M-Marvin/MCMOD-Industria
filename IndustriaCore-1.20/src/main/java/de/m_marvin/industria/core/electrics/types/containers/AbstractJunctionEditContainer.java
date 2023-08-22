package de.m_marvin.industria.core.electrics.types.containers;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.client.util.BlockEntityContainerBase;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.engine.network.CUpdateJunctionLanesPackage;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.util.GameUtility;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractJunctionEditContainer<T extends BlockEntity & IJunctionEdit> extends BlockEntityContainerBase<T> {

	public AbstractJunctionEditContainer(MenuType<?> type, int id, Inventory playerInv, FriendlyByteBuf data) {
		super(type, id, playerInv, data);
	}

	public AbstractJunctionEditContainer(MenuType<?> type, int id, Inventory playerInv, T tileEntity) {
		super(type, id, playerInv, tileEntity);
	}

	@Override
	public int getSlots() {
		return 0;
	}
	
	@Override
	public void init() {}
	
	public void setWireLabels(NodePos node, String[] labels) {
		blockEntity.setCableWireLabels(node, labels);
		IndustriaCore.NETWORK.sendToServer(new CUpdateJunctionLanesPackage(node, labels, false));
	}
	
	public String[] getWireLabels(NodePos node) {
		if (node == null) return new String[] {};
		return this.blockEntity.getCableWireLabels(node);
	}
	
	public NodePos[] getCableNodes() {
		return this.blockEntity.getEditCableNodes(GameUtility.getFacingDirection(this.playerInv.player), this.playerInv.player.getDirection());
	}

	public String[] getInternalLabels(int id) {
		return this.blockEntity.getInternalWireLabels(new NodePos(this.blockEntity.getBlockPos(), id));
	}

	public void setInternalWireLabels(int id, String[] lanes) {
		NodePos node = new NodePos(this.blockEntity.getBlockPos(), id);
		this.blockEntity.setInternalWireLabels(node, lanes);
		IndustriaCore.NETWORK.sendToServer(new CUpdateJunctionLanesPackage(node, lanes, true));
	}
	
}
