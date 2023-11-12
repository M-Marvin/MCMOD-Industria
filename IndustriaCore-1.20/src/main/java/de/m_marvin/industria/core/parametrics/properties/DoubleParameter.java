package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonElement;

public class DoubleParameter extends Parameter<Double> {

	public DoubleParameter(String name, double defaultValue) {
		super(name, defaultValue);
	}
	
	@Override
	public Class<Double> getTypeClass() {
		return Double.class;
	}

	@Override
	public Double parseValue(JsonElement jsonElement) {
		return jsonElement.getAsDouble();
	}
	
}
