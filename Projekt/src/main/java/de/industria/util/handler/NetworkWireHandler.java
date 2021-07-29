package de.industria.util.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.industria.util.blockfeatures.ITENetworkDevice;
import de.industria.util.blockfeatures.ITENetworkDevice.NetworkDeviceType;
import de.industria.util.blockfeatures.ITENetworkDevice.NetworkMessage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public class NetworkWireHandler extends WorldSavedData {

	protected static NetworkWireHandler clientInstance;
	
	protected boolean isServerInstace;
	
	public NetworkWireHandler() {
		this(true);
	}
	
	public NetworkWireHandler(boolean serverInstance) {
		super("dataNetworks");
		this.isServerInstace = serverInstance;
	}
	
	public static NetworkWireHandler getHandlerForWorld(IWorld world) {
		
		if (!world.isClientSide()) {
			DimensionSavedDataManager storage = ((ServerWorld) world).getDataStorage();
			NetworkWireHandler handler = storage.computeIfAbsent(NetworkWireHandler::new, "dataNetworks");
			return handler;
		} else {
			if (clientInstance == null) clientInstance = new NetworkWireHandler(false);
			return clientInstance;
		}
		
	}
	
	public boolean isServerInstace() {
		return isServerInstace;
	}
	
	public void sendMessage(World world, BlockPos pos, NetworkMessage message) {
		
		DataNetwork network = getNetwork(world, pos);
		
		if (network.contains(pos)) {
			
			for (Entry<BlockPos, List<Direction>> deviceEntry : network.positions.entrySet()) {
				BlockPos devicePos = deviceEntry.getKey();
				BlockState deviceState = world.getBlockState(devicePos);
				ITENetworkDevice device = deviceState.getBlock() instanceof ITENetworkDevice ? (ITENetworkDevice) deviceState.getBlock() : null;
				
				if (device != null ? device.getNetworkType() != NetworkDeviceType.WIRING : false) {
					
					NetworkMessage messageClone = message.copy();
					
					if (!devicePos.equals(pos)) {
						if (device.isReciver(messageClone, world, devicePos, deviceState)) {
							
							device.onMessageRecived(messageClone, world, devicePos, deviceState);
							
						}
					}
					
				}
				
			}
			
		}
		
	}
	
	protected boolean scann(World world, BlockPos scannPos, Direction direction, HashMap<BlockPos, List<Direction>> posList, int scannDepth, NetworkDeviceType lastDevice) {
		
		BlockState state = world.getBlockState(scannPos);
		TileEntity tileEntity = world.getBlockEntity(scannPos);
		ITENetworkDevice device = state.getBlock() instanceof ITENetworkDevice ? (ITENetworkDevice) state.getBlock() : tileEntity instanceof ITENetworkDevice ? (ITENetworkDevice) tileEntity : null;
		
		if (device != null && scannDepth < 50000) {
			
			NetworkDeviceType type = device.getNetworkType();
			
			if ((direction != null ? device.canConectNetworkWire(world, scannPos, direction) : true) && lastDevice.canConnectWith(type)) {
				
				boolean flag = false;
				
				if (posList.containsKey(scannPos)) {
					List<Direction> attachedDirection = posList.get(scannPos);
					if (direction != null) {
						if (attachedDirection.contains(direction)) return false;
						attachedDirection.add(direction);
					}
					posList.put(scannPos, attachedDirection);
					flag = true;
				} else {
					List<Direction> attachDirections = new ArrayList<Direction>();
					if (direction != null) attachDirections.add(direction);
					posList.put(scannPos, attachDirections);
					flag = true;
				}
				
				boolean flag2 = device.getNetworkType() == NetworkDeviceType.SWITCH ? device.isNetworkSwitchClosed(world, scannPos, state) : true;
				
				if (flag2) {

					for (Direction d : Direction.values()) {
						
						if (device.canConectNetworkWire(world, scannPos, d.getOpposite()) && (direction != null ? d != direction.getOpposite() : true)) {
							
							BlockPos pos2 = scannPos.relative(d);
							boolean flag1 = this.scann(world, pos2, d, posList, scannDepth + 1, type);
							
							if (flag1) {
								
								List<Direction> sides = posList.get(scannPos);
								sides.add(d.getOpposite());
								posList.put(scannPos, sides);
								
							}
							
						}
						
					}
					
				}
				
				if (flag) {
					
					List<BlockPos> multiParts = new ArrayList<BlockPos>();
					try {
						multiParts = device.getMultiBlockParts(world, scannPos, state);
					} catch (NullPointerException e) {
						System.err.println("Failure by updatign MultiBlock Device!");
					}
					
					if (multiParts != null) {
						
						for (BlockPos multiPart : multiParts) {
							
							if (!posList.containsKey(multiPart)) {
								scann(world, multiPart, null, posList, scannDepth++, lastDevice);
							} else {
								continue;
							}
							
						}
						
					}
					
				}
				
				return flag;
				
			}
			
		}
		
		return false;
		
	}
	
	public DataNetwork getNetwork(World world, BlockPos position) {
		
		if (this.isServerInstace) {
			
			if (world.getBlockState(position).getBlock() instanceof ITENetworkDevice) {
				
				DataNetwork network = new DataNetwork();
				List<Direction> sideList = new ArrayList<Direction>();
				network.positions.put(position, sideList);
				this.scann(world, position, null, network.positions, 0, NetworkDeviceType.WIRING);
				
				return network;
				
			}
					
		}
		
		return new DataNetwork();
		
	}
	
	public static class DataNetwork {
		
		public HashMap<BlockPos, List<Direction>> positions;
		
		public DataNetwork() {
			this.positions = new HashMap<BlockPos, List<Direction>>();
		}
		
		public boolean contains(BlockPos pos) {
			for (BlockPos pos1 : this.positions.keySet()) {
				if (pos1.equals(pos)) return true;
			}
			return false;
		}
		
		public BlockPos[] getConnectedBlocks() {
			return this.positions.keySet().toArray(new BlockPos[this.positions.size()]);
		}
		
		public CompoundNBT write(CompoundNBT nbt) {
			ListNBT deviceList = new ListNBT();
			for (Entry<BlockPos, List<Direction>> entry : this.positions.entrySet()) {
				CompoundNBT entryTag = new CompoundNBT();
				entryTag.put("Position", NBTUtil.writeBlockPos(entry.getKey()));
				ListNBT sides = new ListNBT();
				for (Direction d : entry.getValue()) {
					sides.add(StringNBT.valueOf(d.getName()));
				}
				entryTag.put("AttachedSides", sides);
				deviceList.add(entryTag);
			}
			nbt.put("Devices", deviceList);
			return nbt;
		}
		
		public static DataNetwork read(CompoundNBT nbt) {
			DataNetwork network = new DataNetwork();
			ListNBT deviceList = nbt.getList("Devices", 10);
			for (int i = 0; i < deviceList.size(); i++) {
				CompoundNBT entryTag = deviceList.getCompound(i);
				BlockPos position = NBTUtil.readBlockPos(entryTag.getCompound("Position"));
				List<Direction> sides = new ArrayList<Direction>();
				ListNBT sidesList = entryTag.getList("AttachedSides", 8);
				for (int i1 = 0; i1 < sidesList.size(); i1++) {
					sides.add(Direction.byName(sidesList.getString(i1)));
				}
				network.positions.put(position, sides);
			}
			return network;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof DataNetwork) {
				DataNetwork other = (DataNetwork) obj;
				return other.getConnectedBlocks().equals(this.getConnectedBlocks());
			}
			return false;
		}
		
	}

	@Override
	public void load(CompoundNBT nbt) {
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		return new CompoundNBT();
	}
	
}
