package de.redtec.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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

public class ElectricityNetworkHandler extends WorldSavedData {
	
	List<ElectricityNetwork> networks;
	
	public ElectricityNetworkHandler() {
		super("elctric_networks");
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
			return new ElectricityNetworkHandler();
		}
		
	}
	
	@SuppressWarnings("unused")
	public void update() {
		
		
		// Wird jeden Tick aufgerufen
		
		for (ElectricityNetwork networks : this.networks) {
			
			// TODO
			//Split Networks
			//Delete Empty Block Positions
			//Delete Unused Networks
			//Update Network Variables
			//If >=1 Network has CHanged: this.markDirty()
			
		}
		
	}
	
	public void calculateNetwork(World world, BlockPos position, Direction direction) {
		
		ElectricityNetwork network = getNetwork(position);
		
		if (!network.isUpdated() && !world.isRemote()) {
			
			network.positions.clear();
			this.scann(world, position, direction, network.positions, 0);
			network.needCurrent = 0;
			network.voltage = Voltage.NoLimit;
			
			int capacityCurrent = 0;
			
			System.out.println(network.positions);
			
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
								capacityCurrent += -needCurrent;
								hasCurrentAdded = true;
							}
							
						}
						
					}
					
				}
				
			}
			
			if (network.needCurrent <= capacityCurrent) {
				network.current = network.needCurrent;
			} else {
				network.current = capacityCurrent;
			}
			
			for (Entry<BlockPos, List<Direction>> device : network.positions.entrySet()) {
				
				BlockPos pos = device.getKey();
				BlockState state = world.getBlockState(pos);
				
				if (state.getBlock() instanceof IElectricConnective && !(state.getBlock() instanceof IElectricWire)) {
					
					((IElectricConnective) state.getBlock()).onNetworkChanges(world, pos, state, network);
					
				}
				
			}
			
			this.markDirty();
			
			System.out.println("Network Voltage: " + network.voltage);
			System.out.println("Network Current Needed:  " + network.needCurrent);
			System.out.println("Network Current: " + network.current);
			
		}
		
	}
	
	protected void scann(World world, BlockPos scannPos, Direction direction, HashMap<BlockPos, List<Direction>> posList, int scannDepth) {
		
		BlockState state = world.getBlockState(scannPos);
		
		if (state.getBlock() instanceof IElectricConnective && scannDepth < 50000) {
			
			IElectricConnective device = (IElectricConnective) state.getBlock();
			
			if (device.canConnect(direction, state)) {
				
				if (posList.containsKey(scannPos)) {
					List<Direction> attachedDirection = posList.get(scannPos);
					if (attachedDirection.contains(direction)) return;
					attachedDirection.add(direction);
					posList.put(scannPos, attachedDirection);
				} else {
					List<Direction> attachDirections = new ArrayList<Direction>();
					attachDirections.add(direction);
					posList.put(scannPos, attachDirections);
				}
				
				for (Direction d : Direction.values()) {
					
					if (device.canConnect(d.getOpposite(), state) && d != direction.getOpposite()) {
						
						BlockPos pos2 = scannPos.offset(d);
						this.scann(world, pos2, d, posList, scannDepth + 1);
						
					}
					
				}
				
			}
			
		}
		
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
		public int needCurrent;
		public long lastUpdated;
		
		public ElectricityNetwork() {
			this.positions = new HashMap<BlockPos, List<Direction>>();
			this.voltage = Voltage.NoLimit;
			this.current = 0;
		}
		
		public boolean contains(BlockPos pos) {
			for (BlockPos pos1 : this.positions.keySet()) {
				if (pos1.equals(pos)) return true;
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
			return this.needCurrent <= this.current;
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
