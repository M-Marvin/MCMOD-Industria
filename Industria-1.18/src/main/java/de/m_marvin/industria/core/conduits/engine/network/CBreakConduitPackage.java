package de.m_marvin.industria.core.conduits.engine.network;

import java.util.function.Supplier;

import de.m_marvin.industria.core.conduits.engine.ConduitPos;
import de.m_marvin.industria.core.conduits.engine.ServerConduitPackageHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class CBreakConduitPackage {
	
	protected ConduitPos conduitPos;
	protected boolean dropItems;
	
	public ConduitPos getConduitPos() {
		return conduitPos;
	}
	
	public boolean isDropItems() {
		return dropItems;
	}
	
	public CBreakConduitPackage(ConduitPos conduitPos, boolean dropItems) {
		this.dropItems = dropItems;
		this.conduitPos = conduitPos;
	}
	
	public static void encode(CBreakConduitPackage msg, FriendlyByteBuf buff) {
		msg.conduitPos.write(buff);
		buff.writeBoolean(msg.dropItems);
	}
	
	public static CBreakConduitPackage decode(FriendlyByteBuf buff) {
		ConduitPos conduitPos = ConduitPos.read(buff);
		boolean dropItems = buff.readBoolean();
		return new CBreakConduitPackage(conduitPos, dropItems);
	}
	
	public static void handle(CBreakConduitPackage msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerConduitPackageHandler.handleBreakConduit(msg, ctx.get());
		});
	}
	
}
