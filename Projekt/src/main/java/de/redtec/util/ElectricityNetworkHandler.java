package de.redtec.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.redtec.RedTec;
import de.redtec.packet.SSendENHandeler;
import de.redtec.util.IElectricConnective.DeviceType;
import de.redtec.util.IElectricConnective.Voltage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.network.PacketDistributor;

public class ElectricityNetworkHandler extends WorldSavedData {

	protected static ElectricityNetworkHandler clientInstance;
	protected int updateTimer;
	
	private boolean isServerInstace;
	private List<ElectricityNetwork> networks;
	
	public ElectricityNetworkHandler() {
		this(true);
	}
	
	public ElectricityNetworkHandler(boolean serverInstance) {
		super("elctric_networks");
		this.isServerInstace = serverInstance;
		this.networks = new ArrayList<ElectricityNetworkHandler.ElectricityNetwork>();
	}
	
	@Override
	public void read(CompoundNBT compound) {
		
		this.networks.clear();
		ListNBT networkTag = compound.getList("Networks", 10);
		for (int i = 0; i < networkTag.size(); i++) {
			this.networks.add(ElectricityNetwork.read(networkTag.getCompound(i)));
		}
		
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		
		ListNBT networkTag = new ListNBT();
		for (ElectricityNetwork net : this.networks) {
			networkTag.add(net.write(new CompoundNBT()));
		}
		compound.put("Networks", networkTag);
		return compound;
		
	}
	
	public static ElectricityNetworkHandler getHandlerForWorld(IWorld world) {
		
		if (!world.isRemote()) {
			DimensionSavedDataManager storage = ((ServerWorld) world).getSavedData();
			return storage.getOrCreate(ElectricityNetworkHandler::new, "elctric_networks");
		} else {
			if (clientInstance == null) clientInstance = new ElectricityNetworkHandler(false);
			return clientInstance;
		}
		
	}
	
	public boolean isServerInstace() {
		return isServerInstace;
	}
		
	public void updateNetwork(World world, BlockPos pos) {
		this.calculateNetwork(world, pos);
	}
	
	public void calculateNetwork(World world, BlockPos position) {
		
		if (this.isServerInstace) {
			
			if (world.getBlockState(position).getBlock() instanceof IElectricConnective) {
				
				ElectricityNetwork network = getNetwork(position);
				
				if (!network.isUpdated() && !world.isRemote()) {
					
					network.positions.clear();
					boolean flag = this.scann(world, position, null, network.positions, 0, DeviceType.WIRE);
					
					// If no Device Found, add it self, to prevent endless new Networks;
					if (!flag) {
						List<Direction> attachList = new ArrayList<Direction>();
						network.positions.put(position, attachList);
					}
					
					if (network.positions.size() <= 1) network.lastUpdated = 0;
					network.needCurrent = 0;
					network.voltage = Voltage.NoLimit;
					network.capacity = 0;
					
					for (Entry<BlockPos, List<Direction>> device : network.positions.entrySet()) {
						
						BlockState state = world.getBlockState(device.getKey());
						List<Direction> attacheSides = device.getValue();
						
						if (state.getBlock() instanceof IElectricConnective && !(state.getBlock() instanceof IElectricWire)) {
							
							IElectricConnective device1 = (IElectricConnective) state.getBlock();
							
							boolean hasCurrentAdded = false;
							for (Direction attachSide : attacheSides) {
								
								Voltage voltage = device1.getVoltage(world, device.getKey(), state, attachSide);
								int needCurrent = device1.getNeededCurrent(world, device.getKey(), state, attachSide);
								
								if (needCurrent > 0) {
									
									if (!hasCurrentAdded) {
										network.needCurrent += needCurrent;
										hasCurrentAdded = true;
									}
									
								} else if (needCurrent < 0) {
									
									if (voltage.getVoltage() > network.voltage.getVoltage()) network.voltage = voltage;
									if (!hasCurrentAdded) {
										network.capacity += -needCurrent;
										hasCurrentAdded = true;
									}
									
								}
								
							}
							
						}
						
					}
					
					if (network.needCurrent <= network.capacity) {
						network.current = network.needCurrent;
					} else {
						network.current = network.capacity;
					}
					
					for (Entry<BlockPos, List<Direction>> device : network.positions.entrySet()) {
						
						BlockPos pos = device.getKey();
						BlockState state = world.getBlockState(pos);
						
						if (state.getBlock() instanceof IElectricConnective && !(state.getBlock() instanceof IElectricWire)) {
							
							((IElectricConnective) state.getBlock()).onNetworkChanges(world, pos, state, network);
							
						}
						
					}
					
					if (this.updateTimer++ > 500) {
						this.updateTimer = 0;
						
						SSendENHandeler packet = new SSendENHandeler(this);
						RedTec.NETWORK.send(PacketDistributor.ALL.noArg(), packet);
						
					}
					
//					System.out.println("Network Voltage: " + network.voltage);
//					System.out.println("Network Current Needed:  " + network.needCurrent);
//					System.out.println("Network Capacity: " + network.capacity);
//					System.out.println("Network Current: " + network.current);
//					
//					System.out.println(this.networks.size());
//					System.out.println(getNetwork(position).current);
					
				}
				
			}
			
			for (ElectricityNetwork network1 : this.networks.toArray(new ElectricityNetwork[] {})) {
				
				if (network1.positions.size() <= 1) this.networks.remove(network1);
				
				int lastUpdate = (int) (System.currentTimeMillis() - network1.lastUpdated);
				if (lastUpdate > 500) this.networks.remove(network1);
				
			}
			
		}
		
	}
	
