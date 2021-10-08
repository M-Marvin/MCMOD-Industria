package de.industria.packet;

import java.util.function.Supplier;

import de.industria.util.DataWatcher;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class CRequestBlockEntityUpdates {

	public ChunkPos chunk;
	
	public CRequestBlockEntityUpdates(ChunkPos chunk) {
		this.chunk = chunk;
	}
	
	public CRequestBlockEntityUpdates(PacketBuffer buf) {
		this.chunk = new ChunkPos(buf.readLong());
	}
	
	public static void encode(CRequestBlockEntityUpdates packet, PacketBuffer buf) {
		buf.writeLong(packet.chunk.toLong());
	}
	
	public static void handle(final CRequestBlockEntityUpdates packet, Supplier<NetworkEvent.Context> context) {
		DataWatcher.requestChunkUpdate(packet.chunk);
	}
	
}
