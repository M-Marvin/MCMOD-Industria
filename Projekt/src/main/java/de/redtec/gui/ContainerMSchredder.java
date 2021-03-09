package de.redtec.gui;

import de.redtec.tileentity.TileEntityMSchredder;
import de.redtec.typeregistys.ModContainerType;
import de.redtec.util.ContainerTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class ContainerMSchredder extends ContainerTileEntity<TileEntityMSchredder> {
	
	public ContainerMSchredder(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.SCHREDDER, id, playerInv, data);
	}
	
	public ContainerMSchredder(int id, PlayerInventory playerInv, TileEntityMSchredder tileEntity) {
		super(ModContainerType.SCHREDDER, id, playerInv, tileEntity);
	}

	@Override
	public int getSlots() {
		return 5;
	}

	@Override
	public void init() {
		
		this.addSlot(new Slot(this.tileEntity, 1, 116, 15));
		this.addSlot(new Slot(this.tileEntity, 2, 116, 35));
		this.addSlot(new Slot(this.tileEntity, 3, 116, 55));
		this.addSlot(new Slot(this.tileEntity, 0, 44, 34));
		this.addSlot(new Slot(this.tileEntity, 4, 80, 34));
		
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
