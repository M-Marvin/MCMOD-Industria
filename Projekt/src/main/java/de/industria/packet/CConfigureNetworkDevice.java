package de.industria.packet;

import java.util.function.Supplier;

import de.industria.util.blockfeatures.INetworkDevice;
import de.industria.util.blockfeatures.INetworkDevice.NetworkDeviceIP;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class CConfigureNetworkDevice {
	
	private NetworkDeviceIP deviceIp;
	private BlockPos pos;
	
	public CConfigureNetworkDevice(BlockPos pos, NetworkDeviceIP deviceIp) {
		this.pos = pos;
		this.deviceIp = deviceIp;
	}
	
	public CConfigureNetworkDevice(PacketBuffer buf) {
		this.pos = buf.readBlockPos();
		this.deviceIp = new NetworkDeviceIP(buf.readByteArray());
	}
		
	public static void encode(CConfigureNetworkDevice packet, PacketBuffer buf) {
		buf.writeBlockPos(packet.pos);
		buf.writeByteArray(packet.deviceIp.getIP());
	}
	
	public static void handle(final CConfigureNetworkDevice packet, Supplier<NetworkEvent.Context> context) {
		
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			
			World world = ctx.getSender().level;
			BlockPos devicePos = packet.pos;
			BlockState state = world.getBlockState(devicePos);
			
			if (state.getBlock() instanceof INetworkDevice) {
				
				((INetworkDevice) state.getBlock()).setIP(packet.deviceIp, devicePos, state, world);
				
			}
			
		});
		ctx.setPacketHandled(true);
		
	}
	
}
