package de.m_marvin.industria.core.electrics.types.containers;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.ElectricUtility;
import de.m_marvin.industria.core.electrics.engine.network.CUpdateJunctionLanesPackage;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.util.MathUtility;
import de.m_marvin.industria.core.util.container.AbstractBlockContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractJunctionEditMenu<T extends BlockEntity & IJunctionEdit> extends AbstractBlockContainerMenu {
	
	protected final T blockEntity;
	
	public AbstractJunctionEditMenu(MenuType<?> type, int id, Inventory playerInv, T junctionBlockEntity) {
		super(type, id, playerInv, junctionBlockEntity.getBlockPos(), new DummyContainer(junctionBlockEntity.getBlockPos()));
		this.blockEntity = junctionBlockEntity;
	}
	
	public void setWireLabels(NodePos node, String[] labels) {
		blockEntity.setCableWireLabels(node, labels);
		IndustriaCore.NETWORK.sendToServer(new CUpdateJunctionLanesPackage(node, labels, false));
	}
	
	public String[] getWireLabels(NodePos node) {
		if (node == null) return new String[] {};
		return ElectricUtility.getLaneLabelsSummarized(this.blockEntity.getJunctionLevel(), node);
	}
	
	public NodePos[] getCableNodes() {
		return this.blockEntity.getEditCableNodes(MathUtility.getFacingDirection(this.playerInv.player), this.playerInv.player.getDirection());
	}

	public String[] getInternalLabels(int id) {
		return this.blockEntity.getInternalWireLabels(new NodePos(this.blockEntity.getJunctionBlockPos(), id));
	}

	public boolean connectsOnlyToInternal() {
		return this.blockEntity.connectsOnlyToInternal();
	}
	
	public void setInternalWireLabels(int id, String[] lanes) {
		NodePos node = new NodePos(this.blockEntity.getJunctionBlockPos(), id);
		this.blockEntity.setInternalWireLabels(node, lanes);
		IndustriaCore.NETWORK.sendToServer(new CUpdateJunctionLanesPackage(node, lanes, true));
	}
	
}
