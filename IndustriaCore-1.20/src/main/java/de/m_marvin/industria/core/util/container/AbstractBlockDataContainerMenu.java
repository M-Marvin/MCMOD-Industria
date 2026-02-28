package de.m_marvin.industria.core.util.container;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.util.network.CContainerDataChanged;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class AbstractBlockDataContainerMenu extends AbstractBlockContainerMenu {
	
	protected final FriendlyContainerData dataContainer;
	
	public AbstractBlockDataContainerMenu(MenuType<?> type, int id, Inventory playerInv, FriendlyByteBuf extraData, Container container, FriendlyContainerData dataContainer) {
		super(type, id, playerInv, extraData, container);
		this.dataContainer = dataContainer;
		addDataSlots(this.dataContainer);
		this.dataContainer.fromRawData(extraData.readVarIntArray());
	}
	
	public AbstractBlockDataContainerMenu(MenuType<?> type, int id, Inventory playerInv, BlockPos blockPos, Container container, FriendlyContainerData dataContainer) {
		super(type, id, playerInv, blockPos, container);
		this.dataContainer = dataContainer;
		addDataSlots(this.dataContainer);
	}
	
	public FriendlyContainerData getDataContainer() {
		return dataContainer;
	}
	
	public void sendDataToClient() {
		IndustriaCore.NETWORK.sendToServer(new CContainerDataChanged(this.containerId, this.dataContainer.getRawData()));
	}
	
}
