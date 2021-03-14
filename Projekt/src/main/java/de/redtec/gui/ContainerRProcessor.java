package de.redtec.gui;

import de.redtec.tileentity.TileEntityRSignalProcessorContact;
import de.redtec.typeregistys.ModContainerType;
import de.redtec.util.ContainerTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerRProcessor extends ContainerTileEntity<TileEntityRSignalProcessorContact> {
	
	public ContainerRProcessor(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.PROCESSOR, id, playerInv, data);
	}

	public ContainerRProcessor(int id, PlayerInventory playerInv, TileEntityRSignalProcessorContact tileEntity) {
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
