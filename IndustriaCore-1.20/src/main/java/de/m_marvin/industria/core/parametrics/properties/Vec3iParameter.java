package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import de.m_marvin.univec.impl.Vec3i;

public class Vec3iParameter extends Parameter<Vec3i> {

	public Vec3iParameter(String name, Vec3i defaultValue) {
		super(name, defaultValue);
	}
	
	@Override
	public Class<Vec3i> getTypeClass() {
		return Vec3i.class;
	}

	@Override
	public Vec3i parseValue(JsonElement jsonElement) {
		JsonArray arr = jsonElement.getAsJsonArray();
		return new Vec3i(arr.get(0).getAsInt(), arr.get(1).getAsInt(), arr.get(2).getAsInt());
	}
	
}
