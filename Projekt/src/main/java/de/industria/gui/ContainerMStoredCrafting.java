package de.industria.gui;

import de.industria.tileentity.TileEntityMStoringCraftingTable;
import de.industria.typeregistys.ModContainerType;
import de.industria.util.blockfeatures.ContainerTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class ContainerMStoredCrafting extends ContainerTileEntity<TileEntityMStoringCraftingTable> {

	public ContainerMStoredCrafting(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.STORED_CRAFTING, id, playerInv, data);
	}

	public ContainerMStoredCrafting(int id, PlayerInventory playerInv, TileEntityMStoringCraftingTable tileEntity) {
		super(ModContainerType.STORED_CRAFTING, id, playerInv, tileEntity);
	}

	@Override
	public int getSlots() {
		return 10;
	}

	@Override
	public void init() {
		
		this.addSlot(new CraftingResultSlot(this.tileEntity, 9, 124, 35));
		this.addSlot(new Slot(this.tileEntity, 10, 85, 53));
		
		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 3; ++j) {
				this.addSlot(new Slot(this.tileEntity, j + i * 3, 30 + j * 18, 17 + i * 18));
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