package de.redtec.gui;

import de.redtec.registys.ModContainerType;
import de.redtec.tileentity.TileEntityMRaffinery;
import de.redtec.util.ContainerTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
 
public class ContainerMRaffinery extends ContainerTileEntity<TileEntityMRaffinery> {
	
	public ContainerMRaffinery(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.RAFFINERY, id, playerInv, data);
	}
	
	public ContainerMRaffinery(int id, PlayerInventory playerInv, TileEntityMRaffinery tileEntity) {
		super(ModContainerType.RAFFINERY, id, playerInv, tileEntity);
	}

	@Override
	public int getSlots() {
		return 7;
	}

	@Override
	public void init() {
		
		this.addSlot(new Slot(this.tileEntity, 2, 116, 55));
		this.addSlot(new Slot(this.tileEntity, 1, 80, 55));
		this.addSlot(new Slot(this.tileEntity, 0, 44, 55));
		
		this.addSlot(new Slot(this.tileEntity, 3, 8, 15));
		this.addSlot(new Slot(this.tileEntity, 4, 8, 55));
		
		this.addSlot(new Slot(this.tileEntity, 5, 152, 15));
		this.addSlot(new Slot(this.tileEntity, 6, 152, 55));
		
		for(int k = 0; k < 3; ++k) {
			for(int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInv, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}
		
		for(int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInv, l, 8 + l * 18, 142));
		}
		
	}
	
}