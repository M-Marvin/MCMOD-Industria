package de.redtec.packet;

import java.util.function.Supplier;

import de.redtec.RedTec;
import de.redtec.tileentity.TileEntitySignalProcessorContact;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class CEditProcessorCodePacket {
	
	private ItemStack processorStack;
	private BlockPos pos;

	public CEditProcessorCodePacket(BlockPos pos, ItemStack processorStack) {
		this.pos = pos;
		this.processorStack = processorStack.copy();
	}
	
	public CEditProcessorCodePacket(PacketBuffer buf) {
		this.processorStack = buf.readItemStack();
		this.pos = buf.readBlockPos();
	}
		
	public static void encode(CEditProcessorCodePacket packet, PacketBuffer buf) {
		buf.writeItemStack(packet.processorStack);
		buf.writeBlockPos(packet.pos);
	}
	
	public static void handle(final CEditProcessorCodePacket packet, Supplier<NetworkEvent.Context> context) {
		
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			
			World world = ctx.getSender().world;
			TileEntity contactTileEntity = world.getTileEntity(packet.pos);
			
			if (contactTileEntity instanceof TileEntitySignalProcessorContact) {
				
				boolean flag = ((TileEntitySignalProcessorContact) contactTileEntity).setProcessorStack(packet.processorStack);
				
				if (!flag) RedTec.LOGGER.error("Invalid Processor Item in CEditProcessorPacket @ " + packet.pos);
				
			}
			
		});
		ctx.setPacketHandled(true);
		
	}
	
}
