package de.industria.packet;

import java.util.function.Supplier;

import de.industria.util.DataWatcher;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class CRequestBlockEntityUpdates {
	
	public boolean updateAll;
	public ChunkPos chunk;
	
	public CRequestBlockEntityUpdates(ChunkPos chunk) {
		this.chunk = chunk;
	}

	public CRequestBlockEntityUpdates() {
		this.updateAll = true;
	}
	
	public CRequestBlockEntityUpdates(PacketBuffer buf) {
		this.updateAll = buf.readBoolean();
		if (!this.updateAll) this.chunk = new ChunkPos(buf.readLong());
	}
	
	public static void encode(CRequestBlockEntityUpdates packet, PacketBuffer buf) {
		buf.writeBoolean(packet.updateAll);
		if (!packet.updateAll) buf.writeLong(packet.chunk.toLong());
	}
	
	public static void handle(final CRequestBlockEntityUpdates packet, Supplier<NetworkEvent.Context> context) {
		if (packet.updateAll) {
			DataWatcher.requestChunkUpdate();
		} else {
			DataWatcher.requestChunkUpdate(packet.chunk);
		}
	}
	
}
