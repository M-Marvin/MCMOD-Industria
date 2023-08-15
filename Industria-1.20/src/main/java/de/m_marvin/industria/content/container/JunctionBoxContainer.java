package de.m_marvin.industria.content.container;

import de.m_marvin.industria.content.blockentities.JunctionBoxBlockEntity;
import de.m_marvin.industria.content.registries.ModMenuTypes;
import de.m_marvin.industria.core.electrics.types.containers.AbstractJunctionBoxContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class JunctionBoxContainer extends AbstractJunctionBoxContainer<JunctionBoxBlockEntity> {

	public JunctionBoxContainer(int id, Inventory playerInv, FriendlyByteBuf data) {
		super(ModMenuTypes.JUNCTION_BOX.get(), id, playerInv, data);
	}

	public JunctionBoxContainer(int id, Inventory playerInv, JunctionBoxBlockEntity tileEntity) {
		super(ModMenuTypes.JUNCTION_BOX.get(), id, playerInv, tileEntity);
	}

}
