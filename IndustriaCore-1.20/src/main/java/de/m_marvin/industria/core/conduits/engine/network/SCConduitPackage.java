package de.m_marvin.industria.core.conduits.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.engine.ClientConduitPackageHandler;
import de.m_marvin.industria.core.conduits.engine.ServerConduitPackageHandler;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.registries.Conduits;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

/**
 * Used to send placed/removed updates from server to client and from client to server (bi-directional package)
 **/
public class SCConduitPackage {
		
	public static class SCPlaceConduitPackage {
		
		public final ConduitPos position;
		public final Conduit conduit;
		public final double length;
		
		public SCPlaceConduitPackage(ConduitPos position, Conduit conduit, double length) {
			this.position = position;
			this.conduit = conduit;
			this.length = length;
		}
		
		public ConduitPos getPosition() {
			return position;
		}
		
		public Conduit getConduit() {
			return conduit;
		}
		
		public double getLength() {
			return length;
		}
		
		public static void encode(SCPlaceConduitPackage msg, FriendlyByteBuf buff) {
			msg.position.write(buff);
			buff.writeResourceLocation(Conduits.CONDUITS_REGISTRY.get().getKey(msg.conduit));
			buff.writeDouble(msg.length);
		}
		
		public static SCPlaceConduitPackage decode(FriendlyByteBuf buff) {
			ConduitPos position = ConduitPos.read(buff);
			Conduit conduit = Conduits.CONDUITS_REGISTRY.get().getValue(buff.readResourceLocation());
			double length = buff.readDouble();
			return new SCPlaceConduitPackage(position, conduit, length);
		}
		
		@SuppressWarnings("deprecation")
		public static void handle(SCPlaceConduitPackage msg, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> ServerConduitPackageHandler.handlePlaceConduit(msg, ctx.get()));
				DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> ClientConduitPackageHandler.handlePlaceConduit(msg, ctx.get()));
			});	
			ctx.get().setPacketHandled(true);	
		}
		
	}
	
	public static class SCBreakConduitPackage {
		
		public final ConduitPos position;
		public final boolean dropItems;
		
		public SCBreakConduitPackage(ConduitPos position, boolean dropItems) {
			this.position = position;
			this.dropItems = dropItems;
		}

		public boolean dropItems() {
			return this.dropItems;
		}

		public ConduitPos getPosition() {
			return this.position;
		}
		
		public static void encode(SCBreakConduitPackage msg, FriendlyByteBuf buff) {
			msg.position.write(buff);
			buff.writeBoolean(msg.dropItems);
		}
		
		public static SCBreakConduitPackage decode(FriendlyByteBuf buff) {
			ConduitPos position = ConduitPos.read(buff);
			boolean dropItems = buff.readBoolean();
			return new SCBreakConduitPackage(position, dropItems);
		}
		
		public static void handle(SCBreakConduitPackage msg, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
					ClientConduitPackageHandler.handleRemoveConduit(msg, ctx.get());
				} else if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
					ServerConduitPackageHandler.handleRemoveConduit(msg, ctx.get());
				}
			});
			ctx.get().setPacketHandled(true);
		}
		
	}
	
}
