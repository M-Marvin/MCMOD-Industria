package de.industria.util.handler;

import java.util.Collection;

import com.google.common.collect.ImmutableMap;

import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.Property;
import net.minecraftforge.fluids.FluidStack;

public class FluidStackStateTagHelper {
	
	public static FluidStack makeStackFromState(FluidState fluid) {
		
		ImmutableMap<Property<?>, Comparable<?>> propertys = fluid.getValues();
		
		ListNBT propList = new ListNBT();
		
		for (Property<?> prop : propertys.keySet()) {
			
			CompoundNBT compoundNBT = makeCompoundNBT(fluid, prop);
			propList.add(compoundNBT);
			
		}
		
		CompoundNBT tag = new CompoundNBT();
		tag.put("States", propList);
		
		FluidStack stack = new FluidStack(fluid.getType(), 1000);
		stack.setTag(tag);
		
		return stack;
		
	}
	
	public static FluidState makeStateFromStack(FluidStack fluid) {
		
		FluidState state = fluid.getFluid().defaultFluidState();
		ImmutableMap<Property<?>, Comparable<?>> propertys = state.getValues();
		
		CompoundNBT tag = fluid.getTag();
		
		if (tag != null ? tag.contains("States") : false) {
			
			ListNBT propList = tag.getList("States", 10);
			
			for (int i = 0; i < propList.size(); i++) {
				
				CompoundNBT entry = propList.getCompound(i);
				
				String name = entry.getString("Name");
				String valueName = entry.getString("Value");
				
				Property<?> prop = getProperty(name, propertys);
				if (prop != null) state = setPropertyInState(valueName, prop, state);
				
			}
			
		}
		
		return state;
		
	}
	
	protected static <T extends Comparable<T>> FluidState setPropertyInState(String valueName, Property<T> prop, FluidState state) {
		
		Collection<T> values = prop.getPossibleValues();
		
		for (T value : values) {
			
			if (prop.getName(value).equals(valueName)) return state.setValue(prop, value);
			
		}
		
		return state;
		
	}
	
	protected static Property<?> getProperty(String name, ImmutableMap<Property<?>, Comparable<?>> propertys) {
		
		for (Property<?> prop : propertys.keySet()) {
			
			if (prop.getName().equals(name)) return prop;
			
		}
		
		return null;
		
	}
	
	protected static <T extends Comparable<T>> CompoundNBT makeCompoundNBT(FluidState state, Property<T> prop) {
		
		T value = state.getValue(prop);
		String name = prop.getName();
		String valueName = prop.getName(value);
		
		CompoundNBT compoundNBT = new CompoundNBT();
		compoundNBT.putString("Name", name);
		compoundNBT.putString("Value", valueName);
		return compoundNBT;
		
	}
	
}
