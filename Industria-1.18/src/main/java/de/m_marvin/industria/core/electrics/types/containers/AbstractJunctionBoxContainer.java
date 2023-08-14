package de.m_marvin.industria.core.electrics.types.containers;

import de.m_marvin.industria.core.electrics.types.blockentities.AbstractJunctionBoxBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public abstract class AbstractJunctionBoxContainer<T extends AbstractJunctionBoxBlockEntity> extends AbstractJunctionEditContainer<T> {

	public AbstractJunctionBoxContainer(MenuType<?> type, int id, Inventory playerInv, FriendlyByteBuf data) {
		super(type, id, playerInv, data);
	}

	public AbstractJunctionBoxContainer(MenuType<?> type, int id, Inventory playerInv, T tileEntity) {
		super(type, id, playerInv, tileEntity);
	}

}
