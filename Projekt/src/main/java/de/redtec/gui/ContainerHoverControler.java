package de.redtec.gui;

import de.redtec.registys.ModContainerType;
import de.redtec.tileentity.TileEntityHoverControler;
import de.redtec.util.ContainerTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class ContainerHoverControler extends ContainerTileEntity<TileEntityHoverControler> {

	public ContainerHoverControler(int id, PlayerInventory playerInv, TileEntityHoverControler tileEntity) {
		super(ModContainerType.HOVER_CONTROLER, id, playerInv, tileEntity);
	}

	public ContainerHoverControler(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.HOVER_CONTROLER, id, playerInv, data);
	}

	@Override
	public int getSlots() {
		return 7;
	}

	@Override
	public void init() {

		this.addSlot(new Slot(this.tileEntity, 2, 8, 35));
		this.addSlot(new Slot(this.tileEntity, 1, 27, 54));
		this.addSlot(new Slot(this.tileEntity, 3, 46, 35));
		this.addSlot(new Slot(this.tileEntity, 0, 27, 16));
		
		this.addSlot(new Slot(this.tileEntity, 4, 80, 16));
		this.addSlot(new Slot(this.tileEntity, 5, 80, 54));
		
		this.addSlot(new Slot(this.tileEntity, 6, 152, 35));
		
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
