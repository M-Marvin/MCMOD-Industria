package de.m_marvin.industria.core.electrics.types.containers;

import de.m_marvin.industria.core.conduits.types.ConduitPos.NodePos;
import de.m_marvin.industria.core.electrics.types.blockentities.IJunctionEdit;
import de.m_marvin.industria.core.registries.MenuTypes;
import de.m_marvin.industria.core.util.Direction2d;
import de.m_marvin.univec.impl.Vec2i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

public class JunctionBoxContainer<T extends BlockEntity & IJunctionEdit> extends AbstractJunctionEditContainer<T> {

	public JunctionBoxContainer(MenuType<?> type, int id, Inventory playerInv, FriendlyByteBuf data) {
		super(type, id, playerInv, data);
	}

	public JunctionBoxContainer(MenuType<?> type, int id, Inventory playerInv, T tileEntity) {
		super(type, id, playerInv, tileEntity);
	}

	public JunctionBoxContainer(int id, Inventory playerInv, FriendlyByteBuf data) {
		super(MenuTypes.JUNCTION_BOX.get(), id, playerInv, data);
	}

	public JunctionBoxContainer(int id, Inventory playerInv, T tileEntity) {
		super(MenuTypes.JUNCTION_BOX.get(), id, playerInv, tileEntity);
	}
	
	@FunctionalInterface
	public static interface ExternalNodeConstructor {
		public void construct(Vec2i position, Direction2d orientation, NodePos node);
	}

	@FunctionalInterface
	public static interface InternalNodeConstructor {
		public void construct(Vec2i position, Direction2d orientation, int internalId);
	}
	public void setupScreenConduitNodes(NodePos[] conduitNodes, ExternalNodeConstructor externalNodeConstructor, InternalNodeConstructor internalNodeConstructor) {
		this.blockEntity.setupScreenConduitNodes(this, conduitNodes, externalNodeConstructor, internalNodeConstructor);
	}
	
}
