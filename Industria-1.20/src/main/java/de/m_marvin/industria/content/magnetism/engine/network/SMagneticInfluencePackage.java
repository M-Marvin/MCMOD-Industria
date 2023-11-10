package de.m_marvin.industria.content.magnetism.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.content.magnetism.engine.ClientMagnetismPackageHandler;
import de.m_marvin.industria.content.magnetism.types.MagneticFieldInfluence;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * Used to send added/removed updates from server to client
 **/
public class SMagneticInfluencePackage {
		
	public static class SCAddInfluencePackage {
		
		public final MagneticFieldInfluence influence;
		
		public SCAddInfluencePackage(MagneticFieldInfluence influence) {
			this.influence = influence;
		}
		
		public MagneticFieldInfluence getInfluence() {
			return influence;
		}
		
		public static void encode(SCAddInfluencePackage msg, FriendlyByteBuf buff) {
			// TODO better serialization
			buff.writeNbt(msg.influence.serialize());
		}
		
		public static SCAddInfluencePackage decode(FriendlyByteBuf buff) {
			// TODO better serialization
			MagneticFieldInfluence influence = MagneticFieldInfluence.deserialize(buff.readNbt());
			return new SCAddInfluencePackage(influence);
		}
		
		public static void handle(SCAddInfluencePackage msg, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				ClientMagnetismPackageHandler.handleAddInfluence(msg, ctx.get());
			});	
			ctx.get().setPacketHandled(true);	
		}
		
	}
	
	public static class SCRemoveInfluencePackage {
		
		public final BlockPos position;
		
		public SCRemoveInfluencePackage(BlockPos position) {
			this.position = position;
		}
		
		public BlockPos getPosition() {
			return this.position;
		}
		
		public static void encode(SCRemoveInfluencePackage msg, FriendlyByteBuf buff) {
			buff.writeBlockPos(msg.position);
		}
		
		public static SCRemoveInfluencePackage decode(FriendlyByteBuf buff) {
			BlockPos position = buff.readBlockPos();
			return new SCRemoveInfluencePackage(position);
		}
		
		public static void handle(SCRemoveInfluencePackage msg, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				ClientMagnetismPackageHandler.handleRemoveInfluence(msg, ctx.get());
			});
			ctx.get().setPacketHandled(true);
		}
		
	}
	
}
