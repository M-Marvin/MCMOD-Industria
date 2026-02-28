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
	
	public int getPower() {
		return getDataContainer().getInt(1);
	}
	
	public void setPower(int power) {
		getDataContainer().setInt(1, power);
	}
	
	public int getVoltage() {
		return getDataContainer().getInt(0);
	}
	
	public void setVoltage(int voltage) {
		getDataContainer().setInt(0, voltage);
	}
	
	public double getDeviceVoltage() {
		return getDataContainer().getDouble(2);
	}
	
	public double getDeviceCurrent() {
		return getDataContainer().getDouble(4);
	}
	
}
