package de.m_marvin.industria.core.kinetics.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalDouble;
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
	
	protected final Supplier<Level> level;
	protected Map<Integer, Double> component2ratioMap = new HashMap<>();
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

	@Override
	protected void afterPutComponent(int refId) {
		this.component2ratioMap.remove(refId);
		this.state = PowerNetState.INACTIVE;
	}

	@Override
	protected void afterRemoveComponent(int refId) {
		this.component2ratioMap.remove(refId);
		this.state = PowerNetState.INACTIVE;
	}

	@Override
	protected boolean afterParametrizedConnection(int refId1, int refId2, Double ratio) {
		if (ratio == 0.0) return true;
		Double ratio1 = this.component2ratioMap.get(refId1);
		if (ratio1 != null) {
			Double r = this.component2ratioMap.put(refId2, ratio1 / ratio);
			if (r != null && Double.compare(r, ratio1 / ratio) != 0) setLocked();
		} else {
			Double ratio2 = this.component2ratioMap.get(refId2);
			if (ratio2 == null) {
				if (!this.component2ratioMap.isEmpty())
					return false;
				ratio2 = 1.0;
				this.component2ratioMap.put(refId2, ratio2);
			}
			this.component2ratioMap.put(refId1, ratio2 * ratio);
		}
		return true;
	}

	@Override
	protected void afterIntegrateNetwork(IntSet refIds, KineticNetwork other) {
		for (int refId : refIds) {
			Double ratio = other.component2ratioMap.get(refId);
			if (ratio != null)
				this.component2ratioMap.put(refId, ratio);
		}
		this.state = PowerNetState.INACTIVE;
	}

	@Override
	public void afterChange() {
		System.out.println("KineticNetwork size: " + this.components.size());
		recalculateKinetics();
	}

	@Override
	public void onUpdate() {
		System.out.println("Update KineticNetwork with size: " + this.components.size());
		
		recalculateKinetics();
	}
	
	public void recalculateKinetics() {
		
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

	public double getTransmission(int refId) {
		return this.component2ratioMap.getOrDefault(refId, 0.0);
	}
	
	public void setNetworkSpeed(double speed) {
		this.speed = speed;
	}
	
	public double getSpeed() {
		return speed;
	}
	
	public void reset() {
		this.components.clear();
		this.component2ratioMap.clear();
		this.state = PowerNetState.INACTIVE;
	}
	
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
