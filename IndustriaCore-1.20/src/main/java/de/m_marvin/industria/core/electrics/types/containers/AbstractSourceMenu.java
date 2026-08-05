package de.m_marvin.industria.core.electrics.types.containers;

import de.m_marvin.industria.core.util.container.AbstractBlockDataContainerMenu;
import de.m_marvin.industria.core.util.container.FriendlyContainerData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public abstract class AbstractSourceMenu extends AbstractBlockDataContainerMenu {

	public AbstractSourceMenu(MenuType<?> pMenuType, int pContainerId, Inventory playerInv, FriendlyByteBuf extraData, Container container, FriendlyContainerData dataContainer) {
		super(pMenuType, pContainerId, playerInv, extraData, container, dataContainer);
	}
	
	public AbstractSourceMenu(MenuType<?> pMenuType, int pContainerId, Inventory playerInv, BlockPos blockPos, Container container, FriendlyContainerData dataContainer) {
		super(pMenuType, pContainerId, playerInv, blockPos, container, dataContainer);
	}
	
	public float getPower() {
		return getDataContainer().getFloat(1);
	}
	
	public void setPower(float power) {
		getDataContainer().setFloat(1, power);
	}
	
	public float getDeviceVoltage() {
		return getDataContainer().getFloat(2);
	}
	
	public float getDeviceCurrent() {
		return getDataContainer().getFloat(3);
	}
	
}
