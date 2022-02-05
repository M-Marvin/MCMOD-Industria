package de.industria.packet;

import java.util.function.Supplier;

import de.industria.util.handler.BlueprintFileManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class CSaveBlueprint {

	public String fileName;
	public BlockPos cornerA;
	public BlockPos cornerB;
		
	public CSaveBlueprint(String name, BlockPos cornerA, BlockPos cornerB) {
		this.fileName = name;
		this.cornerA = cornerA;
		this.cornerB = cornerB;
	}
	
	public CSaveBlueprint(PacketBuffer buf) {
		this.fileName = buf.readUtf();
		this.cornerA = buf.readBlockPos();
		this.cornerB = buf.readBlockPos();
	}
	
	public static void encode(CSaveBlueprint packet, PacketBuffer buf) {
		buf.writeUtf(packet.fileName);
		buf.writeBlockPos(packet.cornerA);
		buf.writeBlockPos(packet.cornerB);
	}
	
	public static void handle(final CSaveBlueprint packet, Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			ServerWorld world = context.get().getSender().getLevel();
			BlueprintFileManager.saveNewStrukture(world, packet.cornerA, packet.cornerB, packet.fileName);
		});
		context.get().setPacketHandled(true);		
	}
	
}
