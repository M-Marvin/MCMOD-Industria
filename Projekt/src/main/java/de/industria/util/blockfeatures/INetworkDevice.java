package de.industria.util.blockfeatures;

import java.util.ArrayList;
import java.util.List;

import de.industria.util.handler.NetworkWireHandler;
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
		message.setSenderIP(this.getIP(pos, state, world), world.getGameTime());
		NetworkWireHandler.getHandlerForWorld(world).sendMessage(world, pos, message);
	}

	public default boolean isNetworkSwitchClosed(World world, BlockPos scannPos, BlockState state) {
		return true;
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

		public NetworkDeviceIP copy() {
			return new NetworkDeviceIP(this.ip[0], this.ip[1], this.ip[2], this.ip[3]);
		}

		public String getString() {
			return this.ip[0] + "." + this.ip[1] + "." + this.ip[2] + "." + this.ip[3];
		}
		
		public static NetworkDeviceIP ipFromString(String s) {
			String[] ss = s.split("\\.");
			byte[] ipBytes = new byte[4];
			try {
				ipBytes[0] = Byte.parseByte(ss[0]);
				ipBytes[1] = Byte.parseByte(ss[1]);
				ipBytes[2] = Byte.parseByte(ss[2]);
				ipBytes[3] = Byte.parseByte(ss[3]);
			} catch (Exception e) {
				return null;
			}
			return new NetworkDeviceIP(ipBytes);
		}
		
		@Override
		public int hashCode() {
			return this.getString().hashCode();
		}
		
	}
	
	public static enum NetworkDeviceType {
		
		WIRING(),DEVICE(), SWITCH(); // SWITCH NOT IMPLEMENTED YET
		
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
		public List<Object> data;
		public long sendTime;
		
		public NetworkMessage() {
			this.data = new ArrayList<Object>();
		}
		
		public void setSenderIP(NetworkDeviceIP senderIP, long gameTime) {
			this.senderIP = senderIP;
			this.sendTime = gameTime;
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
		
		public Object[] getArgs() {
			return data.toArray(new Object[] {});
		}
		
		public NetworkMessage copy() {
			NetworkMessage message = new NetworkMessage();
			message.targetIP = this.targetIP.copy();
			message.senderIP = this.senderIP.copy();
			for (Object o : this.data) message.data.add(o);
			message.sendTime = this.sendTime;
			return message;
		}
		
		public String readString() {
			if (this.data.size() == 0) return "";
			if (this.data.get(0) instanceof String) {
				return (String) this.data.remove(0);
			}
			return "";
		}
		public int readInt() {
			if (this.data.size() == 0) return 0;
			if (this.data.get(0) instanceof Integer) {
				return (Integer) this.data.remove(0);
			}
			return 0;
		}
		public boolean readBoolean() {
			if (this.data.size() == 0) return false;
			if (this.data.get(0) instanceof Boolean) {
				return (Boolean) this.data.remove(0);
			}
			return false;
		}
		
		public void writeString(String string) {
			this.data.add(string);
		}
		public void writeInt(int integer) {
			this.data.add(integer);
		}
		public void writeBoolean(boolean bool) {
			this.data.add(bool);
		}
		
		public int getArgCount() {
			return this.data.size();
		}
		
		@Override
		public String toString() {
			String string = "NetworkMessage[target=" + this.targetIP + ",sender=" + this.senderIP + ",args={";
			for (Object o : this.getArgs()) {
				string = string + o + ",";
			}
			return string.substring(0, string.length() - 1) + "}]";
		}

		public long getSendTime() {
			return sendTime;
		}
		
		public void setSendTime(long sendTime) {
			this.sendTime = sendTime;
		}
		
		@Override
		public int hashCode() {
			return this.toString().hashCode();
		}
		
	}
	
}