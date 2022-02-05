package de.industria.gui;

import de.industria.typeregistys.ModContainerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class ContainerLoadBlueprint extends Container {
	
	protected PlayerInventory playerInv;
	protected ItemStack blueprintItem;
	
	protected ContainerLoadBlueprint(int id) {
		super(ModContainerType.LOAD_BLUEPRINT, id);
	}
	
	public ContainerLoadBlueprint(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.LOAD_BLUEPRINT, id);
		this.playerInv = playerInv;
		this.blueprintItem = data.readItem();
	}
	
	public ContainerLoadBlueprint(int id, PlayerInventory playerInv, ItemStack blueprint) {
		super(ModContainerType.LOAD_BLUEPRINT, id);
		this.playerInv = playerInv;
		this.blueprintItem = blueprint;
	}
	
	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}
	
}
