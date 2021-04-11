package de.industria.gui;

import de.industria.typeregistys.ModContainerType;
import de.industria.util.blockfeatures.INetworkDevice.NetworkDeviceIP;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ContainerNetworkConfigurator extends Container {
	
	protected PlayerInventory playerInv;
	protected BlockPos pos;
	protected NetworkDeviceIP ip;
	
	public ContainerNetworkConfigurator(int id, PlayerInventory playerInv, PacketBuffer data) {
		this(id, playerInv, data.readBlockPos(), NetworkDeviceIP.ipFromString(data.readString()));
	}
	
	public ContainerNetworkConfigurator(int id, PlayerInventory playerInv, BlockPos pos, NetworkDeviceIP ip) {
		super(ModContainerType.NETWORK_CONFIGURATOR, id);
		this.playerInv = playerInv;
		this.pos = pos;
		this.ip = ip;
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}
	
	public BlockPos getPos() {
		return pos;
	}

	public NetworkDeviceIP getIP() {
		return ip;
	}
	
}
