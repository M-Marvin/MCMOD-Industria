package de.industria.packet;

import java.util.function.Supplier;

import de.industria.tileentity.TileEntityJigsaw;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class CGenerateJigsaw {
	
	public BlockPos pos;
	public boolean keepJigsaws;
	public int levels;
	
	public CGenerateJigsaw(BlockPos pos, int levels, boolean keepJigsaws) {
		this.pos = pos;
		this.levels = levels;
		this.keepJigsaws = keepJigsaws;
	}
	
	public CGenerateJigsaw(PacketBuffer buf) {
		this.pos = buf.readBlockPos();
		this.levels = buf.readInt();
		this.keepJigsaws = buf.readBoolean();
	}
		
	public static void encode(CGenerateJigsaw packet, PacketBuffer buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeInt(packet.levels);
		buf.writeBoolean(packet.keepJigsaws);
	}
	
	@SuppressWarnings("resource")
	public static void handle(final CGenerateJigsaw packet, Supplier<NetworkEvent.Context> context) {
		
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			
			World world = ctx.getSender().world;
			TileEntity contactTileEntity = world.getTileEntity(packet.pos);
			
			if (contactTileEntity instanceof TileEntityJigsaw) {
				
				((TileEntityJigsaw) contactTileEntity).generateStructure(packet.keepJigsaws, packet.levels, contactTileEntity.getWorld().rand);
				
			}
			
		});
		ctx.setPacketHandled(true);
		
	}
	
}
