package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonElement;

public class StringParameter extends Parameter<String> {

	public StringParameter(String name, String defaultValue) {
		super(name, defaultValue);
	}
	
	@Override
	public Class<String> getTypeClass() {
		return String.class;
	}

	@Override
	public String parseValue(JsonElement jsonElement) {
		return jsonElement.getAsString();
	}
	
}
