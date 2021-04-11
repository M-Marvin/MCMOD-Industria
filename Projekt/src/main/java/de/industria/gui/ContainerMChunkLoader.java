package de.industria.gui;

import de.industria.tileentity.TileEntityMChunkLoader;
import de.industria.typeregistys.ModContainerType;
import de.industria.util.blockfeatures.ContainerTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerMChunkLoader extends ContainerTileEntity<TileEntityMChunkLoader> {
	
	public ContainerMChunkLoader(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.CHUNK_LAODER, id, playerInv, data);
	}

	public ContainerMChunkLoader(int id, PlayerInventory playerInv, TileEntityMChunkLoader tileEntity) {
		super(ModContainerType.CHUNK_LAODER, id, playerInv, tileEntity);
	}

	@Override
	public int getSlots() {
		return 0;
	}

	@Override
	public void init() {}
	
}