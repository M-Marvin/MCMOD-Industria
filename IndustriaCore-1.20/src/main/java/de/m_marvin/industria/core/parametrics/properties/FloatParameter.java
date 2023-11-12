package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonElement;

public class FloatParameter extends Parameter<Float> {

	public FloatParameter(String name, float defaultValue) {
		super(name, defaultValue);
	}
	
	@Override
	public Class<Float> getTypeClass() {
		return Float.class;
	}

	@Override
	public Float parseValue(JsonElement jsonElement) {
		return jsonElement.getAsFloat();
	}
	
}
