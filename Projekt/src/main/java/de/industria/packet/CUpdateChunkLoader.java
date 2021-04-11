package de.industria.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.industria.tileentity.TileEntityMChunkLoader;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class CUpdateChunkLoader {
	
	private List<ChunkPos> activeChunks;
	private BlockPos pos;
	
	public CUpdateChunkLoader(BlockPos pos, List<ChunkPos> activeChunks) {
		this.pos = pos;
		this.activeChunks = activeChunks;
	}
	
	public CUpdateChunkLoader(PacketBuffer buf) {
		this.pos = buf.readBlockPos();
		int chunks = buf.readInt();
		this.activeChunks = new ArrayList<ChunkPos>();
		for (int i = 0; i < chunks; i++) {
			this.activeChunks.add(new ChunkPos(buf.readLong()));
		}
	}
		
	public static void encode(CUpdateChunkLoader packet, PacketBuffer buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeInt(packet.activeChunks.size());
		packet.activeChunks.forEach((chunk) -> buf.writeLong(chunk.asLong()));
	}
	
	public static void handle(final CUpdateChunkLoader packet, Supplier<NetworkEvent.Context> context) {
		
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			
			World world = ctx.getSender().world;
			BlockPos devicePos = packet.pos;
			TileEntity tileEntity = world.getTileEntity(devicePos);
			
			if (tileEntity instanceof TileEntityMChunkLoader) {
				
				((TileEntityMChunkLoader) tileEntity).activeRelativeChunks = packet.activeChunks;
				
			}
			
		});
		ctx.setPacketHandled(true);
		
	}
	
}