package de.redtec.gui;

import de.redtec.tileentity.TileEntityMElectricFurnace;
import de.redtec.typeregistys.ModContainerType;
import de.redtec.util.ContainerTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class ContainerMElectricFurnace extends ContainerTileEntity<TileEntityMElectricFurnace> {

	public ContainerMElectricFurnace(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.ELECTRIC_FURNACE, id, playerInv, data);
	}
	
	public ContainerMElectricFurnace(int id, PlayerInventory playerInv, TileEntityMElectricFurnace tileEntity) {
		super(ModContainerType.ELECTRIC_FURNACE, id, playerInv, tileEntity);
	}
	
	@Override
	public int getSlots() {
		return 2;
	}

	@Override
	public void init() {
		
		this.addSlot(new CraftingResultSlot(this.tileEntity, 1, 116, 35));
		this.addSlot(new Slot(this.tileEntity, 0, 56, 34));
		
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
