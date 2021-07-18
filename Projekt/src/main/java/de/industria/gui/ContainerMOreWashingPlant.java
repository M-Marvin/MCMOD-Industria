package de.industria.gui;

import de.industria.tileentity.TileEntityMOreWashingPlant;
import de.industria.typeregistys.ModContainerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
 
public class ContainerMOreWashingPlant extends ContainerTileEntity<TileEntityMOreWashingPlant> {
	
	public ContainerMOreWashingPlant(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.ORE_WASHING_PLANT, id, playerInv, data);
	}
	
	public ContainerMOreWashingPlant(int id, PlayerInventory playerInv, TileEntityMOreWashingPlant tileEntity) {
		super(ModContainerType.ORE_WASHING_PLANT, id, playerInv, tileEntity);
	}
	
	@Override
	public int getSlots() {
		return 8;
	}
	
	@Override
	public void init() {

		this.addSlot(new Slot(this.tileEntity, 0, 34, 32));
		
		this.addSlot(new Slot(this.tileEntity, 1, 85, 55));
		this.addSlot(new Slot(this.tileEntity, 2, 103, 55));
		this.addSlot(new Slot(this.tileEntity, 3, 121, 55));

		this.addSlot(new Slot(this.tileEntity, 4, 8, 15));
		this.addSlot(new Slot(this.tileEntity, 5, 8, 55));
		
		this.addSlot(new Slot(this.tileEntity, 6, 152, 15));
		this.addSlot(new Slot(this.tileEntity, 7, 152, 55));
		
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