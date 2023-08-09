package de.m_marvin.industria.core.client.electrics;

import de.m_marvin.industria.core.client.util.BlockEntityContainerBase;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.types.blockentities.IEditableJunction;
import de.m_marvin.industria.core.registries.Container;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

public class JunctionBoxContainer<T extends BlockEntity & IEditableJunction<T>> extends BlockEntityContainerBase<T> {
	
	public JunctionBoxContainer(int id, Inventory playerInv, FriendlyByteBuf data) {
		super(Container.JUNCTION_BOX.get(), id, playerInv, data);
	}
	
	public JunctionBoxContainer(int id, Inventory playerInv, T tileEntity) {
		super(Container.JUNCTION_BOX.get(), id, playerInv, tileEntity);
	}
	
	@Override
	public int getSlots() {
		return 0;
	}
	
	@Override
	public void init() {}
	
	public String[] getWireLabels(NodePos node) {
		if (node == null) return new String[] {};
		return this.blockEntity.getCableWireLabels(node);
	}
	
	public NodePos[] getUDLRCableNodes() {
		return this.blockEntity.getUDLRCableNodes(this.playerInv.player.getDirection());
	}
	
}
