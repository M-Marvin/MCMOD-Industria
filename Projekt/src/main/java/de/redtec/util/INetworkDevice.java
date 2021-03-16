package de.redtec.util;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.Unpooled;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public interface INetworkDevice {

	public NetworkDeviceType getNetworkType();
	public NetworkDeviceIP getIP(BlockPos pos, BlockState state, World world);
	public void setIP(NetworkDeviceIP ip, BlockPos pos, BlockState state, World world);
	public default List<BlockPos> getMultiBlockParts(World world, BlockPos pos, BlockState state) {
		return null;
	}
	
	public boolean canConectNetworkWire(IWorldReader world, BlockPos pos, Direction side);
	
	public void onMessageRecived(NetworkMessage message, World world, BlockPos pos, BlockState state);
	public default boolean isReciver(NetworkMessage message, World world, BlockPos pos, BlockState state) {
		return message.getTargetIP().equals(((INetworkDevice) state.getBlock()).getIP(pos, state, world));
	}
	
	public default void sendMessage(NetworkMessage message, World world, BlockPos pos, BlockState state) {
		// TODO
	}
	
	public static class NetworkDeviceIP {
		
		public static final NetworkDeviceIP DEFAULT = new NetworkDeviceIP((byte) 0, (byte) 0, (byte) 0, (byte) 0);
		
		byte[] ip = new byte[] {0, 0, 0, 0};
		
		public NetworkDeviceIP(byte b1, byte b2, byte b3, byte b4) {
			this.ip = new byte[] {b1, b2, b3, b4};
		}
		
		public NetworkDeviceIP(byte[] bytes) {
			this.ip = bytes;
		}
		
		@Override
		public String toString() {
			return "NetworkIP[" + ip[0] + "," + ip[1] + "," + ip[2] + "," + ip[3] + "]";
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof NetworkDeviceIP) {
				return	((NetworkDeviceIP) obj).ip[0] == ip[0] &&
						((NetworkDeviceIP) obj).ip[1] == ip[1] &&
						((NetworkDeviceIP) obj).ip[2] == ip[2] &&
						((NetworkDeviceIP) obj).ip[3] == ip[3];
			}
			return false;
		}
		
		public boolean isSameSub(NetworkDeviceIP other) {
			return	other.ip[0] == ip[0] &&
					other.ip[1] == ip[1] &&
					other.ip[2] == ip[2];
		}
		
		public byte getSubID() {
			return this.ip[3];
		}
		
		public byte[] getIP() {
			return this.ip;
		}

		public CompoundNBT writeNBT() {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putByteArray("ip", this.ip);
			return nbt;
		}
		
		public static NetworkDeviceIP read(CompoundNBT nbt) {
			byte[] ip = nbt.getByteArray("ip");
			return ip.length == 4 ? new NetworkDeviceIP(ip) : DEFAULT;
		}
		
		public void writeBuf(PacketBuffer buffer) {
			buffer.writeByteArray(this.ip);
		}
		
		public static NetworkDeviceIP readBuf(PacketBuffer buffer) {
			return new NetworkDeviceIP(buffer.readByteArray());
		}
		
	}
	
	public static enum NetworkDeviceType {
		
		WIRING(),DEVICE();
		
		public boolean canConnectWith(NetworkDeviceType type) {
			if (this == WIRING) {
				return true;
			} else {
				return type == WIRING;
			}
		}
		
	}
	
	public static class NetworkMessage {
		
		public static final byte BUFFER_STRING = 0x1;
		public static final byte BUFFER_BOOL = 0x2;
		public static final byte BUFFER_INT = 0x3;
		
		public NetworkDeviceIP senderIP;
		public NetworkDeviceIP targetIP;
		public PacketBuffer dataBuffer;
		public List<Byte> formatBuffer;
		
		public NetworkMessage() {
			this.dataBuffer = new PacketBuffer(Unpooled.buffer(256));
			this.formatBuffer = new ArrayList<Byte>();
		}
		
		public PacketBuffer getDataBuffer() {
			return dataBuffer;
		}
		
		public void setSenderIP(NetworkDeviceIP senderIP) {
			this.senderIP = senderIP;
		}
		
		public void setTargetIP(NetworkDeviceIP targetIP) {
			this.targetIP = targetIP;
		}
		
		public NetworkDeviceIP getSenderIP() {
			return senderIP;
		}
		
		public NetworkDeviceIP getTargetIP() {
			return targetIP;
		}
		
		public void writeBuf(PacketBuffer buffer) {
			this.senderIP.writeBuf(buffer);
			this.targetIP.writeBuf(buffer);
			byte[] barr = new byte[this.formatBuffer.size()];
			for (int i = 0; i < this.formatBuffer.size(); i++) barr[i] = this.formatBuffer.get(i);
			buffer.writeByteArray(barr);
			buffer.writeBytes(this.dataBuffer.array());
		}
		
		public static NetworkMessage readBuf(PacketBuffer buffer) {
			NetworkMessage msg = new NetworkMessage();
			msg.targetIP = NetworkDeviceIP.readBuf(buffer);
			msg.senderIP = NetworkDeviceIP.readBuf(buffer);
			byte[] barr = buffer.readByteArray();
			for (byte b : barr) msg.formatBuffer.add(b);
			msg.dataBuffer = new PacketBuffer(Unpooled.buffer(256));
			msg.dataBuffer.writeByteArray(buffer.readByteArray());
			return msg;
		}
		
		public Object[] getArgs() {
			List<Object> args = new ArrayList<Object>();
			for (byte b : this.formatBuffer) {
				if (b == BUFFER_BOOL) {
					args.add(this.dataBuffer.readBoolean());
				} else if (b == BUFFER_INT) {
					args.add(this.dataBuffer.readInt());
				} else if (b == BUFFER_STRING) {
					args.add(this.dataBuffer.readString());
				}
			}
			return args.toArray(new Object[] {});
		}
		
	}
	
}