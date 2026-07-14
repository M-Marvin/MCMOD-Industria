package de.m_marvin.industria.core.kinetics.engine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalDouble;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

import de.m_marvin.industria.IndustriaCore;
import de.m_marvin.industria.core.kinetics.KineticUtility;
import de.m_marvin.industria.core.kinetics.engine.KineticNetworkSpaceCapability.KineticComponent;
import de.m_marvin.industria.core.kinetics.engine.network.SUpdateKineticNetworkPackage;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.KineticReference;
import de.m_marvin.industria.core.registries.Capabilities;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.PowerNetState;
import de.m_marvin.industria.core.util.ufns.SynchronizedFunctionalNetworkSpace.SynchronizedFunctionalNetwork;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;

public class KineticNetwork extends SynchronizedFunctionalNetwork<KineticNetwork, KineticReference, KineticNetworkSpaceCapability.KineticComponent, Double> {
	
	private static record RefPair(int refId1, int refId2) {
		public boolean has(int refId) {
			return refId1 == refId || refId2 == refId;
		}
	}
	
	private final Supplier<Level> level;
	
	private Map<RefPair, Double> ref2ratioMap = new HashMap<>();
	private Map<Integer, Double> component2speedMap = new HashMap<>();
	private double speed = 0;
	private double maxPower;
	private double currentProduction;
	private double currentConsumtion;
	private PowerNetState state = PowerNetState.ACTIVE;
	
	public KineticNetwork(Supplier<Level> level) {
		this.level = level;
	}
	
	@Override
	public void serializeNbt(CompoundTag nbt) {
		super.serializeNbt(nbt);
		
		ListTag ratiosNbt = new ListTag();
		for (var e : this.ref2ratioMap.entrySet()) {
			CompoundTag ratioNbt = new CompoundTag();
			ratioNbt.putInt("RefId1", e.getKey().refId1);
			ratioNbt.putInt("RefId2", e.getKey().refId2);
			ratioNbt.putDouble("Ratio", e.getValue());
			ratiosNbt.add(ratioNbt);
		}
		nbt.put("Ratios", ratiosNbt);
		
		ListTag speedsNbt = new ListTag();
		for (var e : this.component2speedMap.entrySet()) {
			CompoundTag speedNbt = new CompoundTag();
			speedNbt.putInt("RefId", e.getKey());
			speedNbt.putDouble("Speed", e.getValue());
			speedsNbt.add(speedNbt);
		}
		nbt.put("Speeds", speedsNbt);
		
		nbt.putDouble("NetworkSpeed", this.speed);
		nbt.putString("State", this.state.name().toLowerCase());
	}
	
	@Override
	public void deserializeNbt(CompoundTag nbt) {
		super.deserializeNbt(nbt);
		
		ListTag ratiosNbt = nbt.getList("Ratios", 10);
		this.ref2ratioMap.clear();
		for (int i = 0; i < ratiosNbt.size(); i++) {
			CompoundTag ratioNbt = ratiosNbt.getCompound(i);
			int refId1 = ratioNbt.getInt("RefId1");
			int refId2 = ratioNbt.getInt("RefId2");
			double ratio = ratioNbt.getDouble("Ratio");
			this.ref2ratioMap.put(new RefPair(refId1, refId2), ratio);
		}
		
		ListTag speedsNbt = nbt.getList("Speeds", 10);
		this.component2speedMap.clear();
		for (int i = 0; i < speedsNbt.size(); i++) {
			CompoundTag speedNbt = speedsNbt.getCompound(i);
			int refId = speedNbt.getInt("RefId");
			double speed = speedNbt.getDouble("Speed");
			this.component2speedMap.put(refId, speed);
		}
		
		this.speed = nbt.getDouble("NetworkSpeed");
		this.state = PowerNetState.valueOf(nbt.getString("State").toUpperCase());
	}

	@Override
	public void afterChange() {
		if (!getLevel().isClientSide())
			recomputeKinetics();
	}

	@Override
	public void onUpdate() {
		if (!getLevel().isClientSide()) { 
//			resetSpeedMap();
			recomputeKinetics();
		}
	}
	
	public Level getLevel() {
		return level.get();
	}
	
