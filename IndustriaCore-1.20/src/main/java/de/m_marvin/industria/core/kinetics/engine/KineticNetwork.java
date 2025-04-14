package de.m_marvin.industria.core.kinetics.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import de.m_marvin.industria.core.kinetics.engine.KineticHandlerCapabillity.KineticComponent;
import de.m_marvin.industria.core.kinetics.types.blocks.IKineticBlock.KineticReference;
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
	protected void afterParametrizedConnection(int refId1, int refId2, Double ratio) {
		if (ratio == 0.0) return;
		Double ratio1 = this.component2ratioMap.get(refId1);
		if (ratio1 != null) {
			Double r = this.component2ratioMap.put(refId2, ratio1 / ratio);
			if (r != null && Double.compare(r, ratio1 / ratio) != 0) setLocked();
		} else {
			Double ratio2 = this.component2ratioMap.get(refId2);
			if (ratio2 == null) {
				ratio2 = 1.0;
				this.component2ratioMap.put(refId2, ratio2);
			}
			this.component2ratioMap.put(refId1, ratio2 * ratio);
		}
	}

	@Override
	protected void afterIntegrateNetwork(IntSet refIds, KineticNetwork other) {
		for (int refId : refIds) {
			this.component2ratioMap.put(refId, other.component2ratioMap.get(refId));
		}
		this.state = PowerNetState.INACTIVE;
	}

	@Override
	public void afterChange() {
		// TODO Auto-generated method stub
		
		System.out.println("KineticNetwork size: " + this.components.size());
	}

	@Override
	public void onUpdate() {
		// TODO Auto-generated method stub
		
	}

	public double getTransmission(KineticComponent component) {
		return this.component2ratioMap.getOrDefault(component, 0.0);
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
