package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonElement;

public class EnumParameter<E extends Enum<E>> extends Parameter<E> {
	
	protected final Class<E> enumClass;
	
	public EnumParameter(String name, Class<E> enumClass, E defaultValue) {
		super(name, defaultValue);
		this.enumClass = enumClass;
	}
	
	@Override
	public Class<E> getTypeClass() {
		return this.enumClass;
	}

	@Override
	public E parseValue(JsonElement jsonElement) {
		return Enum.valueOf(getTypeClass(), jsonElement.getAsString());
	}
	
}
