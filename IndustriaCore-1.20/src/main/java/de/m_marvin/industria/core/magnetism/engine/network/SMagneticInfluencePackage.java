package de.m_marvin.industria.core.magnetism.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.magnetism.engine.ClientMagnetismPackageHandler;
import de.m_marvin.industria.core.magnetism.types.MagneticFieldInfluence;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * Used to send added/removed updates from server to client
 **/
public class SMagneticInfluencePackage {
		
	public static class SAddInfluencePackage {
		
		public final MagneticFieldInfluence influence;
		
		public SAddInfluencePackage(MagneticFieldInfluence influence) {
			this.influence = influence;
		}
		
		public MagneticFieldInfluence getInfluence() {
			return influence;
		}
		
		public static void encode(SAddInfluencePackage msg, FriendlyByteBuf buff) {
			// TODO better serialization
			buff.writeNbt(msg.influence.serialize());
		}
		
		public static SAddInfluencePackage decode(FriendlyByteBuf buff) {
			// TODO better serialization
			MagneticFieldInfluence influence = MagneticFieldInfluence.deserialize(buff.readNbt());
			return new SAddInfluencePackage(influence);
		}
		
		public static void handle(SAddInfluencePackage msg, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				ClientMagnetismPackageHandler.handleAddInfluence(msg, ctx.get());
			});	
			ctx.get().setPacketHandled(true);	
		}
		
	}
	
	public static class SRemoveInfluencePackage {
		
		public final BlockPos position;
		
		public SRemoveInfluencePackage(BlockPos position) {
			this.position = position;
		}
		
		public BlockPos getPosition() {
			return this.position;
		}
		
		public static void encode(SRemoveInfluencePackage msg, FriendlyByteBuf buff) {
			buff.writeBlockPos(msg.position);
		}
		
		public static SRemoveInfluencePackage decode(FriendlyByteBuf buff) {
			BlockPos position = buff.readBlockPos();
			return new SRemoveInfluencePackage(position);
		}
		
		public static void handle(SRemoveInfluencePackage msg, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				ClientMagnetismPackageHandler.handleRemoveInfluence(msg, ctx.get());
			});
			ctx.get().setPacketHandled(true);
		}
		
	}
	
}
