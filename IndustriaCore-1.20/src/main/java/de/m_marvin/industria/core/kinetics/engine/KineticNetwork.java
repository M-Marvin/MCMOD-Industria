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

import de.m_marvin.industria.core.kinetics.engine.KineticHandlerCapabillity.KineticComponent;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.KineticReference;
import de.m_marvin.industria.core.util.GameUtility;
import de.m_marvin.industria.core.util.types.PowerNetState;
import de.m_marvin.industria.core.util.ufns.SynchronizedFunctionalNetworkSpace.SynchronizedFunctionalNetwork;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.level.Level;

public class KineticNetwork extends SynchronizedFunctionalNetwork<KineticNetwork, KineticReference, KineticHandlerCapabillity.KineticComponent, Double> {
	
	private static record RefPair(int refId1, int refId2) {
		public boolean has(int refId) {
			return refId1 == refId || refId2 == refId;
		}
	}
	
	protected final Supplier<Level> level;
	protected Map<RefPair, Double> components2ratioMap = new HashMap<>();
	protected Map<Integer, Double> component2speedMap = new HashMap<>();
	protected double speed = 0;
	
	protected PowerNetState state = PowerNetState.ACTIVE;
	
	public KineticNetwork(Supplier<Level> level) {
		this.level = level;
	}
	
	public Level getLevel() {
		return level.get();
	}
	
//	public CompoundTag saveNBT(KineticHandlerCapabillity handler) {
//		CompoundTag tag = new CompoundTag();
//		ListTag componentsTag = new ListTag();
//		for (Component component : this.components.values()) {
//			if (component == null) continue;
//			try {
//				CompoundTag compTag = new CompoundTag();
//				component.serializeNbt(compTag);
//				compTag.putDouble("Ratio", this.component2ratioMap.getOrDefault(component, 0.0));
//				componentsTag.add(compTag);
//			} catch (Exception e) {
//				IndustriaCore.LOGGER.error("Failed to serialize kinetic component at " + component.reference() + "!");
//				e.printStackTrace();
//			}
//		}
//		tag.put("Components", componentsTag);
//		tag.putString("State", this.state.name().toLowerCase());
//		return tag;
//	}
//	
//	public void loadNBT(KineticHandlerCapabillity handler, CompoundTag tag) {
//		ListTag componentsTag = tag.getList("Components", ListTag.TAG_COMPOUND);
//		componentsTag.stream().forEach((componentTag) -> {
//			Component component = Component.deserializeNbt((CompoundTag) componentTag);
//			this.components.put(component.reference(), component);
//			this.component2ratioMap.put(component, ((CompoundTag) componentTag).getDouble("Ratio"));
//		});
//		this.state = PowerNetState.valueOf(tag.getString("State").toUpperCase());
//	}
	
	private void resetSpeedMap() {
		this.component2speedMap.clear();
		setState(PowerNetState.INACTIVE);
	}
	
	@Override
	protected void afterPutComponent(int refId) {
		this.components2ratioMap.keySet().stream().filter(p -> p.has(refId)).toList().forEach(this.components2ratioMap::remove);
		resetSpeedMap();
	}

	@Override
	protected void afterRemoveComponent(int refId) {
		this.components2ratioMap.keySet().stream().filter(p -> p.has(refId)).toList().forEach(this.components2ratioMap::remove);
		resetSpeedMap();
	}

	@Override
	protected void afterIntegrateNetwork(IntSet refIds, KineticNetwork other) {
		for (Entry<RefPair, Double> e : other.components2ratioMap.entrySet()) {
			if (refIds.contains(e.getKey().refId1) && refIds.contains(e.getKey().refId2))
				this.components2ratioMap.put(e.getKey(), e.getValue());
		}
		resetSpeedMap();
	}

	@Override
	protected void afterParametrizedConnection(int refId1, int refId2, Double ratio) {
		if (refId1 < refId2)
			this.components2ratioMap.put(new RefPair(refId1, refId2), ratio);
		else
			this.components2ratioMap.put(new RefPair(refId2, refId1), 1 / ratio);
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
		
		if (!this.components2ratioMap.isEmpty()) {

			if (this.component2speedMap.isEmpty()) {
				
				// Recompute speed map
				Queue<Integer> nextEntries = new ArrayDeque<>();
				List<Entry<RefPair, Double>> toCompute = new ArrayList<>();
				nextEntries.add(components2ratioMap.keySet().stream().findAny().get().refId1);
				toCompute.addAll(this.components2ratioMap.entrySet());
				
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
					
					double speed = maxSpeedL.orElseGet(() -> maxSpeedH.getAsDouble());
					
					// Calculate available torque
					double torque = this.components.int2ObjectEntrySet().stream()
						.filter(c -> c.getValue().getSourceSpeed(getLevel()) == 0)
						.mapToDouble(c -> c.getValue().getTorque(getLevel()) / getTransmission(c.getIntKey()))
						.sum();
					
					// Calculate total load
					double load = this.components.int2ObjectEntrySet().stream()
							.filter(c -> c.getValue().getSourceSpeed(getLevel()) == 0)
							.mapToDouble(c -> c.getValue().getTorque(getLevel()) / getTransmission(c.getIntKey()))
							.sum();
					
					// Check for overload
					if (load > torque) {
						setNetworkSpeed(0.0);
						setState(PowerNetState.INACTIVE);
					} 
					
					else {
						
						// Set network rotation speed
						setNetworkSpeed(speed);
						setState(PowerNetState.ACTIVE);
						
					}
					
				}
				
			}
			
		}
		
		// Update rotation speeds
		for (Entry<Integer, KineticComponent> c : this.components.int2ObjectEntrySet()) {
			double ratio = getTransmission(c.getKey());
			double cspeed = ratio == 0.0 ? 0.0 : (getSpeed() / ratio);
			c.getValue().setRPM(getLevel(), cspeed);
		}
		
		// Trigger updates
		listComponents().stream()
			.map(c -> c.reference().pos())
			.distinct()
			.forEach(pos -> GameUtility.triggerClientSync(getLevel(), pos));
		
		
	}
	
	@Override
	public void afterChange() {
		System.out.println("KineticNetwork size: " + this.components.size() + " " + this.hashCode());
		recomputeKinetics();
	}

	@Override
	public void onUpdate() {
		System.out.println("Update KineticNetwork with size: " + this.components.size() + " " + this.hashCode());
		recomputeKinetics();
	}
	
	public double getTransmission(int refId) {
		return this.component2speedMap.getOrDefault(refId, 0.0);
	}
	
	public void setNetworkSpeed(double speed) {
		this.speed = speed;
	}
	
	public double getSpeed() {
		return speed;
	}
	
//	public void reset() {
//		this.components.clear();
//		this.component2speedMap.clear();
//		this.state = PowerNetState.INACTIVE;
//	}
	
	public boolean isEmpty() {
		return components.isEmpty();
	}
	
	public void setLocked() {
		setState(PowerNetState.FAILED);
	}
	
	public void setState(PowerNetState state) {
		this.state = state;
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
