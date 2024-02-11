package de.m_marvin.industria.core.parametrics.engine.network;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import de.m_marvin.industria.core.parametrics.BlockParametrics;
import de.m_marvin.industria.core.parametrics.engine.ClientParametricsPackageHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * Tells the client about existing and removed conduits, for example if a chunk gets loaded on client side.
 * It does not trigger any events on the client side and only updates the conduit-list.
 */
public class SSyncParametricsPackage {
	
	public final Collection<BlockParametrics> parametrics;
	
	public SSyncParametricsPackage(Collection<BlockParametrics> parametrics) {
		this.parametrics = parametrics;
	}
	
	public Collection<BlockParametrics> getParametrics() {
		return this.parametrics;
	}
	
	public static void encode(SSyncParametricsPackage msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.parametrics.size());
		for (BlockParametrics entry : msg.parametrics) {
			entry.writeToBuffer(buff);
		}
	}
	
	public static SSyncParametricsPackage decode(FriendlyByteBuf buff) {
		Set<BlockParametrics> parametrics = new HashSet<>();
		int entryCount = buff.readInt();
		for (int i = 0; i < entryCount; i++) {
			parametrics.add(BlockParametrics.readFromBuffer(buff));
		}
		return new SSyncParametricsPackage(parametrics);
	}
	
	public static void handle(SSyncParametricsPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientParametricsPackageHandler.handleSyncParametricsFromServer(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
