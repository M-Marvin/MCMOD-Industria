package de.industria.gui;

import de.industria.tileentity.TileEntityItemDistributor;
import de.industria.typeregistys.ModContainerType;
import de.industria.util.blockfeatures.ContainerTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class ContainerItemDistributor extends ContainerTileEntity<TileEntityItemDistributor> {
	
	public ContainerItemDistributor(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.ITEM_DISTRIBUTOR, id, playerInv, data);
	}
	
	public ContainerItemDistributor(int id, PlayerInventory playerInv, TileEntityItemDistributor tileEntity) {
		super(ModContainerType.ITEM_DISTRIBUTOR, id, playerInv, tileEntity);
	}

	@Override
	public int getSlots() {
		return 27;
	}

	@Override
	public void init() {
		
		int index = 0;

		for(int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(this.tileEntity, index++, 8 + i1 * 18, 75));
		}
		
		for(int k = 0; k < 3; ++k) {
			for(int i1 = 0; i1 < 3; ++i1) {
				this.addSlot(new Slot(this.tileEntity, index++, 8 + i1 * 18, 17 + k * 18));
			}
		}
		
		for(int k = 0; k < 3; ++k) {
			for(int i1 = 0; i1 < 3; ++i1) {
				this.addSlot(new Slot(this.tileEntity, index++, 115 + i1 * 18, 17 + k * 18));
			}
		}
		
		
		for(int k = 0; k < 3; ++k) {
			for(int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInv, i1 + k * 9 + 9, 8 + i1 * 18, 22 + 84 + k * 18));
			}
		}
		
		for(int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInv, l, 8 + l * 18, 22 + 142));
		}
		
	}
	
}
