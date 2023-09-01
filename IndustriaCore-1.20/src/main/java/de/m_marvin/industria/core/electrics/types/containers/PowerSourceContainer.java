package de.m_marvin.industria.core.electrics.types.containers;

import de.m_marvin.industria.core.electrics.types.blockentities.PowerSourceBlockEntity;
import de.m_marvin.industria.core.registries.MenuTypes;
import de.m_marvin.industria.core.util.container.AbstractBlockEntityContainerBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class PowerSourceContainer extends AbstractBlockEntityContainerBase<PowerSourceBlockEntity> {

	public PowerSourceContainer(int id, Inventory playerInv, FriendlyByteBuf data) {
		super(MenuTypes.POWER_SOURCE.get(), id, playerInv, data);
	}
	
	public PowerSourceContainer(int id, Inventory playerInv, PowerSourceBlockEntity tileEntity) {
		super(MenuTypes.POWER_SOURCE.get(), id, playerInv, tileEntity);
	}

	@Override
	public int getSlots() {
		return 0;
	}

	@Override
	public void init() {}
	
}
