package de.m_marvin.industria.core.parametrics.properties;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.gson.JsonElement;

import net.minecraft.network.FriendlyByteBuf;

public abstract class Parameter<T> {
	
	private static Map<String, Parameter<?>> name2parameterRegistry = new HashMap<>();
	
	protected final T defaultValue;
	protected final String name;
	
	public Parameter(String name, T defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
		
		if (name2parameterRegistry.containsKey(name)) throw new IllegalArgumentException("The name " + name + " is already used by an parameter!");
		name2parameterRegistry.put(name, this);
	}
	
	public static Parameter<?> getParameterByName(String name) {
		return name2parameterRegistry.get(name);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Parameter<?> other) {
			return Objects.equal(this.name, other.name);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	public T defaultValue() {
		return this.defaultValue;
	}
	
	public abstract Class<T> getTypeClass();

	public abstract T parseValue(JsonElement jsonElement);
	
	public abstract void writeValue(FriendlyByteBuf buff, Object value);
	public abstract T readValue(FriendlyByteBuf buff);
	
}
