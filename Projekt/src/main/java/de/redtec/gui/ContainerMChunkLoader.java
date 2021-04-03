package de.redtec.gui;

import de.redtec.tileentity.TileEntityMChunkLoader;
import de.redtec.typeregistys.ModContainerType;
import de.redtec.util.blockfeatures.ContainerTileEntity;
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