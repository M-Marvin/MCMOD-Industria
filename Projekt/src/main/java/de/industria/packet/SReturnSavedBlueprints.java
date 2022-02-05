package de.industria.packet;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Supplier;

import de.industria.gui.ScreenLoadBlueprint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SReturnSavedBlueprints {
	
	public String[] aviable;
	public HashMap<String, BlockPos> sizeMap;
	
	public SReturnSavedBlueprints(String[] aviableBlueprints, HashMap<String, BlockPos> sizeMap) {
		this.aviable = aviableBlueprints;
		this.sizeMap = sizeMap;
	}
	
	public SReturnSavedBlueprints(PacketBuffer buf) {
		int count = buf.readInt();
		this.aviable = new String[count];
		for (int i = 0; i < count; i++) {
			this.aviable[i] = buf.readUtf();
		}
		count = buf.readInt();
		this.sizeMap = new HashMap<String, BlockPos>();
		for (int i = 0; i < count; i++) {
			String file = buf.readUtf();
			BlockPos size = buf.readBlockPos();
			this.sizeMap.put(file, size);
		}
	}
	
	public static void encode(SReturnSavedBlueprints packet, PacketBuffer buf) {
		buf.writeInt(packet.aviable.length);
		for (String s : packet.aviable) {
			buf.writeUtf(s);
		}
		buf.writeInt(packet.sizeMap.size());
		for (Entry<String, BlockPos> entry : packet.sizeMap.entrySet()) {
			buf.writeUtf(entry.getKey());
			buf.writeBlockPos(entry.getValue());
		}
	}
	
	@SuppressWarnings("resource")
	public static void handle(final SReturnSavedBlueprints packet, Supplier<NetworkEvent.Context> context) {
		
		context.get().enqueueWork(() -> {
			Screen screen = Minecraft.getInstance().screen;
			if (screen instanceof ScreenLoadBlueprint) {
				((ScreenLoadBlueprint) screen).setAviableBlueprints(packet.aviable, packet.sizeMap);
			}
		});
		context.get().setPacketHandled(true);
		
	}
	
}
