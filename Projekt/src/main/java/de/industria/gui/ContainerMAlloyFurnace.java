package de.industria.gui;

import de.industria.tileentity.TileEntityMAlloyFurnace;
import de.industria.typeregistys.ModContainerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class ContainerMAlloyFurnace extends ContainerTileEntity<TileEntityMAlloyFurnace> {
	
	public ContainerMAlloyFurnace(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.ALLOY_FURNACE, id, playerInv, data);
	}
	
	public ContainerMAlloyFurnace(int id, PlayerInventory playerInv, TileEntityMAlloyFurnace tileEntity) {
		super(ModContainerType.ALLOY_FURNACE, id, playerInv, tileEntity);
	}

	@Override
	public int getSlots() {
		return 5;
	}

	@Override
	public void init() {
		
		this.addSlot(new Slot(this.tileEntity, 0, 44, 15));
		this.addSlot(new Slot(this.tileEntity, 1, 44, 35));
		this.addSlot(new Slot(this.tileEntity, 2, 44, 55));
		this.addSlot(new Slot(this.tileEntity, 3, 116, 34));
		
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
