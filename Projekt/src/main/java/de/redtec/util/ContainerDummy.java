package de.redtec.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

public class ContainerDummy extends Container {

	public ContainerDummy() {
		super(null, 0);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return false;
	}
	
}
