package de.industria.tileentity;

import de.industria.gui.ContainerCardboardBox;
import de.industria.typeregistys.ModTileEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TileEntityCardboardBox extends TileEntityInventoryBase implements INamedContainerProvider {
	
	public TileEntityCardboardBox() {
		super(ModTileEntityType.CARDBOARD_BOX, 27);
	}

	@Override
	public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
		return new ContainerCardboardBox(id, playerInv, this);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("block.industria.cardboard_box");
	}
	
}
