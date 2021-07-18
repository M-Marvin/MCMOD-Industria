package de.industria.packet;

import java.util.function.Supplier;

import de.industria.tileentity.TileEntityNComputer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class CEditComputerCode {
	
	private String[] code;
	private boolean saveClicked;
	private boolean runClicked;
	private BlockPos pos;
	
	public CEditComputerCode(BlockPos pos, boolean runClicked, boolean saveClicked, String[] code) {
		this.pos = pos;
		this.code = code;
		this.runClicked = runClicked;
		this.saveClicked = saveClicked;
	}
	
	public CEditComputerCode(PacketBuffer buf) {
		this.pos = buf.readBlockPos();
		this.code = new String[10];
		for (int i = 0; i < 10; i++) {
			this.code[i] = buf.readUtf();
		}
		this.saveClicked = buf.readBoolean();
		this.runClicked = buf.readBoolean();
	}
		
	public static void encode(CEditComputerCode packet, PacketBuffer buf) {
		buf.writeBlockPos(packet.pos);
		for (String s : packet.code) {
			buf.writeUtf(s);
		}
		buf.writeBoolean(packet.saveClicked);
		buf.writeBoolean(packet.runClicked);
	}
	
	public static void handle(final CEditComputerCode packet, Supplier<NetworkEvent.Context> context) {
		
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			
			World world = ctx.getSender().level;
			TileEntity contactTileEntity = world.getBlockEntity(packet.pos);
			
			if (contactTileEntity instanceof TileEntityNComputer) {
				
				((TileEntityNComputer) contactTileEntity).onClientUpdate(packet.runClicked, packet.saveClicked, packet.code);
				
			}
			
		});
		ctx.setPacketHandled(true);
		
	}
	
}
