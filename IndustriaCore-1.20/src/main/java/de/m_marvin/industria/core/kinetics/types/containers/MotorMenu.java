package de.m_marvin.industria.core.kinetics.types.containers;

import de.m_marvin.industria.core.registries.MenuTypes;
import de.m_marvin.industria.core.util.container.AbstractBlockDataContainerMenu;
import de.m_marvin.industria.core.util.container.FriendlyContainerData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;

public class MotorMenu extends AbstractBlockDataContainerMenu {
	
	public MotorMenu(int id, Inventory playerInv, FriendlyByteBuf extraData) {
		super(MenuTypes.MOTOR.get(), id, playerInv, extraData, new SimpleContainer(0),
				FriendlyContainerData.empty()
				.nextDoubleItemStatic()
				.nextDoubleItemStatic()
			);
	}
	
	public MotorMenu(int id, Inventory playerInv, BlockPos blockPos, Container container, FriendlyContainerData dataContainer) {
		super(MenuTypes.MOTOR.get(), id, playerInv, blockPos, container, dataContainer);
		checkContainerDataCount(dataContainer, 4);
		checkContainerSize(container, 0);
	}
	
	public double getRPM() {
		return getDataContainer().getDouble(0);
	}
	
	public void setRPM(double rpm) {
		getDataContainer().setDouble(0, rpm);
	}

	public double getTorque() {
		return getDataContainer().getDouble(2);
	}
	
	public void setTorque(double torque) {
		getDataContainer().setDouble(2, torque);
	}
	
}