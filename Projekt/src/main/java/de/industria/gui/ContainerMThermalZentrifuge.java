package de.industria.gui;

import de.industria.tileentity.TileEntityMThermalZentrifuge;
import de.industria.typeregistys.ModContainerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class ContainerMThermalZentrifuge extends ContainerTileEntity<TileEntityMThermalZentrifuge> {
	
	public ContainerMThermalZentrifuge(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.THERMAL_ZENTRIFUGE, id, playerInv, data);
	}
	
	public ContainerMThermalZentrifuge(int id, PlayerInventory playerInv, TileEntityMThermalZentrifuge tileEntity) {
		super(ModContainerType.THERMAL_ZENTRIFUGE, id, playerInv, tileEntity);
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