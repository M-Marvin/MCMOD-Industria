package de.industria.packet;

import java.util.function.Supplier;

import de.industria.util.handler.ElectricityNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SSendENHandeler {
	
	public CompoundNBT enTag;
	
	public SSendENHandeler(ElectricityNetworkHandler handler) {
		this.enTag = handler.makeUpdateTag();
	}
	
	public SSendENHandeler(PacketBuffer buf) {
		this.enTag = buf.readNbt();
	}
	
	public static void encode(SSendENHandeler packet, PacketBuffer buf) {
		buf.writeNbt(packet.enTag);
	}
	
	@SuppressWarnings("resource")
	public static void handle(final SSendENHandeler packet, Supplier<NetworkEvent.Context> context) {
		
		NetworkEvent.Context ctx = context.get();

		ClientWorld world = Minecraft.getInstance().level;
		ElectricityNetworkHandler handler = ElectricityNetworkHandler.getHandlerForWorld(world);
		
		if (!handler.isServerInstace()) handler.deserializeNBT(packet.enTag);
		
		ctx.setPacketHandled(true);
		
	}
	
}
