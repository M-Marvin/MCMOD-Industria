package de.m_marvin.industria.core.util.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.util.container.AbstractBlockDataContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * Tells the server if the player changed the context in an data container slot, triggered by the {@link AbstractBlockDataContainerMenu#sendDataToClient()} function
 */
public class CContainerDataChanged {
	
	public final int containerId;
	public final int[] data;
	
	public CContainerDataChanged(int containerId, int[] data) {
		this.containerId = containerId;
		this.data = data;
	}
	
	public int getContainerId() {
		return containerId;
	}
	
	public int[] getData() {
		return data;
	}
	
	public static void encode(CContainerDataChanged msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.containerId);
		buff.writeVarIntArray(msg.data);
	}
	
	public static CContainerDataChanged decode(FriendlyByteBuf buff) {
		int containerId = buff.readInt();
		int[] data = buff.readVarIntArray();
		return new CContainerDataChanged(containerId, data);
	}
	
	public static void handle(CContainerDataChanged msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender().containerMenu instanceof AbstractBlockDataContainerMenu menu && menu.containerId == msg.containerId)
				menu.getDataContainer().fromRawData(msg.data);
			ctx.get().setPacketHandled(true);
		});
	}
	
}
