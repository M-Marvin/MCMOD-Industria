package de.industria.gui;

import de.industria.tileentity.TileEntityCardboardBox;
import de.industria.typeregistys.ModContainerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class ContainerCardboardBox extends ContainerTileEntity<TileEntityCardboardBox> {
	
	public ContainerCardboardBox(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.CARDBOARD_BOX, id, playerInv, data);
	}
	
	public ContainerCardboardBox(int id, PlayerInventory playerInv, TileEntityCardboardBox tileEntity) {
		super(ModContainerType.CARDBOARD_BOX, id, playerInv, tileEntity);
	}
	
	@Override
	public int getSlots() {
		return 27;
	}

	@Override
	public void init() {
		
		int i = 8;
		int j = 18;
		int index = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlot(new Slot(this.tileEntity, index++, x * 18 + i, y * 18 + j));
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

}
