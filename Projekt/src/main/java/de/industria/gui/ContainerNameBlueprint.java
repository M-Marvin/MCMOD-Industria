package de.industria.gui;

import de.industria.typeregistys.ModContainerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class ContainerNameBlueprint extends Container {
	
	protected PlayerInventory playerInv;
	protected ItemStack blueprintItem;
	
	protected ContainerNameBlueprint(int id) {
		super(ModContainerType.NAME_BLUEPRINT, id);
	}
	
	public ContainerNameBlueprint(int id, PlayerInventory playerInv, PacketBuffer data) {
		super(ModContainerType.NAME_BLUEPRINT, id);
		this.playerInv = playerInv;
		this.blueprintItem = data.readItem();
	}
	
	public ContainerNameBlueprint(int id, PlayerInventory playerInv, ItemStack blueprint) {
		super(ModContainerType.NAME_BLUEPRINT, id);
		this.playerInv = playerInv;
		this.blueprintItem = blueprint;
	}
	
	@Override
	public boolean stillValid(PlayerEntity player) {
		return true;
	}
	
}
