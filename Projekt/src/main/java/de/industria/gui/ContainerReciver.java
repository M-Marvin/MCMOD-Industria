package de.industria.gui;

import de.industria.tileentity.TileEntityRedstoneReciver;
import de.industria.typeregistys.ModContainerType;
import de.industria.util.blockfeatures.ContainerTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class ContainerReciver extends ContainerTileEntity<TileEntityRedstoneReciver> {
	
	public ContainerReciver(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.REDSTONE_RECIVER, id, playerInv, data);
	}
	
	public ContainerReciver(int id, PlayerInventory playerInv, TileEntityRedstoneReciver tileEntity) {
		super(ModContainerType.REDSTONE_RECIVER, id, playerInv, tileEntity);
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public void init() {
		
		this.addSlot(new Slot(this.tileEntity, 0, 80, 36));
		
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
