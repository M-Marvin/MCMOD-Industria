package de.m_marvin.industria.core.electrics.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.electrics.engine.ClientElectricPackageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the client that a networks needs to be updated
 */
public class SUpdateNetworkPackage {
	
	public final BlockPos pos;
	
	public SUpdateNetworkPackage(BlockPos pos) {
		this.pos = pos;
	}
	
	public BlockPos getPos() {
		return pos;
	}
	
	public static void encode(SUpdateNetworkPackage msg, FriendlyByteBuf buff) {
		buff.writeBlockPos(msg.pos);
	}
	
	public static SUpdateNetworkPackage decode(FriendlyByteBuf buff) {
		BlockPos pos = buff.readBlockPos();
		return new SUpdateNetworkPackage(pos);
	}
	
	public static void handle(SUpdateNetworkPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientElectricPackageHandler.handleUpdateNetwork(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
