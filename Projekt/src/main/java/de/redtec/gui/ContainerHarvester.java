package de.redtec.gui;

import de.redtec.tileentity.TileEntityHarvester;
import de.redtec.util.ContainerTileEntity;
import de.redtec.util.ModContainerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class ContainerHarvester extends ContainerTileEntity<TileEntityHarvester> {
	
	public ContainerHarvester(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.HARVESTER, id, playerInv, data);
	}
	
	public ContainerHarvester(int id, PlayerInventory playerInv, TileEntityHarvester tileEntity) {
		super(ModContainerType.HARVESTER, id, playerInv, tileEntity);
	}
	
	@Override
	public void init() {
		
		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 3; ++j) {
				this.addSlot(new Slot(this.tileEntity, j + i * 3, 62 + j * 18, 17 + i * 18));
			}
		}
		
		for(int k = 0; k < 3; ++k) {
			for(int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInv, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}
		
		for(int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInv, l, 8 + l * 18, 142));
		}
		
	}
	
	@Override
	public int getSlots() {
		return 9;
	}
	
}
