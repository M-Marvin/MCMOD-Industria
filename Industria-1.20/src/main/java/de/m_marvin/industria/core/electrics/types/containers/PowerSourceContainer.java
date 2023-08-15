package de.m_marvin.industria.core.electrics.types.containers;

import de.m_marvin.industria.core.electrics.types.blockentities.PowerSourceBlockEntity;
import de.m_marvin.industria.core.registries.Container;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class PowerSourceContainer extends AbstractJunctionEditContainer<PowerSourceBlockEntity> {

	public PowerSourceContainer(int id, Inventory playerInv, FriendlyByteBuf data) {
		super(Container.POWER_SOURCE.get(), id, playerInv, data);
	}

	public PowerSourceContainer(int id, Inventory playerInv, PowerSourceBlockEntity tileEntity) {
		super(Container.POWER_SOURCE.get(), id, playerInv, tileEntity);
	}
	
}
