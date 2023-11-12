package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonElement;

public class BooleanParameter extends Parameter<Boolean> {

	public BooleanParameter(String name, boolean defaultValue) {
		super(name, defaultValue);
	}
	
	@Override
	public Class<Boolean> getTypeClass() {
		return Boolean.class;
	}

	@Override
	public Boolean parseValue(JsonElement jsonElement) {
		return jsonElement.getAsBoolean();
	}
	
}