	protected boolean scann(World world, BlockPos scannPos, Direction direction, HashMap<BlockPos, List<Direction>> posList, int scannDepth, DeviceType lastDevice) {
		
		BlockState state = world.getBlockState(scannPos);
		
		if (state.getBlock() instanceof IElectricConnective && scannDepth < 50000) {
			
			IElectricConnective device = (IElectricConnective) state.getBlock();
			DeviceType type = device.getDeviceType();
			
			if ((direction != null ? device.canConnect(direction, state) : true) && lastDevice.canConnectWith(type)) {
				
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
				
				for (Direction d : Direction.values()) {
					
					if (device.canConnect(d.getOpposite(), state) && (direction != null ? d != direction.getOpposite() : true)) {
						
						BlockPos pos2 = scannPos.offset(d);
						boolean flag1 = this.scann(world, pos2, d, posList, scannDepth + 1, type);
						
						if (flag1) {
							
							List<Direction> sides = posList.get(scannPos);
							sides.add(d.getOpposite());
							posList.put(scannPos, sides);
							
						}
						
					}
					
				}
				
				return flag;
				
			}
			
		}
		
		return false;
		
	}
	
	public ElectricityNetwork getNetwork(BlockPos pos) {
		
		for (ElectricityNetwork net : this.networks) {
			
			if (net.contains(pos)) return net;
			
		}
				
		ElectricityNetwork network = new ElectricityNetwork();
		this.networks.add(network);
		return network;
		
	}
	
	public static class ElectricityNetwork {
		
		public HashMap<BlockPos, List<Direction>> positions;
		public int current;
		public Voltage voltage;
		public int capacity;
		public int needCurrent;
		public long lastUpdated;
		
		public ElectricityNetwork() {
			this.positions = new HashMap<BlockPos, List<Direction>>();
			this.voltage = Voltage.NoLimit;
			this.current = 0;
			this.capacity = 0;
		}
		
		public boolean contains(BlockPos pos) {
			for (BlockPos pos1 : this.positions.keySet()) {
				if (pos1.toLong() == pos.toLong()) return true;
			}
			return false;
		}
		
		public boolean isUpdated() {
			
			long time = System.currentTimeMillis();
			if (this.lastUpdated != time) {
				this.lastUpdated = time;
				return false;
			}
			
			return true;
			
		}
		
		public float getGeneratorProductivity() {
			return Math.max(1F, (float) this.needCurrent / this.current);
		}
		
		public boolean canMachinesRun() {
			return this.needCurrent <= this.current && this.current > 0;
		}
		
		public Voltage getVoltage() {
			return voltage;
		}
		
		public int getCurrent() {
			return current;
		}
		
		public int getNeedCurrent() {
			return needCurrent;
		}
		
		public int getCapacity() {
			return capacity;
		}
		
		public BlockPos[] getConnectedBlocks() {
			return this.positions.keySet().toArray(new BlockPos[this.positions.size()]);
		}
		
		public CompoundNBT write(CompoundNBT nbt) {
			nbt.putInt("Current", this.current);
			nbt.putInt("NeedCurrent", this.needCurrent);
			nbt.putString("Voltage", this.voltage.toString());
			ListNBT deviceList = new ListNBT();
			for (Entry<BlockPos, List<Direction>> entry : this.positions.entrySet()) {
				CompoundNBT entryTag = new CompoundNBT();
				entryTag.put("Position", NBTUtil.writeBlockPos(entry.getKey()));
				ListNBT sides = new ListNBT();
				for (Direction d : entry.getValue()) {
					sides.add(StringNBT.valueOf(d.getName2()));
				}
				entryTag.put("AttachedSides", sides);
				deviceList.add(entryTag);
			}
			nbt.put("Devices", deviceList);
			return nbt;
		}
		
		public static ElectricityNetwork read(CompoundNBT nbt) {
			ElectricityNetwork network = new ElectricityNetwork();
			network.current = nbt.getInt("Current");
			network.needCurrent = nbt.getInt("NeedCurrent");
			network.voltage = Voltage.valueOf(nbt.getString("Voltage"));
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
		
	}
	
}
