package de.m_marvin.industria.network;

import java.util.function.Supplier;

import de.m_marvin.industria.util.ServerPackageHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the server that a conduit got placed by a client
 */
public class CChangeNodesPerBlockPackage {
	
	public int nodesPerBlock;
	
	public CChangeNodesPerBlockPackage(int nodesPerBlock) {
		this.nodesPerBlock = nodesPerBlock;
	}
		
	public int getNodesPerBlock() {
		return nodesPerBlock;
	}
	
	public static void encode(CChangeNodesPerBlockPackage msg, FriendlyByteBuf buff) {
		buff.writeInt(msg.nodesPerBlock);
	}
	
	public static CChangeNodesPerBlockPackage decode(FriendlyByteBuf buff) {
		int nodesPerBlock = buff.readInt();
		return new CChangeNodesPerBlockPackage(nodesPerBlock);
	}
	
	public static void handle(CChangeNodesPerBlockPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
				ServerPackageHandler.handleChangeNodesPerBlock(msg, ctx.get());
			});
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