	private void resetSpeedMap() {
		this.component2speedMap.clear();
	}
	
	@Override
	protected void afterPutComponent(int refId) {
		this.ref2ratioMap.keySet().stream().filter(p -> p.has(refId)).toList().forEach(this.ref2ratioMap::remove);
		resetSpeedMap();
	}

	@Override
	protected void afterRemoveComponent(int refId) {
		this.ref2ratioMap.keySet().stream().filter(p -> p.has(refId)).toList().forEach(this.ref2ratioMap::remove);
		resetSpeedMap();
	}

	@Override
	protected void afterIntegrateNetwork(IntSet refIds, KineticNetwork other) {
		for (Entry<RefPair, Double> e : other.ref2ratioMap.entrySet()) {
			if (	(refIds.contains(e.getKey().refId1) || this.components.containsKey(e.getKey().refId1)) && 
					(refIds.contains(e.getKey().refId2) || this.components.containsKey(e.getKey().refId2)))
				this.ref2ratioMap.put(e.getKey(), e.getValue());
		}
		resetSpeedMap();
	}

	@Override
	protected void afterParametrizedConnection(int refId1, int refId2, Double ratio) {
		if (refId1 < refId2)
			this.ref2ratioMap.put(new RefPair(refId1, refId2), ratio);
		else
			this.ref2ratioMap.put(new RefPair(refId2, refId1), 1 / ratio);
		resetSpeedMap();
	}

	private boolean computeSpeeds(int refId1, int refId2, double ratio) {
		if (ratio == 0.0) return true;
		
		Double speed1 = this.component2speedMap.get(refId1);
		Double speed2 = this.component2speedMap.get(refId2);
		
		if (speed1 == null && speed2 == null && !this.component2speedMap.isEmpty()) {
			setLocked();
			return false;
		}
		
		if (speed1 != null && speed2 != null && speed2 / speed1 != ratio) {
			setLocked();
			return false;
		}
		
		if (speed1 == null) {
			if (speed2 == null)
				this.component2speedMap.put(refId2, speed2 = 1.0);
			this.component2speedMap.put(refId1, speed1 = speed2 * ratio);
		} else {
			this.component2speedMap.put(refId2, speed2 = speed1 / ratio);
		}
		return true;
	}
	
	private void recomputeKinetics() {
		
		if (this.ref2ratioMap.isEmpty()) return;

		if (this.component2speedMap.isEmpty()) {
			
			// Recompute speed map
			Queue<Integer> nextEntries = new ArrayDeque<>();
			List<Entry<RefPair, Double>> toCompute = new ArrayList<>();
			nextEntries.add(ref2ratioMap.keySet().stream().findAny().get().refId1);
			toCompute.addAll(this.ref2ratioMap.entrySet());
			
			compute: while (nextEntries.size() > 0 && !isLocked()) {
				int refId = nextEntries.poll();
				
				for (int i = 0;i < toCompute.size(); i++) {
					var e = toCompute.get(i);
					if (e.getKey().refId2 == refId) {
						if (!computeSpeeds(e.getKey().refId1, refId, e.getValue())) break compute;
						nextEntries.add(e.getKey().refId1);
					} else if (e.getKey().refId1 == refId) {
						if (!computeSpeeds(refId, e.getKey().refId2, e.getValue())) break compute;
						nextEntries.add(e.getKey().refId2);
					} else continue;	
					toCompute.remove(i--);
				}
			}
			
		}
		
		// Check for opposite rotations, if so, skip calculations
		if (isLocked()) {
			setNetworkSpeed(0.0);
		} else {
			
			// Calculate source speeds
			double[] sources = this.components.int2ObjectEntrySet().stream()
				.mapToDouble(c -> c.getValue().getSourceSpeed(getLevel()) * getTransmission(c.getIntKey()))
				.distinct()
				.toArray();
			
			// Find fastest source-speed in network (in both directions)
			OptionalDouble maxSpeedH = DoubleStream.of(sources).filter(s -> s > 0).max();
			OptionalDouble maxSpeedL = DoubleStream.of(sources).filter(s -> s < 0).min();
			
			// Check if any source available
			if (maxSpeedH.isEmpty() && maxSpeedL.isEmpty()) {
				setNetworkSpeed(0.0);
				setState(PowerNetState.INACTIVE);
			} 
			
			// Check for reversed sources
			else if (maxSpeedH.isPresent() && maxSpeedL.isPresent()) {
				setNetworkSpeed(0.0);
				setState(PowerNetState.INACTIVE);
			}
			
			else {
				
				this.speed = maxSpeedL.orElseGet(() -> maxSpeedH.getAsDouble());
				
				// Calculate available torque
				this.currentProduction = this.components.int2ObjectEntrySet().stream()
					.filter(c -> c.getValue().getSourceSpeed(getLevel()) == this.speed)
					.mapToDouble(c -> c.getValue().getTorque(getLevel()) / getTransmission(c.getIntKey()))
					.sum();
				
				// Calculate total load
				this.currentConsumtion = this.components.int2ObjectEntrySet().stream()
						.filter(c -> c.getValue().getSourceSpeed(getLevel()) == 0)
						.mapToDouble(c -> c.getValue().getTorque(getLevel()) / getTransmission(c.getIntKey()))
						.sum();
				
				// Check for overload
				if (this.currentConsumtion > this.currentProduction) {
					this.speed = 0;
					setState(PowerNetState.INACTIVE);
				} else {
					setState(PowerNetState.ACTIVE);
				}
				
			}
			
		}
		
	}
	
