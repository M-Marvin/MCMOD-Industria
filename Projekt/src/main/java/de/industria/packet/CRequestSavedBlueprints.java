package de.industria.packet;

import java.util.HashMap;
import java.util.function.Supplier;

import de.industria.Industria;
import de.industria.util.handler.BlueprintFileManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class CRequestSavedBlueprints {
	
	public CRequestSavedBlueprints() {
	}
	
	public CRequestSavedBlueprints(PacketBuffer buf) {
	}
	
	public static void encode(CRequestSavedBlueprints packet, PacketBuffer buf) {
	}
	
	public static void handle(final CRequestSavedBlueprints packet, Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			ServerWorld world = context.get().getSender().getLevel();
			String[] aviable = BlueprintFileManager.getAviableStructures(world);
			HashMap<String, BlockPos> sizeMap = BlueprintFileManager.getBlueprintSize(world, aviable);
			Industria.NETWORK.sendTo(new SReturnSavedBlueprints(aviable, sizeMap), context.get().getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
		});
		context.get().setPacketHandled(true);		
	}
	
}
