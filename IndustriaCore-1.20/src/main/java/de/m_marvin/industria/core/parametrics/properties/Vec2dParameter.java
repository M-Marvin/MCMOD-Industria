package de.m_marvin.industria.core.parametrics.properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import de.m_marvin.univec.impl.Vec2d;

public class Vec2dParameter extends Parameter<Vec2d> {

	public Vec2dParameter(String name, Vec2d defaultValue) {
		super(name, defaultValue);
	}
	
	@Override
	public Class<Vec2d> getTypeClass() {
		return Vec2d.class;
	}

	@Override
	public Vec2d parseValue(JsonElement jsonElement) {
		JsonArray arr = jsonElement.getAsJsonArray();
		return new Vec2d(arr.get(0).getAsDouble(), arr.get(1).getAsDouble());
	}
	
}
