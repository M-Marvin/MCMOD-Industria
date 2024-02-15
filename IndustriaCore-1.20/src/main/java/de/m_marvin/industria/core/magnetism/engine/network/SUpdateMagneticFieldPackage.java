package de.m_marvin.industria.core.magnetism.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.magnetism.engine.ClientMagnetismPackageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/*
 * Tells the client that a magnetic field needs to be updated
 */
public class SUpdateMagneticFieldPackage {
	
	public final BlockPos pos;
	
	public SUpdateMagneticFieldPackage(BlockPos pos) {
		this.pos = pos;
	}
	
	public BlockPos getPos() {
		return pos;
	}
	
	public static void encode(SUpdateMagneticFieldPackage msg, FriendlyByteBuf buff) {
		buff.writeBlockPos(msg.pos);
	}
	
	public static SUpdateMagneticFieldPackage decode(FriendlyByteBuf buff) {
		BlockPos pos = buff.readBlockPos();
		return new SUpdateMagneticFieldPackage(pos);
	}
	
	public static void handle(SUpdateMagneticFieldPackage msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientMagnetismPackageHandler.handleUpdateMagneticField(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