	public void updateComponents() {

		// Send update to clients
		if (!getLevel().isClientSide())
			IndustriaCore.NETWORK.send(KineticUtility.TRACKING_NETWORK.with(() -> this), new SUpdateKineticNetworkPackage(this));
		
		// Update rotation speeds
		for (Entry<Integer, KineticComponent> c : this.components.int2ObjectEntrySet()) {
			double ratio = getTransmission(c.getKey());
			double cspeed = ratio == 0.0 ? 0.0 : (getNetworkSpeed() / ratio);
			c.getValue().setRPM(getLevel(), cspeed);
		}
		
	}
	
	public Map<KineticReference, Double> getSpeedMap() {
		KineticNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(getLevel(), Capabilities.KINETIC_NETWORK_SPACE_CAPABILITY);
		Map<KineticReference, Double> speedMap = new HashMap<KineticReference, Double>();
		for (var c : listComponents()) {
			int refId = networkSpace.getReferenceId(c.reference());
			if (this.component2speedMap.containsKey(refId))
				speedMap.put(c.reference(), this.component2speedMap.get(refId));
		}
		return speedMap;
	}
	
	public void setSpeedMap(Map<KineticReference, Double> speedMap) {
		KineticNetworkSpaceCapability networkSpace = GameUtility.getLevelCapability(getLevel(), Capabilities.KINETIC_NETWORK_SPACE_CAPABILITY);
		this.component2speedMap.clear();
		for (var e : speedMap.entrySet()) {
			this.component2speedMap.put(networkSpace.getReferenceId(e.getKey()), e.getValue());
		}
	}
	
	public double getTransmission(int refId) {
		return this.component2speedMap.getOrDefault(refId, 0.0);
	}
	
	public void setNetworkSpeed(double speed) {
		this.speed = speed;
	}
	
	public double getNetworkSpeed() {
		return speed;
	}
	
	public void setMaxPower(double maxPower) {
		this.maxPower = maxPower;
	}
	
	public double getMaxPower() {
		return maxPower;
	}
	
	public void setCurrentConsumtion(double currentConsumtion) {
		this.currentConsumtion = currentConsumtion;
	}
	
	public double getCurrentConsumtion() {
		return currentConsumtion;
	}
	
	public void setCurrentProduction(double currentProduction) {
		this.currentProduction = currentProduction;
	}
	
	public double getCurrentProduction() {
		return currentProduction;
	}
	
	public boolean isEmpty() {
		return components.isEmpty();
	}
	
	public void setLocked() {
		setState(PowerNetState.FAILED);
	}
	
	public void setState(PowerNetState state) {
		this.state = state;
		updateComponents();
	}
	
	public PowerNetState getState() {
		return state;
	}
	
	public boolean isLocked() {
		return this.state == PowerNetState.FAILED;
	}
	
	public boolean isOnline() {
		return this.state == PowerNetState.ACTIVE;
	}
	
}
