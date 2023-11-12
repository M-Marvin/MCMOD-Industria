package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonElement;

public class IntegerParameter extends Parameter<Integer> {

	public IntegerParameter(String name, int defaultValue) {
		super(name, defaultValue);
	}
	
	@Override
	public Class<Integer> getTypeClass() {
		return Integer.class;
	}

	@Override
	public Integer parseValue(JsonElement jsonElement) {
		return jsonElement.getAsInt();
	}
	
}
