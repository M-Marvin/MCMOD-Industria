package de.m_marvin.industria.core.electrics.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.electrics.engine.ServerElectricPackageHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * Tells the server if the player used the mains switch in the UI of an electric component
 */
public class CSwitchNetworkStatePackage {
	
	public final int containerId;
	public final boolean state;
	
	public CSwitchNetworkStatePackage(int containerId, boolean state) {
		this.containerId = containerId;
		this.state = state;
	}
	
	public int getContainerId() {
		return containerId;
	}
	
	public boolean getState() {
		return state;
	}
	
	public static void encode(CSwitchNetworkStatePackage msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.containerId);
		buff.writeBoolean(msg.state);
	}
	
	public static CSwitchNetworkStatePackage decode(FriendlyByteBuf buff) {
		int containerId = buff.readInt();
		boolean state = buff.readBoolean();
		return new CSwitchNetworkStatePackage(containerId, state);
	}
	
	public static void handle(CSwitchNetworkStatePackage msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerElectricPackageHandler.handlePlayerSwitchNetwork(msg, ctx.get());
			ctx.get().setPacketHandled(true);
		});
	}
	
}
