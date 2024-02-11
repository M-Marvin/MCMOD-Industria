package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import de.m_marvin.univec.impl.Vec2i;

public class Vec2iParameter extends Parameter<Vec2i> {

	public Vec2iParameter(String name, Vec2i defaultValue) {
		super(name, defaultValue);
	}
	
	@Override
	public Class<Vec2i> getTypeClass() {
		return Vec2i.class;
	}

	@Override
	public Vec2i parseValue(JsonElement jsonElement) {
		JsonArray arr = jsonElement.getAsJsonArray();
		return new Vec2i(arr.get(0).getAsInt(), arr.get(1).getAsInt());
	}
	
}
