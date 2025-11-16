package de.m_marvin.industria.core.conduits.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.ConduitUtility;
import de.m_marvin.industria.core.conduits.engine.ClientConduitPackageHandler;
import de.m_marvin.industria.core.conduits.types.ConduitPos;
import de.m_marvin.industria.core.conduits.types.conduits.Conduit;
import de.m_marvin.industria.core.registries.Conduits;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * Notifies the client about changes to the nbt data of the ConduitEntiy on the server side.
 * This package is only send if requested trough {@link ConduitUtility#triggerClientSync(net.minecraft.world.level.Level, ConduitPos)}
 */
public class SUpdateConduitEntity {
	
	public final ConduitPos position;
	public final Conduit conduit;
	public final CompoundTag updateTag;
	
	public SUpdateConduitEntity(ConduitPos position, Conduit conduit, CompoundTag updateTag) {
		this.position = position;
		this.conduit = conduit;
		this.updateTag = updateTag;
	}
	
	public ConduitPos getPosition() {
		return position;
	}
	
	public Conduit getConduit() {
		return conduit;
	}
	
	public CompoundTag getUpdateTag() {
		return updateTag;
	}
	
	public static void encode(SUpdateConduitEntity msg, FriendlyByteBuf buff) {
		msg.position.writeBuff(buff);
		buff.writeResourceLocation(Conduits.CONDUITS_REGISTRY.get().getKey(msg.conduit));
		buff.writeNbt(msg.updateTag);
	}
	
	public static SUpdateConduitEntity decode(FriendlyByteBuf buff) {
		ConduitPos position = ConduitPos.readBuff(buff);
		Conduit conduit = Conduits.CONDUITS_REGISTRY.get().getValue(buff.readResourceLocation());
		CompoundTag updateTag = buff.readNbt();
		return new SUpdateConduitEntity(position, conduit, updateTag);
	}
	
	public static void handle(SUpdateConduitEntity msg, Supplier<NetworkEvent.Context> ctx) {
		
		ctx.get().enqueueWork(() -> {
			ClientConduitPackageHandler.handleUpdateConduitEntityFromServer(msg, ctx.get());
		});
		ctx.get().setPacketHandled(true);
		
	}
	
}
