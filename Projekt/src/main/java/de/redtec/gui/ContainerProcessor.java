package de.redtec.gui;

import de.redtec.tileentity.TileEntitySignalProcessorContact;
import de.redtec.util.ContainerTileEntity;
import de.redtec.util.ModContainerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerProcessor extends ContainerTileEntity<TileEntitySignalProcessorContact> {
	
	public ContainerProcessor(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.PROCESSOR, id, playerInv, data);
	}

	public ContainerProcessor(int id, PlayerInventory playerInv, TileEntitySignalProcessorContact tileEntity) {
		super(ModContainerType.PROCESSOR, id, playerInv, tileEntity);
	}

	@Override
	public int getSlots() {
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
