package de.m_marvin.industria.core.electrics.types.containers;

import de.m_marvin.industria.core.registries.MenuTypes;
import de.m_marvin.industria.core.util.container.FriendlyContainerData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;

public class VoltageSourceMenu extends AbstractSourceMenu {

	public VoltageSourceMenu(int pContainerId, Inventory playerInv, FriendlyByteBuf extraData) {
		super(MenuTypes.VOLTAGE_SOURCE.get(), pContainerId, playerInv, extraData, new SimpleContainer(0),
				FriendlyContainerData.empty()
				.nextIntItemStatic()
				.nextIntItemStatic()
				.nextFloatItemStatic()
				.nextFloatItemStatic()
			);
	}
	
	public VoltageSourceMenu(int pContainerId, Inventory playerInv, BlockPos blockPos, Container container, FriendlyContainerData dataContainer) {
		super(MenuTypes.VOLTAGE_SOURCE.get(), pContainerId, playerInv, blockPos, container, dataContainer);
		checkContainerDataCount(dataContainer, 4);
		checkContainerSize(container, 0);
	}

	public int getVoltage() {
		return getDataContainer().getInt(0);
	}
	
	public void setVoltage(int voltage) {
		getDataContainer().setInt(0, voltage);
	}
	
}
