package de.industria.gui;

import de.industria.tileentity.TileEntityMAirCompressor;
import de.industria.typeregistys.ModContainerType;
import de.industria.util.blockfeatures.ContainerTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class ContainerMAirCompressor extends ContainerTileEntity<TileEntityMAirCompressor>{
	
	public ContainerMAirCompressor(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.AIR_COMPRESSOR, id, playerInv, data);
	}
	
	public ContainerMAirCompressor(int id, PlayerInventory playerInv, TileEntityMAirCompressor tileEntity) {
		super(ModContainerType.AIR_COMPRESSOR, id, playerInv, tileEntity);
	}

	@Override
	public int getSlots() {
		return 0;
	}

	@Override
	public void init() {

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
