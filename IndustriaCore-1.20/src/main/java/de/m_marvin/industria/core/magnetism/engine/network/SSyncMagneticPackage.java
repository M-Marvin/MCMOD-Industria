package de.m_marvin.industria.core.magnetism.engine.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.m_marvin.industria.core.magnetism.engine.ClientMagnetismPackageHandler;
import de.m_marvin.industria.core.magnetism.types.MagneticField;
import de.m_marvin.industria.core.util.types.SyncRequestType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;

/**
 * Tells the client about existing and removed magnetic fields, for example if a chunk gets loaded on client side.
 * It does not trigger any events on the client side and only updates the field-lists.
 */
public class SSyncMagneticPackage {
	
	public final ChunkPos chunkPos;
	public final List<MagneticField> fields;
	public final SyncRequestType request;
	
	public SSyncMagneticPackage(List<MagneticField> fields, ChunkPos targetChunk, SyncRequestType request) {
		this.fields = fields;
		this.chunkPos = targetChunk;
		this.request = request;
	}
	
	public SSyncMagneticPackage(MagneticField fields, ChunkPos targetChunk, SyncRequestType request) {
		this.fields = new ArrayList<MagneticField>();
		this.fields.add(fields);
		this.chunkPos = targetChunk;
		this.request = request;
	}
	
	public ChunkPos getChunkPos() {
		return chunkPos;
	}
	
	public SyncRequestType getRquest() {
		return request;
	}
	
	public static void encode(SSyncMagneticPackage msg, FriendlyByteBuf buff) {
		buff.writeEnum(msg.request);
		buff.writeChunkPos(msg.chunkPos);
		buff.writeInt(msg.fields.size());
		for (MagneticField field : msg.fields) {
			// TODO better serialization
			buff.writeNbt(field.serialize());
		}
	}
	
	public static SSyncMagneticPackage decode(FriendlyByteBuf buff) {
		SyncRequestType status = buff.readEnum(SyncRequestType.class);
		ChunkPos chunkPos = buff.readChunkPos();
		int count = buff.readInt();
		List<MagneticField> fields = new ArrayList<MagneticField>();
		for (int i = 0; i < count; i++) {
			// TODO better serialization
			MagneticField field = MagneticField.deserialize(buff.readNbt());
			fields.add(field);
		}
		return new SSyncMagneticPackage(fields, chunkPos, status);
	}
	
	public static void handle(SSyncMagneticPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientMagnetismPackageHandler.handleSyncMagneticFromServer(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
