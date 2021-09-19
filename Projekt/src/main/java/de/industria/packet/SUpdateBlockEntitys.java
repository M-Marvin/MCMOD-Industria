package de.industria.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.industria.util.DataWatcher;
import de.industria.util.DataWatcher.SendeableBlockEntityData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SUpdateBlockEntitys {

	public List<SendeableBlockEntityData> blockEntityData;
	
	public SUpdateBlockEntitys(List<SendeableBlockEntityData> blockEntityData) {
		this.blockEntityData = blockEntityData;
	}
	
	public SUpdateBlockEntitys(PacketBuffer buf) {
		int dataCount = buf.readInt();
		this.blockEntityData = new ArrayList<DataWatcher.SendeableBlockEntityData>();
		for (int i = 0; i < dataCount; i++) {
			this.blockEntityData.add(new SendeableBlockEntityData().readBuf(buf));
		}
	}
	
	public static void encode(SUpdateBlockEntitys packet, PacketBuffer buf) {
		buf.writeInt(packet.blockEntityData.size());
		for (SendeableBlockEntityData data : packet.blockEntityData) {
			data.writeBuf(buf);
		}
	}
	
	public static void handle(final SUpdateBlockEntitys packet, Supplier<NetworkEvent.Context> context) {
		
		NetworkEvent.Context ctx = context.get();
		DataWatcher.handleUpdate(packet.blockEntityData);	
		ctx.setPacketHandled(true);
		
	}
	
}
