package de.m_marvin.industria.content.container;

import de.m_marvin.industria.content.blockentities.JunctionBoxBlockEntity;
import de.m_marvin.industria.content.registries.ModContainer;
import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class JunctionBoxContainer extends BlockEntityContainerBase<JunctionBoxBlockEntity> {
	
	public JunctionBoxContainer(int id, Inventory playerInv, FriendlyByteBuf data) {
		super(ModContainer.JUNCTION_BOX.get(), id, playerInv, data);
	}
	
	public JunctionBoxContainer(int id, Inventory playerInv, JunctionBoxBlockEntity tileEntity) {
		super(ModContainer.JUNCTION_BOX.get(), id, playerInv, tileEntity);
	}
	
	@Override
	public int getSlots() {
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	public String[] getWireLabels(NodePos node) {
		if (node == null) return new String[] {};
		return this.blockEntity.getCableWireLabels(node);
	}
	
	public NodePos[] getUDLRCableNodes() {
		return this.blockEntity.getUDLRCableNodes(this.playerInv.player.getDirection());
	}
	
	
	
}
